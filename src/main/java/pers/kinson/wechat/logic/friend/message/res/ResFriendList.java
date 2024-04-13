package pers.kinson.wechat.logic.friend.message.res;

import lombok.Data;
import pers.kinson.wechat.logic.friend.message.vo.FriendItemVo;
import pers.kinson.wechat.net.CmdConst;
import pers.kinson.wechat.net.message.AbstractPacket;

import java.util.List;

@Data
public class ResFriendList extends AbstractPacket {

	private List<FriendItemVo> friends;

	@Override
	public int getPacketType() {
		return CmdConst.ResFriendList;
	}

}
