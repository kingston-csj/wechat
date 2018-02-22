package com.kingston.chat.logic.search;

import com.kingston.chat.base.UiBaseService;
import com.kingston.chat.logic.search.model.RecommendFriendItem;
import com.kingston.chat.ui.R;
import com.kingston.chat.ui.StageController;

import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class SearchManager {

	private static SearchManager instance = new SearchManager();

	public static SearchManager getInstance() {
		return instance;
	}
	
	private void initUi() {
		
	}

	public void refreshRecommendFriends() {
		StageController stageController = UiBaseService.INSTANCE.getStageController();
		Stage stage = stageController.getStageBy(R.id.SearchView);
		GridPane scrollPane = (GridPane)stage.getScene().getRoot().lookup("#friendsGroup");

		for (int i=0;i<3;i++) {
			for (int j=0;j<2;j++) {
				Pane item = stageController.load(R.layout.RecommendFriendItem, Pane.class);
				decorateItem(item, null);
				scrollPane.add(item, i, j);
			}
		}
	}

	private void decorateItem(Pane itemUi, RecommendFriendItem item) {
		Label nickNameUi = (Label) itemUi.lookup("#nickName");
		nickNameUi.setText("起个名字好难");

		Label reasonUi = (Label) itemUi.lookup("#reason");
		reasonUi.setText("10个共同好友");

		ImageView headImage = (ImageView) itemUi.lookup("#headIcon");

	}

}
