package pers.kinson.wechat.logic.chat.message.req;

import pers.kinson.wechat.net.message.AbstractPacket;
import pers.kinson.wechat.net.message.PacketType;

import io.netty.buffer.ByteBuf;

public class ReqChatToGroup extends AbstractPacket {
	
	private long toUserId;
	
	private String content;
	
	public long getToUserId() {
		return toUserId;
	}

	public void setToUserId(long toUserId) {
		this.toUserId = toUserId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public void writeBody(ByteBuf buf) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void readBody(ByteBuf buf) {
		this.toUserId = buf.readLong();
		this.content = readUTF8(buf);
		
	}

	@Override
	public PacketType getPacketType() {
		return PacketType.ReqChatToGroup;
	}

	@Override
	public void execPacket() {
	}
	

}
