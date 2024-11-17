package pers.kinson.wechat.logic.chat;

import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import jforgame.commons.DateUtil;
import pers.kinson.wechat.base.Constants;
import pers.kinson.wechat.base.Context;
import pers.kinson.wechat.base.EventDispatcher;
import pers.kinson.wechat.base.LifeCycle;
import pers.kinson.wechat.base.UiContext;
import pers.kinson.wechat.logic.chat.message.req.ReqChatToChannel;
import pers.kinson.wechat.logic.chat.message.vo.ChatMessage;
import pers.kinson.wechat.logic.chat.message.vo.MessageVo;
import pers.kinson.wechat.net.IOUtil;
import pers.kinson.wechat.ui.R;
import pers.kinson.wechat.ui.StageController;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ChatManager implements LifeCycle {


    private Map<Long, LinkedList<MessageVo>> friendMessage = new HashMap<>();

    @Override
    public void init() {
        EventDispatcher.eventBus.register(this);
    }

    public void sendMessageTo(long friendId, String content) {
        ReqChatToChannel request = new ReqChatToChannel();
        request.setChannel(Constants.CHANNEL_PERSON);
        request.setToUserId(friendId);
        request.setContent(content);

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
        LinkedList<MessageVo> messages = friendMessage.getOrDefault(friendId, new LinkedList<>());
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
            MessageVo messageVo = MessageVo.builder().fromId(msg.getSenderId()).toId(msg.getReceiverId()).content(msg.getContent().getContent()).date(DateUtil.format(new Date())).build();
            friendMessage.putIfAbsent(sourceId, new LinkedList<>());
            friendMessage.get(sourceId).add(messageVo);
        }
        showFriendPrivateMessage(Context.friendManager.getActivatedFriendId());
    }

    private Pane decorateChatRecord(MessageVo message) {
        boolean fromMe = message.getFromId() == Context.userManager.getMyUserId();
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
            nameUi.setText(Context.friendManager.getUserName(message.getFromId()));
        }
        nameUi.setVisible(false);
        Label _createTime = (Label) chatRecord.lookup("#timeUi");
        _createTime.setText(message.getDate());
        Label _body = (Label) chatRecord.lookup("#contentUi");
        _body.setText(message.getContent());

        return chatRecord;
    }

}
