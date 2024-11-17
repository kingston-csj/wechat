package pers.kinson.wechat.logic.chat.message.vo;

import lombok.Builder;
import lombok.Data;
import pers.kinson.wechat.logic.chat.struct.MessageContent;

@Data
@Builder
public class MessageVo {

    private Long fromId;

    private Long toId;

    private String content;

    private String date;

    private MessageContent messageContent;


}
