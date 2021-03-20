package pers.kinson.wechat.logic.chat;

import java.text.SimpleDateFormat;
import java.util.Date;

import pers.kinson.wechat.base.SessionManager;
import pers.kinson.wechat.base.UiBaseService;
import pers.kinson.wechat.logic.chat.message.req.ReqChatToUser;
import pers.kinson.wechat.logic.chat.message.res.ResChatToUser;
import pers.kinson.wechat.logic.user.UserManager;
import pers.kinson.wechat.ui.R;
import pers.kinson.wechat.ui.StageController;

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
		ReqChatToUser request = new ReqChatToUser();
		request.setToUserId(friendId);
		request.setContent(content);

		SessionManager.INSTANCE.sendMessage(request);
	}

	public void receiveFriendPrivateMessage(ResChatToUser msg) {
		long sourceId = msg.getFromUserId();
		String content = msg.getContent();
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
