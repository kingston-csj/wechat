package pers.kinson.wechat.ui.controller;

import java.net.URL;
import java.util.ResourceBundle;

import pers.kinson.wechat.base.Context;
import pers.kinson.wechat.base.UiContext;
import pers.kinson.wechat.logic.friend.message.req.ReqApplyFriendList;
import pers.kinson.wechat.logic.search.SearchManager;
import pers.kinson.wechat.logic.search.message.res.ResSearchFriends;
import pers.kinson.wechat.logic.user.model.UserModel;
import pers.kinson.wechat.net.IOUtil;
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
	private ImageView headImg;
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

	private UserModel userModel = Context.userManager.getMyProfile();

    @Override
	public void initialize(URL location, ResourceBundle resources) {
		username.textProperty().bind(userModel.userNameProperty());
		signature.textProperty().bind(userModel.signatureProperty());
	}

	@FXML
	private void close() {
		System.exit(1);
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
	private void onApplyTabSelected() {
		// 查询好友申请列表
		IOUtil.send(new ReqApplyFriendList());
	}

	@FXML
	private void onClickSetting() {
		StageController stageController = UiContext.stageController;
		stageController.setStage(R.id.PersonSettingView);
	}

	@FXML
	private void queryEvent() {
		SearchManager.getInstance().refreshRecommendFriends(new ResSearchFriends());
	}

	@Override
	public Stage getMyStage() {
		StageController stageController = UiContext.stageController;
		return stageController.getStageBy(R.id.MainView);
	}

}
