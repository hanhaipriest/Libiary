package com.ischoolbar.programmer.servlet;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ischoolbar.programmer.util.CpachaUtil;
import com.ischoolbar.programmer.util.StringUtil;

public class CpachaServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

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
		if("GetLoginVCode".equals(method)){
			getLoginVcode(request,response);
			return;
		}
		if("GetRegisteVCode".equals(method)){
			getRegisteVcode(request,response);
			return;
		}
	}

	private void getRegisteVcode(HttpServletRequest request,
			HttpServletResponse response) {
		// TODO Auto-generated method stub
		int vlength = 4;
		int width = 98;
		int height = 38;
		Object vl = request.getParameter("vl");
		Object w = request.getParameter("w");
		Object h = request.getParameter("h");
		if(vl != null){
			vlength = Integer.parseInt(vl.toString());
		}
		if(w != null){
			width = Integer.parseInt(w.toString());
		}
		if(h != null){
			height = Integer.parseInt(h.toString());
		}
		CpachaUtil cpachaUtil = new CpachaUtil(vlength, width, height);
		String generatorVCode = cpachaUtil.generatorVCode();
		request.getSession().setAttribute("registeCpacha", generatorVCode);
		BufferedImage generatorRotateVCodeImage = cpachaUtil.generatorRotateVCodeImage(generatorVCode, true);
		try {
			ServletOutputStream outputStream = response.getOutputStream();
			ImageIO.write(generatorRotateVCodeImage, "gif", outputStream);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void getLoginVcode(HttpServletRequest request,
			HttpServletResponse response) {
		// TODO Auto-generated method stub
		int vlength = 4;
		int width = 98;
		int height = 38;
		Object vl = request.getParameter("vl");
		Object w = request.getParameter("w");
		Object h = request.getParameter("h");
		if(vl != null){
			vlength = Integer.parseInt(vl.toString());
		}
		if(w != null){
			width = Integer.parseInt(w.toString());
		}
		if(h != null){
			height = Integer.parseInt(h.toString());
		}
		CpachaUtil cpachaUtil = new CpachaUtil(vlength, width, height);
		String generatorVCode = cpachaUtil.generatorVCode();
		request.getSession().setAttribute("loginCpacha", generatorVCode);
		BufferedImage generatorRotateVCodeImage = cpachaUtil.generatorRotateVCodeImage(generatorVCode, true);
		try {
			ServletOutputStream outputStream = response.getOutputStream();
			ImageIO.write(generatorRotateVCodeImage, "gif", outputStream);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	
}
