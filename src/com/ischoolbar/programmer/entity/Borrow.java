package com.ischoolbar.programmer.entity;

import java.sql.Timestamp;

import com.ischoolbar.programmer.annotation.Column;

/**
 * 借阅图书实体
 * @author llq
 *
 */
public class Borrow extends BaseEntity {
	
	@Column(name="user",isForeignEntity=true)
	private User user;//用户
	
	private int userId;

	@Column(name="book",isForeignEntity=true)
	private Book book;//图书
	
	private int bookId;
	
	private int status = 1;//借阅状态：1：借阅中，2：已归还
	
	private int number = 1;//借阅图书数量
	
	private Timestamp borrowTime;//借出时间
	
	private Timestamp returnTime;//归还时间

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Book getBook() {
		return book;
	}

	public void setBook(Book book) {
		this.book = book;
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

	public Timestamp getBorrowTime() {
		return borrowTime;
	}

	public void setBorrowTime(Timestamp borrowTime) {
		this.borrowTime = borrowTime;
	}

	public Timestamp getReturnTime() {
		return returnTime;
	}

	public void setReturnTime(Timestamp returnTime) {
		this.returnTime = returnTime;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getBookId() {
		return bookId;
	}

	public void setBookId(int bookId) {
		this.bookId = bookId;
	}
	
	
	
}
