package pers.kinson.wechat.logic.chat;

import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import jforgame.commons.JsonUtil;
import jforgame.commons.NumberUtil;
import lombok.Getter;
import pers.kinson.wechat.logic.constant.Constants;
import pers.kinson.wechat.base.Context;
import pers.kinson.wechat.base.EventDispatcher;
import pers.kinson.wechat.base.LifeCycle;
import pers.kinson.wechat.base.UiContext;
import pers.kinson.wechat.logic.chat.message.req.ReqChatToChannel;
import pers.kinson.wechat.logic.chat.message.req.ReqFetchNewMessage;
import pers.kinson.wechat.logic.chat.message.req.ReqMarkNewMessage;
import pers.kinson.wechat.logic.chat.message.res.ResNewMessage;
import pers.kinson.wechat.logic.chat.message.res.ResNewMessageNotify;
import pers.kinson.wechat.logic.chat.message.vo.ChatMessage;
import pers.kinson.wechat.logic.chat.message.vo.EmojiVo;
import pers.kinson.wechat.logic.chat.struct.MessageContent;
import pers.kinson.wechat.logic.discussion.message.vo.DiscussionGroupVo;
import pers.kinson.wechat.net.ClientConfigs;
import pers.kinson.wechat.net.CmdConst;
import pers.kinson.wechat.net.HttpResult;
import pers.kinson.wechat.net.IOUtil;
import pers.kinson.wechat.ui.R;
import pers.kinson.wechat.ui.StageController;
import pers.kinson.wechat.util.SchedulerManager;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ChatManager implements LifeCycle {


    private Map<Long, LinkedList<ChatMessage>> friendMessage = new HashMap<>();

    @Getter
    private Map<String, EmojiVo> emojiVoMap;

    @Override
    public void init() {
        Context.messageRouter.registerHandler(CmdConst.ResNewMessageNotify, this::notifyNewMessage);
        Context.messageRouter.registerHandler(CmdConst.ResNewMessage, this::refreshNewMessage);

        EventDispatcher.eventBus.register(this);

        SchedulerManager.INSTANCE.runNow(() -> {
            try {
                HttpResult httpResult = Context.httpClientManager.get(ClientConfigs.REMOTE_HTTP_SERVER + "/emoji/list", new HashMap<>(), HttpResult.class);
                @SuppressWarnings("all")
                LinkedList<EmojiVo> list = JsonUtil.string2Collection(httpResult.getData(), LinkedList.class, EmojiVo.class);
                emojiVoMap = list.stream().collect(Collectors.toMap(EmojiVo::getLabel, Function.identity()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

    }

    public void sendMessageTo(long friendId, MessageContent content) {
        ReqChatToChannel request = new ReqChatToChannel();
        request.setChannel(Constants.CHANNEL_PERSON);
        request.setTarget(friendId);
        request.setContent(JsonUtil.object2String(content));
        request.setContentType(content.getType());
        IOUtil.send(request);
    }

    public void showFriendPrivateMessage(long friendId) {
        if (friendId <= 0) {
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
            msgContainer.getChildren().add(pane);
        });
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

}
