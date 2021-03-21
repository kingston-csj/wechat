package pers.kinson.wechat.logic.chat.message.res;

import pers.kinson.wechat.net.CmdConst;
import pers.kinson.wechat.net.message.AbstractPacket;

public class ResChatToGroup extends AbstractPacket {

	private String content;

	@Override
	public int getPacketType() {
		return CmdConst.ResChatToGroup;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
