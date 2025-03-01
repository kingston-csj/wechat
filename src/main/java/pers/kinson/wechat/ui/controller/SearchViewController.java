package pers.kinson.wechat.ui.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import jforgame.commons.JsonUtil;
import pers.kinson.wechat.base.Context;
import pers.kinson.wechat.base.UiContext;
import pers.kinson.wechat.logic.search.SearchManager;
import pers.kinson.wechat.logic.search.message.req.ReqSearchFriends;
import pers.kinson.wechat.logic.search.message.res.ResSearchFriends;
import pers.kinson.wechat.logic.search.model.RecommendFriendItem;
import pers.kinson.wechat.net.HttpResult;
import pers.kinson.wechat.net.IOUtil;
import pers.kinson.wechat.net.SimpleRequestCallback;
import pers.kinson.wechat.ui.ControlledStage;
import pers.kinson.wechat.ui.R;
import pers.kinson.wechat.ui.StageController;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
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

        try {
            HttpResult httpResult = Context.httpClientManager.get("/search/key", req, HttpResult.class);
            if (httpResult.isOk()) {
                ArrayList<RecommendFriendItem> friendItems = JsonUtil.string2Collection(httpResult.getData(), ArrayList.class, RecommendFriendItem.class);
                SearchManager.getInstance().refreshRecommendFriends(friendItems);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


    @FXML
    private void close() {
        StageController stageController = UiContext.stageController;
        stageController.closeStage(R.Id.SearchView);
    }

    @Override
    public Stage getMyStage() {
        StageController stageController = UiContext.stageController;
        return stageController.getStageBy(R.Id.SearchView);
    }

}
