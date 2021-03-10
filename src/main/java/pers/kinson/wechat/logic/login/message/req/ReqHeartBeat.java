package pers.kinson.wechat.logic.login.message.req;

import pers.kinson.wechat.net.message.AbstractPacket;
import pers.kinson.wechat.net.message.PacketType;

import io.netty.buffer.ByteBuf;

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

	@Override
	public void execPacket() {
		System.out.println("收到客户端的心跳回复");
	}

}
