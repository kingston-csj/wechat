package pers.kinson.wechat.logic.login.message.res;

import pers.kinson.wechat.net.CmdConst;
import pers.kinson.wechat.net.message.AbstractPacket;

public class ResHeartBeat extends AbstractPacket {

	@Override
	public int getPacketType() {
		return CmdConst.RespHeartBeat;
	}

}
