package com.kingston.ui.viewcontroller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Accordion;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import com.kingston.ui.ControlledStage;
import com.kingston.ui.container.ResourceContainer;

public class MainViewController implements ControlledStage {
	
	public static Stage stage;
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
	private Label autograph;

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
					System.out.println("---------------");
				}
				if (arg1 != null) {
					System.out.println("----------------");
				}
			}
		});
	}

	@FXML
	private void min() {
		stage.setIconified(true);
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
		autograph.setStyle("-fx-background-radius:4;-fx-background-color: #136f9b");
	}

	@FXML
	private void autograph_exited() {
		autograph.setStyle("");
	}

	@FXML
	private void headEx() {
		shineImage.setVisible(false);
	}

	@FXML
	private void headEn() {
		shineImage.setVisible(true);
	}

//	@Override
//	public void setController(StageController controller) {
//		this.controller = controller;
//	}
}
