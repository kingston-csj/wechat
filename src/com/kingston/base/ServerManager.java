package com.kingston.base;

import javafx.application.Platform;

import com.kingston.entry.StartClient;
import com.kingston.net.Packet;

public enum ServerManager {
	INSTANCE;

	public void sendServerRequest(Packet request){
		BaseDataPool.channelContext.writeAndFlush(request);
	}
	
	/**
	 * 将任务转移给fxapplication线程延迟执行
	 * @param task
	 */
	public void FXApplicationThreadExcute(Runnable task){
		Platform.runLater(task);
	}
}
