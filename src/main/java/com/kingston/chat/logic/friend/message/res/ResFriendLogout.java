package com.kingston.chat.logic.friend.message.res;

import com.kingston.chat.logic.friend.FriendManager;
import com.kingston.chat.net.message.AbstractPacket;
import com.kingston.chat.net.message.PacketType;

import io.netty.buffer.ByteBuf;

/**
 * 好友注销
 * @author kingston
 */
public class ResFriendLogout extends AbstractPacket {

	private long friendId;

	public long getFriendId() {
		return friendId;
	}

	public void setFriendId(long friendId) {
		this.friendId = friendId;
	}

	@Override
	public PacketType getPacketType() {
		return PacketType.ResFriendLogout;
	}

	@Override
	public void execPacket() {
		FriendManager.getInstance().onFriendLogout(this.friendId);
	}

	@Override
	public void writeBody(ByteBuf buf) {
		buf.writeLong(friendId);
	}

	@Override
	public void readBody(ByteBuf buf) {
		this.friendId = buf.readLong();
	}

}
