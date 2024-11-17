package pers.kinson.wechat.logic.discussion;

import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import jforgame.commons.NumberUtil;
import lombok.Getter;
import pers.kinson.wechat.base.Constants;
import pers.kinson.wechat.base.Context;
import pers.kinson.wechat.base.LifeCycle;
import pers.kinson.wechat.base.UiContext;
import pers.kinson.wechat.fxextend.event.DoubleClickEventHandler;
import pers.kinson.wechat.logic.chat.message.req.ReqFetchNewMessage;
import pers.kinson.wechat.logic.chat.message.res.ResNewMessage;
import pers.kinson.wechat.logic.chat.message.res.ResNewMessageNotify;
import pers.kinson.wechat.logic.chat.message.vo.ChatMessage;
import pers.kinson.wechat.logic.discussion.message.req.ReqViewDiscussionMembers;
import pers.kinson.wechat.logic.discussion.message.res.ResViewDiscussionList;
import pers.kinson.wechat.logic.discussion.message.res.ResViewDiscussionMembersList;
import pers.kinson.wechat.logic.discussion.message.vo.DiscussionGroupVo;
import pers.kinson.wechat.logic.discussion.message.vo.DiscussionMemberVo;
import pers.kinson.wechat.net.CmdConst;
import pers.kinson.wechat.net.IOUtil;
import pers.kinson.wechat.ui.R;
import pers.kinson.wechat.ui.StageController;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DiscussionManager implements LifeCycle {

    private Map<Long, DiscussionGroupVo> discussionGroups = new HashMap<>();

    private Map<Long, Map<Long, DiscussionMemberVo>> groupMembers = new HashMap<>();

    @Getter
    private Long selectedGroupId;

    private Map<Long, List<ChatMessage>> discussionMessages = new ConcurrentHashMap<>();

    @Override
    public void init() {
        Context.messageRouter.registerHandler(CmdConst.ResViewDiscussionList, this::receiveGroupList);
        Context.messageRouter.registerHandler(CmdConst.ResViewDiscussionMembers, this::refreshGroupMembers);
        Context.messageRouter.registerHandler(CmdConst.ResNewMessageNotify, this::notifyNewMessage);
        Context.messageRouter.registerHandler(CmdConst.ResNewMessage, this::refreshNewMessage);
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


    private void decorateFriendItem(Pane itemUi, DiscussionGroupVo groupVo) {
        Label idLabel = (Label) itemUi.lookup("#discussionId");
        idLabel.setText(String.valueOf(groupVo.getId()));
        Hyperlink usernameUi = (Hyperlink) itemUi.lookup("#name");
        usernameUi.setText(groupVo.getName());
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

                    long discussionId = Long.parseLong(userIdUi.getText());
                    DiscussionGroupVo targetFriend = discussionGroups.get(discussionId);
                    if (targetFriend != null) {
                        selectedGroupId = discussionId;
                        openGroupUI(targetFriend);
                    }
                }
            }
        });
    }

    private void openGroupUI(DiscussionGroupVo groupVo) {
        Stage chatStage = UiContext.stageController.setStage(R.id.DiscussionGroup);

        Label userNameUi = (Label) chatStage.getScene().getRoot().lookup("#name");
        userNameUi.setText(groupVo.getName());

//        Context.chatManager.refreshFriendPrivateMessage(groupVo.getUserId());
        ReqViewDiscussionMembers req = new ReqViewDiscussionMembers();
        req.setDiscussionId(selectedGroupId);
        IOUtil.send(req);
    }

    private void refreshGroupMembers(Object packet) {
        ResViewDiscussionMembersList message = (ResViewDiscussionMembersList) packet;
        Map<Long, DiscussionMemberVo> members = new HashMap<>();
        message.getGroups().forEach(e -> {
            members.put(e.getId(), e);
        });
        groupMembers.put(message.getDiscussionId(), members);
        if (UiContext.stageController.isStageShown(R.id.DiscussionGroup)) {
            Stage stage = UiContext.stageController.setStage(R.id.DiscussionGroup);
            TilePane groupListView = (TilePane) stage.getScene().getRoot().lookup("#members");
            groupListView.getChildren().clear();

            groupListView.setPadding(new Insets(10, 10, 10, 10)); // 上，右，下，左


            members.forEach((key, vo) -> {
                VBox vBox = new VBox();
                ImageView head = new ImageView("@../../main/img/head.png");
                head.setFitWidth(50);
                head.setFitHeight(50);
                vBox.getChildren().add(head);
                Label label = new Label(vo.getNickName());
                label.setMaxWidth(50);
                label.setFont(new Font(20));
                vBox.getChildren().add(label);
                groupListView.getChildren().add(vBox);
            });
        }

    }

    private void notifyNewMessage(Object packet) {
        ResNewMessageNotify message = (ResNewMessageNotify) packet;
        DiscussionGroupVo targetDiscussionGroup = discussionGroups.get(NumberUtil.longValue(message.getTopic()));
        if (targetDiscussionGroup != null) {
            ReqFetchNewMessage reqFetchNewMessage = new ReqFetchNewMessage();
            reqFetchNewMessage.setChannel(Constants.CHANNEL_DISCUSSION);
            reqFetchNewMessage.setTopic("" + targetDiscussionGroup.getId());
            reqFetchNewMessage.setMaxSeq(targetDiscussionGroup.getMaxSeq());
            IOUtil.send(reqFetchNewMessage);
        }
    }

    private void refreshNewMessage(Object packet) {
        ResNewMessage message = (ResNewMessage) packet;
        long discussionId = NumberUtil.longValue(message.getTopic());
        discussionMessages.putIfAbsent(discussionId, new LinkedList<>());

        long maxSeq = 0;
        for (ChatMessage e : message.getMessages()) {
            e.setContent(Context.messageContentFactory.parse(e.getType(), e.getJson()));
            maxSeq = Math.max(maxSeq, e.getSeq());
        }

        discussionGroups.get(discussionId).setMaxSeq(maxSeq);
        discussionMessages.get(discussionId).addAll(message.getMessages());

        Stage stage = UiContext.stageController.getStageBy(R.id.DiscussionGroup);
        VBox msgContainer = (VBox) stage.getScene().getRoot().lookup("#msgContainer");
        if (UiContext.stageController.isStageShown(R.id.DiscussionGroup)) {
            message.getMessages().forEach(e -> {
                Pane pane = decorateChatRecord(e);
                msgContainer.getChildren().add(pane);
            });
        }
    }


    private Pane decorateChatRecord(ChatMessage message) {
        boolean fromMe = message.getUserId() == Context.userManager.getMyUserId();
        StageController stageController = UiContext.stageController;
        Pane chatRecord = null;
        if (fromMe) {
            chatRecord = stageController.load(R.layout.PrivateChatItemRight, Pane.class);
        } else {
            chatRecord = stageController.load(R.layout.PrivateChatItemLeft, Pane.class);
        }

        Hyperlink nameUi = (Hyperlink) chatRecord.lookup("#nameUi");
        if (fromMe) {
            nameUi.setText(Context.userManager.getMyProfile().getUserName());
        } else {
            nameUi.setText(Context.friendManager.getUserName(message.getUserId()));
        }
        nameUi.setVisible(false);
        Label _createTime = (Label) chatRecord.lookup("#timeUi");
        _createTime.setText(message.getDate());
        Label _body = (Label) chatRecord.lookup("#contentUi");
        _body.setText(message.getContent().getContent());

        return chatRecord;
    }
}
