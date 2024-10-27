package pers.kinson.wechat.logic.login;

import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import pers.kinson.wechat.base.Constants;
import pers.kinson.wechat.base.Context;
import pers.kinson.wechat.base.LifeCycle;
import pers.kinson.wechat.base.UiBaseService;
import pers.kinson.wechat.logic.login.message.req.ReqHeartBeat;
import pers.kinson.wechat.logic.login.message.req.ReqUserLogin;
import pers.kinson.wechat.logic.login.message.res.ResUserLogin;
import pers.kinson.wechat.logic.user.util.PasswordUtil;
import pers.kinson.wechat.net.CmdConst;
import pers.kinson.wechat.net.IOUtil;
import pers.kinson.wechat.ui.R;
import pers.kinson.wechat.ui.StageController;
import pers.kinson.wechat.util.I18n;
import pers.kinson.wechat.util.SchedulerManager;

public class LoginManager implements LifeCycle {

    @Override
    public void init() {
        Context.messageRouter.registerHandler(CmdConst.ResUserLogin, this::handleLoginResponse);
    }

    /**
     * @param userId
     * @param password 密码明文
     */
    public void beginToLogin(long userId, String password) {
        ReqUserLogin reqLogin = new ReqUserLogin();
        reqLogin.setUserId(userId);
        reqLogin.setUserPwd(PasswordUtil.passwordEncryption(userId, password));
        System.err.println("向服务端发送登录请求");
        IOUtil.send(reqLogin);
    }

    public void handleLoginResponse(Object packet) {
        ResUserLogin resp = (ResUserLogin) packet;
        boolean isSucc = resp.getIsValid() == Constants.TRUE;
        if (isSucc) {
            UiBaseService.INSTANCE.runTaskInFxThread(() -> {
                redirecToMainPanel();
            });

            registerHeartTimer();
        } else {
            UiBaseService.INSTANCE.runTaskInFxThread(() -> {
                StageController stageController = UiBaseService.INSTANCE.getStageController();
                Stage stage = stageController.getStageBy(R.id.LoginView);
                Pane errPane = (Pane) stage.getScene().getRoot().lookup("#errorPane");
                errPane.setVisible(true);
                Label errTips = (Label) stage.getScene().getRoot().lookup("#errorTips");
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
        SchedulerManager.INSTANCE.scheduleAtFixedRate("HEART_BEAT", () -> {
            IOUtil.send(new ReqHeartBeat());
        }, 0, 5 * 1000);
    }

}
