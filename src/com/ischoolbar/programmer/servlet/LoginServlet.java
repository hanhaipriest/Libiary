package com.ischoolbar.programmer.servlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import com.ischoolbar.programmer.dao.UserDao;
import com.ischoolbar.programmer.entity.User;
import com.ischoolbar.programmer.page.Operator;
import com.ischoolbar.programmer.page.Page;
import com.ischoolbar.programmer.page.SearchProperty;
import com.ischoolbar.programmer.util.StringUtil;

public class LoginServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5870852067427524781L;

	private UserDao UserDao = new UserDao();
	
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
		if("login".equals(method)){
			request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
			return;
		}
		if("registe".equals(method)){
			request.getRequestDispatcher("/WEB-INF/views/registe.jsp").forward(request, response);
			return;
		}
		if("registeAct".equals(method)){
			resister(request,response);
			return;
		}
		if("loginAct".equals(method)){
			login(request,response);
			return;
		}
		if("LoginOut".equals(method)){
			request.getSession().setAttribute("user", null);
			response.sendRedirect("LoginServlet?method=login");
			return;
		}
	}

	private void login(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// TODO Auto-generated method stub
		Map<String, String> ret = new HashMap<String, String>();
		response.setCharacterEncoding("UTF-8");
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		String vcode = request.getParameter("vcode");
		String type = request.getParameter("type");
		if(StringUtil.isEmpty(username)){
			ret.put("type", "error");
			ret.put("msg", "用户名不能为空！");
			response.getWriter().write(JSONObject.toJSONString(ret));
			return;
		}
		if(StringUtil.isEmpty(password)){
			ret.put("type", "error");
			ret.put("msg", "密码不能为空！");
			response.getWriter().write(JSONObject.toJSONString(ret));
			return;
		}
		if(StringUtil.isEmpty(vcode)){
			ret.put("type", "error");
			ret.put("msg", "验证码不能为空！");
			response.getWriter().write(JSONObject.toJSONString(ret));
			return;
		}
		Object loginCpacha = request.getSession().getAttribute("loginCpacha");
		if(loginCpacha == null){
			ret.put("type", "error");
			ret.put("msg", "长时间未操作，验证码失效，请刷新后重试！");
			response.getWriter().write(JSONObject.toJSONString(ret));
			return;
		}
		if(!vcode.toUpperCase().equals(loginCpacha.toString().toUpperCase())){
			ret.put("type", "error");
			ret.put("msg", "验证码错误！");
			response.getWriter().write(JSONObject.toJSONString(ret));
			return;
		}
		Page<User> page = new Page<User>(1, 10);
		page.getSearchProporties().add(new SearchProperty("username", username, Operator.EQ));
		page.getSearchProporties().add(new SearchProperty("type", type, Operator.EQ));
		page = UserDao.findList(page);
		if(page.getContent().size() == 0){
			ret.put("type", "error");
			ret.put("msg", "该类型的用户名不存在！");
			response.getWriter().write(JSONObject.toJSONString(ret));
			return;
		}
		
		User user = page.getContent().get(0);
		if(!password.equals(user.getPassword())){
			ret.put("type", "error");
			ret.put("msg", "密码错误！");
			response.getWriter().write(JSONObject.toJSONString(ret));
			return;
		}
		if(user.getStatus() == 0){
			ret.put("type", "error");
			ret.put("msg", "该用户被禁用，请联系管理员！");
			response.getWriter().write(JSONObject.toJSONString(ret));
			return;
		}
		request.getSession().setAttribute("loginCpacha",null);
		request.getSession().setAttribute("user",user);
		ret.put("type", "success");
		ret.put("msg", "登录成功！");
		response.getWriter().write(JSONObject.toJSONString(ret));
	}

	private void resister(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		// TODO Auto-generated method stub
		Map<String, String> ret = new HashMap<String, String>();
		response.setCharacterEncoding("UTF-8");
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		String vcode = request.getParameter("vcode");
		if(StringUtil.isEmpty(username)){
			ret.put("type", "error");
			ret.put("msg", "用户名不能为空！");
			response.getWriter().write(JSONObject.toJSONString(ret));
			return;
		}
		if(StringUtil.isEmpty(password)){
			ret.put("type", "error");
			ret.put("msg", "密码不能为空！");
			response.getWriter().write(JSONObject.toJSONString(ret));
			return;
		}
		if(StringUtil.isEmpty(vcode)){
			ret.put("type", "error");
			ret.put("msg", "验证码不能为空！");
			response.getWriter().write(JSONObject.toJSONString(ret));
			return;
		}
		Object registeCpacha = request.getSession().getAttribute("registeCpacha");
		if(registeCpacha == null){
			ret.put("type", "error");
			ret.put("msg", "长时间未操作，验证码失效，请刷新后重试！");
			response.getWriter().write(JSONObject.toJSONString(ret));
			return;
		}
		if(!vcode.toUpperCase().equals(registeCpacha.toString().toUpperCase())){
			ret.put("type", "error");
			ret.put("msg", "验证码错误！");
			response.getWriter().write(JSONObject.toJSONString(ret));
			return;
		}
		if(isExistUserName(username)){
			ret.put("type", "error");
			ret.put("msg", "该用户名已经存在，请重新输入！");
			response.getWriter().write(JSONObject.toJSONString(ret));
			return;
		}
		User user = new User();
		user.setUsername(username);
		user.setPassword(password);
		if(!UserDao.add(user)){
			ret.put("type", "error");
			ret.put("msg", "注册失败，请联系管理员！");
			response.getWriter().write(JSONObject.toJSONString(ret));
			return;
		}
		request.getSession().setAttribute("registeCpacha",null);
		ret.put("type", "success");
		ret.put("msg", "注册成功！");
		response.getWriter().write(JSONObject.toJSONString(ret));
	}
	
	private boolean isExistUserName(String name){
		Page<User> page = new Page<User>(1, 10);
		page.getSearchProporties().add(new SearchProperty("username", name, Operator.EQ));
		page = UserDao.findList(page);
		if(page.getContent().size() > 0)return true;
		return false;
	}
}
