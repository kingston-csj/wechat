package pers.kinson.wechat.javafx;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Random;

public class StageShakeApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        Button shakeButton = new Button("抖动窗口");
        shakeButton.setOnAction(event -> {
            shakeStageWithTimeline(primaryStage);
        });

        VBox layout = new VBox(shakeButton);
        Scene scene = new Scene(layout, 300, 200);
        primaryStage.setScene(scene);
        primaryStage.setTitle("可抖动的窗口（使用Timeline）");
        primaryStage.show();
    }

    private void shakeStageWithTimeline(Stage stage) {
        // 定义抖动的幅度范围（可以根据需要调整）
        int shakeRange = 50;
        // 定义抖动的次数（可以根据需要调整）
        int shakeCount = 10;
        // 获取窗口当前位置
        double originalX = stage.getX();
        double originalY = stage.getY();

        Random random = new Random();

        Timeline timeline = new Timeline();

        for (int i = 0; i < shakeCount; i++) {
            // 随机生成新的位置偏移量
            int offsetX = random.nextInt(shakeRange * 2) - shakeRange;
            int offsetY = random.nextInt(shakeRange * 2) - shakeRange;

            // 创建KeyFrame来设置每次位置变化
            KeyFrame keyFrame = new KeyFrame(
                    Duration.seconds(0.08),  // 每次位置变化的时间间隔，可根据需要调整
                    event -> {
                        stage.setX(originalX + offsetX);
                        stage.setY(originalY + offsetY);
                    }
            );

            timeline.getKeyFrames().add(keyFrame);
        }

        // 设置动画循环播放一次
        timeline.setCycleCount(1);

        // 动画播放完毕后，将窗口位置恢复到原始位置
        timeline.setOnFinished(event -> {
            stage.setX(originalX);
            stage.setY(originalY);
        });

        // 启动动画
        timeline.play();
    }

    public static void main(String[] args) {
        launch(args);
    }
}