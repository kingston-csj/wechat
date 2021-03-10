package pers.kinson.wechat.logic.friend.message.res;

import pers.kinson.wechat.logic.friend.FriendManager;
import pers.kinson.wechat.net.message.AbstractPacket;
import pers.kinson.wechat.net.message.PacketType;

import io.netty.buffer.ByteBuf;

/**
 * 好友注销
 * @author kinson
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
