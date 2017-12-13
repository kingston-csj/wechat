package com.kingston.chat.net.message;

public class IllegalPacketException  extends RuntimeException{


	public IllegalPacketException() {
		super();
	}

	public IllegalPacketException(String message) {
		super(message);
	}
}
