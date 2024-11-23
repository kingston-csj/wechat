package pers.kinson.wechat.logic.chat;

import jforgame.commons.JsonUtil;
import pers.kinson.wechat.base.LifeCycle;
import pers.kinson.wechat.logic.chat.struct.MessageContent;

import java.util.HashMap;
import java.util.Map;

public class MessageContentFactory implements LifeCycle {

    public final static byte TYPE_NORMAL = 0;

    Map<Byte, Class<MessageContent>> mapper = new HashMap<>();

    @Override
    public void init() {
        mapper.put(TYPE_NORMAL, MessageContent.class);
    }

    public MessageContent parse(byte type, String json) {
        return JsonUtil.string2Object(json, mapper.get(type));
    }
}
