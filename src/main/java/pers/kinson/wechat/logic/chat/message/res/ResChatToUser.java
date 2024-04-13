package pers.kinson.wechat.logic.chat.message.res;

import lombok.Data;
import pers.kinson.wechat.net.CmdConst;
import pers.kinson.wechat.net.message.AbstractPacket;

@Data
public class ResChatToUser extends AbstractPacket {

	private long fromUserId;

	private String content;

	@Override
	public int getPacketType() {
		return CmdConst.ResChatToUser;
	}

}
