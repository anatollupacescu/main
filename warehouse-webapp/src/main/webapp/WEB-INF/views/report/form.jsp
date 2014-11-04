<%@page import="com.funny.basic.ItemState"%>
<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@ page session="false" %>
<%@ page import="com.funny.ui.Report" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<html>
<head>
	<META http-equiv="Content-Type" content="text/html;charset=UTF-8">
	<title><fmt:message key="report.title"/></title>
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
		<fmt:message key="report.title"/>
	</h1>
	<div class="span-12 last">	
		<form:form modelAttribute="report" method="post">
		  	<fieldset>		
				<legend><fmt:message key="report.criteria"/></legend>
				<table>
				<tr>
				<td>
					<form:label	for="<%=Report.fields.client.toString() %>" 
						path="<%=Report.fields.client.toString() %>"><fmt:message key="report.column.client"/></form:label><br/>
					<form:select path="<%=Report.fields.client.toString() %>">
						<form:option value="-1" label="Select" />
						<form:options items="${report.clientList}" 
							itemValue="id" itemLabel="name" />
					</form:select>
				</td>
				
				<td>
					<form:label	for="<%=Report.fields.employee.toString() %>" 
						path="<%=Report.fields.employee.toString() %>"><fmt:message key="report.column.employee"/></form:label><br/>
					<form:select path="<%=Report.fields.employee.toString() %>">
						<form:option value="-1" label="Select" />
						<form:options items="${report.employeeList}" 
							itemValue="id" itemLabel="name" />
					</form:select>
				</td>

				<td>
					<form:label	for="<%=Report.fields.type.toString() %>" 
						path="<%=Report.fields.type.toString() %>" ><fmt:message key="report.column.type"/></form:label><br/>
					<form:select path="<%=Report.fields.type.toString() %>">
						<form:option value="-1" label="Select" />
						<form:options items="${report.typeList}" 
							itemValue="id" itemLabel="name" />
					</form:select>
				</td>

				<td>
					<form:label	for="<%=Report.fields.state.toString() %>" 
						path="<%=Report.fields.state.toString() %>" ><fmt:message key="report.column.state"/></form:label><br/>
					<form:select path="<%=Report.fields.state.toString() %>">
						<form:option value="ANY" label="Select" />
						<form:options items="${report.itemStateList}" />
					</form:select>
				</td>

				<tr>
					<td>
					<form:label	for="<%=Report.fields.dateFrom.toString() %>" 
						path="<%=Report.fields.dateFrom.toString() %>"><fmt:message key="report.column.from_date"/></form:label><br/>
					<form:input path="<%=Report.fields.dateFrom.toString() %>" cssErrorClass="error"/>
					<form:errors path="<%=Report.fields.dateFrom.toString() %>" />
					</td>
					<td>
					<form:label	for="<%=Report.fields.dateFrom.toString() %>" 
						path="<%=Report.fields.dateFrom.toString() %>"><fmt:message key="report.column.to_date"/></form:label><br/>
					<form:input path="<%=Report.fields.dateTo.toString() %>" cssErrorClass="error"/>
					<form:errors path="<%=Report.fields.dateTo.toString() %>" />
					</td>
					<td></td>
					<td><input type="submit" value="submit"/></td>
				<tr>

				</tr>
				</table>
			</fieldset>
		</form:form>
	</div>
	
	<hr/>
	
	<table>
	
	<tr>
		<th><fmt:message key="report.column.type"/></th>
		<th><fmt:message key="report.column.state"/></th>
		<th><fmt:message key="report.column.date_in"/></th>
		<th><fmt:message key="report.column.date_out"/></th>
		<th><fmt:message key="report.column.client"/></th>
		<th><fmt:message key="report.column.employee"/></th>
		<th><fmt:message key="report.column.count"/></th>
		<th><fmt:message key="report.column.price_in"/></th>
		<th><fmt:message key="report.column.sum_in"/></th>
		<th><fmt:message key="report.column.price_out"/></th>
		<th><fmt:message key="report.column.sum_out"/></th>
		<c:if test="${report.state eq 'IESIRE'}">
			<th><fmt:message key="report.column.revenue"/></th>
		</c:if>
	</tr>
	
	<c:set var="totalCount" value="0" scope="page" />
	<c:set var="totalSum" value="0" scope="page" />
	
	<c:forEach items="${reportItems}" var="current">
        <tr>
          <td><a href="update/${current.id}/"><c:out value="${current.type}" /></a></td>
          <td><c:out value="${current.state}" /></td>
          <td><fmt:formatDate pattern="yyyy/MM/dd" value="${current.inDate}" /></td>
          <td><fmt:formatDate pattern="yyyy/MM/dd" value="${current.outDate}" /></td>
          <td><c:out value="${current.job.client}" /></td>
          <td><c:out value="${current.employee}" /></td>
          <td><c:out value="${current.count}" /></td>
          <td><c:out value="${current.inPrice}" /></td>
          <td><c:out value="${current.inPrice * current.count}" /></td>
          <td><c:out value="${current.outPrice}" /></td>
          <td><c:out value="${current.outPrice * current.count}" /></td>
          <c:if test="${report.state eq 'IESIRE'}">
          	<td><c:out value="${current.count * (current.outPrice - current.inPrice)}" /></td>
          </c:if>
        </tr>
        
        <c:set var="totalCount" value="${totalCount + current.count}" scope="page" />
		<c:set var="totalSum" value="${totalSum + (current.count * current.inPrice)}" scope="page" />
		
      </c:forEach>
      
			<tr>
				<td>Total</td>
				<td></td>
				<td></td>
				<td></td>
				<td></td>
				<td></td>
				<td><c:out value="${totalCount}" /></td>
				<td></td>
				<td><c:out value="${totalSum}" /></td>
				<td></td>				
			</tr>
	
     </table>

</div>
</body>
</html>