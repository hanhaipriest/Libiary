package com.ischoolbar.programmer.util;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.LinkedList;
import java.util.logging.Logger;

import javax.sql.DataSource;

public class DbUtil implements DataSource{

	private String dbUrl = "jdbc:mysql://localhost:3306/db_library_manager_web?useUnicode=true&characterEncoding=UTF-8";
	private String dbUser = "root";
	private String dbPassword = "";
	private String dbDriver = "com.mysql.jdbc.Driver";
	
	private static DbUtil dbUtil = new DbUtil();
	
	public static DbUtil getInstance(){
		return dbUtil;
	} 
	
	private DbUtil(){
		
	}
	
	public static int POOL_MAX = 5;//最大连接数
	
	public Connection connection = null;//表示当前的connection
	
	private LinkedList<Connection> pool = new LinkedList<Connection>();//数据库连接池
	
	public Connection getConnection(){
		
		try {
			if(pool.size() == 0){
				Class.forName(dbDriver);
				for(int i=0; i< POOL_MAX;i++){
					pool.add(DriverManager.getConnection(dbUrl, dbUser, dbPassword));
				}
				System.out.println("初始化连接池，成功建立" + POOL_MAX + "个数据库连接放到连接池中！");
			}
			connection = pool.remove(0);//从连接池中取出一个并返回
			
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return connection;
	}
	
	/**
	 * 释放数据库链接到连接池中
	 */
	public void releaseConnection(){
		//放回到连接池中
		pool.add(connection);
		System.out.println("成功归还连接到连接池，当前连接池有" + pool.size() + "个空闲链接！");
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		DbUtil dbUtil = new DbUtil();
		dbUtil.getConnection();
	}
	@Override
	public PrintWriter getLogWriter() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public int getLoginTimeout() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void setLogWriter(PrintWriter out) throws SQLException {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void setLoginTimeout(int seconds) throws SQLException {
		// TODO Auto-generated method stub
		
	}
	@Override
	public boolean isWrapperFor(Class<?> arg0) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public <T> T unwrap(Class<T> arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Connection getConnection(String username, String password)
			throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

}
