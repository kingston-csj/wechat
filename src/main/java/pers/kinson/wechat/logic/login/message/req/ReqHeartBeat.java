package pers.kinson.wechat.logic.login.message.req;

import pers.kinson.wechat.net.CmdConst;
import pers.kinson.wechat.net.message.AbstractPacket;

public class ReqHeartBeat extends AbstractPacket {

	@Override
	public int getPacketType() {
		return CmdConst.ReqHeartBeat;
	}

}
