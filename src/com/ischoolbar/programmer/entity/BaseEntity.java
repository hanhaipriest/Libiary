package com.ischoolbar.programmer.entity;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;

import com.ischoolbar.programmer.annotation.Column;

/**
 * 实体基类
 * @author llq
 *
 */
public class BaseEntity implements Serializable{
	@Column(name="id",primaryKey=true,autoIncrease=true)
	public int id;
	@Column(name="create_time",updateEnable=false,autoCreateTime=true)
	public Timestamp createTime;
	@Column(name="update_time")
	public Timestamp updateTime;
	public BaseEntity(){
		setUpdateTime(new Timestamp(System.currentTimeMillis()));
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Timestamp getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}
	public Timestamp getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Timestamp updateTime) {
		this.updateTime = updateTime;
	}
	
	
}
