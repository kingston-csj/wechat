package pers.kinson.wechat.logic.chat;

import javafx.application.Platform;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import jforgame.commons.JsonUtil;
import jforgame.commons.NumberUtil;
import jforgame.commons.TimeUtil;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.input.CountingInputStream;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import pers.kinson.wechat.SystemConfig;
import pers.kinson.wechat.base.Context;
import pers.kinson.wechat.base.EventDispatcher;
import pers.kinson.wechat.base.LifeCycle;
import pers.kinson.wechat.base.UiContext;
import pers.kinson.wechat.logic.chat.message.req.ReqChatToChannel;
import pers.kinson.wechat.logic.chat.message.req.ReqFetchNewMessage;
import pers.kinson.wechat.logic.chat.message.req.ReqMarkNewMessage;
import pers.kinson.wechat.logic.chat.message.res.ResModifyMessage;
import pers.kinson.wechat.logic.chat.message.res.ResNewMessage;
import pers.kinson.wechat.logic.chat.message.res.ResNewMessageNotify;
import pers.kinson.wechat.logic.chat.message.vo.ChatMessage;
import pers.kinson.wechat.logic.chat.message.vo.EmojiVo;
import pers.kinson.wechat.logic.chat.struct.MessageContent;
import pers.kinson.wechat.logic.constant.Constants;
import pers.kinson.wechat.logic.discussion.message.vo.DiscussionGroupVo;
import pers.kinson.wechat.logic.file.message.push.PushBeginTransferFile;
import pers.kinson.wechat.logic.file.message.req.ReqOnlineTransferFileFinish;
import pers.kinson.wechat.net.CmdConst;
import pers.kinson.wechat.net.HttpResult;
import pers.kinson.wechat.net.IOUtil;
import pers.kinson.wechat.ui.R;
import pers.kinson.wechat.ui.StageController;
import pers.kinson.wechat.util.Base64CodecUtil;
import pers.kinson.wechat.util.SchedulerManager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
public class ChatManager implements LifeCycle {


    private Map<Long, LinkedList<ChatMessage>> friendMessage = new HashMap<>();

    @Getter
    private ConcurrentMap<String, EmojiVo> emojiVoMap = new ConcurrentHashMap<>();

    @Override
    public void init() {
        Context.messageRouter.registerHandler(CmdConst.ResNewMessageNotify, this::notifyNewMessage);
        Context.messageRouter.registerHandler(CmdConst.ResNewMessage, this::refreshNewMessage);
        Context.messageRouter.registerHandler(CmdConst.ResModifyMessage, this::refreshMessage);
        Context.messageRouter.registerHandler(CmdConst.PushBeginOnlineFileTransfer, this::doTransferFile);

        EventDispatcher.eventBus.register(this);

        SchedulerManager.INSTANCE.runDelay(() -> {
            try {
                HttpResult httpResult = Context.httpClientManager.get(SystemConfig.getInstance().getServer().getRemoteHttpUrl() + "/emoji/list", new HashMap<>(), HttpResult.class);
                @SuppressWarnings("all") LinkedList<EmojiVo> list = JsonUtil.string2Collection(httpResult.getData(), LinkedList.class, EmojiVo.class);
                for (EmojiVo emojiVo : list) {
                    Image image = new Image(emojiVo.getUrl());
                    emojiVo.setImage(image);
                    emojiVoMap.put(emojiVo.getLabel(), emojiVo);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, 3 * TimeUtil.MILLIS_PER_SECOND);

    }

    public void sendMessageTo(long friendId, MessageContent content) {
        ReqChatToChannel request = new ReqChatToChannel();
        request.setChannel(Constants.CHANNEL_PERSON);
        request.setTarget(friendId);
        request.setContent(JsonUtil.object2String(content));
        request.setContentType(content.getType());
        IOUtil.send(request);
    }

    public void showFriendPrivateMessage(Long friendId) {
        if (friendId == null) {
            return;
        }
        StageController stageController = UiContext.stageController;
        Stage stage = stageController.getStageBy(R.id.ChatToPoint);
        VBox msgContainer = (VBox) stage.getScene().getRoot().lookup("#msgContainer");
        msgContainer.getChildren().clear();
        LinkedList<ChatMessage> messages = friendMessage.getOrDefault(friendId, new LinkedList<>());
        if (messages.isEmpty()) {
            return;
        }
        messages.forEach(e -> {
            Pane pane = decorateChatRecord(e);
            pane.setId("recordPane@" + e.getId());
            msgContainer.getChildren().add(pane);
        });
        ScrollPane scrollPane = (ScrollPane) stage.getScene().getRoot().lookup("#msgScrollPane");
        // 使用Platform.runLater确保在布局更新后设置滚动值
        Platform.runLater(() -> scrollPane.setVvalue(1));
    }

    public void receiveFriendPrivateMessage(List<ChatMessage> messages) {
        for (ChatMessage msg : messages) {
            long sourceId = msg.getSenderId();
            if (sourceId == Context.userManager.getMyUserId()) {
                sourceId = msg.getReceiverId();
            }
            friendMessage.putIfAbsent(sourceId, new LinkedList<>());
            friendMessage.get(sourceId).add(msg);
        }
        showFriendPrivateMessage(Context.friendManager.getActivatedFriendId());
    }

    public void refreshFriendPrivateMessage(ChatMessage message) {
        // 先修改数据
        long sourceId = message.getSenderId();
        if (sourceId == Context.userManager.getMyUserId()) {
            sourceId = message.getReceiverId();
        }
        for (ChatMessage chatMsg : friendMessage.get(sourceId)) {
            if (chatMsg.getId() == message.getId()) {
                chatMsg.setContent(message.getContent());
            }
        }
        StageController stageController = UiContext.stageController;
        // 已经创建的消息ui，修改内容
        if (stageController.isStageShown(R.id.ChatToPoint)) {
            Stage stage = stageController.getStageBy(R.id.ChatToPoint);
            Pane msgContainer = (Pane) stage.getScene().getRoot().lookup("#recordPane@" + message.getId());
            Context.messageContentFactory.refreshItem(message.getContent().getType(), msgContainer, message);
        }
    }

    private Pane decorateChatRecord(ChatMessage message) {
        boolean fromMe = message.getSenderId() == Context.userManager.getMyUserId();
        StageController stageController = UiContext.stageController;
        Pane chatRecord = null;
        if (fromMe) {
            chatRecord = stageController.load(R.layout.PrivateChatItemRight, Pane.class);
        } else {
            chatRecord = stageController.load(R.layout.PrivateChatItemLeft, Pane.class);
        }

        Hyperlink nameUi = (Hyperlink) chatRecord.lookup("#nameUi");
        if (fromMe) {
            nameUi.setText(Context.userManager.getMyProfile().getUserName());
        } else {
            nameUi.setText(Context.friendManager.getUserName(message.getSenderId()));
        }
        nameUi.setVisible(false);
        Label _createTime = (Label) chatRecord.lookup("#timeUi");
        _createTime.setText(message.getDate());
        FlowPane _body = (FlowPane) chatRecord.lookup("#contentUi");

        Context.messageContentFactory.displayUi(message.getContent().getType(), _body, message);
//        chatRecord.setStyle("-fx-border-color: red");
//        _body.setStyle("-fx-border-color: blue");
        return chatRecord;
    }

    private void notifyNewMessage(Object packet) {
        ResNewMessageNotify message = (ResNewMessageNotify) packet;
        ReqFetchNewMessage reqFetchNewMessage = new ReqFetchNewMessage();
        // 这里先写点丑代码，后续优化
        if (message.getChannel() == Constants.CHANNEL_DISCUSSION) {
            DiscussionGroupVo targetDiscussionGroup = Context.discussionManager.getDiscussionGroupVo(NumberUtil.longValue(message.getTopic()));
            if (targetDiscussionGroup != null) {
                reqFetchNewMessage.setChannel(Constants.CHANNEL_DISCUSSION);
                reqFetchNewMessage.setTopic(targetDiscussionGroup.getId());
                reqFetchNewMessage.setMaxSeq(targetDiscussionGroup.getMaxSeq());
            }
        } else if (message.getChannel() == Constants.CHANNEL_PERSON) {
            reqFetchNewMessage.setTopic(Context.userManager.getMyUserId());
            reqFetchNewMessage.setMaxSeq(Context.userManager.getMyProfile().getChatMaxSeq());

            Context.friendManager.updateRedPoint(message.getSenderId(), true);
        }
        IOUtil.send(reqFetchNewMessage);
    }

    private void refreshNewMessage(Object packet) {
        ResNewMessage message = (ResNewMessage) packet;
        if (message.getMessages() == null || message.getMessages().isEmpty()) {
            return;
        }
        long maxSeq = 0;
        for (ChatMessage e : message.getMessages()) {
            e.setContent(Context.messageContentFactory.parse(e.getType(), e.getJson()));
            maxSeq = Math.max(maxSeq, e.getId());
        }

        ReqMarkNewMessage reqMarkNewMessage = new ReqMarkNewMessage();
        reqMarkNewMessage.setChannel(message.getChannel());
        reqMarkNewMessage.setMaxSeq(maxSeq);
        // 根据消息来源进行分发
        if (message.getChannel() == Constants.CHANNEL_DISCUSSION) {
            long discussionId = message.getMessages().get(0).getReceiverId();
            reqMarkNewMessage.setTopic(discussionId);
            Context.discussionManager.receiveDiscussionMessages(maxSeq, message.getMessages());
        } else if (message.getChannel() == Constants.CHANNEL_PERSON) {
            Context.userManager.getMyProfile().setChatMaxSeq(maxSeq);
            Context.chatManager.receiveFriendPrivateMessage(message.getMessages());
        }

        // 收到消息之后再通知服务器，保证不丢消息
        IOUtil.send(reqMarkNewMessage);
    }

    private void refreshMessage(Object packet) {
        ResModifyMessage resModifyMessage = (ResModifyMessage) packet;
        ChatMessage message = resModifyMessage.getMessage();
        message.setContent(Context.messageContentFactory.parse(message.getType(), message.getJson()));
        if (resModifyMessage.getChannel() == Constants.CHANNEL_PERSON) {
            refreshFriendPrivateMessage(message);
        }
    }

    @SneakyThrows
    private void doTransferFile(Object packet) {
        PushBeginTransferFile message = (PushBeginTransferFile) packet;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        // 创建HttpPost请求，指定服务器地址和端口
        HttpPost httpPost = new HttpPost(message.getHost());
        // 构建要上传的文件实体
        File fileToUpload = new File(message.getFileUrl()); // 替换为实际要上传的文件路径
//        HttpEntity fileEntity = MultipartEntityBuilder.create()
//                .addBinaryBody("file", fileToUpload, ContentType.APPLICATION_OCTET_STREAM, fileToUpload.getName())
//                .build();
        HttpEntity fileEntity = buildFileEntityWithProgress(fileToUpload);
        // 设置请求实体
        httpPost.setEntity(fileEntity);

        // 设置请求头，这里设置文件名，与服务器端接收时获取文件名的方式对应
        httpPost.setHeader("requestId", message.getRequestId());
        httpPost.setHeader("secretKey", message.getSecretKey());
        httpPost.setHeader("fileName", Base64CodecUtil.encode(message.getFileName()));
        // 发送请求并获取响应
        try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
            // 处理响应
            HttpEntity responseEntity = response.getEntity();
            if (responseEntity != null) {
                System.out.println("Response content: " + org.apache.http.util.EntityUtils.toString(responseEntity));
            }
        } catch (Exception e) {
            log.error("", e);
        } finally {
            // 关闭资源
            httpClient.close();
        }

        // 由发送方通知文件传输结束
        ReqOnlineTransferFileFinish notify = new ReqOnlineTransferFileFinish();
        notify.setMessageId(NumberUtil.longValue(message.getRequestId()));
        IOUtil.send(notify);
    }

    private static HttpEntity buildFileEntityWithProgress(File fileToUpload) {
        try {
            CountingInputStream countingInputStream = new CountingInputStream(FileUtils.openInputStream(fileToUpload));
            ProgressFileBody progressFileBody = new ProgressFileBody(new FileBody(fileToUpload, ContentType.APPLICATION_OCTET_STREAM), countingInputStream);
            // 创建文件实体构建器
            MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
            entityBuilder.addPart("file", progressFileBody);
            // 获取文件总大小
            long fileSize = fileToUpload.length();
            // 构建文件实体
            HttpEntity fileEntity = entityBuilder.build();
            // 关闭CountingInputStream，确保资源释放
            countingInputStream.close();
            return fileEntity;
        } catch (Exception e) {
            log.error("", e);
        }
        return null;
    }

    static class ProgressFileBody extends FileBody {
        private final CountingInputStream countingInputStream;

        public ProgressFileBody(FileBody fileBody, CountingInputStream countingInputStream) {
            super(fileBody.getFile(), fileBody.getContentType());
            this.countingInputStream = countingInputStream;
        }

        @Override
        public void writeTo(OutputStream out) throws IOException {
            try (InputStream in = this.getInputStream()) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                long fileSize = this.getFile().length();
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                    int percent = (int) ((countingInputStream.getByteCount() * 100L) / fileSize);
                    System.out.println("上传进度: " + percent + "%");
                }
            }
        }
    }

}
