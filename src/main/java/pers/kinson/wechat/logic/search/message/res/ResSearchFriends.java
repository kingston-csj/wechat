package pers.kinson.wechat.logic.search.message.res;

import io.netty.buffer.ByteBuf;
import lombok.Data;
import pers.kinson.wechat.logic.search.model.RecommendFriendItem;
import pers.kinson.wechat.net.CmdConst;
import pers.kinson.wechat.net.message.AbstractPacket;

import java.util.ArrayList;
import java.util.List;

@Data
public class ResSearchFriends extends AbstractPacket {

	private List<RecommendFriendItem> friends;

	@Override
	public int getPacketType() {
		return CmdConst.ResSearchFriends;
	}

}
