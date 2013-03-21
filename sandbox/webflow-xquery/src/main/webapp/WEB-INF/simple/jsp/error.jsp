<%@include file="../../taglibs.jsp" %>

<html>
<head>
	<META http-equiv="Content-Type" content="text/html;charset=UTF-8"/>
	<title><fmt:message key="client.title"/></title>
	<link rel="stylesheet" href="<c:url value="/resources/blueprint/screen.css" />" type="text/css" media="screen, projection">
	<link rel="stylesheet" href="<c:url value="/resources/blueprint/print.css" />" type="text/css" media="print">
	<!--[if lt IE 8]>
		<link rel="stylesheet" href="<c:url value="/resources/blueprint/ie.css" />" type="text/css" media="screen, projection">
	<![endif]-->
</head>	
<body>
	<div class="container">
		<h2>
			<c:out value="${error}" />
		</h2>
	</div>
</body>
</html>