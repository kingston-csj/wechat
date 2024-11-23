package pers.kinson.wechat.logic.chat.struct;

import lombok.Data;

@Data
public class MessageContent {

    /**
     * 冗余字段
     */
    private byte type;

    private String content;
}
