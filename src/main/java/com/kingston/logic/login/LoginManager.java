package com.kingston.logic.login;

import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import com.kingston.base.ServerManager;
import com.kingston.logic.GlobalConst;
import com.kingston.logic.login.message.ReqUserLoginPacket;
import com.kingston.logic.login.message.RespUserLoginPacket;
import com.kingston.ui.R;
import com.kingston.ui.StageController;

public class LoginManager {

	private static LoginManager instance = new  LoginManager();

	private LoginManager() {}

	public static LoginManager getInstance(){
		return instance;
	}

	public void beginToLogin(long userId, String password) {
		ReqUserLoginPacket reqLogin= new ReqUserLoginPacket();  
		reqLogin.setUserId(userId);
		reqLogin.setUserName("Netty爱好者");  
		reqLogin.setUserPwd(password);  
		System.err.println("向服务端发送登录请求");  
		ServerManager.INSTANCE.sendServerRequest(reqLogin);
	}

	public void handleLoginResponse(RespUserLoginPacket resp){
		boolean isSucc = resp.getIsValid() == GlobalConst.SUCC;
		if (isSucc) {
			ServerManager.INSTANCE.FXApplicationThreadExcute(() -> {
				enterMainPanel(resp.getAlertMsg());
			});
		}else {
			ServerManager.INSTANCE.FXApplicationThreadExcute(() -> {
				StageController stageController = ServerManager.INSTANCE.getStageController();
				Stage stage = stageController.getStageBy(R.Id.LoginView);
				Pane errPane = (Pane)stage.getScene().getRoot().lookup("#loginError");
				errPane.setVisible(true);
			});
		}
	}

	private void enterMainPanel(String nickName) {
		StageController stageController = ServerManager.INSTANCE.getStageController();
		stageController.switchStage(R.Id.MainView, R.Id.LoginView);
//		Label _username = (Label) ComponentContainer._MAIN_PARENT.getChildrenUnmodifiable().get(6);
//		_username.setText(nickName);
	}
	
}
