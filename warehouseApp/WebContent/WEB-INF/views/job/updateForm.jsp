<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@ page session="false" %>
<%@ page import="com.funny.entity.Job" %>
<%@ page import="com.funny.ui.JobItem" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<html>
<head>
	<META http-equiv="Content-Type" content="text/html;charset=UTF-8">
	<title><fmt:message key="job.update.title"/></title>
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
		<fmt:message key="job.update.title"/>
	</h1>
	<div class="span-12 last">	
		<form:form modelAttribute="job" action="/swat/job/update" method="post" name="myform">
		  	<fieldset>		
				<legend><fmt:message key="job.fields"/></legend>
					<form:hidden path="id" />
					<p>
						<form:label for="<%=Job.fields.client.toString()%>"
							path="<%=Job.fields.client.toString() %>"
							cssErrorClass="error"><fmt:message key="job.field.client"/></form:label><br/>
							
						<form:select path="<%=Job.fields.client.toString() %>"
							items="${clientList}" itemValue="id" itemLabel="name"
							cssErrorClass="error" />
						<form:errors path="<%=Job.fields.client.toString() %>" />
					</p>
					
					<p>
						<form:label for="<%=Job.fields.description.toString() %>"
							path="<%=Job.fields.description.toString() %>"
							cssErrorClass="error"><fmt:message key="job.field.description"/></form:label><br/>
						<form:input path="<%=Job.fields.description.toString() %>" />
						<form:errors path="<%=Job.fields.description.toString() %>" />
					</p>

					<table>
						<tr>
							<th></th>
							<th><fmt:message key="job.field.item"/></th>
							<th><fmt:message key="job.field.employee"/></th>
							<th><fmt:message key="job.field.price_out"/></th>
							<th><fmt:message key="job.field.count"/></th>
							<th><fmt:message key="job.field.sum"/></th>
							<th></th>
						</tr>

						<c:forEach items="${installedItems}" var="item" varStatus="status">
							<tr>
								<td><c:out value="${status.count}" />)</td>
								<td><c:out value="${item.type}" /></td>
								<td><c:out value="${item.employee}" /></td>
								<td><c:out value="${item.outPrice}" /></td>
								<td><c:out value="${item.count}" /></td>
								<td><c:out value="${item.count * item.outPrice}" /></td>
								<td><a href="/swat/job/removeItem/${job.id}/${item.id}">Delete</a></td>
							</tr>
						</c:forEach>
					</table>
					
				<hr/>
				<p>	
					<input type="submit" value="submit"/>
				</p>
			</fieldset>
				
		</form:form>
		
		<form:form modelAttribute="jobItem" action="/swat/job/addItem" method="post" >
			<legend><fmt:message key="item.fields"/></legend>
				<form:hidden path="<%=JobItem.fields.jobId.toString() %>" value="${job.id}"/>	
				<fieldset>
					<p>
						<form:label for="<%=JobItem.fields.item.toString() %>" path="<%=JobItem.fields.item.toString() %>" cssErrorClass="error">
							<fmt:message key="job.field.item"/>
						</form:label><br/>
						
						<form:select path="<%=JobItem.fields.item.toString() %>" 
							cssErrorClass="error"
							multiple="false" >
							<c:forEach items="${itemList}" var="item">
								<form:option value="${item.id}">
									<c:out value="${item.type}"/> (<c:out value="${item.count}"/>) <c:out value="${item.inPrice}"/>
								</form:option>
							</c:forEach>
						</form:select>
						<form:errors path="<%=JobItem.fields.item.toString() %>" />
					</p>
					<p>
						<form:label for="<%=JobItem.fields.description.toString() %>" 
							path="<%=JobItem.fields.description.toString() %>"
							cssErrorClass="error"><fmt:message key="job.field.description"/></form:label>
						<br />
						<form:input path="<%=JobItem.fields.description.toString() %>" />
						<form:errors path="<%=JobItem.fields.description.toString() %>" />
					</p>
					
					<p>
						<form:label for="<%=JobItem.fields.count.toString() %>" 
							path="<%=JobItem.fields.count.toString() %>"
							cssErrorClass="error"><fmt:message key="job.field.count"/></form:label>
						<br />
						<form:input path="<%=JobItem.fields.count.toString() %>" />
						<form:errors path="<%=JobItem.fields.count.toString() %>" />
					</p>
					
					<p>
						<form:label for="<%=JobItem.fields.employee.toString() %>" 
							path="<%=JobItem.fields.employee.toString() %>" 
							cssErrorClass="error">
							<fmt:message key="job.field.employee"/>
						</form:label><br/>
						
						<form:select path="<%=JobItem.fields.employee.toString() %>" 
							items="${employeeList}"
							itemValue="id" itemLabel="name" cssErrorClass="error"
							multiple="false" />
						<form:errors path="<%=JobItem.fields.employee.toString() %>" />
					</p>
					<p>
						<form:label for="<%=JobItem.fields.outPrice.toString() %>" 
							path="<%=JobItem.fields.outPrice.toString() %>"
							cssErrorClass="error"><fmt:message key="job.field.price_out"/></form:label>
						<br />
						<form:input path="<%=JobItem.fields.outPrice.toString() %>" />
						<form:errors path="<%=JobItem.fields.outPrice.toString() %>" />
					</p>
					<hr/>
					<p>	
						<input type="submit" value="submit"/>
					</p>
				</fieldset>
			</form:form>
	</div>
	<hr/>
	
</div>
</body>
</html>