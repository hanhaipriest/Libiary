<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <title>图书管理系统 管理员后台</title>
    <link rel="shortcut icon" href="favicon.ico"/>
	<link rel="bookmark" href="favicon.ico"/>
    <link rel="stylesheet" type="text/css" href="easyui/css/default.css" />
    <link rel="stylesheet" type="text/css" href="easyui/themes/default/easyui.css" />
    <link rel="stylesheet" type="text/css" href="easyui/themes/icon.css" />
    <script type="text/javascript" src="easyui/jquery.min.js"></script>
    <script type="text/javascript" src="easyui/jquery.easyui.min.js"></script>
	<script type="text/javascript" src='easyui/js/outlook2.js'> </script>
    <script type="text/javascript">
	 var _menus = {"menus":[
						{"menuid":"1","icon":"","menuname":"用户管理",
							"menus":[
									{"menuid":"11","menuname":"用户列表","icon":"icon-user-student","url":"UserServlet?method=toUserListView"}
								]
						},
						{"menuid":"2","icon":"","menuname":"图书管理",
							"menus":[
									{"menuid":"21","menuname":"分类列表","icon":"icon-book-open","url":"BookCategoryServlet?method=toBookCategoryListView"},
									{"menuid":"21","menuname":"图书列表","icon":"icon-book-open","url":"BookServlet?method=toBookListView"},
								]
						},
						{"menuid":"3","icon":"","menuname":"借阅管理",
							"menus":[
									{"menuid":"31","menuname":"借阅列表","icon":"icon-world","url":"BorrowServlet?method=toBorrowListView"},
								]
						},
						{"menuid":"4","icon":"","menuname":"借阅统计",
							"menus":[
									{"menuid":"41","menuname":"统计图表","icon":"icon-world","url":"BorrowStatsServlet?method=toBorrowStatsListView"},
								]
						},
				]};


    </script>

</head>
<body class="easyui-layout" style="overflow-y: hidden"  scroll="no">
	<noscript>
		<div style=" position:absolute; z-index:100000; height:2046px;top:0px;left:0px; width:100%; background:white; text-align:center;">
		    <img src="images/noscript.gif" alt='抱歉，请开启脚本支持！' />
		</div>
	</noscript>
    <div region="north" split="true" border="false" style="overflow: hidden; height: 30px;
        background: url(images/layout-browser-hd-bg.gif) #7f99be repeat-x center 50%;
        line-height: 20px;color: #fff; font-family: Verdana, 微软雅黑,黑体">
        <span style="float:right; padding-right:20px;" class="head"><span style="color:red; font-weight:bold;">${user.username}&nbsp;</span>您好&nbsp;&nbsp;&nbsp;<a href="LoginServlet?method=LoginOut" id="loginOut">安全退出</a></span>
        <span style="padding-left:10px; font-size: 16px; ">【猿来入此】图书管理系统</span>
    </div>
    <div region="south" split="true" style="height: 30px; background: #D2E0F2; ">
        <div class="footer">Copyright &copy; 【猿来入此】 By artisan</div>
    </div>
    <div region="west" hide="true" split="true" title="导航菜单" style="width:180px;" id="west">
	<div id="nav" class="easyui-accordion" fit="true" border="false">
		<!--  导航内容 -->
	</div>
	
    </div>
    <div id="mainPanle" region="center" style="background: #eee; overflow-y:hidden">
        <div id="tabs" class="easyui-tabs"  fit="true" border="false" >
			<jsp:include page="/WEB-INF/views/welcome.jsp" />
		</div>
    </div>
	
	
</body>
</html>