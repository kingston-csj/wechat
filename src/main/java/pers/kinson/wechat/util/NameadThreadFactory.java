package pers.kinson.wechat.util;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 可命名线程工厂
 * @author kinson
 */
public class NameadThreadFactory implements ThreadFactory {

	private ThreadGroup threadGroup;

	private String groupName;

	private final boolean daemo;

	private AtomicInteger idGenerator = new AtomicInteger(1);

	public NameadThreadFactory(String group) {
		this(group, false);
	}

	public NameadThreadFactory(String group, boolean daemo) {
		this.groupName = group;
		this.daemo = daemo;
	}

	@Override
	public Thread newThread(Runnable r) {
		String name = getNextThreadName();
		Thread ret = new Thread(threadGroup, r, name, 0);
		ret.setDaemon(daemo);
		return ret;
	}

	private String getNextThreadName() {
		return this.groupName + "-thread-" + this.idGenerator.getAndIncrement();
	}

}