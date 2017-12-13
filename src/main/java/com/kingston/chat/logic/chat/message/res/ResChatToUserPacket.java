package com.kingston.chat.logic.chat.message.res;

import com.kingston.chat.logic.chat.ChatManager;
import com.kingston.chat.net.message.AbstractPacket;
import com.kingston.chat.net.message.PacketType;

import io.netty.buffer.ByteBuf;

public class ResChatToUserPacket extends AbstractPacket {

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

	@Override
	public void execPacket() {
		ChatManager.getInstance().receiveFriendPrivateMessage(fromUserId, content);

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
