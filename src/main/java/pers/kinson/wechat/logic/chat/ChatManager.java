package pers.kinson.wechat.logic.chat;

import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import jforgame.commons.DateUtil;
import pers.kinson.wechat.base.Context;
import pers.kinson.wechat.base.EventDispatcher;
import pers.kinson.wechat.base.LifeCycle;
import pers.kinson.wechat.base.UiContext;
import pers.kinson.wechat.logic.chat.message.req.ReqChatToUser;
import pers.kinson.wechat.logic.chat.message.res.ResChatToUser;
import pers.kinson.wechat.logic.chat.message.vo.MessageVo;
import pers.kinson.wechat.net.CmdConst;
import pers.kinson.wechat.net.IOUtil;
import pers.kinson.wechat.ui.R;
import pers.kinson.wechat.ui.StageController;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class ChatManager implements LifeCycle {


    private Map<Long, LinkedList<MessageVo>> friendMessage = new HashMap<>();

    @Override
    public void init() {
        Context.messageRouter.registerHandler(CmdConst.ResChatToUser, this::receiveFriendPrivateMessage);

        EventDispatcher.eventBus.register(this);
    }

    public void sendMessageTo(long friendId, String content) {
        ReqChatToUser request = new ReqChatToUser();
        request.setToUserId(friendId);
        request.setContent(content);

        IOUtil.send(request);
    }

    public void refreshFriendPrivateMessage(Long friendId) {
        StageController stageController = UiContext.stageController;
        Stage stage = stageController.getStageBy(R.id.ChatToPoint);
        VBox msgContainer = (VBox) stage.getScene().getRoot().lookup("#msgContainer");

        LinkedList<MessageVo> messages = friendMessage.getOrDefault(friendId, new LinkedList<>());
        if (messages.isEmpty()) {
            return;
        }
        msgContainer.getChildren().clear();
        messages.forEach(e -> {
            Pane pane = decorateChatRecord(e);
            msgContainer.getChildren().add(pane);
        });
    }

    public void receiveFriendPrivateMessage(Object packet) {
        ResChatToUser msg = (ResChatToUser) packet;
        Long sourceId = msg.getFromUserId();
        if (sourceId == Context.userManager.getMyUserId()) {
            sourceId = msg.getToUserId();
        }
        MessageVo messageVo = MessageVo.builder().fromId(msg.getFromUserId()).toId(msg.getToUserId()).content(msg.getContent()).date(DateUtil.format(new Date())).build();
        friendMessage.putIfAbsent(sourceId, new LinkedList<>());
        friendMessage.get(sourceId).add(messageVo);

        Stage stage = UiContext.stageController.getStageBy(R.id.ChatToPoint);
        VBox msgContainer = (VBox) stage.getScene().getRoot().lookup("#msgContainer");
        if (UiContext.stageController.isStageShown(R.id.ChatToPoint)) {
            Pane pane = decorateChatRecord(messageVo);
            msgContainer.getChildren().add(pane);
        }
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
