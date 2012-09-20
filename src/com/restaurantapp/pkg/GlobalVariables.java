package com.restaurantapp.pkg;

import android.app.Application;

public class GlobalVariables extends Application {
	public String loginBtn_txt;
	public String username;
	public String user_id;

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUsername() {
		return username;
	}

	public void setLoginBtn_txt(String loginBtn_txt) {
		this.loginBtn_txt = loginBtn_txt;
	}

	public String getLoginBtn_txt() {
		return loginBtn_txt;
	}

}
