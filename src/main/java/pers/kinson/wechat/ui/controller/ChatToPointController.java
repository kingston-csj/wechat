package pers.kinson.wechat.ui.controller;

import java.io.IOException;

import pers.kinson.wechat.base.UiBaseService;
import pers.kinson.wechat.logic.chat.ChatManager;
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


