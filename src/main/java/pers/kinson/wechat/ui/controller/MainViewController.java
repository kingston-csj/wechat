package pers.kinson.wechat.ui.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import pers.kinson.wechat.base.UiBaseService;
import pers.kinson.wechat.logic.search.SearchManager;
import pers.kinson.wechat.logic.user.UserManager;
import pers.kinson.wechat.logic.user.model.UserModel;
import pers.kinson.wechat.ui.ControlledStage;
import pers.kinson.wechat.ui.R;
import pers.kinson.wechat.ui.StageController;
import pers.kinson.wechat.ui.container.ResourceContainer;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Accordion;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class MainViewController implements ControlledStage, Initializable{

	@FXML
	private ImageView close;
	@FXML
	private ImageView min;
	@FXML
	private ImageView shineImage;
	@FXML
	private Accordion friends;
	@FXML
	private ScrollPane friendSp;
	@FXML
	private Label username;
	@FXML
	private Label signature;

	private UserModel userModel = UserManager.getInstance().getMyProfile();

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		username.textProperty().bind(userModel.userNameProperty());
		signature.textProperty().bind(userModel.signaturePropertiy());
	}

	@FXML
	private void close() {
		System.exit(1);
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
	private void bind() {
		friendSp.setFitToWidth(false);
		friends.expandedPaneProperty().addListener(new ChangeListener<TitledPane>() {
			@Override
			public void changed(ObservableValue<? extends TitledPane> arg0, TitledPane arg1, TitledPane arg2) {
				if (arg2 != null) {
					System.out.println("-------11111111--------");
				}
				if (arg1 != null) {
					System.out.println("-------2222222222---------");
				}
			}
		});
	}

	@FXML
	private void min() {
		getMyStage().setIconified(true);
	}

	@FXML
	private void username_entered() {
		username.setStyle("-fx-background-radius:4;-fx-background-color: #136f9b");
	}

	@FXML
	private void username_exited() {
		username.setStyle("");
	}

	@FXML
	private void autograph_entered() {
		signature.setStyle("-fx-background-radius:4;-fx-background-color: #136f9b");
	}

	@FXML
	private void autograph_exited() {
		signature.setStyle("");
	}

	@FXML
	private void headEx() {
		shineImage.setVisible(false);
	}

	@FXML
	private void headEn() {
		shineImage.setVisible(true);
	}

	@FXML
	private void queryEvent() {
		SearchManager.getInstance().refreshRecommendFriends(new ArrayList<>());
	}

	@Override
	public Stage getMyStage() {
		StageController stageController = UiBaseService.INSTANCE.getStageController();
		return stageController.getStageBy(R.id.MainView);
	}

	public void refreshProfileInfo(String name) {
		userModel.setUserName(name);
	}

}
