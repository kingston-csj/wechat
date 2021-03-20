package pers.kinson.wechat.logic.search.message.req;

import io.netty.buffer.ByteBuf;
import pers.kinson.wechat.net.message.AbstractPacket;
import pers.kinson.wechat.net.message.PacketType;

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

}
