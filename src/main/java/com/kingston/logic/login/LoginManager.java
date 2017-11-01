package com.kingston.logic.login;

import com.kingston.base.Constants;
import com.kingston.base.IoBaseService;
import com.kingston.base.UiBaseService;
import com.kingston.logic.login.message.ReqHeartBeatPacket;
import com.kingston.logic.login.message.ReqUserLoginPacket;
import com.kingston.logic.login.message.ResUserLoginPacket;
import com.kingston.ui.R;
import com.kingston.ui.StageController;
import com.kingston.util.I18n;
import com.kingston.util.SchedulerManager;

import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class LoginManager {

	private static LoginManager instance = new  LoginManager();

	private LoginManager() {}

	public static LoginManager getInstance() {
		return instance;
	}

	public void beginToLogin(long userId, String password) {
		ReqUserLoginPacket reqLogin= new ReqUserLoginPacket();
		reqLogin.setUserId(userId);
		reqLogin.setUserPwd(password);
		System.err.println("向服务端发送登录请求");
		IoBaseService.INSTANCE.sendServerRequest(reqLogin);
	}

	public void handleLoginResponse(ResUserLoginPacket resp) {
		boolean isSucc = resp.getIsValid() == Constants.TRUE;
		if (isSucc) {
			UiBaseService.INSTANCE.runTaskInFxThread(() -> {
				redirecToMainPanel();
			});

			registerHeartTimer();
		}else {
			UiBaseService.INSTANCE.runTaskInFxThread(() -> {
				StageController stageController = UiBaseService.INSTANCE.getStageController();
				Stage stage = stageController.getStageBy(R.id.LoginView);
				Pane errPane = (Pane)stage.getScene().getRoot().lookup("#errorPane");
				errPane.setVisible(true);
				Label errTips = (Label)stage.getScene().getRoot().lookup("#errorTips");
				errTips.setText(I18n.get("login.operateFailed"));
			});
		}
	}

	private void redirecToMainPanel() {
		StageController stageController = UiBaseService.INSTANCE.getStageController();
		stageController.switchStage(R.id.MainView, R.id.LoginView);
	}

	/**
	 * 注册心跳事件
	 */
	private void registerHeartTimer() {
		SchedulerManager.INSTANCE.scheduleAtFixedRate("HEART_BEAT", ()->{
			IoBaseService.INSTANCE.sendServerRequest(new ReqHeartBeatPacket());
		}, 0, 5*1000);
	}

}
