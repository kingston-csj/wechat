package pers.kinson.wechat.logic.user;

import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import pers.kinson.wechat.logic.constant.Constants;
import pers.kinson.wechat.base.Context;
import pers.kinson.wechat.base.LifeCycle;
import pers.kinson.wechat.base.UiContext;
import pers.kinson.wechat.logic.chat.message.req.ReqFetchNewMessage;
import pers.kinson.wechat.logic.user.message.req.ReqUserRegister;
import pers.kinson.wechat.logic.user.message.res.ResUserInfo;
import pers.kinson.wechat.logic.user.message.res.ResUserRegister;
import pers.kinson.wechat.logic.user.model.UserModel;
import pers.kinson.wechat.logic.user.util.PasswordUtil;
import pers.kinson.wechat.net.CmdConst;
import pers.kinson.wechat.net.HttpResult;
import pers.kinson.wechat.net.IOUtil;
import pers.kinson.wechat.net.SimpleRequestCallback;
import pers.kinson.wechat.ui.R;
import pers.kinson.wechat.ui.StageController;
import pers.kinson.wechat.util.I18n;

public class UserManager implements LifeCycle {


    private UserModel profile = new UserModel();

    @Override
    public void init() {
        Context.messageRouter.registerHandler(CmdConst.ResUserInfo, this::updateMyProfile);
        Context.messageRouter.registerHandler(CmdConst.ResCommon, this::commonAlert);
    }

    public void updateMyProfile(Object packet) {
        ResUserInfo userInfo = (ResUserInfo) packet;
        profile.setSex(userInfo.getSex());
        profile.setSignature(userInfo.getSignature());
        profile.setUserId(userInfo.getUserId());
        profile.setUserName(userInfo.getUserName());
        profile.setAvatar(userInfo.getAvatar());
        profile.setChatMaxSeq(userInfo.getMaxChatSeq());

        // 拉取私聊列表
        ReqFetchNewMessage reqFetchNewMessage = new ReqFetchNewMessage();
        reqFetchNewMessage.setMaxSeq(Context.userManager.getMyProfile().getChatMaxSeq());
        IOUtil.send(reqFetchNewMessage);
    }

    public UserModel getMyProfile() {
        return this.profile;
    }

    public long getMyUserId() {
        return this.profile.getUserId();
    }

    public void registerAccount(long userId, byte sex, String nickName, String password) {
        ReqUserRegister request = new ReqUserRegister();
        request.setUserId(userId);
        request.setNickName(nickName);
        // 明文密码加密
        String secretPsw = PasswordUtil.passwordEncryption(userId, password);
        request.setPassword(secretPsw);
        request.setSex(sex);

        System.err.println("向服务端发送注册请求");
        IOUtil.callback(request, new SimpleRequestCallback<ResUserRegister>() {
            @Override
            public void onSuccess(ResUserRegister callBack) {
                handleRegisterResponse(callBack);
            }
        });
    }

    public void handleRegisterResponse(ResUserRegister data) {
        byte resultCode = data.getResultCode();
        String message = data.getMessage();
        boolean isSucc = resultCode == Constants.TRUE;
        StageController stageController = UiContext.stageController;
        Stage stage = stageController.getStageBy(R.id.RegisterView);
        Label errorTips = (Label) stage.getScene().getRoot().lookup("#errorTips");
        if (isSucc) {
            UiContext.runTaskInFxThread(() -> {
                errorTips.setVisible(true);
                errorTips.setText(I18n.get("register.operateSucc"));
                long userId = Long.parseLong(message);
                gotoLoginPanel(userId);
            });
        } else {
            UiContext.runTaskInFxThread(() -> {
                errorTips.setVisible(true);
                errorTips.setText(I18n.get("register.nickUsed"));
            });
        }
    }

    private void gotoLoginPanel(long userId) {
        StageController stageController = UiContext.stageController;
        stageController.switchStage(R.id.LoginView, R.id.RegisterView);
        Stage stage = stageController.getStageBy(R.id.LoginView);
        TextField userIdField = (TextField) stage.getScene().getRoot().lookup("#userId");
        userIdField.setText(String.valueOf(userId));
    }

    private void commonAlert(Object packet) {
        HttpResult systemAlert = (HttpResult) packet;
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("系统提示");
        alert.setContentText("错误码:" + systemAlert.getCode());
        alert.showAndWait();
    }

}
