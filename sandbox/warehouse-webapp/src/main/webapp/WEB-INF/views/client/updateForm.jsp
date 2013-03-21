<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@ page session="false" %>
<%@ page import="com.funny.entity.Client" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<html>
<head>
	<META http-equiv="Content-Type" content="text/html;charset=UTF-8">
	<title><fmt:message key="client.update.title"/></title>
	<link rel="stylesheet" href="<c:url value="/resources/blueprint/screen.css" />" type="text/css" media="screen, projection">
	<link rel="stylesheet" href="<c:url value="/resources/blueprint/print.css" />" type="text/css" media="print">
	<!--[if lt IE 8]>
		<link rel="stylesheet" href="<c:url value="/resources/blueprint/ie.css" />" type="text/css" media="screen, projection">
	<![endif]-->
</head>	
<body>

	<h3>
		<a href="/swat/client/">Up</a>
	</h3>

<div class="container">
	<h1>
		<fmt:message key="client.update.title"/>
	</h1>
	<div class="span-12 last">	
		<form:form modelAttribute="client" action="/swat/client/update" method="post">
		  	<fieldset>		
				<legend><fmt:message key="client.fields"/></legend>
				<form:hidden path="id" />
				<p>
					<form:label	for="<%=Client.fields.name.toString()%>" 
						path="<%=Client.fields.name.toString()%>" cssErrorClass="error">Name</form:label><br/>
					<form:input path="<%=Client.fields.name.toString()%>" />
					<form:errors path="<%=Client.fields.name.toString()%>" />			
				</p>
				<p>	
					<form:label for="<%=Client.fields.telephoneNo.toString()%>" 
						path="<%=Client.fields.telephoneNo.toString()%>" cssErrorClass="error">Telephone Number</form:label><br/>
					<form:input path="<%=Client.fields.telephoneNo.toString()%>" />
					<form:errors path="<%=Client.fields.telephoneNo.toString()%>" />
				</p>
				<p>	
					<input type="submit" />
				</p>
			</fieldset>
		</form:form>
	</div>
	<hr>	

</div>
</body>
</html>