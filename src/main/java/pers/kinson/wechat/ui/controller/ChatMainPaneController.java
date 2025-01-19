package pers.kinson.wechat.ui.controller;

import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import pers.kinson.wechat.base.UiContext;
import pers.kinson.wechat.ui.ControlledStage;
import pers.kinson.wechat.ui.R;
import pers.kinson.wechat.ui.StageController;


@Slf4j
public class ChatMainPaneController implements ControlledStage {


    @Override
    public void onStageShown() {
    }

    @Override
    public Stage getMyStage() {
        StageController stageController = UiContext.stageController;
        return stageController.getStageBy(R.Id.ChatContainer);
    }

}


