package com.ischoolbar.programmer.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import com.ischoolbar.programmer.dao.BorrowDao;
import com.ischoolbar.programmer.util.StringUtil;
/**
 * 统计图表控制器
 * @author llq
 *
 */
public class BorrowStatsServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2924531720058362191L;

	
	private BorrowDao borrowDao = new BorrowDao();
	
	
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
		if("toBorrowStatsListView".equals(method)){
			request.getRequestDispatcher("/WEB-INF/views/borrow_stats.jsp").forward(request, response);
			return;
		}
		if("BorrowStatsList".equals(method)){
			getBorrowStatsList(request,response);
			return;
		}
		
	}

	private void getBorrowStatsList(HttpServletRequest request,
			HttpServletResponse response) {
		// TODO Auto-generated method stub
		String type = request.getParameter("type");
		Map<String, Object> ret = new HashMap<String, Object>();
		ret.put("type", "success");
		if("user".equals(type)){
			ret.put("title", "用户借阅排行榜");
		}
		if("book".equals(type)){
			ret.put("title", "图书借阅排行榜");
		}
		List<Map<String, Object>> stats = borrowDao.getStats(type);
		List<String> nameList = new ArrayList<String>();
		List<Integer> numList = new ArrayList<Integer>();
		for(Map<String, Object> m:stats){
			nameList.add(m.get("name").toString());
			numList.add(Integer.parseInt(m.get("num")+""));
		}
		ret.put("nameList", nameList);
		ret.put("numList", numList);
		StringUtil.writrToPage(response, JSONObject.toJSONString(ret));
	}
}
