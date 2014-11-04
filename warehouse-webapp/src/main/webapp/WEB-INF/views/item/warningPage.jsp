<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@ page session="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<html>
<head>
	<META http-equiv="Content-Type" content="text/html;charset=UTF-8">
	<title>@Controller Example</title>
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
		Warning
	</h1>
	<div class="span-12 last">
	
			The item you try to remove is a part of the following jobs :

			<table>
				<tr>
					<th>Date</th>
					<th>Client</th>
					<th>Description</th>
				</tr>
				<c:forEach items="${jobList}" var="current">
					<tr>
						<td><c:out value="${current.date}" />
						<td>
						<td><c:out value="${current.client}" />
						<td>
						<td><c:out value="${current.description}" />
						<td>
					</tr>
				</c:forEach>
			</table>

		</div>
	<hr>	
	<ul>
		<li> <a href="?locale=en_us">us</a> |  <a href="?locale=en_gb">gb</a> | <a href="?locale=es_es">es</a> | <a href="?locale=de_de">de</a> </li>
	</ul>	
</div>
</body>
</html>