package pers.kinson.wechat.logic.chat.message.req;

import jforgame.socket.share.annotation.MessageMeta;
import lombok.Data;
import pers.kinson.wechat.net.CmdConst;

@Data
@MessageMeta(cmd = CmdConst.ReqChatToUser)
public class ReqChatToUser  {

	private long toUserId;

	private String content;

}
