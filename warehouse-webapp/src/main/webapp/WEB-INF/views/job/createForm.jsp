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
	<title><fmt:message key="job.title"/></title>
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
		<fmt:message key="job.title"/>
	</h1>
	<div class="span-12 last">	
		<form:form modelAttribute="job" action="/swat/job" method="post">
		  	<fieldset>		
				<legend><fmt:message key="job.fields"/></legend>
				<p>
					<form:label	for="<%=Job.fields.client.toString() %>" 
						path="<%=Job.fields.client.toString() %>" cssErrorClass="error"><fmt:message key="job.field.client"/></form:label><br/>
						
					<form:select path="<%=Job.fields.client.toString() %>" items="${clientList}" 
						itemValue="id" itemLabel="name" 
						cssErrorClass="error" />
				</p>
				
				<p>
					<form:label	for="<%=Job.fields.description.toString() %>" 
						path="<%=Job.fields.description.toString() %>" cssErrorClass="error"><fmt:message key="job.field.description"/></form:label><br/>
					<form:input path="<%=Job.fields.description.toString() %>" />
					<form:errors path="<%=Job.fields.description.toString() %>" />
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
		<th><fmt:message key="job.field.description"/></th>
		<th><fmt:message key="job.field.date"/></th>
		<th><fmt:message key="job.field.client"/></th>
		<th></th>
		<th></th>
	</tr>
	<c:forEach items="${jobList}" var="current">
        <tr>
          <td><a href="${current.id}/"><c:out value="${current.description}" /></a></td>
          <td><fmt:formatDate pattern="yyyy/MM/dd" value="${current.date}" /></td>
          <td><c:out value="${current.client}" /></td>
          <td><a href="update/${current.id}/">Edit</a></td>
          <td><a href="delete/${current.id}/">Delete</a></td>
        </tr>
      </c:forEach>
     </table>
	<hr>		

</div>
</body>
</html>