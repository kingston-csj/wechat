package com.kingston.logic.user;

public class MyProfile {

	private long userId;
	/** 账号昵称 */
	private String userName;
	/** 个性签名　*/
	private String signature;
	/** 性别 */
	private byte sex;

	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getSignature() {
		return signature;
	}
	public void setSignature(String signature) {
		this.signature = signature;
	}
	public byte getSex() {
		return sex;
	}
	public void setSex(byte sex) {
		this.sex = sex;
	}

	@Override
	public String toString() {
		return "MyProfile [userId=" + userId + ", userName=" + userName + ", signature=" + signature + ", sex=" + sex
				+ "]";
	}

}
