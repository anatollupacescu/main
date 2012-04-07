<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@ page session="false" %>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="com.funny.entity.Item" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<html>
<head>
	<META http-equiv="Content-Type" content="text/html;charset=UTF-8">
	<title><fmt:message key="item.title"/></title>
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
		<fmt:message key="item.title"/>
	</h1>
	<div class="span-12 last">	
		<form:form modelAttribute="item" method="post">
		  	<fieldset>		
				<legend><fmt:message key="item.fields"/></legend>
				<p>
				<form:select path="<%=Item.fields.type.toString() %>" items="${itemTypeList}" 
					itemValue="id" itemLabel="name" 
					cssErrorClass="error" multiple="false"/>
				<form:errors path="<%=Item.fields.count.toString() %>" />
				</p>
				<p>	
					<form:label for="<%=Item.fields.inPrice.toString() %>" 
						path="<%=Item.fields.inPrice.toString() %>" cssErrorClass="error"><fmt:message key="item.field.price"/></form:label><br/>
					<form:input path="<%=Item.fields.inPrice.toString() %>" />
					<form:errors path="<%=Item.fields.inPrice.toString() %>" />
				</p>
				<p>	
					<form:label for="<%=Item.fields.count.toString() %>" 
						path="<%=Item.fields.count.toString() %>" cssErrorClass="error"><fmt:message key="item.field.count"/></form:label><br/>
					<form:input path="<%=Item.fields.count.toString() %>" />
					<form:errors path="<%=Item.fields.count.toString() %>" />
				</p>
				<p>
					<input type="submit" value="submit" />
				</p>
			</fieldset>
		</form:form>
	</div>
	
	<hr>
	<table>
	<tr>
		<th><fmt:message key="item.field.inDate"/></th>
		<th><fmt:message key="item.field.type"/></th>
		<th><fmt:message key="item.field.price"/></th>
		<th><fmt:message key="item.field.count"/></th>
		<th><fmt:message key="item.field.sum"/></th>
		<th></th>
	</tr>
	
	<c:forEach items="${itemList}" var="current">
	
		<tr>
			  <td><fmt:formatDate pattern="yyyy/MM/dd" value="${current.inDate}" /></td>
	      	  <td><c:out value="${current.type}" /></td>
	          <td><c:out value="${current.count}" /></td>
	          <td><c:out value="${current.inPrice}" /></td>
	          <td><c:out value="${current.inPrice * current.count}" /></td>
	          <td><a href="delete/${current.id}/">Delete</a></td>
		</tr>

	</c:forEach>

	</table>
	<hr>		

</div>
</body>
</html>