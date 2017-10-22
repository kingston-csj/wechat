package com.kingston;

import java.io.IOException;

import com.kingston.base.ClientBaseService;
import com.kingston.net.message.PacketType;
import com.kingston.net.transport.SocketClient;
import com.kingston.ui.R;
import com.kingston.ui.StageController;

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
		StageController stageController = ClientBaseService.INSTANCE.getStageController();
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

		stageController.loadStage(R.id.ChatToPoint, R.layout.ChatToPoint, StageStyle.UNDECORATED);

		//显示MainView舞台
		stageController.setStage(R.id.LoginView);
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
