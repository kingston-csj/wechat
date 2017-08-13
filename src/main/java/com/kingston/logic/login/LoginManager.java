package com.kingston.logic.login;

import com.kingston.base.ClientBaseService;
import com.kingston.logic.GlobalConst;
import com.kingston.logic.login.message.ReqUserLoginPacket;
import com.kingston.logic.login.message.RespUserLoginPacket;
import com.kingston.ui.R;
import com.kingston.ui.StageController;

import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class LoginManager {

	private static LoginManager instance = new  LoginManager();

	private LoginManager() {}

	public static LoginManager getInstance(){
		return instance;
	}

	public void beginToLogin(long userId, String password) {
		ReqUserLoginPacket reqLogin= new ReqUserLoginPacket();
		reqLogin.setUserId(userId);
		reqLogin.setUserPwd(password);
		System.err.println("向服务端发送登录请求");
		ClientBaseService.INSTANCE.sendServerRequest(reqLogin);
	}

	public void handleLoginResponse(RespUserLoginPacket resp){
		boolean isSucc = resp.getIsValid() == GlobalConst.SUCC;
		if (isSucc) {
			ClientBaseService.INSTANCE.runTaskInFxThread(() -> {
				enterMainPanel(resp.getAlertMsg());
			});
		}else {
			ClientBaseService.INSTANCE.runTaskInFxThread(() -> {
				StageController stageController = ClientBaseService.INSTANCE.getStageController();
				Stage stage = stageController.getStageBy(R.id.LoginView);
				Pane errPane = (Pane)stage.getScene().getRoot().lookup("#loginError");
				errPane.setVisible(true);
				Label errTips = (Label)stage.getScene().getRoot().lookup("#errorTips");
				errTips.setText(R.string.FAIL_TO_CONNECT_SERVER);
			});
		}
	}

	private void enterMainPanel(String nickName) {
		StageController stageController = ClientBaseService.INSTANCE.getStageController();
		stageController.switchStage(R.id.MainView, R.id.LoginView);
//		Label _username = (Label) ComponentContainer._MAIN_PARENT.getChildrenUnmodifiable().get(6);
//		_username.setText(nickName);
	}

}
