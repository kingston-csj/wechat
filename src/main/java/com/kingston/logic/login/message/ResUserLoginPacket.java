package com.kingston.logic.login.message;

import io.netty.buffer.ByteBuf;

import com.kingston.logic.login.LoginManager;
import com.kingston.net.message.AbstractPacket;
import com.kingston.net.message.PacketType;

public class ResUserLoginPacket extends AbstractPacket{

	private String alertMsg;
	private byte isValid;
	
	@Override
	public void writeBody(ByteBuf buf) {
		writeUTF8(buf, alertMsg);
		buf.writeByte(isValid);
	}

	@Override
	public void readBody(ByteBuf buf) {
		this.alertMsg = readUTF8(buf);
		this.isValid = buf.readByte();
	}

	@Override
	public PacketType getPacketType() {
		return PacketType.RespUserLogin;
	}

	@Override
	public void execPacket() {
		System.err.println("receive login "+ alertMsg);
		LoginManager.getInstance().handleLoginResponse(this);
	}

	public String getAlertMsg() {
		return alertMsg;
	}

	public void setAlertMsg(String alertMsg) {
		this.alertMsg = alertMsg;
	}

	public byte getIsValid() {
		return isValid;
	}

	public void setIsValid(byte isValid) {
		this.isValid = isValid;
	}

}
