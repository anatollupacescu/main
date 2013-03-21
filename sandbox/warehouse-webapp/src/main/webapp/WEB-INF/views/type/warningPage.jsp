<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@ page session="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<html>
<head>
	<META http-equiv="Content-Type" content="text/html;charset=UTF-8">
	<title><fmt:message key="warning.title"/></title>
	<link rel="stylesheet" href="<c:url value="/resources/blueprint/screen.css" />" type="text/css" media="screen, projection">
	<link rel="stylesheet" href="<c:url value="/resources/blueprint/print.css" />" type="text/css" media="print">
	<!--[if lt IE 8]>
		<link rel="stylesheet" href="<c:url value="/resources/blueprint/ie.css" />" type="text/css" media="screen, projection">
	<![endif]-->
</head>	
<body>
	<h3>
		<a href="${pageContext.request.contextPath}/">Up</a>
	</h3>
<div class="container">
	<h1>
		<fmt:message key="warning.title"/>
	</h1>
	<div class="span-12 last">
	
		<fmt:message key="type.warning"/>

		<table>
		<tr>
			<th><fmt:message key="item.field.type"/></th>
			<th><fmt:message key="item.field.price"/></th>
			<th><fmt:message key="item.field.count"/></th>
			<th><fmt:message key="item.field.sum"/></th>
			<th><fmt:message key="item.field.sell_price"/></th>
			<th><fmt:message key="item.field.state"/></th>
			<th></th>
		</tr>
		
		<c:forEach items="${itemList}" var="current">
		
			<tr>
		      	  <td><c:out value="${current.type}" /></td>
		          <td><c:out value="${current.count}" /></td>
		          <td><c:out value="${current.inPrice}" /></td>
		          <td><c:out value="${current.inPrice * current.count}" /></td>
		          <td><c:out value="${current.outPrice}" /></td>
		          <td><c:out value="${current.state}" /></td>
		          <td><a href="delete/${current.id}/">Delete</a></td>
			</tr>
	
		</c:forEach>
	
		</table>

		</div>
	<hr>	

</div>
</body>
</html>