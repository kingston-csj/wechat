package pers.kinson.wechat.logic.chat.message.res;

import jforgame.socket.share.annotation.MessageMeta;
import lombok.Data;
import pers.kinson.wechat.logic.chat.message.vo.ChatMessage;
import pers.kinson.wechat.net.CmdConst;

import java.util.List;

@Data
@MessageMeta(cmd = CmdConst.ResNewMessage)
public class ResNewMessage {

    private byte channel;

    private long topic;

    private List<ChatMessage> messages;
}
