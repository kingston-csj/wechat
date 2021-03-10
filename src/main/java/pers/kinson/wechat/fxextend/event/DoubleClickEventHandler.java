package pers.kinson.wechat.fxextend.event;

import javafx.event.Event;
import javafx.event.EventHandler;

public class DoubleClickEventHandler<T extends Event> implements EventHandler<Event> {

	/** 点击计数器（用于模拟双击效果） */
	private int clickCounter;
	/** 上一次点击的时间戳 */
	private long lastClickTime;
	/** 两次点击之间的时间间隔少于100毫秒才有效 */
	private final long interval = 300;

	@Override
	public void handle(Event event) {

	}

	public boolean checkVaild() {
		clickCounter++;
		long now = System.currentTimeMillis();
		if (clickCounter % 2 != 0) {
			lastClickTime = now;
			return false;
		}
		clickCounter = 0;

		boolean result = false;
		long diff = now - lastClickTime;
//		System.err.println("=="+diff);
		if (diff < interval) {
			result =  true;
		}
		lastClickTime = now;
		return result;
	}

}
