package pers.kinson.wechat.logic.chat.handler;

import javafx.scene.layout.Pane;
import pers.kinson.wechat.logic.chat.message.vo.ChatMessage;

public interface MessageContentUiHandler {

    void display(Pane parent, ChatMessage message);

    byte type();

    /**
     * 对已经创建的消息进行更新
     *
     * @param parent
     * @param message
     */
    default void refresh(Pane parent, ChatMessage message) {
    }

}
