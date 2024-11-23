package pers.kinson.wechat.logic.chat.handler;

import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import pers.kinson.wechat.base.MessageContentType;
import pers.kinson.wechat.logic.chat.message.vo.ChatMessage;
import pers.kinson.wechat.logic.chat.struct.MediaMessageContent;

public class ImageContentHandler implements MessageContentUiHandler {

    @Override
    public void display(Pane parent, ChatMessage message) {
        MediaMessageContent mediaMessageContent = (MediaMessageContent) message.getContent();
        ImageView imageView = new ImageView(mediaMessageContent.getUrl());
        parent.getChildren().add(imageView);
    }

    @Override
    public byte type() {
        return MessageContentType.TYPE_IMAGE;
    }

}
