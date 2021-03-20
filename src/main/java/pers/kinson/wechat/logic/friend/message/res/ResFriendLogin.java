package pers.kinson.wechat.logic.friend.message.res;

import io.netty.buffer.ByteBuf;
import pers.kinson.wechat.net.message.AbstractPacket;
import pers.kinson.wechat.net.message.PacketType;

/**
 * 好友登录
 * @author kinson
 */
public class ResFriendLogin extends AbstractPacket {

	private long friendId;

	public long getFriendId() {
		return friendId;
	}

	public void setFriendId(long friendId) {
		this.friendId = friendId;
	}

	@Override
	public PacketType getPacketType() {
		return PacketType.ResFriendLogin;
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
