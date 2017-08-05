package com.kingston.service.login;

import io.netty.buffer.ByteBuf;

import com.kingston.base.ServerManager;
import com.kingston.net.Packet;
import com.kingston.net.PacketType;

public class ClientHeartBeat extends Packet{

	@Override
	public void writePacketBody(ByteBuf buf) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void readPacketBody(ByteBuf buf) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public PacketType getPacketType() {
		return PacketType.ClientHeartBeat;
	}

	@Override
	public void execPacket() {
		System.err.println("收到服务端的ping包，回复pong包");  
		ServerManager.INSTANCE.sendServerRequest(new ServerHeartBeat());  
	}

}
