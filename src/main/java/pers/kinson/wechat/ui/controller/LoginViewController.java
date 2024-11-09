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
import pers.kinson.wechat.base.Context;
import pers.kinson.wechat.base.UiContext;
import pers.kinson.wechat.ui.ControlledStage;
import pers.kinson.wechat.ui.R;
import pers.kinson.wechat.ui.StageController;
import pers.kinson.wechat.ui.container.ResourceContainer;
import pers.kinson.wechat.util.NumberUtil;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

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
		stageController.switchStage(R.id.RegisterView, R.id.LoginView);
	}

	@Override
	public Stage getMyStage() {
        StageController stageController = UiContext.stageController;
		return stageController.getStageBy(R.id.LoginView);
	}

}
