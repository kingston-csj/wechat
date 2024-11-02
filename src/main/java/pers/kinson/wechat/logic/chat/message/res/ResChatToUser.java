package pers.kinson.wechat.logic.chat.message.res;

import jforgame.socket.share.annotation.MessageMeta;
import lombok.Data;
import pers.kinson.wechat.net.CmdConst;

@Data
@MessageMeta(cmd = CmdConst.ResChatToUser)
public class ResChatToUser  {

	private Long fromUserId;

	private Long toUserId;

	private String content;

}
