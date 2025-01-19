package pers.kinson.wechat.ui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import jforgame.commons.NumberUtil;
import lombok.extern.slf4j.Slf4j;
import pers.kinson.wechat.base.Context;
import pers.kinson.wechat.base.UiContext;
import pers.kinson.wechat.logic.chat.message.req.ReqChatToChannel;
import pers.kinson.wechat.logic.chat.struct.TextMessageContent;
import pers.kinson.wechat.logic.chat.ui.EmojiPopup;
import pers.kinson.wechat.logic.constant.Constants;
import pers.kinson.wechat.logic.file.FileUiUtil;
import pers.kinson.wechat.ui.R;
import pers.kinson.wechat.ui.StageController;

import java.io.IOException;


@Slf4j
public class ChatToPointController  {

    @FXML
    private Label userIdUi;

    @FXML
    private TextArea msgInput;

    @FXML
    private ScrollPane msgScrollPane;


    @FXML
    private void sendMessage() {
        final long userId = Long.parseLong(userIdUi.getText());
        String message = msgInput.getText();
        TextMessageContent content = new TextMessageContent();
        content.setContent(message);
        Context.chatManager.sendMessageTo(userId, content);
        msgInput.clear();
    }


    public Stage getMyStage() {
        StageController stageController = UiContext.stageController;
        return stageController.getStageBy(R.Id.ChatContainer);
    }

    @FXML
    private void createDiscussion() {
        StageController stageController = UiContext.stageController;
        stageController.setStage(R.Id.CreateDiscussion);
    }

    @FXML
    private void showFacePanel() {
        EmojiPopup emojiPopup = new EmojiPopup(msgInput);
        emojiPopup.show(getMyStage().getScene().getWindow());
    }

    @FXML
    private void sendImageResource() throws IOException {
        ReqChatToChannel reqChatToChannel = new ReqChatToChannel();
        reqChatToChannel.setChannel(Constants.CHANNEL_PERSON);
        reqChatToChannel.setTarget(NumberUtil.longValue(userIdUi.getText()));

        FileUiUtil.sendImageResource(getMyStage(), reqChatToChannel);
    }

    @FXML
    private void sendOfflineFileResource() throws IOException {
        ReqChatToChannel reqChatToChannel = new ReqChatToChannel();
        reqChatToChannel.setChannel(Constants.CHANNEL_PERSON);
        reqChatToChannel.setTarget(NumberUtil.longValue(userIdUi.getText()));

        FileUiUtil.sendFileResource(getMyStage(), reqChatToChannel);
    }

    @FXML
    private void sendOnlineFileResource() throws IOException {
        FileUiUtil.sendOnlineFileResource(getMyStage(), NumberUtil.longValue(userIdUi.getText()));
    }


}


