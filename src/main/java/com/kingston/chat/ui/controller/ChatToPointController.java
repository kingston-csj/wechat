package com.kingston.chat.ui.controller;

import java.io.IOException;

import com.kingston.chat.base.UiBaseService;
import com.kingston.chat.logic.chat.ChatManager;
import com.kingston.chat.ui.ControlledStage;
import com.kingston.chat.ui.R;
import com.kingston.chat.ui.StageController;

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

		System.out.println("----send message---" + message);

		ChatManager.getInstance().sendMessageTo(userId, message);
	}


	@Override
	public Stage getMyStage() {
		StageController stageController = UiBaseService.INSTANCE.getStageController();
		return stageController.getStageBy(R.id.ChatToPoint);
	}

	@FXML
	private void close() {
		UiBaseService.INSTANCE.getStageController().closeStage(R.id.ChatToPoint);
	}


}


