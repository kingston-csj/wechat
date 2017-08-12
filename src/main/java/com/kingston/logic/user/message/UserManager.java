package com.kingston.logic.user.message;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import com.kingston.base.ClientBaseService;
import com.kingston.logic.GlobalConst;
import com.kingston.ui.R;
import com.kingston.ui.StageController;

public class UserManager {
	
	private static UserManager instance = new UserManager();
	
	public static UserManager getInstance() {
		return instance;
	}
	
	public void registerAccount(byte sex, String nickName, String password) {
		ReqUserRegisterPacket request = new ReqUserRegisterPacket();
		request.setNickName(nickName);
		request.setPassword(password);
		request.setSex(sex);
		
		System.err.println("向服务端发送注册请求");  
		ClientBaseService.INSTANCE.sendServerRequest(request);
	}
	
	public void handleRegisterResponse(byte resultCode, String message) {
		boolean isSucc = resultCode == GlobalConst.SUCC;
		StageController stageController = ClientBaseService.INSTANCE.getStageController();
		Stage stage = stageController.getStageBy(R.id.RegisterView);
		Label errorTips = (Label)stage.getScene().getRoot().lookup("#errorText");
		if (isSucc) {
			ClientBaseService.INSTANCE.runTaskInFxThread(() -> {
				errorTips.setVisible(true);
				errorTips.setText(R.string.REGISTER_SUCC);
				long userId = Long.parseLong(message);
				gotoLoginPanel(userId);
			});
		}else {
			ClientBaseService.INSTANCE.runTaskInFxThread(() -> {
				errorTips.setVisible(true);
				errorTips.setText(R.string.REGISTER_FAILED);
			});
		}
	}
	
	private void gotoLoginPanel(long userId) {
		StageController stageController = ClientBaseService.INSTANCE.getStageController();
		stageController.switchStage(R.id.LoginView, R.id.RegisterView);
		Stage stage = stageController.getStageBy(R.id.LoginView);
		TextField userIdField = (TextField)stage.getScene().getRoot().lookup("#"+R.id.UserId);
		userIdField.setText(String.valueOf(userId));
	}

}
