package com.kingston.logic.friend.message;

import java.util.ArrayList;
import java.util.List;

import com.kingston.logic.friend.FriendManager;
import com.kingston.logic.friend.vo.FriendItemVo;
import com.kingston.net.message.AbstractPacket;
import com.kingston.net.message.PacketType;

import io.netty.buffer.ByteBuf;

public class ResFriendListPacket extends AbstractPacket {

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
