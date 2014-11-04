<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@ page session="false" %>
<%@ page import="com.funny.entity.Job" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<html>
<head>
	<META http-equiv="Content-Type" content="text/html;charset=UTF-8">
	<title><fmt:message key="job.view.title"/></title>
	<link rel="stylesheet" href="<c:url value="/resources/blueprint/screen.css" />" type="text/css" media="screen, projection">
	<link rel="stylesheet" href="<c:url value="/resources/blueprint/print.css" />" type="text/css" media="print">
	<!--[if lt IE 8]>
		<link rel="stylesheet" href="<c:url value="/resources/blueprint/ie.css" />" type="text/css" media="screen, projection">
	<![endif]-->
</head>	
<body>
	<h3>
		<a href="/swat/job/">Up</a>
	</h3>
<div class="container">
	<h1>
		<fmt:message key="job.view.title"/>
	</h1>
	<div class="span-12 last">	
		<form:form modelAttribute="job">
			<fieldset>		
			<legend><fmt:message key="job.fields"/></legend>
					<p>
						<form:label for="<%=Job.fields.client.toString()%>" path="<%=Job.fields.client.toString() %>">
							<fmt:message key="job.field.client"/>
						</form:label>
						<br/>
						<form:input path="<%=Job.fields.client.toString() %>" readonly="true" />
					</p>
					
					<p>
						<form:label for="<%=Job.fields.description.toString() %>" path="<%=Job.fields.description.toString() %>"><fmt:message key="job.field.description"/></form:label><br/>
						<form:input path="<%=Job.fields.description.toString() %>" readonly="true" />
					</p>

					<table>
						<tr>
							<th></th>
							<th><fmt:message key="job.field.item"/></th>
							<th><fmt:message key="job.field.employee"/></th>
							<th><fmt:message key="job.field.price_out"/></th>
							<th><fmt:message key="job.field.count"/></th>
							<th><fmt:message key="job.field.sum"/></th>
						</tr>

						<c:forEach items="${installedItems}" var="item" varStatus="status">
							<tr>
								<td><c:out value="${status.count}" />)</td>
								<td><c:out value="${item.type}" /></td>
								<td><c:out value="${item.employee}" /></td>
								<td><c:out value="${item.outPrice}" /></td>
								<td><c:out value="${item.count}" /></td>
								<td><c:out value="${item.count * item.outPrice}" /></td>
							</tr>
						</c:forEach>
					</table>
			</fieldset>
		</form:form>
	</div>
	<hr>
</div>
</body>
</html>