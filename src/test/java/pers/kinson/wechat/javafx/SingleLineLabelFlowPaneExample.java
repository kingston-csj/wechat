package pers.kinson.wechat.javafx;

import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class SingleLineLabelFlowPaneExample extends Application {

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
            adjustLabels(flowPane, newWidth.doubleValue());
        });

        // 创建场景并显示
        Scene scene = new Scene(flowPane, 400, 300); // 设置场景的初始宽度和高度
        primaryStage.setTitle("Single Line Label FlowPane Example");
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
     * 动态调整FlowPane中的Label，确保每个Label只有一行
     */
    private void adjustLabels(FlowPane flowPane, double flowPaneWidth) {
        double currentX = 0; // 当前行的起始X坐标
        double rowHeight = 0; // 当前行的高度

        // 记录需要拆分的Label
        List<Runnable> splitTasks = new ArrayList<>();

        // 遍历FlowPane的所有子节点
        for (Node node : flowPane.getChildren()) {
            if (node instanceof Label) {
                Label label = (Label) node;
                label.setWrapText(false); // 禁用文本换行
                label.setMaxHeight(Label.USE_PREF_SIZE); // 限制Label高度为单行

                // 计算Label的文本宽度
                double textWidth = getTextWidth(label);

                // 检查当前行是否还能容纳Label
                if (currentX + textWidth > flowPaneWidth) {
                    // 如果当前行无法容纳，记录拆分任务
                    double availableWidth = flowPaneWidth - currentX;
                    splitTasks.add(() -> splitLabel(flowPane, label, availableWidth));
                    currentX = 0; // 重置当前行的X坐标
                    rowHeight = 0; // 重置当前行的高度
                }

                // 更新当前行的X坐标和高度
                currentX += textWidth + flowPane.getHgap();
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

        // 执行拆分任务
        for (Runnable task : splitTasks) {
            task.run();
        }
    }

    /**
     * 拆分Label，将超出的部分放到新的一行
     */
    private void splitLabel(FlowPane flowPane, Label label, double availableWidth) {
        String text = label.getText();
        double textWidth = getTextWidth(label);

        // 如果文本宽度超过可用宽度，拆分文本
        if (textWidth > availableWidth) {
            int splitIndex = findSplitIndex(label, availableWidth);
            String firstPart = text.substring(0, splitIndex);
            String secondPart = text.substring(splitIndex);

            // 更新当前Label的文本
            label.setText(firstPart);

            // 创建一个新的Label，存放剩余文本
            Label newLabel = new Label(secondPart);
            newLabel.setMaxHeight(Label.USE_PREF_SIZE); // 限制高度为单行

            // 将新的Label插入到FlowPane中
            int index = flowPane.getChildren().indexOf(label);
            flowPane.getChildren().add(index + 1, newLabel);
        }
    }

    /**
     * 计算Label的文本宽度
     */
    private double getTextWidth(Label label) {
        return label.getFont().getSize() * label.getText().length() * 0.6; // 估算文本宽度
    }

    /**
     * 找到文本的拆分位置
     */
    private int findSplitIndex(Label label, double availableWidth) {
        String text = label.getText();
        double currentWidth = 0;

        for (int i = 0; i < text.length(); i++) {
            currentWidth += label.getFont().getSize() * 0.6; // 估算每个字符的宽度
            if (currentWidth > availableWidth) {
                return i;
            }
        }

        return text.length();
    }

    public static void main(String[] args) {
        launch(args);
    }
}