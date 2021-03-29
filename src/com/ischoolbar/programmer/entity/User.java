package com.ischoolbar.programmer.entity;

import com.ischoolbar.programmer.annotation.Column;


/**
 * 用户实体
 * @author llq
 *
 */
public class User extends BaseEntity{
	@Column(name="username")
	private String username;
	@Column(name="password")
	private String password;
	@Column(name="type")
	private int type = 2;//用户类型
	@Column(name="status")
	private int status = 1;//状态1：正常，0表示禁用
	
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	
	
}
