package com.kingston.base;

import com.kingston.net.IoSession;
import com.kingston.net.message.AbstractPacket;
import com.kingston.ui.StageController;

import io.netty.channel.Channel;
import javafx.application.Platform;

/**
 * 提供一些基础服务接口
 * @author kingston
 */
public enum ClientBaseService {
	
	INSTANCE;
	
	private StageController stageController = new StageController();
	/** 通信会话 */
	private IoSession session;
	
	public void registerSession(Channel channel) {
		this.session = new IoSession(channel);
	}

	public void sendServerRequest(AbstractPacket request){
		this.session.sendPacket(request);
	}
	
	public StageController getStageController() {
		return stageController;
	}
	
	/**
	 * 将任务转移给fxapplication线程延迟执行
	 * @param task
	 */
	public void runTaskInFxThread(Runnable task){
		Platform.runLater(task);
	}
	
}
