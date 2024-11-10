package pers.kinson.wechat.logic.chat.message.req;

import jforgame.socket.share.annotation.MessageMeta;
import lombok.Data;
import pers.kinson.wechat.net.CmdConst;

@Data
@MessageMeta(cmd = CmdConst.ReqChatToGroup)
public class ReqChatToGroup  {

	/**
	 * 频道
	 */
	private byte channel;

	private long toUserId;
	
	private String content;
	
}
