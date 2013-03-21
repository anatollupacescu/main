<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>

<%@page session="false" %>

<%@taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<html>
<head>
	<META http-equiv="Content-Type" content="text/html;charset=UTF-8"/>
	<title>Simple</title>
	<link rel="stylesheet" href="<c:url value="/resources/blueprint/screen.css" />" type="text/css" media="screen, projection">
	<link rel="stylesheet" href="<c:url value="/resources/blueprint/print.css" />" type="text/css" media="print">
	<!--[if lt IE 8]>
		<link rel="stylesheet" href="<c:url value="/resources/blueprint/ie.css" />" type="text/css" media="screen, projection">
	<![endif]-->
</head>
<body>
<div class="container">
	<h1>
		Simple
	</h1>
	
	<br/><c:out value="${error}" />
	
	<div class="span-12 last">
		<form id="startOrder" method="post" >
		  	<fieldset>
				<legend>Fields</legend>
				<p>
					<label for="name">Name</label><br/>
					<input type="text" id="name" name="name" />
				</p>
				
				<p>	
					<input type="submit" value="submit" name="_eventId_submit" />
				</p>
			</fieldset>
		</form>
	</div>
	<TABLE>
	<c:forEach var="ri" items="${rItems}">
    <TR>
    	<TD>No</TD>
        <c:forEach items="${ri}" var="entry">
			<TH><c:out value="${entry.key}" /></TH>
		</c:forEach>
    </TR>
	  </c:forEach>
	<c:forEach var="ri" items="${rItems}" varStatus="loop">
        <TR>
        <TD><c:out value="${loop.count}" /></TD>
	        <c:forEach items="${ri}" var="entry" >
				<TD><c:out value="${entry.value}" /></TD>
			</c:forEach>
        </TR>
      </c:forEach>
      </TABLE>
</div>
</body>
</html>