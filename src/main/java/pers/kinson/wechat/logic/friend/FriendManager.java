package pers.kinson.wechat.logic.friend;

import com.google.common.eventbus.Subscribe;
import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.control.Accordion;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import lombok.Getter;
import pers.kinson.wechat.base.Context;
import pers.kinson.wechat.base.EventDispatcher;
import pers.kinson.wechat.base.LifeCycle;
import pers.kinson.wechat.base.UiContext;
import pers.kinson.wechat.fxextend.event.DoubleClickEventHandler;
import pers.kinson.wechat.logic.constant.RedPointId;
import pers.kinson.wechat.logic.friend.message.req.ReqApplyResult;
import pers.kinson.wechat.logic.friend.message.res.ResApplyFriendList;
import pers.kinson.wechat.logic.friend.message.res.ResFriendList;
import pers.kinson.wechat.logic.friend.message.res.ResQueryFriendsOnlineStatus;
import pers.kinson.wechat.logic.friend.message.vo.FriendApplyVo;
import pers.kinson.wechat.logic.friend.message.vo.FriendItemVo;
import pers.kinson.wechat.logic.redpoint.RedPointEvent;
import pers.kinson.wechat.net.CmdConst;
import pers.kinson.wechat.net.IOUtil;
import pers.kinson.wechat.ui.R;
import pers.kinson.wechat.ui.StageController;
import pers.kinson.wechat.util.ImageUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class FriendManager implements LifeCycle {

    private Map<Long, FriendItemVo> friends = new LinkedHashMap<>();

    private Map<Integer, String> groupNames = new LinkedHashMap<>();

    private String defaultGroupName = "未分组";

    @Getter
    private Long activatedFriendId;

    @Override
    public void init() {
        Context.messageRouter.registerHandler(CmdConst.ResFriendList, this::receiveFriendsList);
        Context.messageRouter.registerHandler(CmdConst.ResApplyFriendList, this::refreshFriendApplyView);
        Context.messageRouter.registerHandler(CmdConst.ResFriendOnlineStatus, this::refreshFriendOnlineStatus);

        EventDispatcher.eventBus.register(this);
    }

    private void receiveFriendsList(Object packet) {
        ResFriendList resFriends = (ResFriendList) packet;
        receiveFriendsList(resFriends.getFriends());
    }

    public void receiveFriendsList(List<FriendItemVo> friendItems) {
        friends.clear();
        for (FriendItemVo item : friendItems) {
            friends.put(item.getUserId(), item);
        }

        refreshMyFriendsView();
    }

    public FriendItemVo queryFriend(long friendId) {
        return this.friends.get(friendId);
    }

    public void refreshMyFriendsView() {
        StageController stageController = UiContext.stageController;
        Stage stage = stageController.getStageBy(R.id.MainView);
        ScrollPane scrollPane = (ScrollPane) stage.getScene().getRoot().lookup("#friendSp");
        Accordion friendGroup = (Accordion) scrollPane.getContent();
        friendGroup.getPanes().clear();

        Map<Integer, List<FriendItemVo>> friendsByGroup = rangeToGroupFriends();
        for (Map.Entry<Integer, List<FriendItemVo>> entry : friendsByGroup.entrySet()) {
            int groupId = entry.getKey();
            decorateFriendGroup(friendGroup, groupId, entry.getValue());
        }
    }

    /**
     * 调整成好友分组结构
     */
    private Map<Integer, List<FriendItemVo>> rangeToGroupFriends() {
        Map<Integer, List<FriendItemVo>> groupFriends = new LinkedHashMap<>();
        for (FriendItemVo item : friends.values()) {
            int groupId = item.getGroup();
            List<FriendItemVo> friendsByGroup = groupFriends.computeIfAbsent(groupId, k -> new ArrayList<>());
            if (groupId > 0) {
                this.groupNames.put(groupId, item.getGroupName());
            }
            friendsByGroup.add(item);
        }
        return groupFriends;
    }


    private void decorateFriendGroup(Accordion container, int groupId, List<FriendItemVo> friendItems) {
        ListView<Node> listView = new ListView<>();
        int onlineCount = 0;
        StageController stageController = UiContext.stageController;
        for (FriendItemVo item : friendItems) {
            if (item.isOnline()) {
                onlineCount++;
            }
            Pane pane = stageController.load(R.layout.FriendItem, Pane.class);
            pane.setId("friend@" + item.getUserId());
            decorateFriendItem(pane, item);
            listView.getItems().add(pane);
        }

        bindDoubleClickEvent(listView);
        String groupName = this.groupNames.getOrDefault(groupId, defaultGroupName);
        // 显示在线人数
        String groupInfo = groupName + onlineCount + "/" + friendItems.size();
        TitledPane tp = new TitledPane(groupInfo, listView);
        tp.setId("group@" + groupId);
        container.getPanes().add(tp);
    }

    private void decorateFriendItem(Pane itemUi, FriendItemVo friendVo) {
        Label autographLabel = (Label) itemUi.lookup("#signature");
        autographLabel.setText(friendVo.getSignature());
        Hyperlink usernameUi = (Hyperlink) itemUi.lookup("#userName");
        usernameUi.setText(friendVo.getFullName());

        //隐藏域，聊天界面用
        Label userIdUi = (Label) itemUi.lookup("#friendId");
        userIdUi.setText(String.valueOf(friendVo.getUserId()));
        ImageView headImage = (ImageView) itemUi.lookup("#headIcon");
        if (!friendVo.isOnline()) {
            headImage.setImage(ImageUtil.convertToGray(headImage.getImage()));
        }
    }

    @Subscribe
    public void onEvent(RedPointEvent event) {
        if (event.getPoints().containsKey(RedPointId.FRIEND_APPLY)) {
        }
    }

    private void refreshFriendApplyView(Object message) {
        ResApplyFriendList resApplyFriendList = (ResApplyFriendList) message;
        StageController stageController = UiContext.stageController;
        Stage stage = stageController.getStageBy(R.id.MainView);
        ListView applyListView = (ListView) stage.getScene().getRoot().lookup("#applies");
        applyListView.getItems().clear();
        decorateApplyItem(applyListView, resApplyFriendList.getRecords());
    }

    private void refreshFriendOnlineStatus(Object message) {
        ResQueryFriendsOnlineStatus onlineStatus = (ResQueryFriendsOnlineStatus) message;
        Stage stage = UiContext.stageController.getStageBy(R.id.MainView);
        ScrollPane scrollPane = (ScrollPane) stage.getScene().getRoot().lookup("#friendSp");
        Accordion parentContainer = (Accordion) scrollPane.getContent();
        Set<Long> onlineIds = new HashSet<>(onlineStatus.getIds());
        boolean changed = false;
        for (Map.Entry<Long, FriendItemVo> entry : friends.entrySet()) {
            long friendId = entry.getKey();
            FriendItemVo vo = entry.getValue();
            byte status = 0;
            // 下线
            if (vo.isOnline() && !onlineIds.contains(friendId)) {
                status = 1;
            }
            // 上线
            if (!vo.isOnline() && onlineIds.contains(friendId)) {
                status = 2;
            }
            vo.setOnline((byte) (onlineIds.contains(friendId) ? 1 : 0));
            if (status > 0) {
                changed = true;
                Node itemUi = lookUpFriendItem(parentContainer, vo.getUserId());
                if (itemUi != null) {
                    ImageView headImage = (ImageView) itemUi.lookup("#headIcon");
                    if (status == 1) {
                        headImage.setImage(ImageUtil.convertToGray(headImage.getImage()));
                    } else {
                        headImage.setImage(new Image(vo.getHeadUrl()));
                    }
                }
            }
        }
        if (changed) {
            Map<Integer, Integer> onlineCounter = new HashMap<>();
            Map<Integer, Integer> friendByGroup = new HashMap<>();
            for (Map.Entry<Long, FriendItemVo> entry : friends.entrySet()) {
                FriendItemVo vo = entry.getValue();
                if (vo.isOnline()) {
                    int prevOnline = onlineCounter.getOrDefault(vo.getGroup(), 0);
                    onlineCounter.put(vo.getGroup(), prevOnline + 1);
                }
                int prevSum = onlineCounter.getOrDefault(vo.getGroup(), 0);
                friendByGroup.put(vo.getGroup(), prevSum + 1);
            }

            for (Map.Entry<Integer, Integer> entry : friendByGroup.entrySet()) {
                Integer groupId = entry.getKey();
                TitledPane groupUi = (TitledPane) parentContainer.lookup("#group@" + groupId);
                String groupName = this.groupNames.getOrDefault(groupId, defaultGroupName);
                Integer online = onlineCounter.getOrDefault(groupId,0);
                String groupInfo = groupName + online + "/" + entry.getValue();
                groupUi.setText(groupInfo);
            }
        }
    }

    private Node lookUpFriendItem(Accordion friendGroup, Long friendId) {
        FriendItemVo vo = friends.get(friendId);
        TitledPane groupUi = (TitledPane) friendGroup.lookup("#group@" + vo.getGroup());
        ListView<Node> childContainer = (ListView<Node>) groupUi.getContent();
        for (Node itemUi : childContainer.getItems()) {
            if (Objects.equals(itemUi.getId(), "friend@" + vo.getUserId())) {
                return itemUi;
            }
        }
        return null;
    }


    @SuppressWarnings("all")
    private void decorateApplyItem(ListView listView, List<FriendApplyVo> friendItems) {
        StageController stageController = UiContext.stageController;
        Long myUserId = Context.userManager.getMyUserId();
        for (FriendApplyVo item : friendItems) {
            Pane pane = stageController.load(R.layout.ApplyItem, Pane.class);
            Node agreeBtn = pane.lookup("#agreeBtn");
            Node rejectBtn = pane.lookup("#rejectBtn");
            Label statusLabel = (Label) pane.lookup("#status");
            Label remarkLabel = (Label) pane.lookup("#remark");
            remarkLabel.setText(item.getRemark());
            Hyperlink userNameLink = (Hyperlink) pane.lookup("#userName");

            // 未处理
            if (item.getStatus() == 0) {
                // 别人申请添加我为好友
                if (item.getToId() == myUserId) {
                    agreeBtn.setVisible(true);
                    rejectBtn.setVisible(true);
                    userNameLink.setText(item.getFromName());
                    ReqApplyResult reqApplyResult = new ReqApplyResult();
                    reqApplyResult.setApplyId(item.getId());
                    agreeBtn.setOnMouseClicked(e -> {
                        agreeBtn.setVisible(false);
                        rejectBtn.setVisible(false);
                        reqApplyResult.setStatus((byte) 1);
                        statusLabel.setVisible(true);
                        statusLabel.setText("已同意");
                        IOUtil.send(reqApplyResult);
                    });

                    rejectBtn.setOnMouseClicked(e -> {
                        agreeBtn.setVisible(false);
                        rejectBtn.setVisible(false);
                        reqApplyResult.setStatus((byte) 2);
                        statusLabel.setVisible(true);
                        statusLabel.setText("已拒绝");
                        IOUtil.send(reqApplyResult);
                    });
                } else {
                    // 我申请添加别人为好友
                    userNameLink.setText(item.getToName());
                    statusLabel.setVisible(true);
                    statusLabel.setText("正在审核");
                }
            } else {
                // 已处理
                statusLabel.setVisible(true);
                statusLabel.setText(item.getStatus() == 1 ? "已同意" : "已拒绝");
            }

            listView.getItems().add(pane);
        }
    }


    private void bindDoubleClickEvent(ListView<Node> listView) {
        listView.setOnMouseClicked(new DoubleClickEventHandler<Event>() {
            @Override
            public void handle(Event event) {
                if (this.checkValid()) {
                    ListView<Node> view = (ListView<Node>) event.getSource();
                    Node selectedItem = view.getSelectionModel().getSelectedItem();
                    if (selectedItem == null)
                        return;
                    Pane pane = (Pane) selectedItem;
                    Label userIdUi = (Label) pane.lookup("#friendId");

                    long friendId = Long.parseLong(userIdUi.getText());
                    FriendItemVo targetFriend = friends.get(friendId);

                    long selfId = Context.userManager.getMyUserId();
                    if (friendId == selfId) {
                        //不能跟自己聊天
                        return;
                    }
                    if (targetFriend != null) {
                        openChat2PointPanel(targetFriend);
                    }
                }
            }
        });
    }

    private void openChat2PointPanel(FriendItemVo targetFriend) {
        StageController stageController = UiContext.stageController;
        Stage chatStage = stageController.setStage(R.id.ChatToPoint);

        Label userIdUi = (Label) chatStage.getScene().getRoot().lookup("#userIdUi");
        userIdUi.setText(String.valueOf(targetFriend.getUserId()));
        Hyperlink userNameUi = (Hyperlink) chatStage.getScene().getRoot().lookup("#userName");
        Label signatureUi = (Label) chatStage.getScene().getRoot().lookup("#signature");
        userNameUi.setText(targetFriend.getFullName());
        signatureUi.setText(targetFriend.getSignature());

        activatedFriendId = targetFriend.getUserId();

        Context.friendManager.updateRedPoint(targetFriend.getUserId(), false);
        Context.chatManager.showFriendPrivateMessage(targetFriend.getUserId());
    }

    public String getUserName(Long userId) {
        if (userId == Context.userManager.getMyUserId()) {
            return Context.userManager.getMyProfile().getUserName();
        }
        return friends.get(userId).getUserName();
    }

    public Collection<FriendItemVo> getFriends() {
        return friends.values();
    }

    public void updateRedPoint(Long friendId, boolean show) {
        if (Objects.equals(friendId, Context.userManager.getMyUserId())) {
            return;
        }
        // 当前已经聊得嗨起了
        if (show && Objects.equals(friendId, activatedFriendId)) {
            return;
        }
        Stage stage = UiContext.stageController.getStageBy(R.id.MainView);
        ScrollPane scrollPane = (ScrollPane) stage.getScene().getRoot().lookup("#friendSp");
        Accordion parentContainer = (Accordion) scrollPane.getContent();
        Node node = lookUpFriendItem(parentContainer, friendId);
        if (node != null) {
            node.lookup("#redDot").setVisible(show);
        }
    }

    public void resetActivatedFriendId() {
        this.activatedFriendId = null;
    }

}
