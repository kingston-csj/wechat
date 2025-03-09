package pers.kinson.wechat.logic.discussion;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import jforgame.commons.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import pers.kinson.wechat.base.Context;
import pers.kinson.wechat.base.LifeCycle;
import pers.kinson.wechat.base.MessageContentType;
import pers.kinson.wechat.base.UiContext;
import pers.kinson.wechat.logic.chat.ChatPaneHandler;
import pers.kinson.wechat.logic.chat.message.req.ReqChatToChannel;
import pers.kinson.wechat.logic.chat.message.req.ReqFetchNewMessage;
import pers.kinson.wechat.logic.chat.message.vo.ChatMessage;
import pers.kinson.wechat.logic.chat.model.ChatContact;
import pers.kinson.wechat.logic.chat.struct.TextMessageContent;
import pers.kinson.wechat.logic.constant.Constants;
import pers.kinson.wechat.logic.discussion.message.req.ReqViewDiscussionMembers;
import pers.kinson.wechat.logic.discussion.message.res.ResViewDiscussionList;
import pers.kinson.wechat.logic.discussion.message.res.ResViewDiscussionMembersList;
import pers.kinson.wechat.logic.discussion.message.vo.DiscussionGroupVo;
import pers.kinson.wechat.logic.discussion.message.vo.DiscussionMemberVo;
import pers.kinson.wechat.logic.file.FileUiUtil;
import pers.kinson.wechat.logic.system.ApplicationEffect;
import pers.kinson.wechat.logic.system.AvatarCache;
import pers.kinson.wechat.net.CmdConst;
import pers.kinson.wechat.net.IOUtil;
import pers.kinson.wechat.ui.R;
import pers.kinson.wechat.ui.StageController;
import pers.kinson.wechat.util.ImageUtil;
import pers.kinson.wechat.util.SchedulerManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class DiscussionManager implements LifeCycle, ChatPaneHandler {

    private Map<Long, DiscussionGroupVo> discussionGroups = new HashMap<>();

    private Map<Long, Map<Long, DiscussionMemberVo>> groupMembers = new HashMap<>();

    @Override
    public void init() {
        Context.messageRouter.registerHandler(CmdConst.ResViewDiscussionList, this::receiveGroupList);
        Context.messageRouter.registerHandler(CmdConst.ResViewDiscussionMembers, this::refreshGroupMembers);
    }

    public void updateDiscussionSeq(long discussionId, long maxSeq) {
        discussionGroups.get(discussionId).setMaxSeq(maxSeq);
    }

    private void receiveGroupList(Object packet) {
        ResViewDiscussionList resDiscussionList = (ResViewDiscussionList) packet;
        StageController stageController = UiContext.stageController;
        Stage stage = stageController.getStageBy(R.Id.MainView);
        ListView groupListView = (ListView) stage.getScene().getRoot().lookup("#groups");
        groupListView.getItems().clear();

        discussionGroups.clear();
        for (DiscussionGroupVo group : resDiscussionList.getGroups()) {
            discussionGroups.put(group.getId(), group);
            Pane pane = stageController.load(R.Layout.DiscussionItem, Pane.class);
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
        listView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                ListView<Node> view = (ListView<Node>) event.getSource();
                Node selectedItem = view.getSelectionModel().getSelectedItem();
                if (selectedItem == null) return;
                Pane pane = (Pane) selectedItem;
                Label userIdUi = (Label) pane.lookup("#discussionId");

                long discussionId = Long.parseLong(userIdUi.getText());
                DiscussionGroupVo target = discussionGroups.get(discussionId);
                if (target != null) {
                    Context.chatManager.openChatPanel(target);
                }
            }
        });
    }

    @Override
    public Pane loadMessagePane() {
        StageController stageController = UiContext.stageController;
        return stageController.load(R.Layout.DiscussionGroup, Pane.class);
    }

    public void onChatPaneShow(Parent root, ChatContact chatModel) {
        Label userNameUi = (Label) root.lookup("#name");
        userNameUi.setText(chatModel.getName());
        ReqViewDiscussionMembers req = new ReqViewDiscussionMembers();
        req.setDiscussionId(chatModel.getId());
        IOUtil.send(req);

        // 拉取讨论组聊天内容
        ReqFetchNewMessage reqFetchNewMessage = new ReqFetchNewMessage();
        reqFetchNewMessage.setMaxSeq(discussionGroups.get(chatModel.getId()).getMaxSeq());
        reqFetchNewMessage.setChannel(Constants.CHANNEL_DISCUSSION);
        reqFetchNewMessage.setTopic(chatModel.getId());
        IOUtil.send(reqFetchNewMessage);

        registerEvent(root, chatModel);
    }

    private void registerEvent(Parent root, ChatContact chatModel) {
        TextArea msgInput = (TextArea) root.lookup("#msgInput");
        msgInput.requestFocus();

        msgInput.setOnKeyPressed(event -> {
            // 注册enter快捷键
            if (event.getCode() == KeyCode.ENTER) {
                String message = msgInput.getText();
                ReqChatToChannel request = new ReqChatToChannel();
                request.setChannel(Constants.CHANNEL_DISCUSSION);
                request.setTarget(Context.discussionManager.getSelectedGroupId());
                TextMessageContent content = new TextMessageContent();
                request.setContentType(MessageContentType.TEXT);
                content.setContent(message);
                request.setContent(JsonUtil.object2String(content));

                IOUtil.send(request);
                msgInput.clear();
            }
            // 注册ctrl+v快捷键
            // 复制系统剪贴板图片资源
            if (event.isControlDown() && event.getCode() == KeyCode.V) {
                SchedulerManager.INSTANCE.runNow(()->{
                    ReqChatToChannel reqChatToChannel = new ReqChatToChannel();
                    reqChatToChannel.setChannel(Constants.CHANNEL_DISCUSSION);
                    reqChatToChannel.setTarget(chatModel.getId());
                    FileUiUtil.onCopyClipboardResource(msgInput, reqChatToChannel);
                });
            }
        });
    }

    private void refreshGroupMembers(Object packet) {
        ResViewDiscussionMembersList message = (ResViewDiscussionMembersList) packet;
        Map<Long, DiscussionMemberVo> members = new HashMap<>();
        message.getGroups().forEach(e -> {
            members.put(e.getId(), e);
        });
        groupMembers.put(message.getDiscussionId(), members);
        if (UiContext.stageController.isStageShown(R.Id.ChatContainer)) {
            if (!(Context.chatManager.getActivatedContact() instanceof DiscussionGroupVo)) {
                return;
            }
            Stage stage = UiContext.stageController.setStage(R.Id.ChatContainer);
            TilePane groupListView = (TilePane) stage.getScene().getRoot().lookup("#members");
            groupListView.getChildren().clear();

            groupListView.setPadding(new Insets(10, 10, 10, 10)); // 上，右，下，左

            members.forEach((key, vo) -> {
                VBox vBox = new VBox();
                Image image = AvatarCache.getOrCreateImage(vo.getAvatar());
                ImageView head = new ImageView(image);
                head.setFitWidth(50);
                head.setFitHeight(50);
                vBox.getChildren().add(head);
                if (vo.getOnline() == 0) {
                    try {
                        head.setImage(ImageUtil.convertToGray(head.getImage()));
                    } catch (Exception e) {
                        log.error("", e);
                    }
                }
                Label label = new Label(vo.getNickName());
                label.setMaxWidth(50);
                label.setFont(new Font(20));

                vBox.getChildren().add(label);
                groupListView.getChildren().add(vBox);
            });
        }
    }

    public DiscussionGroupVo getDiscussionGroupVo(long discussionId) {
        return discussionGroups.get(discussionId);
    }

    public long getSelectedGroupId() {
        ChatContact contact = Context.chatManager.getActivatedContact();
        if (contact instanceof DiscussionGroupVo) {
            return contact.getId();
        }
        return 0;
    }
}
