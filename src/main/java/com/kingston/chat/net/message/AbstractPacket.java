package com.kingston.chat.net.message;

public abstract class AbstractPacket extends ByteBufBean {

	abstract public PacketType  getPacketType();

	abstract public void execPacket();


	/**
	 *  是否开启gzip压缩(默认关闭)
	 *  消息体数据大的时候才开启，非常小的包压缩后体积反而变大，而且耗时
	 */
	public boolean isUseCompression() {
		return false;
	}


}
