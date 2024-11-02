package pers.kinson.wechat.ui.controller;

import java.io.IOException;

import pers.kinson.wechat.base.Context;
import pers.kinson.wechat.base.UiContext;
import pers.kinson.wechat.ui.ControlledStage;
import pers.kinson.wechat.ui.R;
import pers.kinson.wechat.ui.StageController;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class ChatToPointController implements ControlledStage {

	@FXML
	private Label userIdUi;

	@FXML
	private TextArea msgInput;

	@FXML
	private ScrollPane outputMsgUi;

	@FXML
	private void sendMessage() throws IOException {
		final long userId = Long.parseLong(userIdUi.getText());
		String message = msgInput.getText();
		Context.chatManager.sendMessageTo(userId, message);
		msgInput.setText("");
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


}


