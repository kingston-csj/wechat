package pers.kinson.wechat.logic.login.message.req;

import io.netty.buffer.ByteBuf;
import pers.kinson.wechat.net.message.AbstractPacket;
import pers.kinson.wechat.net.message.PacketType;

public class ReqUserLogin extends AbstractPacket {

	private long userId;
	private String userPwd;

	@Override
	public void writeBody(ByteBuf buf) {
		buf.writeLong(userId);
		writeUTF8(buf, userPwd);
	}

	@Override
	public void readBody(ByteBuf buf) {
		this.userId  = buf.readLong();
		this.userPwd = readUTF8(buf);

		System.err.println("id="+userId+",pwd="+userPwd);
	}

	@Override
	public PacketType getPacketType() {
		return PacketType.ReqUserLogin;
	}

	public String getUserPwd() {
		return userPwd;
	}

	public void setUserPwd(String userPwd) {
		this.userPwd = userPwd;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

}
