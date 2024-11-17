package pers.kinson.wechat.ui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;
import pers.kinson.wechat.base.Constants;
import pers.kinson.wechat.base.Context;
import pers.kinson.wechat.base.UiContext;
import pers.kinson.wechat.logic.chat.message.req.ReqChatToChannel;
import pers.kinson.wechat.net.IOUtil;
import pers.kinson.wechat.ui.ControlledStage;
import pers.kinson.wechat.ui.R;
import pers.kinson.wechat.ui.StageController;

public class DiscussionGroupController implements ControlledStage {

    @FXML
    private Label name;

    @FXML
    private TextArea msgInput;

    @FXML
    private ScrollPane outputMsgUi;

    @FXML
    private TilePane members;


    @Override
    public Stage getMyStage() {
        StageController stageController = UiContext.stageController;
        return stageController.getStageBy(R.id.DiscussionGroup);
    }

    @FXML
    private void close() {
        UiContext.stageController.closeStage(R.id.DiscussionGroup);
    }

    @FXML
    private void createDiscussion() {
        StageController stageController = UiContext.stageController;
        stageController.setStage(R.id.CreateDiscussion);
    }


    @FXML
    private void sendMessage() {
        String message = msgInput.getText();
        ReqChatToChannel request = new ReqChatToChannel();
        request.setChannel(Constants.CHANNEL_DISCUSSION);
        request.setToUserId(Context.discussionManager.getSelectedGroupId());
        request.setContent(message);

        IOUtil.send(request);
        msgInput.setText("");
    }

}


