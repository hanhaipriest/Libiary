package com.ischoolbar.programmer.servlet;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import com.ischoolbar.programmer.dao.BookCategoryDao;
import com.ischoolbar.programmer.dao.BookDao;
import com.ischoolbar.programmer.dao.BorrowDao;
import com.ischoolbar.programmer.dao.UserDao;
import com.ischoolbar.programmer.entity.Book;
import com.ischoolbar.programmer.entity.BookCategory;
import com.ischoolbar.programmer.entity.Borrow;
import com.ischoolbar.programmer.entity.User;
import com.ischoolbar.programmer.page.Operator;
import com.ischoolbar.programmer.page.Page;
import com.ischoolbar.programmer.page.SearchProperty;
import com.ischoolbar.programmer.util.StringUtil;
/**
 * 借阅图书管理控制器
 * @author llq
 *
 */
public class BorrowServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4386109520796986005L;

	private BookDao bookDao = new BookDao();
	
	private BookCategoryDao bookCategoryDao = new BookCategoryDao();
	
	private BorrowDao borrowDao = new BorrowDao();
	
	private UserDao userDao = new UserDao();
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		Object attribute = request.getParameter("method");
		String method = "";
		if(attribute != null){
			method = attribute.toString();
		}
		if("toBorrowListView".equals(method)){
			request.getRequestDispatcher("/WEB-INF/views/borrow.jsp").forward(request, response);
			return;
		}
		if("BorrowList".equals(method)){
			getBorrowList(request,response);
			return;
		}
		if("BookReturn".equals(method)){
			returnBorrow(request,response);
			return;
		}
		if("AddBorrow".equals(method)){
			addBorrow(request,response);
			return;
		}
		
	}



	private void addBorrow(HttpServletRequest request,
			HttpServletResponse response) {
		// TODO Auto-generated method stub
		int boodId = Integer.parseInt(request.getParameter("bookId"));
		int realBorrowNumber = Integer.parseInt(request.getParameter("realBorrowNumber"));
		Book book = bookDao.find(boodId);
		Map<String, Object> ret = new HashMap<String, Object>();
		if(book.getFreeNumber() < realBorrowNumber){
			ret.put("type", "error");
			ret.put("msg", "数量超过最大可借数!");
			StringUtil.writrToPage(response, JSONObject.toJSONString(ret));
			return;
		}
		if(realBorrowNumber < 0){
			ret.put("type", "error");
			ret.put("msg", "数量不能小于0!");
			StringUtil.writrToPage(response, JSONObject.toJSONString(ret));
			return;
		}
		User loginUser = (User)request.getSession().getAttribute("user");
		if(isExist(book, loginUser)){
			ret.put("type", "error");
			ret.put("msg", "存在正在借阅的该书，请先归还后再借阅!");
			StringUtil.writrToPage(response, JSONObject.toJSONString(ret));
			return;
		}
		Borrow borrow = new Borrow();
		borrow.setUser(loginUser);
		borrow.setBook(book);
		borrow.setNumber(realBorrowNumber);
		borrow.setBookId(boodId);
		borrow.setUserId(loginUser.getId());
		borrow.setBorrowTime(new Timestamp(System.currentTimeMillis()));
		if(!borrowDao.add(borrow)){
			ret.put("type", "error");
			ret.put("msg", "借阅失败，请联系管理员!");
			StringUtil.writrToPage(response, JSONObject.toJSONString(ret));
			return;
		}
		book.setFreeNumber(book.getFreeNumber() - realBorrowNumber);
		if(book.getFreeNumber() == 0){
			book.setStatus(2);
		}
		if(!bookDao.update(book)){
			ret.put("type", "error");
			ret.put("msg", "图书信息更新失败，请联系管理员!");
			StringUtil.writrToPage(response, JSONObject.toJSONString(ret));
			return;
		}
		ret.put("type", "success");
		ret.put("msg", "图书借阅成功!");
		StringUtil.writrToPage(response, JSONObject.toJSONString(ret));
	}

	private void returnBorrow(HttpServletRequest request,
			HttpServletResponse response) {
		// TODO Auto-generated method stub
		int id = Integer.parseInt(request.getParameter("id"));
		int realReturnNumber = Integer.parseInt(request.getParameter("realReturnNumber"));
		Map<String, Object> ret = new HashMap<String, Object>();
		Borrow borrow = borrowDao.find(id);
		if(borrow == null){
			ret.put("type", "error");
			ret.put("msg", "请选择要借阅的信息!");
			StringUtil.writrToPage(response, JSONObject.toJSONString(ret));
			return;
		}
		if(borrow.getNumber() < realReturnNumber){
			ret.put("type", "error");
			ret.put("msg", "归还数量大于借出数量，请检查头脑!");
			StringUtil.writrToPage(response, JSONObject.toJSONString(ret));
			return;
		}
		
		//判断是全部归还还是部分归还
		if(realReturnNumber == borrow.getNumber()){
			//全部归还
			//首先更新归还状态，表示已经归还
			borrow.setStatus(2);
			borrow.setReturnTime(new Timestamp(System.currentTimeMillis()));
			if(!borrowDao.update(borrow)){
				ret.put("type", "error");
				ret.put("msg", "借阅信息更新失败!");
				StringUtil.writrToPage(response, JSONObject.toJSONString(ret));
				return;
			}
		}
		
		//判断是否是部分归还
		if(borrow.getNumber() > realReturnNumber){
			//部分归还
			borrow.setNumber(borrow.getNumber() - realReturnNumber);
			if(!borrowDao.update(borrow)){
				ret.put("type", "error");
				ret.put("msg", "借阅信息更新失败!");
				StringUtil.writrToPage(response, JSONObject.toJSONString(ret));
				return;
			}
		}
		
		//更新图书信息
		Book book = bookDao.find(borrow.getBookId());
		book.setFreeNumber(book.getFreeNumber() + realReturnNumber);
		if(book.getStatus() == 2){
			//若图书状态是全部借出
			book.setStatus(1);//设置成状态为可借
		}
		if(!bookDao.update(book)){
			ret.put("type", "error");
			ret.put("msg", "图书信息更新失败!");
			StringUtil.writrToPage(response, JSONObject.toJSONString(ret));
			return;
		}
		
		ret.put("type", "success");
		ret.put("msg", "图书更新成功!");
		StringUtil.writrToPage(response, JSONObject.toJSONString(ret));
	}

	

	private void getBorrowList(HttpServletRequest request,
			HttpServletResponse response) {
		// TODO Auto-generated method stub
		String username = request.getParameter("username");
		String bookName = request.getParameter("bookName");
		int status = Integer.parseInt(StringUtil.isEmpty(request.getParameter("status")) ? "-1" : request.getParameter("status"));
		int pageNumber = Integer.parseInt(request.getParameter("page"));
		int pageSize = Integer.parseInt(request.getParameter("rows"));
		Page<Borrow> page = new Page<Borrow>(pageNumber, pageSize);
		User loginedUser = (User)request.getSession().getAttribute("user");
		//管理员
		if(loginedUser.getType() == 1){
			if(!StringUtil.isEmpty(username)){
				Page<User> userPage = new Page<User>(1, 10);
				userPage.getSearchProporties().add(new SearchProperty("username", "%"+username + "%", Operator.LIKE));
				userPage = userDao.findList(userPage);
				if(userPage.getContent().size() > 0){
					List<String> ids = new ArrayList<String>();
					for(User u:userPage.getContent()){
						ids.add(u.getId()+"");
					}
					page.getSearchProporties().add(new SearchProperty("user_id", StringUtil.join(ids, ","), Operator.IN));
				}
			}
		}
		//普通用户
		if(loginedUser.getType() != 1){
			page.getSearchProporties().add(new SearchProperty("user_id", loginedUser.getId(), Operator.EQ));
		}
		//根据图书名称搜索
		if(!StringUtil.isEmpty(bookName)){
			Page<Book> bookPage = new Page<Book>(1, 10);
			bookPage.getSearchProporties().add(new SearchProperty("name", "%"+bookName+"%", Operator.LIKE));
			bookPage = bookDao.findList(bookPage);
			List<String> ids = new ArrayList<String>();
			for(Book b:bookPage.getContent()){
				ids.add(b.getId()+"");
			}
			if(bookPage.getContent().size() > 0){
				page.getSearchProporties().add(new SearchProperty("book_id", StringUtil.join(ids, ","), Operator.IN));
			}
		}
		
		//根据状态搜索
		if(status != -1){
			page.getSearchProporties().add(new SearchProperty("status", status, Operator.EQ));
		}
		
		page = borrowDao.findList(page);
		Map<String, Object> ret = new HashMap<String, Object>();
		ret.put("total", page.getTotal());
		ret.put("rows", page.getContent());
		StringUtil.writrToPage(response, JSONObject.toJSONString(ret));
	}
	
	private boolean isExist(Book book,User user){
		Page<Borrow> page = new Page<Borrow>(1, 999);
		page.getSearchProporties().add(new SearchProperty("user_id", user.getId(), Operator.EQ));
		page.getSearchProporties().add(new SearchProperty("status", 1, Operator.EQ));
		page = borrowDao.findList(page);
		if(page.getContent().size() >0){
			for(Borrow b:page.getContent()){
				if(b.getBookId() == book.getId())return true;
			}
		}
		return false;
	}
	
}
