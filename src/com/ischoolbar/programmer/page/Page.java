package com.ischoolbar.programmer.page;

import java.util.ArrayList;
import java.util.List;

/**
 * 分页查询封装
 * @author llq
 *
 */

public class Page<T> {
	private int pageNumber;//当前页数
	
	private int pageSize;//每页显示数量
	
	private int total;//总记录数
	
	private int totalPage;//总页数
	
	private int offset;//对应数据库里的游标
	
	private List<T> content = new ArrayList<T>();//结果集
	
	private List<SearchProperty> searchProporties = new ArrayList<SearchProperty>();
	
	public Page(int pageNumber,int pageSize){
		this.pageNumber = pageNumber;
		this.pageSize = pageSize;
		this.offset = (this.pageNumber - 1) * this.pageSize;
	}

	public int getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public List<T> getContent() {
		return content;
	}

	public void setContent(List<T> content) {
		this.content = content;
	}

	public List<SearchProperty> getSearchProporties() {
		return searchProporties;
	}

	public void setSearchProporties(List<SearchProperty> searchProporties) {
		this.searchProporties = searchProporties;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public int getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}
	
}
