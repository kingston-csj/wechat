package pers.kinson.wechat.javafx;

import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

public class MixedContentFlowPaneExample extends Application {

    @Override
    public void start(Stage primaryStage) {
        // 创建一个FlowPane
        FlowPane flowPane = new FlowPane();
        flowPane.setHgap(10); // 设置水平间距
        flowPane.setVgap(10); // 设置垂直间距

        // 添加混合内容（Label和Image）
        flowPane.getChildren().addAll(
                new Label("这是一个较短的文本。"),
                createImageView("http://3.95.236.112:8085/database/avatar/2.jpg"),
                new Label("这是一个非常长的文本，我们希望它能够自动换行，以便在FlowPane中正确显示。"),
                createImageView("http://3.95.236.112:8085/database/avatar/2.jpg"),
                new Label("另一个短文本。"),
                createImageView("http://3.95.236.112:8085/database/avatar/2.jpg")
        );

        // 监听FlowPane的宽度变化，动态调整Label的宽度
        flowPane.widthProperty().addListener((obs, oldWidth, newWidth) -> {
            adjustLabelWidths(flowPane, newWidth.doubleValue());
        });

        // 创建场景并显示
        Scene scene = new Scene(flowPane, 400, 300); // 设置场景的初始宽度和高度
        primaryStage.setTitle("Mixed Content FlowPane Example");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * 创建一个ImageView
     */
    private ImageView createImageView(String imageUrl) {
        Image image = new Image(imageUrl);
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(100); // 设置图片宽度
        imageView.setPreserveRatio(true); // 保持图片比例
        return imageView;
    }

    /**
     * 动态调整FlowPane中所有Label的宽度
     */
    private void adjustLabelWidths(FlowPane flowPane, double flowPaneWidth) {
        double currentX = 0; // 当前行的起始X坐标
        double rowHeight = 0; // 当前行的高度

        for (Node node : flowPane.getChildren()) {
            if (node instanceof Label) {
                Label label = (Label) node;
                label.setWrapText(true); // 确保文本可以换行

                // 计算当前行的剩余宽度
                double remainingWidth = flowPaneWidth - currentX - flowPane.getHgap();
                if (remainingWidth < 0) {
                    remainingWidth = flowPaneWidth; // 如果当前行已满，换到下一行
                }

                // 设置Label的最大宽度为剩余宽度
                label.setMaxWidth(remainingWidth);

                // 更新当前行的X坐标和高度
                currentX += label.getBoundsInParent().getWidth() + flowPane.getHgap();
                rowHeight = Math.max(rowHeight, label.getBoundsInParent().getHeight());
            } else if (node instanceof ImageView) {
                ImageView imageView = (ImageView) node;

                // 检查当前行是否还能容纳图片
                if (currentX + imageView.getFitWidth() > flowPaneWidth) {
                    // 换到下一行
                    currentX = 0;
                    rowHeight = 0;
                }

                // 更新当前行的X坐标和高度
                currentX += imageView.getFitWidth() + flowPane.getHgap();
                rowHeight = Math.max(rowHeight, imageView.getBoundsInParent().getHeight());
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}