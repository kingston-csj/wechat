package pers.kinson.wechat.logic.login.message.res;

import pers.kinson.wechat.base.SessionManager;
import pers.kinson.wechat.logic.login.message.req.ReqHeartBeat;
import pers.kinson.wechat.net.message.AbstractPacket;
import pers.kinson.wechat.net.message.PacketType;

public class ResHeartBeat extends AbstractPacket {

	@Override
	public PacketType getPacketType() {
		return PacketType.ResHeartBeat;
	}

	@Override
	public void execPacket() {
		System.err.println("收到服务端的ping包，回复pong包");
		SessionManager.INSTANCE.sendMessage(new ReqHeartBeat());
	}

}
