package com.ischoolbar.programmer.dao;

import java.io.ObjectInputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ischoolbar.programmer.entity.Book;
import com.ischoolbar.programmer.entity.Borrow;
import com.ischoolbar.programmer.entity.User;

public class BorrowDao extends BaseDao<Borrow> {
	public List<Map<String, Object>> getStats(String type){
		List<Map<String, Object>> ret = new ArrayList<Map<String,Object>>();
		String sql = "";
		if("user".equals(type)){
			sql = "select count(id) as num,user from borrow  GROUP BY user_id ORDER BY num limit 0,20";
		}
		if("book".equals(type)){
			sql = "select count(id) as num,book from borrow  GROUP BY book_id ORDER BY num limit 0,20";
		}
		try {
			PreparedStatement prepareStatement = getDbUtil().connection.prepareStatement(sql);
			ResultSet executeQuery = prepareStatement.executeQuery();
			while(executeQuery.next()){
				Map<String, Object> stats = new HashMap<String, Object>();
				User user;
				Book book;
				stats.put("num", executeQuery.getInt("num"));
				if("user".equals(type)){
					ObjectInputStream objectInputStream = new ObjectInputStream(executeQuery.getBlob("user").getBinaryStream());
					user = (User)objectInputStream.readObject();
					stats.put("name", user.getUsername());
				}
				if("book".equals(type)){
					ObjectInputStream objectInputStream = new ObjectInputStream(executeQuery.getBlob("book").getBinaryStream());
					book = (Book)objectInputStream.readObject();
					stats.put("name", book.getName());
				}
				ret.add(stats);
			}
			closeConnection();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}
}
