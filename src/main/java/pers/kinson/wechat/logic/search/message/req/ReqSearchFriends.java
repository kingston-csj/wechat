package pers.kinson.wechat.logic.search.message.req;

import lombok.Data;
import pers.kinson.wechat.net.CmdConst;
import pers.kinson.wechat.net.message.AbstractPacket;

@Data
public class ReqSearchFriends extends AbstractPacket {

	/** 昵称或qq号 */
	private String key;

	@Override
	public int getPacketType() {
		return CmdConst.ReqSearchFriends;
	}

}
