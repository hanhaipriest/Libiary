<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta charset="UTF-8">
	<title>图书借阅统计列表</title>
	<link rel="stylesheet" type="text/css" href="easyui/themes/default/easyui.css">
	<link rel="stylesheet" type="text/css" href="easyui/themes/icon.css">
	<link rel="stylesheet" type="text/css" href="easyui/css/demo.css">
	<script type="text/javascript" src="easyui/jquery.min.js"></script>
	<script type="text/javascript" src="easyui/jquery.easyui.min.js"></script>
	<script type="text/javascript" src="easyui/js/validateExtends.js"></script>
	<script type="text/javascript">
	
	$(function() {	
	  	
	  	$("#book-btn").click(function(){
	  		getStatsData('book');
	  	});
	  	$("#user-btn").click(function(){
	  		getStatsData('user');
	  	});
	  	
	  	
	});
function getStatsData(type){
	$.ajax({
		url:'BorrowStatsServlet?method=BorrowStatsList&type='+type,
		type:'post',
		dataType:'json',
		success:function(data){
			if(data.type == 'success'){
				var option = {
		        	    title: {
		        	        text: data.title,
		        	        subtext: '数据只拉取前20名'
		        	    },
		        	    tooltip: {
		        	        trigger: 'axis',
		        	        axisPointer: {
		        	            type: 'shadow'
		        	        }
		        	    },
		        	    legend: {
		        	        data: ['借阅次数']
		        	    },
		        	    grid: {
		        	        left: '3%',
		        	        right: '4%',
		        	        bottom: '3%',
		        	        containLabel: true
		        	    },
		        	    xAxis: {
		        	        type: 'value',
		        	        boundaryGap: [0, 0.01]
		        	    },
		        	    yAxis: {
		        	        type: 'category',
		        	        data: data.nameList
		        	    },
		        	    series: [
		        	        {
		        	            name: '借阅次数',
		        	            type: 'bar',
		        	            data: data.numList
		        	        }
		        	    ]
		        	};
				// 使用刚指定的配置项和数据显示图表。
		        myChart.setOption(option);
			}else{
				$.messager.alert("消息提醒", data.msg, "warning");
			}
		}
	});
}
	
</script>
</head>
<body>
	<div class="panel-header"><div class="panel-title panel-with-icon">图书借阅排行统计</div><div class="panel-icon icon-more"></div><div class="panel-tool"></div></div>
	<!-- 工具栏 -->
	<div id="toolbar" class="datagrid-toolbar">
		<div style="margin-top: 3px;">
		<a id="book-btn" href="javascript:;" class="easyui-linkbutton" data-options="iconCls:'icon-world',plain:true">图书排行</a>
		<a id="user-btn" href="javascript:;" class="easyui-linkbutton" data-options="iconCls:'icon-world',plain:true">用户排行</a>
		</div>
	</div>
	<!-- 为 ECharts 准备一个具备大小（宽高）的 DOM -->
    <div id="main" style="width: 960px;height:600px;"></div>
	
</body>
<script type="text/javascript" src="echarts/echarts.min.js"></script>
<script type="text/javascript">
        // 基于准备好的dom，初始化echarts实例
        var myChart = echarts.init(document.getElementById('main'));

        // 指定图表的配置项和数据
        

        
</script>
</html>