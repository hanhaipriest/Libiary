package com.ischoolbar.programmer.entity;

import com.ischoolbar.programmer.annotation.Column;

/**
 * 图书实体
 * @author llq
 *
 */
public class Book extends BaseEntity {
	
	private String name;//图书名称

	@Column(name="book_category",isForeignEntity=true)
	private BookCategory bookCategory;//图书分类
	
	private int status;//图书状态
	
	private int number = 1;//图书数量
	
	private int freeNumber = 1;//可借图书数量
	
	private String info;//图书介绍
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BookCategory getBookCategory() {
		return bookCategory;
	}

	public void setBookCategory(BookCategory bookCategory) {
		this.bookCategory = bookCategory;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public int getFreeNumber() {
		return freeNumber;
	}

	public void setFreeNumber(int freeNumber) {
		this.freeNumber = freeNumber;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}
	
}
