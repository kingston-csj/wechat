package pers.kinson.wechat.ui.controller;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextFlow;
import pers.kinson.wechat.base.Context;
import pers.kinson.wechat.base.UiContext;
import pers.kinson.wechat.logic.chat.MessageTextUiEditor;
import pers.kinson.wechat.logic.chat.struct.ContentElemNode;
import pers.kinson.wechat.logic.chat.struct.MessageContent;
import pers.kinson.wechat.logic.chat.ui.EmojiPopup;
import pers.kinson.wechat.ui.ControlledStage;
import pers.kinson.wechat.ui.R;
import pers.kinson.wechat.ui.StageController;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class ChatToPointController implements ControlledStage  {

    @FXML
    private Label userIdUi;

    @FXML
    private TextArea msgInput;


    @FXML
    private void sendMessage() throws IOException {
        final long userId = Long.parseLong(userIdUi.getText());
        String message = msgInput.getText();
        MessageContent content = new MessageContent();
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

}


