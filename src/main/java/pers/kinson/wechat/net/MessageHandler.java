package pers.kinson.wechat.net;

import pers.kinson.wechat.net.message.AbstractPacket;

@FunctionalInterface
public interface MessageHandler<T extends AbstractPacket> {

     void action(T data);
}
