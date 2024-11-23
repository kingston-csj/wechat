package pers.kinson.wechat.logic.chat.handler;

import javafx.scene.layout.Pane;
import pers.kinson.wechat.logic.chat.message.vo.ChatMessage;

public interface MessageContentUiHandler {

    void display(Pane parent, ChatMessage message);

    byte type();
}
