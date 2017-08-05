package com.kingston.net;

import com.kingston.net.message.Packet;

import io.netty.channel.Channel;

public class IoSession {

	/** 网络连接channel */
	private Channel channel;

	private String ipAddr;

	private boolean reconnected;

	public IoSession() {

	}

	public IoSession(Channel channel) {
		this.channel = channel;
	}
	
	
	/**
	 * 向客户端发送消息
	 * @param packet
	 */
	public void sendPacket(Packet packet) {
		if (packet == null) {
			return;
		}
		if (channel != null) {
			channel.writeAndFlush(packet);
		}
	}

}
