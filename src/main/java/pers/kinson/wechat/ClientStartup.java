package pers.kinson.wechat;

import javafx.application.Application;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.SneakyThrows;
import pers.kinson.wechat.base.Context;
import pers.kinson.wechat.base.UiContext;
import pers.kinson.wechat.database.SqliteDbUtil;
import pers.kinson.wechat.database.SqliteDdl;
import pers.kinson.wechat.logic.system.ApplicationEffect;
import pers.kinson.wechat.net.IOUtil;
import pers.kinson.wechat.ui.R;
import pers.kinson.wechat.ui.StageController;

import java.io.IOException;

public class ClientStartup extends Application {


    @Override
    public void init() throws Exception {
        Context.init();
    }

    @Override
    public void start(final Stage stage) throws IOException {
        // 本地数据库
        SqliteDdl.createDatabase();
        SqliteDbUtil.clearExpiredMessage();

        //与服务端建立连接
        connectToServer();
        StageController stageController = UiContext.stageController;
        stageController.setPrimaryStage("root", stage);

        Stage loginStage = stageController.loadStage(R.id.LoginView, R.layout.LoginView,
                StageStyle.UNDECORATED);
        loginStage.setTitle("QQ");

        stageController.loadStage(R.id.RegisterView, R.layout.RegisterView, StageStyle.UNDECORATED);
//        Stage mainStage = stageController.loadStage(R.id.MainView, R.layout.MainView, StageStyle.UTILITY);
        Stage mainStage = stageController.loadStage(R.id.MainView, R.layout.MainView, StageStyle.UNDECORATED);
        stageController.loadStage(R.id.PersonSettingView, R.layout.PersonSettingView, StageStyle.UNDECORATED);

        //把主界面放在右上方
        Screen screen = Screen.getPrimary();
        double rightTopX = screen.getVisualBounds().getWidth() * 0.75;
        double rightTopY = screen.getVisualBounds().getHeight() * 0.05;
        mainStage.setX(rightTopX);
        mainStage.setY(rightTopY);

        stageController.loadStage(R.id.ChatToPoint, R.layout.ChatToPoint, StageStyle.UTILITY);
        stageController.loadStage(R.id.CreateDiscussion, R.layout.CreateDiscussion, StageStyle.UTILITY);
        stageController.loadStage(R.id.DiscussionGroup, R.layout.DiscussionGroup, StageStyle.UTILITY);

        stageController.loadStage(R.id.SearchView, R.layout.SearchFriendView,
                StageStyle.UTILITY);

        //显示MainView舞台
        stageController.setStage(R.id.LoginView);

        mainStage.setTitle("wechat");

        ApplicationEffect.setNormalIcons(mainStage);
        ApplicationEffect.setNormalIcons(loginStage);
        ApplicationEffect.registerEffect(mainStage);
    }

    private void connectToServer() {
        new Thread() {
            @SneakyThrows
            public void run() {
                IOUtil.init();
            }

        }.start();
    }

    public static void main(String[] args) {
        launch();
    }
}
