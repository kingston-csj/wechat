package com.kingston.ui.controller;

import com.kingston.base.ClientBaseService;
import com.kingston.ui.ControlledStage;
import com.kingston.ui.R;
import com.kingston.ui.StageController;

import javafx.fxml.FXML;
import javafx.stage.Stage;

public class ChatToPointController implements ControlledStage {

	@Override
	public Stage getMyStage() {
		StageController stageController = ClientBaseService.INSTANCE.getStageController();
		return stageController.getStageBy(R.id.ChatToPoint);
	}

	@FXML
	private void close() {
		ClientBaseService.INSTANCE.getStageController().closeStge(R.id.ChatToPoint);
	}



}


