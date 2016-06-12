package com.kingston.net.codec;

import com.kingston.net.Packet;
import com.kingston.net.PacketManager;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

public class PacketDecoder extends LengthFieldBasedFrameDecoder{

	public PacketDecoder(int maxFrameLength, int lengthFieldOffset,
			int lengthFieldLength, int lengthAdjustment,
			int initialBytesToStrip) {
		super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment,
				initialBytesToStrip);
	}
	
	public Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
		in = (ByteBuf) super.decode(ctx, in);
		if(in.readableBytes() <= 0) return null ;
		short packetType = in.readShort();
		Packet packet = PacketManager.createNewPacket(packetType);
		packet.readFromBuff(in);
		
		return packet;
	}


}
