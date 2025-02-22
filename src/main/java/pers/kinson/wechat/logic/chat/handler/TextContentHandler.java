package pers.kinson.wechat.logic.chat.handler;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import lombok.extern.slf4j.Slf4j;
import pers.kinson.wechat.base.MessageContentType;
import pers.kinson.wechat.logic.chat.message.vo.ChatMessage;
import pers.kinson.wechat.logic.chat.struct.ContentElemNode;
import pers.kinson.wechat.logic.chat.struct.TextMessageContent;
import pers.kinson.wechat.logic.file.ClipboardUtil;
import pers.kinson.wechat.util.MessageTextUiEditor;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
public class TextContentHandler implements MessageContentUiHandler {

    @Override
    public void display(Pane parent, ChatMessage message) {
        FlowPane flowPane = (FlowPane) parent;
        flowPane.setHgap(10); // 设置水平间距
        flowPane.setVgap(10); // 设置垂直间距

        TextMessageContent textMessageContent = (TextMessageContent) message.getMessageContent();
        List<ContentElemNode> nodes = MessageTextUiEditor.parseMessage(textMessageContent.getContent());
        for (ContentElemNode node : nodes) {
            try {
                parent.getChildren().add(node.toUi());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        flowPane.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/chat/css/chat_item.css")).toExternalForm());
        // 监听FlowPane的焦点变化
        flowPane.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                // 聚焦时添加样式类
                flowPane.getStyleClass().add("focused-flowpane");
                flowPane.getStyleClass().remove("unfocused-flowpane");
            } else {
                // 失去焦点时移除样式类
                flowPane.getStyleClass().remove("focused-flowpane");
                flowPane.getStyleClass().add("unfocused-flowpane");
            }
        });
        // 监听FlowPane的宽度变化，动态调整Label的宽度
        // 如果文本/图片超过flowPane的宽度，则自动切行
        flowPane.widthProperty().addListener((obs, oldWidth, newWidth) -> {
            // 没有这段代码，如果是纯文本的话，没法切行，因为newWidth超过flowPane的最大宽度了， 不知道什么原因。。。
            int maxWidth = 480;
            if (newWidth.doubleValue() > maxWidth) {
                flowPane.setMaxWidth(maxWidth); // 强制限制宽度
            }
            adjustLabels(flowPane, Math.min(newWidth.doubleValue(), maxWidth));
        });

        // 确保每一个flowpane都能聚焦
        flowPane.setFocusTraversable(true);
        // 为FlowPane设置鼠标点击事件，确保点击时获取焦点。这句代码是必须的！
        flowPane.setOnMouseClicked(event -> flowPane.requestFocus());

        flowPane.setOnKeyPressed(event -> handleKeyPress(event, flowPane));
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
            ClipboardUtil.copyToClipboard(content);
//            System.out.println("Copied from " + flowPane.getId() + ": " + content); // 打印复制的内容
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
                    try {
                        // image.impl_getUrl()方法无法编译成功，只能用反射
                        Field field = Image.class.getDeclaredField("url");
                        field.setAccessible(true);
                        content.append("Image: ").append(field.get(image)).append("\n");
                    } catch (Exception e) {
                        log.error("", e);
                    }
                }
            }
        }

        return content.toString();
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
                // 设置Label的最大宽度为当前行的剩余宽度
                double availableWidth = flowPaneWidth - currentX;
                label.setMaxWidth(availableWidth);

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
            newLabel.setMaxWidth(flowPane.getWidth()); // 设置最大宽度为FlowPane的宽度

            // 将新的Label插入到FlowPane中
            int index = flowPane.getChildren().indexOf(label);
            flowPane.getChildren().add(index + 1, newLabel);
        }
    }

    /**
     * 计算Label的文本宽度
     */
    private double getTextWidth(Label label) {
        Text text = new Text(label.getText());
        text.setFont(label.getFont());
        return text.getLayoutBounds().getWidth();
    }

    /**
     * 找到文本的拆分位置
     */
    private int findSplitIndex(Label label, double availableWidth) {
        String text = label.getText();
        double currentWidth = 0;

        for (int i = 0; i < text.length(); i++) {
            Text tempText = new Text(text.substring(0, i + 1));
            tempText.setFont(label.getFont());
            currentWidth = tempText.getLayoutBounds().getWidth();

            if (currentWidth > availableWidth) {
                return i; // 返回拆分位置
            }
        }

        return text.length(); // 如果不需要拆分，返回文本长度
    }

    @Override
    public byte type() {
        return MessageContentType.TEXT;
    }

}
