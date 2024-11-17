package pers.kinson.wechat.logic.chat.struct;

import lombok.Data;

@Data
public class MessageContent {

    private byte type;

    private String content;
}
