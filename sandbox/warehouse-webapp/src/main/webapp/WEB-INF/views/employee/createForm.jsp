<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@ page session="false" %>
<%@ page import="com.funny.entity.Employee" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<html>
<head>
	<META http-equiv="Content-Type" content="text/html;charset=UTF-8">
	<title><fmt:message key="employee.title"/></title>
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
		<fmt:message key="employee.title"/>
	</h1>
	<div class="span-12 last">	
		<form:form modelAttribute="employee" action="/swat/employee" method="post">
		  	<fieldset>		
				<legend><fmt:message key="employee.fields"/></legend>
				<p>
					<form:label	for="<%=Employee.fields.name.toString()%>" 
						path="<%=Employee.fields.name.toString()%>" cssErrorClass="error"><fmt:message key="employee.field.name"/></form:label><br/>
					<form:input path="<%=Employee.fields.name.toString()%>" />
					<form:errors path="<%=Employee.fields.name.toString()%>" />			
				</p>
				<p>	
					<input type="submit" value="submit"/>
				</p>
			</fieldset>
		</form:form>
	</div>
	
	<hr>
	<table>
	<tr>
		<th><fmt:message key="employee.field.name"/></th>
		<th></th>
	</tr>
	<c:forEach items="${employeeList}" var="current">
        <tr>
          <td><c:out value="${current.name}" /></td>          
          <td><a href="delete/${current.id}/">Delete</a></td>
          
        </tr>
      </c:forEach>
     </table>
	<hr>		
</div>
</body>
</html>