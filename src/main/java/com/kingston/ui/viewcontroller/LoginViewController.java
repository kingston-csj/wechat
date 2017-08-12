package com.kingston.ui.viewcontroller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import com.kingston.base.ClientBaseService;
import com.kingston.logic.login.LoginManager;
import com.kingston.ui.ControlledStage;
import com.kingston.ui.R;
import com.kingston.ui.StageController;
import com.kingston.ui.container.ResourceContainer;
import com.kingston.util.NumberUtil;

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
	private ImageView close;
	@FXML
	private ImageView min;

	@FXML
	private Pane loginError;

	@FXML
	private void login() throws IOException {
		final long useId = Long.parseLong(userId.getText());
		final String psw = password.getText();

		LoginManager.getInstance().beginToLogin(useId, psw);

//		ObservableList<Node> list = ComponentContainer._LOGIN.getChildrenUnmodifiable();
//		for (Node node : list) {
//			node.setDisable(true);
//		}

		StageController controller = ClientBaseService.INSTANCE.getStageController();
		Stage loginStage = controller.getStageBy(R.id.LoginView);
//		ComponentContainer._LOGIN.getChildrenUnmodifiable().get(9).setDisable(false);
//		ComponentContainer._LOGIN.getChildrenUnmodifiable().get(10).setDisable(false);
//		ComponentContainer._LOGIN.getChildrenUnmodifiable().get(11).setDisable(false);
//		ComponentContainer._LOGIN.getChildrenUnmodifiable().get(12).setDisable(false);
//		ComponentContainer._LOGIN.getChildrenUnmodifiable().get(1).setVisible(false);
//		ComponentContainer._LOGIN.getChildrenUnmodifiable().get(11).setVisible(true);

	}

	@FXML
	private void close() {
		System.exit(1);
	}

	@FXML
	private void min() {
	}

	@FXML
	private void closeEntered() {
		Image image = ResourceContainer.getClose_1();
		close.setImage(image);
	}

	@FXML
	private void closeExited() {
		Image image = ResourceContainer.getClose();
		close.setImage(image);
	}

	@FXML
	private void minEntered() {
		Image image = ResourceContainer.getMin_1();
		min.setImage(image);
	}

	@FXML
	private void minExited() {
		Image image = ResourceContainer.getMin();
		min.setImage(image);
	}


	@FXML
	private void callBackLogin() {
//		ObservableList<Node> list = ComponentContainer._LOGIN.getChildrenUnmodifiable();
//		for (Node node : list) {
//			node.setDisable(false);
//		}
//		ComponentContainer._LOGIN.getChildrenUnmodifiable().get(1).setVisible(true);
//		ComponentContainer._LOGIN.getChildrenUnmodifiable().get(11).setVisible(false);
//		ComponentContainer._LOGIN.getChildrenUnmodifiable().get(13).setVisible(true);
//		ComponentContainer._LOGIN.getChildrenUnmodifiable().get(14).setVisible(true);
		loginError.setVisible(false);
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


}
