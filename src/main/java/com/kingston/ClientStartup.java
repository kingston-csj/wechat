package com.kingston;

import java.io.IOException;

import com.kingston.base.ClientBaseService;
import com.kingston.net.transport.ChatClient;
import com.kingston.ui.R;
import com.kingston.ui.StageController;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ClientStartup extends Application {

	@Override
	public void init() throws Exception {

	}

	@Override
	public void start(final Stage stage) throws IOException {

		connectToServer();

		StageController stageController = ClientBaseService.INSTANCE.getStageController();
		stageController.setPrimaryStage("root", stage);

		Stage loginStage = stageController.loadStage(R.id.LoginView, R.layout.LoginView, StageStyle.UNDECORATED);
		loginStage.setTitle("QQ");

		stageController.loadStage(R.id.RegisterView, R.layout.RegisterView);
		stageController.loadStage(R.id.MainView, R.layout.MainView);

		//显示MainView舞台
		stageController.setStage(R.id.LoginView);
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
