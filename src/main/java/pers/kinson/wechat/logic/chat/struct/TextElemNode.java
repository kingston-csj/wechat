package pers.kinson.wechat.logic.chat.struct;

import javafx.scene.Node;
import javafx.scene.control.Label;

public class TextElemNode implements ContentElemNode {

    private String content;

    public TextElemNode(String content) {
        this.content = content;
    }

    @Override
    public Node toUi() {
//        // 这里宽度与图片之间总是有一段空隙，不知道怎么调，丑是丑点，总比不能复制文本好吧
//        TextField textField = new TextField(content);
//        textField.setEditable(false); // 设置为true以允许编辑文本
//        textField.setMouseTransparent(false);
//        textField.setFocusTraversable(true);
//        textField.setStyle("-fx-background-color: transparent;" +
//                "-fx-border-color: transparent;" +
//                "-fx-padding: 0;  ");
//
//        return textField;
        Label label = new Label (content);
        label.setMouseTransparent(false);
        label.setFocusTraversable(true);
        label.setWrapText(true);
        return label;
    }
}