package pers.kinson.wechat.logic.login;

import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import lombok.SneakyThrows;
import pers.kinson.wechat.base.Context;
import pers.kinson.wechat.base.LifeCycle;
import pers.kinson.wechat.base.UiContext;
import pers.kinson.wechat.logic.login.message.req.ReqConnectSocket;
import pers.kinson.wechat.logic.login.message.req.ReqLoginPlatform;
import pers.kinson.wechat.logic.login.message.res.ResConnectServer;
import pers.kinson.wechat.logic.system.EmojiCache;
import pers.kinson.wechat.logic.user.util.PasswordUtil;
import pers.kinson.wechat.net.CmdConst;
import pers.kinson.wechat.net.IOUtil;
import pers.kinson.wechat.ui.R;
import pers.kinson.wechat.ui.StageController;
import pers.kinson.wechat.util.I18n;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LoginManager implements LifeCycle {

    @Override
    public void init() {
        Context.messageRouter.registerHandler(CmdConst.ResSocketLogin, this::handleLoginResponse);
    }

    /**
     * @param userId
     * @param password 密码明文
     */
    public void beginToLogin(long userId, String password) {
        ReqLoginPlatform reqLogin = new ReqLoginPlatform();
        reqLogin.setUsername(String.valueOf(userId));
        reqLogin.setPassword(PasswordUtil.passwordEncryption(userId, password));
        System.err.println("向服务端发送登录请求");
        try {
            Map<String, String> httpResult = Context.httpClientManager.get("/oauth/token", reqLogin, HashMap.class);
            if (httpResult.containsKey("access_token")) {
                //与socket服务端建立连接
                try {
                    Context.userManager.getMyProfile().setRequestToken(httpResult.get("access_token"));
                    // 数据初始化
                    Context.settingManager.init();
                    EmojiCache.loadEmoji();

                    IOUtil.init();
                    ReqConnectSocket reqConnectSocket = new  ReqConnectSocket();
                    reqConnectSocket.setUserId(userId);
                    IOUtil.send(reqConnectSocket);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else {
                StageController stageController = UiContext.stageController;
                Stage stage = stageController.getStageBy(R.Id.LoginView);
                Pane errPane = (Pane) stage.getScene().getRoot().lookup("#errorPane");
                errPane.setVisible(true);
                Label errTips = (Label) stage.getScene().getRoot().lookup("#errorTips");
                errTips.setText(I18n.get("login.operateFailed"));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void connectToServer() {
        new Thread() {
            @SneakyThrows
            public void run() {
                IOUtil.init();
            }

        }.start();
    }

    private void redirectToMainPanel() {
        StageController stageController = UiContext.stageController;
        stageController.switchStage(R.Id.MainView, R.Id.LoginView);
    }

    private void handleLoginResponse(Object packet) {
        ResConnectServer response = (ResConnectServer) packet;
        if (response.getStatus() == 0) {
            redirectToMainPanel();
        }
    }

}
