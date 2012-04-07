<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@page import="java.util.*"%>
<%@page import="com.util.Const"%>
<%@page import="com.model.Theme"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Frameset//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-frameset.dtd">

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">

<%@include file="parts/header.jsp"%>

<body>

<!-- start #header -->
	<div id="menu">
		<ul>
			<li class="active"><a href="<%= request.getContextPath() %>/themes" accesskey="1" title="">Discussion</a></li>
		</ul>
	</div>
<!-- end #header -->

<!-- start #content -->
<div id="wrapper">
	<div id="content">
		<div style="clear: both;">&nbsp;</div>
		<h2 class="title">Login form</h2>
		<div style="clear: both;">&nbsp;</div>
		<%= request.getAttribute("error") == null ? "" : "<hr/>"+request.getAttribute("error")+"<hr/>" %>
		<form action="<%=  request.getContextPath() %>/login" method="post">
			<div>
				<a>Email</a>&nbsp;
				<input type="text" name="email" value="<%= request.getAttribute("email") == null ? "" : request.getAttribute("email") %>"/>&nbsp;
				<%= request.getAttribute("erroremail") == null ? "" : request.getAttribute("erroremail") %>
			</div>
			<br/>
			<div>
				<a>Password</a>&nbsp;
				<input type="password" name="password" />&nbsp;
				<%=(request.getAttribute("errorpassword") == null) ? "" : request.getAttribute("errorpassword") %>
			</div>
			<br/>
			<div>
				<input type="submit" value="Login" />
			</div>
			<br/>
			<div>
				<a>Username</a>&nbsp;
				<input type="text" name="username" value="<%=(request.getAttribute("username") == null) ? "" : request.getAttribute("username") %>"/>&nbsp;
				<%=(request.getAttribute("errorusername") == null) ? "" : request.getAttribute("errorusername") %>
			</div>
			<br/>
			<div>
				<input type="submit" name = "Register" value="Register" />
			</div>
			<br/>
		</form>
	</div>
</div>
<!-- end #content -->

<!-- start #footer -->
<div id="footer">
	<p id="legal">Copyright © 2012 The Green House. Designed by <a href="http://www.freecsstemplates.org/">Free CSS Templates</a></p>
	<p id="brand">The Green House</p>
</div>
<!-- end #footer -->

</body>
</html>