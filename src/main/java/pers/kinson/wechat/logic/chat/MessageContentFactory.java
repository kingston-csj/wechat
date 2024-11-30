package pers.kinson.wechat.logic.chat;

import javafx.scene.layout.Pane;
import jforgame.commons.ClassScanner;
import jforgame.commons.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import pers.kinson.wechat.base.LifeCycle;
import pers.kinson.wechat.base.MessageContentType;
import pers.kinson.wechat.logic.chat.handler.MessageContentUiHandler;
import pers.kinson.wechat.logic.chat.message.vo.ChatMessage;
import pers.kinson.wechat.logic.chat.struct.FileMessageContent;
import pers.kinson.wechat.logic.chat.struct.FileOnlineTransferMessageContent;
import pers.kinson.wechat.logic.chat.struct.ImageMessageContent;
import pers.kinson.wechat.logic.chat.struct.MessageContent;
import pers.kinson.wechat.logic.chat.struct.TextMessageContent;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
public class MessageContentFactory implements LifeCycle {


    Map<Byte, Class<? extends MessageContent>> mapper = new HashMap<>();

    Map<Byte, MessageContentUiHandler> handlers = new HashMap<>();

    @Override
    public void init() {
        mapper.put(MessageContentType.TEXT, TextMessageContent.class);
        mapper.put(MessageContentType.IMAGE, ImageMessageContent.class);
        mapper.put(MessageContentType.FILE, FileMessageContent.class);
        mapper.put(MessageContentType.ONLINE_TRANSFER, FileOnlineTransferMessageContent.class);

        Set<Class<?>> handlerClazzs = ClassScanner.listAllSubclasses("pers.kinson.wechat.logic.chat.handler", MessageContentUiHandler.class);
        handlerClazzs.forEach(c -> {
            try {
                MessageContentUiHandler handler = (MessageContentUiHandler) c.newInstance();
                handlers.put(handler.type(), handler);
            } catch (Exception e) {
                log.error("", e);
                System.exit(1);
            }
        });
    }

    public MessageContent parse(byte type, String json) {
        return JsonUtil.string2Object(json, mapper.get(type));
    }

    public void displayUi(byte type, Pane parent, ChatMessage message) {
        handlers.get(type).display(parent, message);
    }

    public void refreshItem(byte type, Pane parent, ChatMessage message) {
        handlers.get(type).refresh(parent, message);
    }
}
