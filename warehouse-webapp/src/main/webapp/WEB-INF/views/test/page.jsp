<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@ page session="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<html>
<head>
	<META http-equiv="Content-Type" content="text/html;charset=UTF-8">
	<title><fmt:message key="type.title"/></title>
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
		<fmt:message key="type.title"/>
	</h1>
	<div class="span-12 last">	
		<form:form method="post" >
		  	<fieldset>	
				<table>
				<c:forEach items="${fields}" var="field">
					<c:if test="${fn:startsWith(field.key, '_')==false}">
	        			<tr>
							<td>${field.key}</td>
							<td><input name="${field.key}" value="${field.value}" type="text"/></td>
						</tr>
					</c:if>
    			</c:forEach>
    
				<tr>
					<td>
					<input type="submit" value="submit"/>
					</td>
				</tr>
				</table>
			</fieldset>
		</form:form>
	</div>
	
</div>
</body>
</html>