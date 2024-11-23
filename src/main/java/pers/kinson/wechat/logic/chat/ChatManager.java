package pers.kinson.wechat.logic.chat;

import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import jforgame.commons.JsonUtil;
import jforgame.commons.TimeUtil;
import lombok.Getter;
import pers.kinson.wechat.base.Constants;
import pers.kinson.wechat.base.Context;
import pers.kinson.wechat.base.EventDispatcher;
import pers.kinson.wechat.base.LifeCycle;
import pers.kinson.wechat.base.UiContext;
import pers.kinson.wechat.logic.chat.message.req.ReqChatToChannel;
import pers.kinson.wechat.logic.chat.message.vo.ChatMessage;
import pers.kinson.wechat.logic.chat.message.vo.EmojiVo;
import pers.kinson.wechat.logic.chat.message.vo.MessageVo;
import pers.kinson.wechat.logic.chat.struct.ContentElemNode;
import pers.kinson.wechat.logic.chat.struct.MessageContent;
import pers.kinson.wechat.net.ClientConfigs;
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
        EventDispatcher.eventBus.register(this);

        SchedulerManager.INSTANCE.registerUniqueTimeoutTask("fetchEmoji", () -> {
            try {
                HttpResult httpResult = Context.httpClientManager.get(ClientConfigs.REMOTE_HTTP_SERVER + "/emoji/list", new HashMap<>(), HttpResult.class);
                @SuppressWarnings("all")
                LinkedList<EmojiVo> list = JsonUtil.string2Collection(httpResult.getData(), LinkedList.class, EmojiVo.class);
                emojiVoMap = list.stream().collect(Collectors.toMap(EmojiVo::getLabel, Function.identity()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, TimeUtil.MILLIS_PER_SECOND);

    }

    public void sendMessageTo(long friendId, MessageContent content) {
        ReqChatToChannel request = new ReqChatToChannel();
        request.setChannel(Constants.CHANNEL_PERSON);
        request.setToUserId(friendId);
        request.setContent(JsonUtil.object2String(content));

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
//            MessageVo messageVo = MessageVo.builder().fromId(msg.getSenderId()).toId(msg.getReceiverId())
//                    .messageContent(msg.getContent()).date(msg.getDate()).build();
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
        if (message.getContent().getType() == MessageContentFactory.TYPE_NORMAL) {
            List<ContentElemNode> nodes = MessageTextUiEditor.parseMessage(message.getContent().getContent());
            for (ContentElemNode node : nodes) {
                _body.getChildren().add(node.toUi());
            }
        }
        return chatRecord;
    }

}
