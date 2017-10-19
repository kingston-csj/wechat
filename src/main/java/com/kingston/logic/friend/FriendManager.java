package com.kingston.logic.friend;

import java.util.List;

import com.kingston.base.ClientBaseService;
import com.kingston.logic.friend.vo.FriendItemVo;
import com.kingston.ui.R;
import com.kingston.ui.StageController;

import javafx.scene.Node;
import javafx.scene.control.Accordion;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class FriendManager {

	private static FriendManager instance = new FriendManager();

	public static FriendManager getInstance() {
		return instance;
	}

	public void showMyFriendsView(List<FriendItemVo> friendItems) {
		StageController stageController = ClientBaseService.INSTANCE.getStageController();
		Stage stage = stageController.getStageBy(R.id.MainView);
		ScrollPane scrollPane = (ScrollPane)stage.getScene().getRoot().lookup("#friendSp");
		Accordion friendGroup = (Accordion)scrollPane.getContent();

		ListView<Node> listView = new ListView<Node>();
		int onlineCount = 0;
		for (FriendItemVo item:friendItems) {
			Pane pane = stageController.load(R.layout.FriendItem, Pane.class);
			decorateFriendItem(pane, item);
			listView.getItems().add(pane);
		}

		final TitledPane tp = new TitledPane("我的好友0/"+friendItems.size(), listView);
		friendGroup.getPanes().clear();
		friendGroup.getPanes().add(tp);
	}

	private void decorateFriendItem(Pane itemUi, FriendItemVo friendVo) {
		Label autographLabel = (Label) itemUi.lookup("#signature");
		autographLabel.setText(friendVo.getSignature());
		Hyperlink _username_ = (Hyperlink) itemUi.lookup("#userName");
		_username_.setText(friendVo.getFullName());
	}

}
