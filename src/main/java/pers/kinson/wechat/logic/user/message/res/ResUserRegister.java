package pers.kinson.wechat.logic.user.message.res;

import io.netty.buffer.ByteBuf;
import pers.kinson.wechat.net.CmdConst;
import pers.kinson.wechat.net.message.AbstractPacket;

public class ResUserRegister extends AbstractPacket {
	
	private byte resultCode;
	
	private String message;
	
	public byte getResultCode() {
		return resultCode;
	}

	public void setResultCode(byte resultCode) {
		this.resultCode = resultCode;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public void writeBody(ByteBuf buf) {
		buf.writeByte(resultCode);
		writeUTF8(buf, message);
	}

	@Override
	public void readBody(ByteBuf buf) {
		this.resultCode = buf.readByte();
		this.message = readUTF8(buf);
		
	}

	@Override
	public int getPacketType() {
		return CmdConst.ResUserRegister;
	}

	@Override
	public String toString() {
		return "ResUserRegisterPacket [resultCode=" + resultCode + ", message=" + message + "]";
	}
	
}
