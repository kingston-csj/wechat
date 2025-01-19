package pers.kinson.wechat.ui.controller;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import pers.kinson.wechat.SystemConfig;
import pers.kinson.wechat.base.Context;
import pers.kinson.wechat.base.UiContext;
import pers.kinson.wechat.net.HttpResult;
import pers.kinson.wechat.ui.ControlledStage;
import pers.kinson.wechat.ui.R;
import pers.kinson.wechat.ui.StageController;
import pers.kinson.wechat.ui.container.ResourceContainer;
import pers.kinson.wechat.util.NumberUtil;
import pers.kinson.wechat.util.SchedulerManager;
import pers.kinson.wechat.util.XmlUtils;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

@Slf4j
public class LoginViewController implements ControlledStage, Initializable {

    @FXML
    private Button login;
    @FXML
    private TextField userId;
    @FXML
    private PasswordField password;
    @FXML
    private CheckBox rememberPsw;
    @FXML
    private CheckBox autoLogin;
    @FXML
    private ImageView closeBtn;
    @FXML
    private ImageView minBtn;
    @FXML
    private ProgressBar loginProgress;
    @FXML
    private Pane errorPane;
    @FXML
    private Label errorTips;

    @FXML
    private Label notice;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //验证规则：　userId非空且为数字　password非空
        login.disableProperty().bind(
                Bindings.createBooleanBinding(
                        () -> userId.getText().isEmpty() ||
                                !NumberUtil.isInteger(userId.getText()) ||
                                password.getText().isEmpty(),
                        userId.textProperty(),
                        password.textProperty()));
    }

    @Override
    public void onStageShown() {
        login.setVisible(false);
        notice.setText("版本检测中");
        try {
            HttpResult httpResult = Context.httpClientManager.get(SystemConfig.getInstance().getServer().getRemoteHttpUrl() + "/system/version", new HashMap<>(), HttpResult.class);
            String serverVersion = httpResult.getData();
            String clientVersion = SystemConfig.getInstance().getClient().getVersion();
            if (serverVersion.equals(clientVersion)) {
                notice.setVisible(false);
                login.setVisible(true);
            } else {
                downloadNewClient(serverVersion);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void downloadNewClient(String version) {
        notice.setText("客户端下载中，请稍候");
        // 异步下载
        SchedulerManager.INSTANCE.runNow(() -> {
            try {
                HttpResult httpResult = Context.httpClientManager.get(SystemConfig.getInstance().getServer().getRemoteHttpUrl() + "/system/clientApp", new HashMap<>(), HttpResult.class);
                String downloadUrl = httpResult.getData();
                ProgressMonitor monitor = new ProgressMonitor() {
                    @Override
                    public void updateTransferred(long changed) {
                        super.updateTransferred(changed);
                        UiContext.runTaskInFxThread(() -> {
                            double rate = (double) getProgress() / getMaximum();
                            String formattedValue = String.format("%.2f%%", rate * 100);
                            notice.setText("客户端下载进度:" + formattedValue);
                        });
                    }
                };
                Context.httpClientManager.downloadFile(downloadUrl, "wechat.jar", monitor);
                UiContext.runTaskInFxThread(() -> {
                    notice.setText("客户端下载成功，请关闭重启");
                });
                SystemConfig.getInstance().getClient().setVersion(version);
                XmlUtils.saveToFile("system.xml", SystemConfig.getInstance());
            } catch (Exception e) {
                log.error("", e);
            }
        });
    }

    @FXML
    private void login() throws IOException {
        final long useId = Long.parseLong(userId.getText());
        final String psw = password.getText();

//		if (!SessionManager.INSTANCE.isConnectedSever()) {
//			errorPane.setVisible(true);
//			errorTips.setText(I18n.get("login.failToConnect"));
//			return;
//		}

        loginProgress.setVisible(true);
        login.setVisible(false);

        Context.loginManager.beginToLogin(useId, psw);
    }

    @FXML
    private void close() {
        System.exit(1);
    }

    @FXML
    private void min() {
        Stage stage = getMyStage();
        if (stage != null) {
            stage.setIconified(true);
        }
    }

    @FXML
    private void closeEntered() {
        Image image = ResourceContainer.getClose_1();
        closeBtn.setImage(image);
    }

    @FXML
    private void closeExited() {
        Image image = ResourceContainer.getClose();
        closeBtn.setImage(image);
    }

    @FXML
    private void minEntered() {
        Image image = ResourceContainer.getMin_1();
        minBtn.setImage(image);
    }

    @FXML
    private void minExited() {
        Image image = ResourceContainer.getMin();
        minBtn.setImage(image);
    }

    @FXML
    private void backToLogin() {
        loginProgress.setVisible(false);
        errorPane.setVisible(false);
        login.setVisible(true);
    }

    @FXML
    private void login_en() {
        login.setStyle("-fx-background-radius:4;-fx-background-color: #097299");
    }

    @FXML
    private void login_ex() {
        login.setStyle("-fx-background-radius:4;-fx-background-color: #09A3DC");
    }

    @FXML
    private void gotoRegister() {
        StageController stageController = UiContext.stageController;
        stageController.switchStage(R.Id.RegisterView, R.Id.LoginView);
    }

    @Override
    public Stage getMyStage() {
        StageController stageController = UiContext.stageController;
        return stageController.getStageBy(R.Id.LoginView);
    }

}
