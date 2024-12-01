package pers.kinson.wechat.logic.system;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.util.Duration;
import lombok.Getter;
import lombok.Setter;

public class ApplicationEffect {

    @Getter
    @Setter
    private static boolean mewMessageEffect = false;

    private static boolean isBlinking = false;
    private static Timeline blinkTimer;

    private static String message;


    public static void registerEffect(Stage mainStage) {
        mainStage.iconifiedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                // 窗口最小化时开始闪烁效果
                startBlink(mainStage);
            } else {
                // 窗口恢复时停止闪烁效果
                stopBlink(mainStage);
            }
        });
    }


    private static boolean effect = true;

    private static void startBlink(Stage mainStage) {
        if (!isBlinking) {
            isBlinking = true;
            blinkTimer = new Timeline(new KeyFrame(Duration.seconds(0.5), event -> {
                // 这里可以修改任务栏图标相关的视觉提示，例如在Windows上可以尝试修改应用的通知状态
                // 这里只是简单地打印消息作为示例
                Platform.runLater(() -> {
                    if (effect) {
                        mainStage.setTitle(message);
                    } else {
                        mainStage.setTitle(" ");
                    }
                    effect = !effect;
                });
            }));
            blinkTimer.setCycleCount(Timeline.INDEFINITE);
            blinkTimer.play();
        }
    }

    private static void stopBlink(Stage mainStage) {
        if (isBlinking) {
            isBlinking = false;
            blinkTimer.stop();
            mainStage.setTitle("wechat");
        }
    }

    public static void setMessage(String message) {
        ApplicationEffect.message = message;
    }
}
