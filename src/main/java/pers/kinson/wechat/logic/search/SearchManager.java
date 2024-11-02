package pers.kinson.wechat.logic.search;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import pers.kinson.wechat.base.Context;
import pers.kinson.wechat.base.UiContext;
import pers.kinson.wechat.logic.friend.message.req.ReqApplyFriend;
import pers.kinson.wechat.logic.friend.message.res.ResApplyFriend;
import pers.kinson.wechat.logic.search.message.res.ResSearchFriends;
import pers.kinson.wechat.logic.search.model.RecommendFriendItem;
import pers.kinson.wechat.net.IOUtil;
import pers.kinson.wechat.net.SimpleRequestCallback;
import pers.kinson.wechat.ui.R;
import pers.kinson.wechat.ui.StageController;

import java.util.List;

public class SearchManager {

    private static SearchManager instance = new SearchManager();

    public static SearchManager getInstance() {
        return instance;
    }

    public void refreshRecommendFriends(ResSearchFriends resSearchFriends) {
        List<RecommendFriendItem> items = resSearchFriends.getFriends();
        StageController stageController = UiContext.stageController;
//		stageController.switchStage(R.id.SearchView, R.id.MainView);
        Stage stage = stageController.setStage(R.id.SearchView);
        GridPane scrollPane = lookUpFriendsContainer();
        scrollPane.getChildren().clear();

        if (items == null || items.isEmpty()) {
            // 暂时填充假数据
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 2; j++) {
                    Pane item = stageController.load(R.layout.RecommendFriendItem, Pane.class);
                    decorateItem(item, null);
                    scrollPane.add(item, i, j);
                }
            }
        } else {
            for (int i = 0; i < items.size(); i++) {
                int colIndex = items.size() / 3;
                int rowIndex = items.size() % 3;
                Pane itemUi = stageController.load(R.layout.RecommendFriendItem, Pane.class);
                decorateItem(itemUi, items.get(i));
                scrollPane.add(itemUi, colIndex, rowIndex);
            }
        }

    }

    private GridPane lookUpFriendsContainer() {
        StageController stageController = UiContext.stageController;
        // 使用SplitPane有坑，由于SplitPane没有children子标签，所以这样需要间接lookup
        Stage stage = stageController.getStageBy(R.id.SearchView);
        SplitPane splitPane = (SplitPane) stage.getScene().getRoot().lookup("#friendsSplitPane");
        ObservableList<Node> itmes = splitPane.getItems();
        AnchorPane anchorPane = (AnchorPane) itmes.get(1);

        GridPane scrollPane = (GridPane) anchorPane.lookup("#friendsContainer");
        return scrollPane;
    }

    private void decorateItem(Pane itemUi, RecommendFriendItem item) {
        Label nickNameUi = (Label) itemUi.lookup("#nickName");
        nickNameUi.setText(item == null ? "起个名字好难" : item.getNickName());
        Label reasonUi = (Label) itemUi.lookup("#reason");
        reasonUi.setText("10个共同好友");

        ImageView headImage = (ImageView) itemUi.lookup("#headIcon");
        Button addBtn = (Button) itemUi.lookup("#addBtn");
        addBtn.setOnMouseClicked(e -> {
            ReqApplyFriend reqApply = new ReqApplyFriend();
            reqApply.setFrom(Context.userManager.getMyProfile().getUserId());
            reqApply.setTo(item.getUserId());
            IOUtil.callback(reqApply, new SimpleRequestCallback<ResApplyFriend>() {
                @Override
                public void onSuccess(ResApplyFriend callBack) {
                }
            });
        });
    }

}
