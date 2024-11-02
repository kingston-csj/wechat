package pers.kinson.wechat.net;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pers.kinson.wechat.base.LifeCycle;
import pers.kinson.wechat.base.UiContext;

import java.util.HashMap;
import java.util.Map;

public class MessageRouter implements LifeCycle {

    @Override
    public void init() {
    }

    private Logger logger = LoggerFactory.getLogger(getClass().getName());

    private Map<Integer, MessageHandler> handlers = new HashMap<>();

    public void registerHandler(int msgId, MessageHandler handler) {
        MessageHandler prev = handlers.put(msgId, handler);
        if (prev != null) {
            throw new IllegalArgumentException("消息路由重复注册, id = " + msgId);
        }
    }

    public void execPacket(Object pact) {
        if (pact == null) return;
        try {
            int cmd = IOUtil.messageFactory.getMessageId(pact.getClass());
            MessageHandler handler = handlers.get(cmd);
            if (handler == null) {
                logger.error("消息路由[{}]未注册", pact.getClass().getSimpleName());
            }
            // 基本都是ui操作，直接切ui线程吧
               UiContext.runTaskInFxThread(() -> {
                handler.action(pact);
            });
        } catch (Exception e) {
            logger.error("", e);
        }
    }

}
