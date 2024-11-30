package pers.kinson.wechat.logic.discussion;

import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import lombok.Getter;
import pers.kinson.wechat.logic.constant.Constants;
import pers.kinson.wechat.base.Context;
import pers.kinson.wechat.base.LifeCycle;
import pers.kinson.wechat.base.UiContext;
import pers.kinson.wechat.fxextend.event.DoubleClickEventHandler;
import pers.kinson.wechat.logic.chat.message.req.ReqFetchNewMessage;
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
import pers.kinson.wechat.util.ImageUtil;

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
                if (this.checkValid()) {
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
        ReqViewDiscussionMembers req = new ReqViewDiscussionMembers();
        req.setDiscussionId(selectedGroupId);
        IOUtil.send(req);

        // 拉取讨论组聊天内容
        ReqFetchNewMessage reqFetchNewMessage = new ReqFetchNewMessage();
        reqFetchNewMessage.setMaxSeq(discussionGroups.get(groupVo.getId()).getMaxSeq());
        reqFetchNewMessage.setChannel(Constants.CHANNEL_DISCUSSION);
        reqFetchNewMessage.setTopic(groupVo.getId());
        IOUtil.send(reqFetchNewMessage);
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
                if (vo.getOnline() == 0) {
                    head.setImage(ImageUtil.convertToGray(head.getImage()));
                }
                vBox.getChildren().add(label);
                groupListView.getChildren().add(vBox);
            });
        }

    }

    public void receiveDiscussionMessages(long maxSeq, List<ChatMessage> messages) {
        long discussionId = 0;
        for (ChatMessage message : messages) {
             discussionId = message.getReceiverId();
            discussionMessages.putIfAbsent(discussionId, new LinkedList<>());
            discussionGroups.get(discussionId).setMaxSeq(maxSeq);
            discussionMessages.get(discussionId).add(message);
        }

        if (UiContext.stageController.isStageShown(R.id.DiscussionGroup)) {
            Stage stage = UiContext.stageController.getStageBy(R.id.DiscussionGroup);
            VBox msgContainer = (VBox) stage.getScene().getRoot().lookup("#msgContainer");
            msgContainer.getChildren().clear();
            List<ChatMessage> allMsg = discussionMessages.get(discussionId);
            allMsg.forEach(e -> {
                Pane pane = decorateChatRecord(e);
                msgContainer.getChildren().add(pane);
            });
        }
    }


    private Pane decorateChatRecord(ChatMessage message) {
        boolean fromMe = message.getSenderId() == Context.userManager.getMyUserId();
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
            nameUi.setText(Context.friendManager.getUserName(message.getSenderId()));
        }
        nameUi.setVisible(false);
        Label _createTime = (Label) chatRecord.lookup("#timeUi");
        _createTime.setText(message.getDate());
        FlowPane _body = (FlowPane) chatRecord.lookup("#contentUi");

        Context.messageContentFactory.displayUi(message.getContent().getType(), _body, message);

        return chatRecord;
    }

    public DiscussionGroupVo getDiscussionGroupVo(long discussionId) {
        return discussionGroups.get(discussionId);
    }
}
