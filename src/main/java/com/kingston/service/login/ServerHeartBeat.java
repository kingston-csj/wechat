package com.kingston.service.login;

import com.kingston.net.Packet;
import com.kingston.net.PacketType;

import io.netty.buffer.ByteBuf;

public class ServerHeartBeat extends Packet{

	@Override
	public void writePacketBody(ByteBuf buf) {
	}

	@Override
	public void readPacketBody(ByteBuf buf) {
		
	}

	@Override
	public PacketType getPacketType() {
		return PacketType.ServerHearBeat;
	}

	@Override
	public void execPacket() {
		System.out.println("收到客户端的心跳回复");
	}

}
