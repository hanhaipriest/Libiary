package com.ischoolbar.programmer.util;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

/**
 * 字符串公用操作类
 * @author llq
 *
 */
public class StringUtil {
	
	/**
	 * 判断字符串是否为空
	 * @param str
	 * @return
	 */
	public static boolean isEmpty(String str){
		if(str == null || "".equals(str))return true;
		return false;
	}
	
	/**
	 * 将驼峰命名的字符串转换为下划线字符串
	 * @param str
	 * @return
	 */
	public static String convertToUnderline(String str){
		if(isEmpty(str))return null;
		String ret = "";
		for(int i=0; i< str.length();i++){
			char charAt = str.charAt(i);
			if(Character.isUpperCase(charAt)){
				if(i == 0){
					ret += String.valueOf(charAt).toLowerCase();
					continue;
				}
				ret += "_" + String.valueOf(charAt).toLowerCase();
				continue;
			}
			ret += charAt;
		}
		return ret;
	}
	
	/**
	 * 按照指定的字符将数组分割成字符串
	 * @param list
	 * @param split
	 * @return
	 */
	public static String join(List<String> list,String split){
		String ret = "";
		for(Object object : list){
			ret += object + split;
		}
		ret = ret.substring(0,ret.lastIndexOf(split));
		return ret;
	}
	
	/**
	 * 将指定字符串写入HttpServletResponse
	 * @param response
	 * @param content
	 */
	public static void writrToPage(HttpServletResponse response,String content){
		response.setCharacterEncoding("UTF-8");
		try {
			response.getWriter().write(content);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
