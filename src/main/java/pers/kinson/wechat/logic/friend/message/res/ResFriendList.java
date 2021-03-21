package pers.kinson.wechat.logic.friend.message.res;

import io.netty.buffer.ByteBuf;
import pers.kinson.wechat.logic.friend.message.vo.FriendItemVo;
import pers.kinson.wechat.net.CmdConst;
import pers.kinson.wechat.net.message.AbstractPacket;

import java.util.ArrayList;
import java.util.List;

public class ResFriendList extends AbstractPacket {

	private List<FriendItemVo> friends;

	@Override
	public int getPacketType() {
		return CmdConst.ResFriendList;
	}

	public List<FriendItemVo> getFriends() {
		return friends;
	}

	public void setFriends(List<FriendItemVo> friends) {
		this.friends = friends;
	}

	@Override
	public void writeBody(ByteBuf buf) {
		buf.writeInt(friends.size());
		for (FriendItemVo item:friends) {
			item.writeBody(buf);
		}
	}

	@Override
	public void readBody(ByteBuf buf) {
		int size = buf.readInt();
		this.friends = new ArrayList<>(size);
		for (int i=0;i<size;i++) {
			FriendItemVo item = new FriendItemVo();
			item.readBody(buf);
			friends.add(item);
		}
	}

}
