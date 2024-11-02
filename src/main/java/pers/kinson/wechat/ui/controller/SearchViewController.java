package pers.kinson.wechat.ui.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import pers.kinson.wechat.base.UiContext;
import pers.kinson.wechat.logic.search.SearchManager;
import pers.kinson.wechat.logic.search.message.req.ReqSearchFriends;
import pers.kinson.wechat.logic.search.message.res.ResSearchFriends;
import pers.kinson.wechat.net.IOUtil;
import pers.kinson.wechat.net.SimpleRequestCallback;
import pers.kinson.wechat.ui.ControlledStage;
import pers.kinson.wechat.ui.R;
import pers.kinson.wechat.ui.StageController;

import java.net.URL;
import java.util.ResourceBundle;

public class SearchViewController implements ControlledStage, Initializable {

    @FXML
    private GridPane friendsContainer;

    @FXML
    private TextField friendKey;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // TODO Auto-generated method stub

    }

    @FXML
    public void onSearchBtnClicked() {
        String key = friendKey.getText();
        ReqSearchFriends req = new ReqSearchFriends();
        req.setKey(key);

        IOUtil.callback(req, new SimpleRequestCallback<ResSearchFriends>() {
            @Override
            public void onSuccess(ResSearchFriends callBack) {
                   UiContext.runTaskInFxThread(() -> {
                    SearchManager.getInstance().refreshRecommendFriends(callBack);
                });
            }
        });
    }


    @FXML
    private void close() {
        StageController stageController = UiContext.stageController;
        stageController.closeStage(R.id.SearchView);
    }

    @Override
    public Stage getMyStage() {
        StageController stageController = UiContext.stageController;
        return stageController.getStageBy(R.id.SearchView);
    }

}
