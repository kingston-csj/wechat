package com.kingston.ui;

public final class R {

	public static class id {
		public static final String UserId = "userId";
		public static final String RegisterView = "RegisterView";
		public static final String LoginView = "Login_View";
		public static final String MainView = "MainView";
	}

	public static class layout {
		/** 注册界面 */
		public static final String RegisterView = "register/xml/register.fxml";
		/** 登录界面 */
		public static final String LoginView = "login/xml/login.fxml";
		/** 主界面 */
		public static final String MainView = "main/xml/main.fxml";
	}

	public static class string {
		public static final String FAIL_TO_CONNECT_SERVER = "连接服务器失败";
		public static final String REGISTER_SUCC = "注册成功！";
		public static final String REGISTER_FAILED = "昵称已存在！";
		public static final String LOGIN_FAILED = "您输入的密码或帐号不正确！";
	}

}
