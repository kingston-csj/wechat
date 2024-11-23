package pers.kinson.wechat.logic.chat.message.req;

import jforgame.socket.share.annotation.MessageMeta;
import lombok.Data;
import pers.kinson.wechat.net.CmdConst;

@Data
@MessageMeta(cmd = CmdConst.ReqChatToChannel)
public class ReqChatToChannel {

	/**
	 * 频道
	 */
	private byte channel;

	/**
	 * 接收目标，可以是用户id或者讨论组id
	 */
	private long target;

	private byte contentType;
	
	private String content;
	
}
