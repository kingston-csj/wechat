package com.kingston.chat.ui.controller;

import java.net.URL;
import java.util.ResourceBundle;

import com.kingston.chat.base.SessionManager;
import com.kingston.chat.base.UiBaseService;
import com.kingston.chat.logic.search.message.req.ReqSearchFriends;
import com.kingston.chat.ui.ControlledStage;
import com.kingston.chat.ui.R;
import com.kingston.chat.ui.StageController;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class SearchViewController implements ControlledStage, Initializable {

	@FXML
	private GridPane friendsContainer;

	@FXML
	private TextField friendKey;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub

	}

	@FXML
	public void onSearchBtnClicked() {
		String key = friendKey.getText();
		ReqSearchFriends req = new ReqSearchFriends();
		req.setKey(key);
		SessionManager.INSTANCE.sendMessage(req);
	}


	@FXML
	private void close() {
		StageController stageController = UiBaseService.INSTANCE.getStageController();
		stageController.closeStage(R.id.SearchView);
	}

	@Override
	public Stage getMyStage() {
		StageController stageController = UiBaseService.INSTANCE.getStageController();
		return stageController.getStageBy(R.id.SearchView);
	}

}
