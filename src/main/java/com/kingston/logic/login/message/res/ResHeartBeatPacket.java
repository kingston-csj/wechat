package com.kingston.logic.login.message.res;

import io.netty.buffer.ByteBuf;

import com.kingston.base.IoBaseService;
import com.kingston.logic.login.message.req.ReqHeartBeatPacket;
import com.kingston.net.message.AbstractPacket;
import com.kingston.net.message.PacketType;

public class ResHeartBeatPacket extends AbstractPacket{

	@Override
	public void writeBody(ByteBuf buf) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void readBody(ByteBuf buf) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public PacketType getPacketType() {
		return PacketType.ResHeartBeat;
	}

	@Override
	public void execPacket() {
		System.err.println("收到服务端的ping包，回复pong包");  
		IoBaseService.INSTANCE.sendServerRequest(new ReqHeartBeatPacket());  
	}

}
