package pers.kinson.wechat.logic.user.message.req;

import pers.kinson.wechat.base.Constants;
import pers.kinson.wechat.net.message.AbstractPacket;
import pers.kinson.wechat.net.message.PacketType;

import io.netty.buffer.ByteBuf;

public class ReqUserRegister extends AbstractPacket {

	private long userId;
	/** 性别{@link Constants#SEX_OF_BOY} */
	private byte sex;

	private String nickName;

	private String password;

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public byte getSex() {
		return sex;
	}

	public void setSex(byte sex) {
		this.sex = sex;
	}

	@Override
	public void writeBody(ByteBuf buf) {
		buf.writeLong(userId);
		buf.writeByte(sex);
		writeUTF8(buf, nickName);
		writeUTF8(buf, password);
	}

	@Override
	public void readBody(ByteBuf buf) {
		this.userId = buf.readLong();
		this.sex = buf.readByte();
		this.nickName = readUTF8(buf);
		this.password = readUTF8(buf);
	}

	@Override
	public PacketType getPacketType() {
		return PacketType.ReqUserRegister;
	}

	@Override
	public void execPacket() {
		// TODO Auto-generated method stub
	}

}
