package com.kingston.base;

import com.kingston.net.IoSession;
import com.kingston.net.message.Packet;

import io.netty.channel.Channel;
import javafx.application.Platform;

public enum ServerManager {
	
	INSTANCE;
	
	private IoSession session;
	
	public void registerSession(Channel channel) {
		this.session = new IoSession(channel);
	}

	public void sendServerRequest(Packet request){
		this.session.sendPacket(request);
	}
	
	/**
	 * 将任务转移给fxapplication线程延迟执行
	 * @param task
	 */
	public void FXApplicationThreadExcute(Runnable task){
		Platform.runLater(task);
	}
	
}
