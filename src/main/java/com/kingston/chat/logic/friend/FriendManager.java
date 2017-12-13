package com.kingston.chat.logic.friend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.kingston.chat.base.Constants;
import com.kingston.chat.base.UiBaseService;
import com.kingston.chat.fxextend.event.DoubleClickEventHandler;
import com.kingston.chat.logic.friend.model.FriendItemVo;
import com.kingston.chat.logic.user.UserManager;
import com.kingston.chat.ui.R;
import com.kingston.chat.ui.StageController;
import com.kingston.chat.util.ImageUtil;

import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.control.Accordion;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class FriendManager {

	private static FriendManager instance = new FriendManager();

	private Map<Long, FriendItemVo> friends = new HashMap<>();

	private Map<Integer, String> groupNames = new HashMap<>();
	/** 分组好友视图 */
	private TreeMap<Integer, List<FriendItemVo>> groupFriends = new TreeMap<>();

	public static FriendManager getInstance() {
		return instance;
	}

	/**
	 * 好友登录刷新
	 * @param friendId
	 */
	public void onFriendLogin(long friendId) {
		FriendItemVo friend = friends.get(friendId);
		if (friend != null) {
			friend.setOnline(Constants.ONLINE_STATUS);
			List<FriendItemVo> friendItems = new ArrayList<>(friends.values());
			receiveFriendsList(friendItems);
		}
	}

	/**
	 * 好友下线刷新
	 * @param friendId
	 */
	public void onFriendLogout(long friendId) {
		FriendItemVo friend = friends.get(friendId);
		if (friend != null) {
			friend.setOnline(Constants.OFFLINE_STATUS);
			List<FriendItemVo> friendItems = new ArrayList<>(friends.values());
			receiveFriendsList(friendItems);
		}
	}

	public void receiveFriendsList(List<FriendItemVo> friendItems) {
		friends.clear();
		for (FriendItemVo item:friendItems) {
			friends.put(item.getUserId(), item);
		}
		rangeToGroupFriends(friendItems);

		UiBaseService.INSTANCE.runTaskInFxThread(() -> {
			refreshMyFriendsView(friendItems);
		});

	}

	public FriendItemVo queryFriend(long friendId) {
		return this.friends.get(friendId);
	}

	public void refreshMyFriendsView(List<FriendItemVo> friendItems) {
		StageController stageController = UiBaseService.INSTANCE.getStageController();
		Stage stage = stageController.getStageBy(R.id.MainView);
		ScrollPane scrollPane = (ScrollPane)stage.getScene().getRoot().lookup("#friendSp");
		Accordion friendGroup = (Accordion)scrollPane.getContent();
		friendGroup.getPanes().clear();

		for (Map.Entry<Integer, List<FriendItemVo>> entry:groupFriends.entrySet()) {
			int groupId = entry.getKey();
			String groupName = this.groupNames.get(groupId);
			decorateFriendGroup(friendGroup, groupName, entry.getValue());
		}
	}

	/**
	 * 调整成好友分组结构
	 */
	private void rangeToGroupFriends(List<FriendItemVo> friendItems) {
		this.groupFriends.clear();
		TreeMap<Integer, List<FriendItemVo>> groupFriends = new TreeMap<>();
		for (FriendItemVo item:friendItems) {
			int groupId= item.getGroup();
			List<FriendItemVo> frendsByGroup = groupFriends.get(groupId);
			if (frendsByGroup == null) {
				frendsByGroup = new ArrayList<>();
				groupFriends.put(groupId, frendsByGroup);
			}
			this.groupNames.put(groupId, item.getGroupName());
			frendsByGroup.add(item);
		}
		this.groupFriends = groupFriends;
	}


	private void decorateFriendGroup(Accordion container, String groupName, List<FriendItemVo> friendItems) {
		ListView<Node> listView = new ListView<Node>();
		int onlineCount = 0;
		StageController stageController = UiBaseService.INSTANCE.getStageController();
		for (FriendItemVo item:friendItems) {
			if (item.isOnlie()) {
				onlineCount++;
			}
			Pane pane = stageController.load(R.layout.FriendItem, Pane.class);
			decorateFriendItem(pane, item);
			listView.getItems().add(pane);
		}

		bindDoubleClickEvent(listView);
		String groupInfo = groupName + " " + onlineCount+"/"+friendItems.size();
		TitledPane tp = new TitledPane(groupInfo, listView);
		container.getPanes().add(tp);
	}

	private void decorateFriendItem(Pane itemUi, FriendItemVo friendVo) {
		Label autographLabel = (Label) itemUi.lookup("#signature");
		autographLabel.setText(friendVo.getSignature());
		Hyperlink usernameUi = (Hyperlink) itemUi.lookup("#userName");
		usernameUi.setText(friendVo.getFullName());

		//隐藏域，聊天界面用
		Label userIdUi = (Label)itemUi.lookup("#friendId");
		userIdUi.setText(String.valueOf(friendVo.getUserId()));

		ImageView headImage = (ImageView) itemUi.lookup("#headIcon");

		if (!friendVo.isOnlie()) {
			headImage.setImage(ImageUtil.convertToGray(headImage.getImage()));
		}

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
					Label userIdUi = (Label)pane.lookup("#friendId");
//					System.out.println("==text="+userIdUi.getText());

					long friendId = Long.parseLong(userIdUi.getText());
					FriendItemVo targetFriend = friends.get(friendId);

					long selfId = UserManager.getInstance().getMyUserId();
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
		StageController stageController = UiBaseService.INSTANCE.getStageController();
		Stage chatStage = stageController.setStage(R.id.ChatToPoint);

		Label userIdUi = (Label)chatStage.getScene().getRoot().lookup("#userIdUi");
		userIdUi.setText(String.valueOf(targetFriend.getUserId()));
		Hyperlink userNameUi = (Hyperlink)chatStage.getScene().getRoot().lookup("#userName");
		Label signatureUi = (Label)chatStage.getScene().getRoot().lookup("#signature");
		userNameUi.setText(targetFriend.getFullName());
		signatureUi.setText(targetFriend.getSignature());
	}

}
