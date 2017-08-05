package com.kingston.logic.login;

import com.kingston.base.ServerManager;
import com.kingston.logic.login.message.ClientLogin;
import com.kingston.logic.login.message.ServerLogin;

public class LoginManager {

	private static LoginManager instance = new  LoginManager();
	
	private LoginManager() {}
	
	public static LoginManager getInstance(){
		return instance;
	}
	
	public void beginToLogin() {
		ServerLogin reqLogin= new ServerLogin();  
		reqLogin.setUserId(1);
		reqLogin.setUserName("Netty爱好者");  
		reqLogin.setUserPwd("world");  
		System.err.println("向服务端发送登录请求");  
		
		ServerManager.INSTANCE.sendServerRequest(reqLogin);
	}
	
	public void receiveServerMsg(ClientLogin resp){
	}
}
