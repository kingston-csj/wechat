package pers.kinson.wechat.logic.chat.message.req;

import lombok.Data;
import pers.kinson.wechat.net.CmdConst;
import pers.kinson.wechat.net.message.AbstractPacket;

@Data
public class ReqChatToUser extends AbstractPacket {

	private long toUserId;

	private String content;

	@Override
	public int getPacketType() {
		return CmdConst.ReqChatToUser;
	}

}
