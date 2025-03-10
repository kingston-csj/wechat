package pers.kinson.wechat.ui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;
import jforgame.commons.JsonUtil;
import pers.kinson.wechat.base.Context;
import pers.kinson.wechat.base.MessageContentType;
import pers.kinson.wechat.base.UiContext;
import pers.kinson.wechat.logic.chat.message.req.ReqChatToChannel;
import pers.kinson.wechat.logic.chat.struct.TextMessageContent;
import pers.kinson.wechat.logic.chat.ui.EmojiPopup;
import pers.kinson.wechat.logic.constant.Constants;
import pers.kinson.wechat.logic.file.FileUiUtil;
import pers.kinson.wechat.net.IOUtil;
import pers.kinson.wechat.ui.R;
import pers.kinson.wechat.ui.StageController;

import java.io.IOException;

public class DiscussionGroupController  {

    @FXML
    private Label name;

    @FXML
    private TextArea msgInput;

    @FXML
    private TilePane members;

    public Stage getMyStage() {
        StageController stageController = UiContext.stageController;
        return stageController.getStageBy(R.Id.ChatContainer);
    }

    @FXML
    private void close() {
        UiContext.stageController.closeStage(R.Id.DiscussionGroup);
    }

    @FXML
    private void createDiscussion() {
        StageController stageController = UiContext.stageController;
        stageController.setStage(R.Id.CreateDiscussion);
    }

    @FXML
    private void sendMessage() {
        String message = msgInput.getText();
        ReqChatToChannel request = new ReqChatToChannel();
        request.setChannel(Constants.CHANNEL_DISCUSSION);
        request.setTarget(Context.discussionManager.getSelectedGroupId());
        TextMessageContent content = new TextMessageContent();
        request.setContentType(MessageContentType.TEXT);
        content.setContent(message);
        request.setContent(JsonUtil.object2String(content));

        IOUtil.send(request);
        msgInput.setText("");
    }

    @FXML
    private void showFacePanel() {
        EmojiPopup emojiPopup = new EmojiPopup(msgInput);
        emojiPopup.show(getMyStage().getScene().getWindow());
    }

    @FXML
    private void sendImageResource() throws IOException {
        ReqChatToChannel reqChatToChannel = new ReqChatToChannel();
        reqChatToChannel.setChannel(Constants.CHANNEL_DISCUSSION);
        reqChatToChannel.setTarget(Context.discussionManager.getSelectedGroupId());

        FileUiUtil.sendImageResource(getMyStage(), reqChatToChannel);
    }

    @FXML
    private void sendFileResource() throws IOException {
        ReqChatToChannel reqChatToChannel = new ReqChatToChannel();
        reqChatToChannel.setChannel(Constants.CHANNEL_DISCUSSION);
        reqChatToChannel.setTarget(Context.discussionManager.getSelectedGroupId());

        FileUiUtil.sendFileResource(getMyStage(), reqChatToChannel);
    }

}


