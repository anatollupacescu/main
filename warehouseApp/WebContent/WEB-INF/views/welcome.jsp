<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@ page session="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<html>
<head>
	<META http-equiv="Content-Type" content="text/html;charset=UTF-8">
	<title><fmt:message key="welcome.title"/></title>
	<link rel="stylesheet" href="<c:url value="/resources/blueprint/screen.css" />" type="text/css" media="screen, projection">
	<link rel="stylesheet" href="<c:url value="/resources/blueprint/print.css" />" type="text/css" media="print">
	<!--[if lt IE 8]>
		<link rel="stylesheet" href="<c:url value="/resources/blueprint/ie.css" />" type="text/css" media="screen, projection">
	<![endif]-->
</head>
<body>
<div class="container">  
	<p>
		<h1><fmt:message key="welcome.title"/></h1>
	</p>
	
	<hr>	
		<p> <a href="?locale=en_us">en</a> | <a href="?locale=ro">ro</a> |  <a href="?locale=ru">ru</a> </p>
	<ul>
		<li><a href="client/"><fmt:message key="welcome.clients"/></a></li>
		<li><a href="employee/"><fmt:message key="welcome.employees"/></a></li>
		<li><a href="type/"><fmt:message key="welcome.types"/></a></li>
		<li><a href="item/"><fmt:message key="welcome.items"/></a></li>
		<li><a href="job/"><fmt:message key="welcome.jobs"/></a></li>
		<li><a href="report/"><fmt:message key="welcome.report"/></a></li>
	</ul>
</div>
</body>
</html>