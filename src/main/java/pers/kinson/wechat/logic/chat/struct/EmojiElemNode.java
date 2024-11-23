package pers.kinson.wechat.logic.chat.struct;

import javafx.scene.Node;
import javafx.scene.image.ImageView;

public class EmojiElemNode implements ContentElemNode {

    private String url;

    public EmojiElemNode(String url) {
        this.url = url;
    }

    @Override
    public Node toUi() {
        ImageView imageView = new ImageView(url);
        imageView.setFitWidth(30);
        imageView.setFitHeight(30);
        imageView.setStyle("-fx-background-color: transparent;" +
                "-fx-border-color: transparent;" +
                "-fx-padding: 0;");
        return imageView;
    }
}