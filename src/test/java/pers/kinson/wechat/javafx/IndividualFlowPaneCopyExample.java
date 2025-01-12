package pers.kinson.wechat.javafx;

import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class IndividualFlowPaneCopyExample extends Application {

    @Override
    public void start(Stage primaryStage) {
        // 创建一个Pane作为根容器
        Pane root = new Pane();

        // 创建多个FlowPane
        FlowPane flowPane1 = createFlowPane("FlowPane 1");
        FlowPane flowPane2 = createFlowPane("FlowPane 2");
        FlowPane flowPane3 = createFlowPane("FlowPane 3");

        // 将FlowPane添加到根容器中
        VBox container = new VBox(10); // 使用VBox布局
        container.getChildren().addAll(flowPane1, flowPane2, flowPane3);
        root.getChildren().add(container);

        // 创建场景并显示
        Scene scene = new Scene(root, 400, 300);
        primaryStage.setTitle("Individual FlowPane Copy Example");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * 创建一个FlowPane并添加内容
     */
    private FlowPane createFlowPane(String name) {
        FlowPane flowPane = new FlowPane();
        flowPane.setHgap(10); // 设置水平间距
        flowPane.setVgap(10); // 设置垂直间距

        // 添加混合内容（Label和Image）
        flowPane.getChildren().addAll(
                new Label(name + " - 这是一个较短的文本。"),
                createImageView("http://3.95.236.112:8085/database/avatar/2.jpg"),
                new Label(name + " - 这是一个非常长的文本，我们希望它能够自动换行，以便在FlowPane中正确显示。"),
                createImageView("http://3.95.236.112:8085/database/avatar/2.jpg")
        );

        // 设置FlowPane可聚焦
        flowPane.setFocusTraversable(true);

        // 为每个FlowPane单独设置键盘事件监听器
        flowPane.setOnKeyPressed(event -> handleKeyPress(event, flowPane));

        return flowPane;
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
     * 处理键盘事件
     */
    private void handleKeyPress(KeyEvent event, FlowPane flowPane) {
        // 检查是否按下了Ctrl+C
        if (event.isControlDown() && event.getCode() == KeyCode.C) {
            // 获取FlowPane的内容
            String content = getFlowPaneContent(flowPane);

            // 将内容复制到剪贴板
            copyToClipboard(content);
            System.out.println("Copied from " + flowPane.getId() + ": " + content); // 打印复制的内容（可选）
        }
    }

    /**
     * 获取FlowPane的内容
     */
    private String getFlowPaneContent(FlowPane flowPane) {
        StringBuilder content = new StringBuilder();

        // 遍历FlowPane的所有子节点
        for (Node node : flowPane.getChildren()) {
            if (node instanceof Label) {
                // 如果是Label，追加文本
                content.append(((Label) node).getText()).append("\n");
            } else if (node instanceof ImageView) {
                // 如果是ImageView，追加图片URL（如果有）
                ImageView imageView = (ImageView) node;
                Image image = imageView.getImage();
                if (image != null) {
//                    content.append("Image: ").append(image.impl_getUrl()).append("\n");
                }
            }
        }

        return content.toString();
    }

    /**
     * 将内容复制到剪贴板
     */
    private void copyToClipboard(String content) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent clipboardContent = new ClipboardContent();
        clipboardContent.putString(content); // 将内容放入剪贴板
        clipboard.setContent(clipboardContent);
    }

    public static void main(String[] args) {
        launch(args);
    }
}