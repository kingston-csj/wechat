package com.kingston.logic.login.message.req;

import io.netty.buffer.ByteBuf;

import com.kingston.net.message.AbstractPacket;
import com.kingston.net.message.PacketType;

public class ReqHeartBeatPacket extends AbstractPacket{

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
