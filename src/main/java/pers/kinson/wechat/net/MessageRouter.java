package pers.kinson.wechat.net;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pers.kinson.wechat.net.message.AbstractPacket;
import pers.kinson.wechat.net.message.IllegalPacketException;
import pers.kinson.wechat.net.message.PacketType;

import java.util.HashMap;
import java.util.Map;

public enum MessageRouter {

    INSTANCE;

    private Logger logger = LoggerFactory.getLogger(getClass().getName());

    private Map<Integer, MessageHandler> handlers = new HashMap<>();

    public void register(int msgId, MessageHandler handler) {
        MessageHandler prev = handlers.put(msgId, handler);
        if (prev != null) {
            throw new IllegalArgumentException("消息路由重复注册, id = " + msgId);
        }
    }

    public void execPacket(AbstractPacket pact) {
        if (pact == null) return;
        try {
            MessageHandler handler = handlers.get(pact.getPacketType().getType());
            if (handler == null) {
                logger.error("消息路由[{}]未注册", pact.getClass().getSimpleName());
            }
            handler.action(pact);
        } catch (Exception e) {
            logger.error("", e);
        }
    }

    public AbstractPacket createNewPacket(int packetType) {
        Class<? extends AbstractPacket> packetClass = PacketType.getPacketClassBy(packetType);
        if (packetClass == null) {
            throw new IllegalPacketException("类型为" + packetType + "的消息定义不存在");
        }
        AbstractPacket packet = null;
        try {
            packet = packetClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalPacketException("类型为" + packetType + "的消息实例化失败");
        }

        return packet;
    }

}
