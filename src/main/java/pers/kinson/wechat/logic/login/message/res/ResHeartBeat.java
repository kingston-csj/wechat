package pers.kinson.wechat.logic.login.message.res;

import pers.kinson.wechat.net.message.AbstractPacket;
import pers.kinson.wechat.net.message.PacketType;

public class ResHeartBeat extends AbstractPacket {

	@Override
	public PacketType getPacketType() {
		return PacketType.ResHeartBeat;
	}

}
