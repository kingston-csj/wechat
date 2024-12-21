package pers.kinson.wechat.logic.chat.handler;

import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import pers.kinson.wechat.base.MessageContentType;
import pers.kinson.wechat.logic.chat.message.vo.ChatMessage;
import pers.kinson.wechat.logic.chat.struct.ContentElemNode;
import pers.kinson.wechat.logic.chat.struct.TextMessageContent;
import pers.kinson.wechat.util.MessageTextUiEditor;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.List;

public class TextContentHandler implements MessageContentUiHandler {

    @Override
    public void display(Pane parent, ChatMessage message) {
        TextMessageContent textMessageContent = (TextMessageContent)message.getMessageContent();
        List<ContentElemNode> nodes = MessageTextUiEditor.parseMessage(textMessageContent.getContent());
        for (ContentElemNode node : nodes) {
            try {
                parent.getChildren().add(node.toUi());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // 这段代码没生效。。。
        parent.setOnKeyPressed(event -> {
            // 注册ctrl+c快捷键
            // 复制语文本到剪贴板
            if (event.isControlDown() && event.getCode() == KeyCode.C) {
                // 获取系统剪贴板
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

                // 将文本内容转换为可传输的数据格式（StringSelection）
                StringSelection stringSelection = new StringSelection(textMessageContent.getContent());
                // 将数据设置到剪贴板
                clipboard.setContents(stringSelection, null);
                System.out.println("文本已成功复制到剪贴板！");
            }
        });
    }

    @Override
    public byte type() {
        return MessageContentType.TEXT;
    }

}
