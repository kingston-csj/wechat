package pers.kinson.wechat.logic.login.message.req;

import io.netty.buffer.ByteBuf;
import pers.kinson.wechat.net.message.AbstractPacket;
import pers.kinson.wechat.net.message.PacketType;

public class ReqHeartBeat extends AbstractPacket {

	@Override
	public void writeBody(ByteBuf buf) {
	}

	@Override
	public void readBody(ByteBuf buf) {
		
	}

	@Override
	public PacketType getPacketType() {
		return PacketType.ReqHeartBeat;
	}

}
