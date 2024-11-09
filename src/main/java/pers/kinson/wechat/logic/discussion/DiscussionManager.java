package pers.kinson.wechat.logic.discussion;

import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import pers.kinson.wechat.base.Context;
import pers.kinson.wechat.base.LifeCycle;
import pers.kinson.wechat.base.UiContext;
import pers.kinson.wechat.fxextend.event.DoubleClickEventHandler;
import pers.kinson.wechat.logic.discussion.message.res.ResViewDiscussionList;
import pers.kinson.wechat.logic.discussion.message.vo.DiscussionGroupVo;
import pers.kinson.wechat.logic.friend.message.vo.FriendItemVo;
import pers.kinson.wechat.net.CmdConst;
import pers.kinson.wechat.ui.R;
import pers.kinson.wechat.ui.StageController;

import java.util.HashMap;
import java.util.Map;

public class DiscussionManager implements LifeCycle {

    private Map<Long, DiscussionGroupVo> discussionGroups = new HashMap<>();

    @Override
    public void init() {
        Context.messageRouter.registerHandler(CmdConst.ResApplyFriendList, this::receiveGroupList);
    }

    private void receiveGroupList(Object packet) {
        ResViewDiscussionList resFriends = (ResViewDiscussionList) packet;
        StageController stageController = UiContext.stageController;
        Stage stage = stageController.getStageBy(R.id.MainView);
        ListView groupListView = (ListView) stage.getScene().getRoot().lookup("#groups");
        groupListView.getItems().clear();

        discussionGroups.clear();
        for (DiscussionGroupVo group : resFriends.getGroups()) {
            discussionGroups.put(group.getId(), group);
            Pane pane = stageController.load(R.layout.DiscussionItem, Pane.class);
            decorateFriendItem(pane, group);
            groupListView.getItems().add(pane);
        }

        bindDoubleClickEvent(groupListView);
    }


    private void decorateFriendItem(Pane itemUi, DiscussionGroupVo friendVo) {
        Hyperlink usernameUi = (Hyperlink) itemUi.lookup("#name");
        usernameUi.setText(friendVo.getName());
        ImageView headImage = (ImageView) itemUi.lookup("#headIcon");
    }

    private void bindDoubleClickEvent(ListView<Node> listView) {
        listView.setOnMouseClicked(new DoubleClickEventHandler<Event>() {
            @Override
            public void handle(Event event) {
                if (this.checkVaild()) {
                    ListView<Node> view = (ListView<Node>) event.getSource();
                    Node selectedItem = view.getSelectionModel().getSelectedItem();
                    if (selectedItem == null)
                        return;
                    Pane pane = (Pane) selectedItem;
                    Label userIdUi = (Label) pane.lookup("#discussionId");

                    long friendId = Long.parseLong(userIdUi.getText());
                    DiscussionGroupVo targetFriend = discussionGroups.get(friendId);
                    if (targetFriend != null) {
                        openChat2PointPanel(targetFriend);
                    }
                }
            }
        });
    }

    private void openChat2PointPanel(DiscussionGroupVo targetFriend) {
        StageController stageController = UiContext.stageController;
        Stage chatStage = stageController.setStage(R.id.ChatToPoint);

        Hyperlink userNameUi = (Hyperlink) chatStage.getScene().getRoot().lookup("#name");
        userNameUi.setText(targetFriend.getName());

//        Context.chatManager.refreshFriendPrivateMessage(targetFriend.getUserId());
    }
}
