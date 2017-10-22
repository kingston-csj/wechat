package com.kingston.ui.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.kingston.base.ClientBaseService;
import com.kingston.logic.login.LoginManager;
import com.kingston.ui.ControlledStage;
import com.kingston.ui.R;
import com.kingston.ui.StageController;
import com.kingston.ui.container.ResourceContainer;
import com.kingston.util.I18n;
import com.kingston.util.NumberUtil;

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
	private void login() throws IOException {
		final long useId = Long.parseLong(userId.getText());
		final String psw = password.getText();

		if (!ClientBaseService.INSTANCE.isConnectedSever()) {
			errorPane.setVisible(true);
			errorTips.setText(I18n.get("login.failToConnect"));
			return;
		}

		loginProgress.setVisible(true);
		login.setVisible(false);

		LoginManager.getInstance().beginToLogin(useId, psw);
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
		StageController stageController = ClientBaseService.INSTANCE.getStageController();
		stageController.switchStage(R.id.RegisterView, R.id.LoginView);
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		//验证规则：　userId非空且为数字　password非空
		login.disableProperty().bind(
			Bindings.createBooleanBinding(
				() -> userId.getText().length() == 0 ||
					  !NumberUtil.isInteger(userId.getText()) ||
					  password.getText().length() == 0,
				userId.textProperty(),
				password.textProperty()));
	}

	@Override
	public Stage getMyStage() {
		StageController stageController = ClientBaseService.INSTANCE.getStageController();
		return stageController.getStageBy(R.id.LoginView);
	}

}
