package pers.kinson.wechat.logic.chat;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;
import jforgame.commons.DateUtil;
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
import pers.kinson.wechat.config.SystemConfig;
import pers.kinson.wechat.base.Context;
import pers.kinson.wechat.base.EventDispatcher;
import pers.kinson.wechat.base.LifeCycle;
import pers.kinson.wechat.base.UiContext;
import pers.kinson.wechat.database.SqliteDbUtil;
import pers.kinson.wechat.logic.chat.message.req.ReqChatToChannel;
import pers.kinson.wechat.logic.chat.message.req.ReqFetchNewMessage;
import pers.kinson.wechat.logic.chat.message.req.ReqMarkNewMessage;
import pers.kinson.wechat.logic.chat.message.res.ResModifyMessage;
import pers.kinson.wechat.logic.chat.message.res.ResNewMessage;
import pers.kinson.wechat.logic.chat.message.res.ResNewMessageNotify;
import pers.kinson.wechat.logic.chat.message.vo.ChatMessage;
import pers.kinson.wechat.logic.chat.message.vo.EmojiVo;
import pers.kinson.wechat.logic.chat.model.ChatContact;
import pers.kinson.wechat.logic.chat.struct.MessageContent;
import pers.kinson.wechat.logic.chat.struct.Resource;
import pers.kinson.wechat.logic.constant.Constants;
import pers.kinson.wechat.logic.discussion.message.vo.DiscussionGroupVo;
import pers.kinson.wechat.logic.file.message.push.PushBeginTransferFile;
import pers.kinson.wechat.logic.file.message.req.ReqOnlineTransferFileFinish;
import pers.kinson.wechat.logic.friend.message.vo.FriendItemVo;
import pers.kinson.wechat.logic.system.AvatarCache;
import pers.kinson.wechat.net.CmdConst;
import pers.kinson.wechat.net.HttpResult;
import pers.kinson.wechat.net.IOUtil;
import pers.kinson.wechat.ui.R;
import pers.kinson.wechat.ui.StageController;
import pers.kinson.wechat.ui.controller.ProgressMonitor;
import pers.kinson.wechat.util.Base64CodecUtil;
import pers.kinson.wechat.util.FileUtil;
import pers.kinson.wechat.util.SchedulerManager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
public class ChatManager implements LifeCycle {


    private Map<Long, LinkedList<ChatMessage>> friendMessage = new HashMap<>();

    /**
     * 保存每个好友最小的消息序号
     */
    private Map<Long, Long> friendMinSeq = new HashMap<>();

    private Map<Integer, ChatPaneHandler> chatPaneHandlers = new HashMap<>();

    @Override
    public void init() {
        Context.messageRouter.registerHandler(CmdConst.ResNewMessageNotify, this::notifyNewMessage);
        Context.messageRouter.registerHandler(CmdConst.ResNewMessage, this::refreshNewMessage);
        Context.messageRouter.registerHandler(CmdConst.ResModifyMessage, this::refreshMessage);
        Context.messageRouter.registerHandler(CmdConst.PushBeginOnlineFileTransfer, this::doTransferFile);

        EventDispatcher.eventBus.register(this);

        FileUtil.createDirectory("asserts/emoji");

        chatPaneHandlers.put(ChatContact.TYPE_FRIEND, Context.friendManager);
        chatPaneHandlers.put(ChatContact.TYPE_DISCUSSION, Context.discussionManager);
    }

    public void sendMessageTo(long friendId, MessageContent content) {
        ReqChatToChannel request = new ReqChatToChannel();
        request.setChannel(Constants.CHANNEL_PERSON);
        request.setTarget(friendId);
        request.setContent(JsonUtil.object2String(content));
        request.setContentType(content.getType());
        IOUtil.send(request);
    }

    public List<ChatMessage> loadHistoryMessage(Long friendId, boolean force) {
        // 如果不是强制，则只在第一次打开界面才读取
        if (!force) {
            if (friendMessage.containsKey(friendId)) {
                return Collections.emptyList();
            }
        }
        long minSeq = friendMinSeq.getOrDefault(friendId, Long.MAX_VALUE);
        long myUserId = Context.userManager.getMyUserId();
        List<ChatMessage> chatMessages = SqliteDbUtil.queryPrivateMessages(myUserId, friendId, minSeq);
        for (int i = chatMessages.size() - 1; i >= 0; i--) {
            ChatMessage msg = chatMessages.get(i);
            msg.setMessageContent(Context.messageContentFactory.parse(msg.getType(), msg.getContent()));
            msg.setDate(DateUtil.format(new Date(NumberUtil.longValue(msg.getDate()))));
            long sourceId = msg.getSender();
            if (sourceId == Context.userManager.getMyUserId()) {
                sourceId = msg.getReceiver();
            }
            friendMessage.putIfAbsent(sourceId, new LinkedList<>());
            friendMessage.get(sourceId).add(msg);
            minSeq = Math.min(minSeq, msg.getId());
        }
        friendMinSeq.put(friendId, minSeq);
        return chatMessages;
    }

    public void showFriendPrivateMessage(Long friendId) {
        if (friendId == null) {
            return;
        }
        LinkedList<ChatMessage> messages = friendMessage.getOrDefault(friendId, new LinkedList<>());
        if (messages.isEmpty()) {
            return;
        }
        showFriendPrivateMessage(friendId, messages, false);
    }

    public void showFriendPrivateMessage(long friendId, List<ChatMessage> messages, boolean history) {
        if (messages.isEmpty()) {
            return;
        }
        StageController stageController = UiContext.stageController;
        if (!stageController.isStageShown(R.Id.ChatContainer)) {
            return;
        }
        Stage stage = stageController.getStageBy(R.Id.ChatContainer);
        VBox msgContainer = contact2Pane.get("1_" + friendId);
        messages.forEach(e -> {
            // 已经存在的就不要重新创建了
            if (msgContainer.lookup("#recordPane@" + e.getId()) == null) {
                Pane pane = decorateChatRecord(e);
                pane.setId("recordPane@" + e.getId());
                if (history) {
                    msgContainer.getChildren().add(0, pane);
                } else {
                    msgContainer.getChildren().add(pane);
                }
            }
        });
        msgContainer.requestLayout(); // 强制刷新布局
        ScrollPane scrollPane = (ScrollPane) stage.getScene().getRoot().lookup("#msgScrollPane");
        // 使用Platform.runLater确保在布局更新后设置滚动值
        Platform.runLater(() -> {
            if (history) {
                scrollPane.setVvalue(0);
            } else {
                scrollPane.setVvalue(1);
            }
        });
    }

    public void receiveFriendPrivateMessage(List<ChatMessage> messages) {
        long targetUserId = 0;
        for (ChatMessage msg : messages) {
            targetUserId = msg.getSender();
            if (targetUserId == Context.userManager.getMyUserId()) {
                targetUserId = msg.getReceiver();
            }
            long date = 0;
            try {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                date = formatter.parse(msg.getDate()).getTime();
            } catch (Exception e) {

            }
            SqliteDbUtil.insertMessage(msg.getId(), msg.getContent(), Constants.CHANNEL_PERSON, msg.getReceiver(), msg.getSender(), date, msg.getType());
            friendMessage.putIfAbsent(targetUserId, new LinkedList<>());
            friendMessage.get(targetUserId).add(msg);
        }
        showFriendPrivateMessage(targetUserId);
    }

    public void refreshFriendPrivateMessage(ChatMessage message) {
        // 先修改数据
        long sourceId = message.getSender();
        if (sourceId == Context.userManager.getMyUserId()) {
            sourceId = message.getReceiver();
        }
        for (ChatMessage chatMsg : friendMessage.get(sourceId)) {
            if (chatMsg.getId() == message.getId()) {
                chatMsg.setMessageContent(message.getMessageContent());
            }
        }
        StageController stageController = UiContext.stageController;
        // 已经创建的消息ui，修改内容
        if (stageController.isStageShown(R.Id.ChatContainer)) {
            Stage stage = stageController.getStageBy(R.Id.ChatContainer);
            Pane msgContainer = (Pane) stage.getScene().getRoot().lookup("#recordPane@" + message.getId());
            Context.messageContentFactory.refreshItem(message.getMessageContent().getType(), msgContainer, message);
        }
    }

    private Pane decorateChatRecord(ChatMessage message) {
        boolean fromMe = message.getSender() == Context.userManager.getMyUserId();
        StageController stageController = UiContext.stageController;
        Pane chatRecord = null;
        if (fromMe) {
            chatRecord = stageController.load(R.Layout.PrivateChatItemRight, Pane.class);
        } else {
            chatRecord = stageController.load(R.Layout.PrivateChatItemLeft, Pane.class);
        }

        Hyperlink nameUi = (Hyperlink) chatRecord.lookup("#nameUi");
        ImageView headImage = (ImageView) chatRecord.lookup("#headImage");
        if (fromMe) {
            nameUi.setText(Context.userManager.getMyProfile().getUserName());
            headImage.setImage(getAvatarImage(Context.userManager.getMyProfile().getUserId()));
        } else {
            nameUi.setText(Context.friendManager.getUserName(message.getSender()));
            headImage.setImage(getAvatarImage(message.getSender()));
        }
        nameUi.setVisible(false);
        Label _createTime = (Label) chatRecord.lookup("#timeUi");
        _createTime.setText(message.getDate());
        FlowPane _body = (FlowPane) chatRecord.lookup("#contentUi");

        Context.messageContentFactory.displayUi(message.getMessageContent().getType(), _body, message);
//        chatRecord.setStyle("-fx-border-color: red");
//        _body.setStyle("-fx-border-color: blue");
        return chatRecord;
    }

    private Image getAvatarImage(long userId) {
        if (Context.userManager.getMyProfile().getUserId() == userId) {
            return AvatarCache.getOrCreateImage(Context.userManager.getMyProfile().getAvatar());
        }
        FriendItemVo friendVo = Context.friendManager.queryFriend(userId);
        return AvatarCache.getOrCreateImage(friendVo.getHeadUrl());
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
            e.setMessageContent(Context.messageContentFactory.parse(e.getType(), e.getContent()));
            maxSeq = Math.max(maxSeq, e.getId());
        }

        ReqMarkNewMessage reqMarkNewMessage = new ReqMarkNewMessage();
        reqMarkNewMessage.setChannel(message.getChannel());
        reqMarkNewMessage.setMaxSeq(maxSeq);
        // 根据消息来源进行分发
        if (message.getChannel() == Constants.CHANNEL_DISCUSSION) {
            long discussionId = message.getMessages().get(0).getReceiver();
            reqMarkNewMessage.setTopic(discussionId);
            receiveDiscussionMessages(maxSeq, message.getMessages());
        } else if (message.getChannel() == Constants.CHANNEL_PERSON) {
            Context.userManager.getMyProfile().setChatMaxSeq(maxSeq);
            receiveFriendPrivateMessage(message.getMessages());
        }

        // 收到消息之后再通知服务器，保证不丢消息
        IOUtil.send(reqMarkNewMessage);
    }

    private void refreshMessage(Object packet) {
        ResModifyMessage resModifyMessage = (ResModifyMessage) packet;
        ChatMessage message = resModifyMessage.getMessage();
        message.setMessageContent(Context.messageContentFactory.parse(message.getType(), message.getContent()));
        if (resModifyMessage.getChannel() == Constants.CHANNEL_PERSON) {
            refreshFriendPrivateMessage(message);
        }
    }


    // 动态设置联系人列表数据（有序map）
    private Map<String, ChatContact> sortedContacts = new LinkedHashMap<>();
    // 动态设置联系人列表数据
    ObservableList<ChatContact> contacts = FXCollections.observableArrayList();
    // 不同联系人对应不同的消息面板缓存
    private Map<String, VBox> contact2Pane = new HashMap<>();

    /**
     * 当前激活的联系人信息
     */
    @Getter
    private ChatContact activatedContact;

    public void openChatPanel(ChatContact contact) {
        StageController stageController = UiContext.stageController;
        Stage chatStage = stageController.setStage(R.Id.ChatContainer);
        Parent root = chatStage.getScene().getRoot();
        String key = contact.getKey();
        VBox targetMsgContainer = contact2Pane.get(key);

        Pane container = (Pane) root.lookup("#container");
        container.getChildren().clear();
        container.getChildren().add(chatPaneHandlers.get(contact.getType()).loadMessagePane());

        if (!sortedContacts.containsKey(key)) {
            contacts.add(contact);
            VBox msgContainer = new VBox();
            msgContainer.setId("msgContainer");
            msgContainer.setFillWidth(false);
            contact2Pane.put(key, msgContainer);
            sortedContacts.put(key, contact);
            targetMsgContainer = msgContainer;
        }
        ListView contactList = (ListView) root.lookup("#contactList");
        contactList.setItems(contacts);

        // 自定义 ListView 的单元格
        contactList.setCellFactory(new Callback<ListView<ChatContact>, ListCell<ChatContact>>() {
            @Override
            public ListCell<ChatContact> call(ListView<ChatContact> param) {
                return new ListCell<ChatContact>() {
                    @Override
                    protected void updateItem(ChatContact contact, boolean empty) {
                        super.updateItem(contact, empty);
                        if (empty || contact == null) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            // 创建单元格内容
                            HBox cellContent = new HBox(10); // 设置间距
                            ImageView imageView = new ImageView(contact.getAvatar());
                            imageView.setFitWidth(40);
                            imageView.setFitHeight(40);
                            cellContent.getChildren().addAll(
                                    imageView, // 头像
                                    new Text(contact.getName()) // 姓名
                            );
                            setGraphic(cellContent);
                        }
                    }
                };
            }
        });

        // 绑定联系人点击事件
        contactList.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null) {
                container.getChildren().clear();
                // 切换到对应的聊天内容
                ChatContact newContact = (ChatContact) newValue;
                container.getChildren().add(chatPaneHandlers.get(newContact.getType()).loadMessagePane());
                VBox newMsgContainer = contact2Pane.get(newContact.getKey());
                chatPaneHandlers.get(newContact.getType()).onChatPaneShow(container, newContact);
                ScrollPane msgScrollPane = (ScrollPane) root.lookup("#msgScrollPane");
                msgScrollPane.setContent(newMsgContainer);
                msgScrollPane.requestLayout(); // 强制刷新布局
                activatedContact = newContact;
            }
        });

        int selectedItemIndex = 0;
        for (int i = 0; i < contacts.size(); i++) {
            if (Objects.equals(contacts.get(i).getId(), contact.getId())) {
                selectedItemIndex = i;
                break;
            }
        }
        // 主动设置点击态
        contactList.getSelectionModel().select(selectedItemIndex);
        ScrollPane msgScrollPane = (ScrollPane) root.lookup("#msgScrollPane");
        msgScrollPane.setContent(targetMsgContainer);

        chatPaneHandlers.get(contact.getType()).onChatPaneShow(container, contact);

        activatedContact = contact;
    }

    private Map<Long, List<ChatMessage>> discussionMessages = new ConcurrentHashMap<>();

    public void receiveDiscussionMessages(long maxSeq, List<ChatMessage> messages) {
        long discussionId = 0;
        for (ChatMessage message : messages) {
            discussionId = message.getReceiver();
            discussionMessages.putIfAbsent(discussionId, new LinkedList<>());
            discussionMessages.get(discussionId).add(message);
            Context.discussionManager.updateDiscussionSeq(discussionId, maxSeq);
        }

        StageController stageController = UiContext.stageController;
        Stage stage = stageController.getStageBy(R.Id.ChatContainer);
        VBox msgContainer = contact2Pane.get("2_" + discussionId);

        List<ChatMessage> allMsg = discussionMessages.get(discussionId);
        allMsg.forEach(e -> {
            // 已经存在的就不要重新创建了
            if (msgContainer.lookup("#recordPane@" + e.getId()) == null) {
                Pane pane = decorateDiscussionChatRecord(e);
                pane.setId("recordPane@" + e.getId());
                msgContainer.getChildren().add(pane);
            }
        });
        ScrollPane scrollPane = (ScrollPane) stage.getScene().getRoot().lookup("#msgScrollPane");
        // 使用Platform.runLater确保在布局更新后设置滚动值
        Platform.runLater(() -> scrollPane.setVvalue(1));
    }


    private Pane decorateDiscussionChatRecord(ChatMessage message) {
        boolean fromMe = message.getSender() == Context.userManager.getMyUserId();
        StageController stageController = UiContext.stageController;
        Pane chatRecord = stageController.load(R.Layout.DiscussionChatItem, Pane.class);

        Hyperlink nameUi = (Hyperlink) chatRecord.lookup("#nameUi");
        ImageView headImage = (ImageView) chatRecord.lookup("#headImage");
        if (fromMe) {
            nameUi.setText(Context.userManager.getMyProfile().getUserName());
            headImage.setImage(getAvatarImage(Context.userManager.getMyProfile().getUserId()));
        } else {
            nameUi.setText(Context.friendManager.getUserName(message.getSender()));
            headImage.setImage(getAvatarImage(message.getSender()));
        }

        nameUi.setVisible(false);
        Label _createTime = (Label) chatRecord.lookup("#timeUi");
        _createTime.setText(message.getDate());
        FlowPane _body = (FlowPane) chatRecord.lookup("#contentUi");

        Context.messageContentFactory.displayUi(message.getMessageContent().getType(), _body, message);

        return chatRecord;
    }


    @SneakyThrows
    private void doTransferFile(Object packet) {
        PushBeginTransferFile message = (PushBeginTransferFile) packet;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        // 创建HttpPost请求，指定服务器地址和端口
        HttpPost httpPost = new HttpPost(message.getHost());
        // 构建要上传的文件实体
        File fileToUpload = new File(message.getFileUrl()); // 替换为实际要上传的文件路径
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
