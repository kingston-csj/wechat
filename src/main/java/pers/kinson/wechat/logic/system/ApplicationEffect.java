package pers.kinson.wechat.logic.system;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.util.Duration;
import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.List;

public class ApplicationEffect {

    @Getter
    @Setter
    private static boolean mewMessageEffect = false;

    private static boolean isBlinking = false;
    private static Timeline blinkTimer;

    private static List<Image> normalIcons = Collections.singletonList(new Image("/assets/img/icon/icon.jpg"));
    private static List<Image> warmIcons = Collections.singletonList(new Image("/assets/img/icon/icon_gray.jpg"));

    private static Stage mainStage;

    public static void registerEffect(Stage mainStage) {
        ApplicationEffect.mainStage = mainStage;
        mainStage.getIcons().setAll(normalIcons);
//        mainStage.iconifiedProperty().addListener((observable, oldValue, newValue) -> {
//            if (newValue) {
//                // 窗口最小化时开始闪烁效果
//                startBlink(mainStage);
//            } else {
//                // 窗口恢复时停止闪烁效果
//                stopBlink(mainStage);
//            }
//        });
    }


    private static boolean effect = true;

    public static void startBlink() {
        if (!isBlinking) {
            isBlinking = true;
            blinkTimer = new Timeline(new KeyFrame(Duration.seconds(0.8), event -> {
                // 这里可以修改任务栏图标相关的视觉提示，例如在Windows上可以尝试修改应用的通知状态
                // 这里只是简单地打印消息作为示例
                Platform.runLater(() -> {
                    if (effect) {
                        mainStage.getIcons().setAll(normalIcons);
                    } else {
                        mainStage.getIcons().setAll(warmIcons);
                    }
                    effect = !effect;
                });
            }));
            blinkTimer.setCycleCount(Timeline.INDEFINITE);
            blinkTimer.play();
        }
    }

    public static void stopBlink() {
        if (isBlinking) {
            isBlinking = false;
            blinkTimer.stop();
            mainStage.getIcons().setAll(normalIcons);
        }
    }

}
