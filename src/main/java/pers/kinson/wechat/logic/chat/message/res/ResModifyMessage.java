package pers.kinson.wechat.logic.chat.message.res;

import jforgame.socket.share.annotation.MessageMeta;
import lombok.Data;
import pers.kinson.wechat.logic.chat.message.vo.ChatMessage;
import pers.kinson.wechat.net.CmdConst;

@Data
@MessageMeta(cmd = CmdConst.ResModifyMessage)
public class ResModifyMessage {

    private byte channel;

    private long topic;

    private ChatMessage message;
}
