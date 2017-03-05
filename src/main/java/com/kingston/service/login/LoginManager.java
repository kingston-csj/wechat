package com.kingston.service.login;


public class LoginManager {

	private static LoginManager instance = new  LoginManager();
	
	private LoginManager(){
		
	}
	
	public static LoginManager getInstance(){
		return instance;
	}
	
	public void receiveServerMsg(ClientLogin resp){
	}
}
