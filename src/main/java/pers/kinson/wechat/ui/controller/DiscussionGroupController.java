package pers.kinson.wechat.ui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
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
import pers.kinson.wechat.ui.ControlledStage;
import pers.kinson.wechat.ui.R;
import pers.kinson.wechat.ui.StageController;
import pers.kinson.wechat.util.SchedulerManager;

import java.io.IOException;

public class DiscussionGroupController implements ControlledStage {

    @FXML
    private Label name;

    @FXML
    private TextArea msgInput;

    @FXML
    private TilePane members;

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
    }

    private void sendClipboardImage() {
        ReqChatToChannel reqChatToChannel = new ReqChatToChannel();
        reqChatToChannel.setChannel(Constants.CHANNEL_DISCUSSION);
        reqChatToChannel.setTarget(Context.discussionManager.getSelectedGroupId());

        FileUiUtil.sendClipboardResource(msgInput, reqChatToChannel);
    }

    @Override
    public Stage getMyStage() {
        StageController stageController = UiContext.stageController;
        return stageController.getStageBy(R.id.DiscussionGroup);
    }

    @FXML
    private void close() {
        UiContext.stageController.closeStage(R.id.DiscussionGroup);
    }

    @FXML
    private void createDiscussion() {
        StageController stageController = UiContext.stageController;
        stageController.setStage(R.id.CreateDiscussion);
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


