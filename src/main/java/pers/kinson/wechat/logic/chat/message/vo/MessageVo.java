package pers.kinson.wechat.logic.chat.message.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MessageVo {

    private Long fromId;

    private Long toId;

    private String content;

    private String date;
}
