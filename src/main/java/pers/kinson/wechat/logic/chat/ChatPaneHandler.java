package pers.kinson.wechat.logic.chat;

import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import pers.kinson.wechat.logic.chat.model.ChatContact;

public interface ChatPaneHandler {

    Pane loadMessagePane();

    void onChatPaneShow(Parent root, ChatContact chatModel);
}
