package pers.kinson.wechat.logic.chat.struct;

import javafx.scene.Node;
import javafx.scene.text.Text;

public class TextElemNode implements ContentElemNode {

    private String content;

    public TextElemNode(String content) {
        this.content = content;
    }

    @Override
    public Node toUi() {
//        TextField text = new TextField(content);
//        text.setEditable(false); // 设置为true以允许编辑文本
//        text.setMouseTransparent(false);
//        text.setFocusTraversable(true);
//        text.setStyle("-fx-background-color: transparent;" +
//                "-fx-border-color: transparent;" +
//                "-fx-padding: 0;  ");
//        return text;
        Text label = new Text (content);
        label.setMouseTransparent(false);
        label.setFocusTraversable(true);
        return label;
    }
}