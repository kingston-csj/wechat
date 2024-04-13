package pers.kinson.wechat.logic.friend.message.res;

import lombok.Data;
import pers.kinson.wechat.net.CmdConst;
import pers.kinson.wechat.net.message.AbstractPacket;

/**
 * 好友注销
 * @author kinson
 */
@Data
public class ResFriendLogout extends AbstractPacket {

	private long friendId;

	@Override
	public int getPacketType() {
		return CmdConst.ResFriendLogout;
	}

}
