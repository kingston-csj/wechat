import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class SimpleIconApp extends Application {
    private List<Image> icons = new ArrayList<>();
    private Image normalIcon;
    private Image newMessageIcon;

    private boolean effect = true;

    @Override
    public void start(Stage primaryStage) {
        // 加载正常图标
        normalIcon = new Image("/assets/img/icon/icon.jpg");
        // 加载有新消息图标
        newMessageIcon = new Image("/assets/img/icon/icon_gray.jpg");
        // 初始化图标列表，先添加正常图标
        icons.add(normalIcon);

        // 设置窗口的图标列表
        primaryStage.getIcons().setAll(icons);

        VBox layout = new VBox();
        Scene scene = new Scene(layout, 300, 200);
        primaryStage.setScene(scene);
        primaryStage.setTitle("可切换图标的舞台");
        primaryStage.show();
        Timeline blinkTimer = new Timeline(new KeyFrame(Duration.seconds(0.8), event -> {
            // 这里可以修改任务栏图标相关的视觉提示，例如在Windows上可以尝试修改应用的通知状态
            // 这里只是简单地打印消息作为示例
            Platform.runLater(() -> {
                // 模拟有新消息到来，切换图标
                simulateNewMessage(primaryStage);
                effect = !effect;
            });
        }));
        blinkTimer.setCycleCount(Timeline.INDEFINITE);
        blinkTimer.play();
    }

    private void simulateNewMessage(Stage primaryStage) {
        // 清空原来的图标列表
        icons.clear();
        if (effect) {
            // 添加有新消息图标
            icons.add(newMessageIcon);
        } else {
            icons.add(normalIcon);
        }

        // 设置新的图标列表
        primaryStage.getIcons().setAll(icons);
    }

    public static void main(String[] args) {
        launch(args);
    }
}