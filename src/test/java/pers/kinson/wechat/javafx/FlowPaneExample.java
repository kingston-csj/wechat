package pers.kinson.wechat.javafx;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

public class FlowPaneExample extends Application {

    @Override
    public void start(Stage primaryStage) {
        // 创建一个FlowPane
        FlowPane flowPane = new FlowPane();
        flowPane.setHgap(10); // 设置水平间距
        flowPane.setVgap(10); // 设置垂直间距

        // 创建一个Label并设置长文本
        Label label = new Label("这是一个非常长的文本，我们希望它能够自动换行，以便在FlowPane中正确显示。");
        label.setWrapText(true); // 启用文本换行

        // 创建一个Image并显示
        Image image = new Image("http://3.95.236.112:8085/database/avatar/2.jpg");
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(100); // 设置图片宽度
        imageView.setPreserveRatio(true); // 保持图片比例

        // 将Label和Image添加到FlowPane中
        flowPane.getChildren().addAll(label, imageView);

        // 监听FlowPane的宽度变化，动态调整Label的宽度
        flowPane.widthProperty().addListener((obs, oldWidth, newWidth) -> {
            // 计算可用宽度：FlowPane宽度 - 图片宽度 - 水平间距
            double availableWidth = newWidth.doubleValue() - imageView.getFitWidth() - flowPane.getHgap();
            label.setMaxWidth(availableWidth); // 设置Label的最大宽度
        });

        // 创建场景并显示
        Scene scene = new Scene(flowPane, 400, 200); // 设置场景的初始宽度和高度
        primaryStage.setTitle("FlowPane Auto Wrap Example");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}