package pers.kinson.wechat.ui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
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
import pers.kinson.wechat.logic.system.ApplicationEffect;
import pers.kinson.wechat.ui.ControlledStage;
import pers.kinson.wechat.ui.R;
import pers.kinson.wechat.ui.StageController;
import pers.kinson.wechat.util.SchedulerManager;

import java.io.IOException;


@Slf4j
public class ChatToPointController implements ControlledStage {

    @FXML
    private Label userIdUi;

    @FXML
    private TextArea msgInput;

    @FXML
    private Button sendBtn;


    @Override
    public void onStageShown() {
        msgInput.requestFocus();

        msgInput.setOnKeyPressed(event -> {
            // 注册enter快捷键
            if (event.getCode() == KeyCode.ENTER) {
                sendMessage();
            }
            // 注册ctrl+v快捷键
            // 复制系统剪贴板图片资源
            if (event.isControlDown() && event.getCode() == KeyCode.V) {
                SchedulerManager.INSTANCE.runNow(this::sendClipboardImage);
            }
        });

        // 获得焦点，关闭小图标闪动
        msgInput.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                ApplicationEffect.stopBlink();
            }
        });
    }

    private void sendClipboardImage() {
        ReqChatToChannel reqChatToChannel = new ReqChatToChannel();
        reqChatToChannel.setChannel(Constants.CHANNEL_PERSON);
        reqChatToChannel.setTarget(NumberUtil.longValue(userIdUi.getText()));

        FileUiUtil.sendClipboardResource(msgInput, reqChatToChannel);
    }


    @FXML
    private void sendMessage() {
        final long userId = Long.parseLong(userIdUi.getText());
        String message = msgInput.getText();
        TextMessageContent content = new TextMessageContent();
        content.setContent(message);
        Context.chatManager.sendMessageTo(userId, content);
        msgInput.clear();
    }


    @Override
    public Stage getMyStage() {
        StageController stageController = UiContext.stageController;
        return stageController.getStageBy(R.id.ChatToPoint);
    }

    @FXML
    private void close() {
        UiContext.stageController.closeStage(R.id.ChatToPoint);
        Context.friendManager.resetActivatedFriendId();
    }

    @FXML
    private void createDiscussion() {
        StageController stageController = UiContext.stageController;
        stageController.setStage(R.id.CreateDiscussion);
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


