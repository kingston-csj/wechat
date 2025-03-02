package pers.kinson.wechat.logic.chat.struct;

import javafx.scene.Node;
import javafx.scene.image.ImageView;
import pers.kinson.wechat.base.Context;
import pers.kinson.wechat.logic.chat.message.vo.EmojiVo;
import pers.kinson.wechat.logic.system.EmojiCache;

public class EmojiElemNode implements ContentElemNode {

    private String label;

    public EmojiElemNode(String label) {
        this.label = label;
    }

    @Override
    public Node toUi() {
        EmojiVo target = EmojiCache.getEmojiVoMap().get(label);
        ImageView imageView = new ImageView(target.getImage());
        imageView.setFitWidth(30);
        imageView.setFitHeight(30);
        imageView.setStyle("-fx-background-color: transparent;" +
                "-fx-border-color: transparent;" +
                "-fx-padding: 0;");
        return imageView;
    }
}