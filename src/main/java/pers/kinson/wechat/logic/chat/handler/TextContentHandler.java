package pers.kinson.wechat.logic.chat.handler;

import javafx.scene.layout.Pane;
import pers.kinson.wechat.base.MessageContentType;
import pers.kinson.wechat.util.MessageTextUiEditor;
import pers.kinson.wechat.logic.chat.message.vo.ChatMessage;
import pers.kinson.wechat.logic.chat.struct.ContentElemNode;
import pers.kinson.wechat.logic.chat.struct.TextMessageContent;

import java.util.List;

public class TextContentHandler implements MessageContentUiHandler {

    @Override
    public void display(Pane parent, ChatMessage message) {
        TextMessageContent textMessageContent = (TextMessageContent)message.getContent();
        List<ContentElemNode> nodes = MessageTextUiEditor.parseMessage(textMessageContent.getContent());
        for (ContentElemNode node : nodes) {
            parent.getChildren().add(node.toUi());
        }
    }

    @Override
    public byte type() {
        return MessageContentType.TYPE_TEXT;
    }

}
