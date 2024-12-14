package pers.kinson.wechat.ui.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
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

import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.io.IOException;


@Slf4j
public class ChatToPointController implements ControlledStage {

    @FXML
    private Label userIdUi;

    @FXML
    private TextArea msgInput;

    @FXML
    private ScrollPane msgScrollPane;

    private long lastScrollTime;


    @Override
    public void onStageShown() {
        msgInput.requestFocus();

        // 添加监听器来检测是否滚动到顶部
        msgScrollPane.vvalueProperty().addListener((observable, oldValue, newValue) -> {
            // 判断是否接近顶部（考虑一定的误差范围）
            if (newValue.doubleValue() <= 0.001) {
                long now = System.currentTimeMillis();
                // 间隔太短，不触发
                if (now - lastScrollTime < 3000L) {
                    return;
                }
                lastScrollTime = now;
            }
        });

        msgInput.setOnKeyPressed(event -> {
            // 注册enter快捷键
            if (event.getCode() == KeyCode.ENTER) {
                sendMessage();
            }
            // 注册ctrl+v快捷键
            // 复制系统剪贴板图片资源
            if (event.isControlDown() && event.getCode() == KeyCode.V) {
                SchedulerManager.INSTANCE.runNow(this::onCopyClipboardResource);
            }
        });

        // 获得焦点，关闭小图标闪动
        msgInput.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                ApplicationEffect.stopBlink();
            }
        });
    }

    private void onCopyClipboardResource() {
        ReqChatToChannel reqChatToChannel = new ReqChatToChannel();
        reqChatToChannel.setChannel(Constants.CHANNEL_PERSON);
        reqChatToChannel.setTarget(NumberUtil.longValue(userIdUi.getText()));

        FileUiUtil.onCopyClipboardResource(msgInput, reqChatToChannel);
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


