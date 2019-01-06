package com.kingston.chat;

import java.io.IOException;

import com.kingston.chat.base.UiBaseService;
import com.kingston.chat.net.message.PacketType;
import com.kingston.chat.net.transport.SocketClient;
import com.kingston.chat.ui.R;
import com.kingston.chat.ui.StageController;

import javafx.application.Application;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ClientStartup extends Application {

	@Override
	public void init() throws Exception {
		PacketType.initPackets();
	}

	@Override
	public void start(final Stage stage) throws IOException {
		//与服务端建立连接
		connectToServer();
		StageController stageController = UiBaseService.INSTANCE.getStageController();
		stageController.setPrimaryStage("root", stage);

		Stage loginStage = stageController.loadStage(R.id.LoginView, R.layout.LoginView,
								StageStyle.UNDECORATED);
		loginStage.setTitle("QQ");

		stageController.loadStage(R.id.RegisterView, R.layout.RegisterView, StageStyle.UNDECORATED);
		Stage mainStage = stageController.loadStage(R.id.MainView, R.layout.MainView, StageStyle.UNDECORATED);

		//把主界面放在右上方
		Screen screen = Screen.getPrimary();
		double rightTopX = screen.getVisualBounds().getWidth()*0.75;
		double rightTopY = screen.getVisualBounds().getHeight()*0.05;
		mainStage.setX(rightTopX);
		mainStage.setY(rightTopY);

		stageController.loadStage(R.id.ChatToPoint, R.layout.ChatToPoint, StageStyle.UTILITY);

		Stage searchStage = stageController.loadStage(R.id.SearchView, R.layout.SeachFriendView,
				StageStyle.UTILITY);

		//显示MainView舞台
		stageController.setStage(R.id.LoginView);
//		stageController.setStage(R.id.SearchView);
	}

	private void connectToServer() {
		new Thread() {
			public void run() {
				new SocketClient().start();
			};
		}.start();
	}

	public static void main(String[] args) {
		launch();
	}
}
