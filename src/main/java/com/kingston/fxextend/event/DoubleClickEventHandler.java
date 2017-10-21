package com.kingston.fxextend.event;

import javafx.event.Event;
import javafx.event.EventHandler;

public class DoubleClickEventHandler<T extends Event> implements EventHandler<Event> {

	/** 点击计数器（用于模拟双击效果） */
	private int clickCounter;

	@Override
	public void handle(Event event) {

	}

	public boolean checkVaild() {
		clickCounter++;
		if (clickCounter%2 != 0) {
			return false;
		}
		clickCounter = 0;
		return true;
	}

}
