package pers.kinson.wechat.logic.search.message.req;

import pers.kinson.wechat.net.message.AbstractPacket;
import pers.kinson.wechat.net.message.PacketType;

import io.netty.buffer.ByteBuf;

public class ReqSearchFriends extends AbstractPacket {

	/** 昵称或qq号 */
	private String key;

	@Override
	public PacketType getPacketType() {
		return PacketType.ReqSearchFriends;
	}

	public void writeBody(ByteBuf buf) {
		writeUTF8(buf, key);
	}

	public void readBody(ByteBuf buf) {
		this.key = readUTF8(buf);
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	@Override
	public void execPacket() {
	}

}
