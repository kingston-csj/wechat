package pers.kinson.wechat.logic.chat.message.res;

import pers.kinson.wechat.net.message.AbstractPacket;
import pers.kinson.wechat.net.message.PacketType;

public class ResChatToGroup extends AbstractPacket {

	private String content;

	@Override
	public PacketType getPacketType() {
		return PacketType.ResChatToUser;
	}

	@Override
	public void execPacket() {
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
