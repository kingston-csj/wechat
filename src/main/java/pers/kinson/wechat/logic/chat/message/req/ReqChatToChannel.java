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

	private long toUserId;

	private byte contentType;
	
	private String content;
	
}
