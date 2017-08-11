package com.kingston.logic.login.message;

import io.netty.buffer.ByteBuf;

import com.kingston.base.ServerManager;
import com.kingston.net.message.AbstractPacket;
import com.kingston.net.message.PacketType;

public class RespHeartBeatPacket extends AbstractPacket{

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
		return PacketType.RespHeartBeat;
	}

	@Override
	public void execPacket() {
		System.err.println("收到服务端的ping包，回复pong包");  
		ServerManager.INSTANCE.sendServerRequest(new ReqHeartBeatPacket());  
	}

}
