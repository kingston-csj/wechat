package pers.kinson.wechat.logic.chat.message.res;

import io.netty.buffer.ByteBuf;
import pers.kinson.wechat.net.message.AbstractPacket;
import pers.kinson.wechat.net.message.PacketType;

public class ResChatToUser extends AbstractPacket {

	private long fromUserId;

	private String content;

	@Override
	public void writeBody(ByteBuf buf) {
		buf.writeLong(fromUserId);
		writeUTF8(buf, content);
	}

	@Override
	public void readBody(ByteBuf buf) {
		this.fromUserId = buf.readLong();
		this.content = readUTF8(buf);
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public long getFromUserId() {
		return fromUserId;
	}

	public void setFromUserId(long fromUserId) {
		this.fromUserId = fromUserId;
	}

	@Override
	public PacketType getPacketType() {
		return PacketType.ResChatToUser;
	}

}
