package pers.kinson.wechat.logic.friend.message.res;

import java.util.ArrayList;
import java.util.List;

import pers.kinson.wechat.logic.friend.FriendManager;
import pers.kinson.wechat.logic.friend.message.vo.FriendItemVo;
import pers.kinson.wechat.net.message.AbstractPacket;
import pers.kinson.wechat.net.message.PacketType;

import io.netty.buffer.ByteBuf;

public class ResFriendList extends AbstractPacket {

	private List<FriendItemVo> friends;

	@Override
	public PacketType getPacketType() {
		return PacketType.ResFriendList;
	}

	public List<FriendItemVo> getFriends() {
		return friends;
	}

	public void setFriends(List<FriendItemVo> friends) {
		this.friends = friends;
	}

	@Override
	public void execPacket() {
		FriendManager.getInstance().receiveFriendsList(friends);

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
