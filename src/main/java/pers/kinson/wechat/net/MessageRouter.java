package pers.kinson.wechat.net;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pers.kinson.wechat.base.LifeCycle;
import pers.kinson.wechat.net.message.AbstractPacket;
import pers.kinson.wechat.net.message.IllegalPacketException;
import pers.kinson.wechat.util.ClassScanner;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MessageRouter implements LifeCycle {

    @Override
    public void init()  {
        Set<Class<?>> clazzs = ClassScanner.listAllSubclasses("pers.kinson.wechat", AbstractPacket.class);
        clazzs.forEach(c -> {
            try {
                AbstractPacket pact = (AbstractPacket) c.newInstance();
                int id = pact.getPacketType();
                Class<?> prev = msgPool.put(id, (Class<? extends AbstractPacket>) c);
                if (prev != null) {
                    throw new IllegalStateException("消息重复 " + id);
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(-1);
            }
        });
    }

    private Logger logger = LoggerFactory.getLogger(getClass().getName());

    private Map<Integer, MessageHandler> handlers = new HashMap<>();
    private Map<Integer, Class<? extends AbstractPacket>> msgPool = new HashMap<>();

    public void registerHandler(int msgId, MessageHandler handler) {
        MessageHandler prev = handlers.put(msgId, handler);
        if (prev != null) {
            throw new IllegalArgumentException("消息路由重复注册, id = " + msgId);
        }
    }

    public void execPacket(AbstractPacket pact) {
        if (pact == null) return;
        try {
            MessageHandler handler = handlers.get(pact.getPacketType());
            if (handler == null) {
                logger.error("消息路由[{}]未注册", pact.getClass().getSimpleName());
            }
            handler.action(pact);
        } catch (Exception e) {
            logger.error("", e);
        }
    }

    public AbstractPacket createNewPacket(int packetType) {
        Class<? extends AbstractPacket> packetClass = msgPool.get(packetType);
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
