package pers.kinson.wechat.base;

import pers.kinson.wechat.ui.StageController;

import javafx.application.Platform;

public class UiContext {

	public static StageController stageController = new StageController();

	/**
	 * 将任务转移给fxapplication线程延迟执行
	 * @param task
	 */
	public static void runTaskInFxThread(Runnable task){
		Platform.runLater(task);
	}

}
