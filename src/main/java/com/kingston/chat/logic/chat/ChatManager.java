package com.kingston.chat.logic.chat;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.kingston.chat.base.IoBaseService;
import com.kingston.chat.base.UiBaseService;
import com.kingston.chat.logic.chat.message.req.ReqChatToUserPacket;
import com.kingston.chat.logic.user.UserManager;
import com.kingston.chat.ui.R;
import com.kingston.chat.ui.StageController;

import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ChatManager {

	private static ChatManager self = new ChatManager();

	private ChatManager() {}

	public static ChatManager getInstance() {
		return self;
	}

	public void sendMessageTo(long friendId, String content) {
		ReqChatToUserPacket request = new ReqChatToUserPacket();
		request.setToUserId(friendId);
		request.setContent(content);

		IoBaseService.INSTANCE.sendServerRequest(request);
	}

	public void receiveFriendPrivateMessage(long sourceId, String content) {
		StageController stageController = UiBaseService.INSTANCE.getStageController();
		Stage stage = stageController.getStageBy(R.id.ChatToPoint);
		VBox msgContainer = (VBox)stage.getScene().getRoot().lookup("#msgContainer");

		UiBaseService.INSTANCE.runTaskInFxThread(()-> {
			Pane pane = null;
			if (sourceId == UserManager.getInstance().getMyUserId()) {
				pane = stageController.load(R.layout.PrivateChatItemRight, Pane.class);
			}else {
				pane = stageController.load(R.layout.PrivateChatItemLeft, Pane.class);
			}

			decorateChatRecord(content, pane);
			msgContainer.getChildren().add(pane);
		});

	}

	private void decorateChatRecord(String message, Pane chatRecord) {
		Hyperlink _nikename = (Hyperlink) chatRecord.lookup("#nameUi");
		_nikename.setText(message);
		_nikename.setVisible(false);
		Label _createTime = (Label) chatRecord.lookup("#timeUi");
		_createTime.setText(new SimpleDateFormat("yyyy年MM月dd日  HH:mm:ss").format(new Date()));
		Label _body = (Label) chatRecord.lookup("#contentUi");
		_body.setText(message);
	}

}
