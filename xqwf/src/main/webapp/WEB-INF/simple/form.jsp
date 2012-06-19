<%@include file="../../taglibs.jsp" %>

<html>
<head>
	<META http-equiv="Content-Type"�content="text/html;charset=UTF-8"/>
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
</div>
</body>
</html>