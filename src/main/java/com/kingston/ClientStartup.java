package com.kingston;

import java.io.IOException;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import com.kingston.base.ServerManager;
import com.kingston.net.transport.ChatClient;
import com.kingston.ui.R;
import com.kingston.ui.StageController;

public class ClientStartup extends Application {

	@Override
	public void init() throws Exception {

	}

	@Override
	public void start(final Stage stage) throws IOException {

		connectToServer();
		
		StageController stageController = ServerManager.INSTANCE.getStageController();
		stageController.setPrimaryStage("root", stage);

		Stage loginStage = stageController.loadStage(R.Id.LoginView, R.Layout.LoginView, StageStyle.UNDECORATED);
		loginStage.setTitle("QQ");
		
		Stage mainStage = stageController.loadStage(R.Id.MainView, R.Layout.MainView);

		//显示MainView舞台
		stageController.setStage(R.Id.LoginView);
	}

	private void connectToServer() {
		new Thread() {
			public void run() {
				new ChatClient().start();
			};
		}.start();
	}

	public static void main(String[] args) {
		launch();
	}
}
