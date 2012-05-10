<%@include file="taglibs.jsp" %>

<html>
<head>
	<META http-equiv="Content-Type" content="text/html;charset=UTF-8">
	<title><fmt:message key="welcome.title"/></title>
	<link rel="stylesheet" href="<c:url value="/resources/blueprint/screen.css" />" type="text/css" media="screen, projection">
	<link rel="stylesheet" href="<c:url value="/resources/blueprint/print.css" />" type="text/css" media="print">
	<!--[if lt IE 8]>
		<link rel="stylesheet" href="<c:url value="/resources/blueprint/ie.css" />" type="text/css" media="screen, projection">
	<![endif]-->
</head>
<body>
<div class="container">  
	<p>
		<h1><fmt:message key="welcome.title"/></h1>
	</p>
	
	<hr>	
		<p> <a href="?locale=en_us">en</a> | <a href="?locale=ro">ro</a> |  <a href="?locale=ru">ru</a> </p>
	<ul>
		<li><a href="id/user/"><fmt:message key="welcome.clients"/></a></li>
	</ul>
</div>
</body>
</html>