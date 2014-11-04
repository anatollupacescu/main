<%@page import="com.util.HistoryUtils"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@page import="java.util.*"%>
<%@page import="com.util.Const"%>
<%@page import="com.model.Theme"%>

<html xmlns="http://www.w3.org/1999/xhtml" lang="en">

<%@include file="parts/header.jsp"%>

<body>

<%@include file="parts/history.jsp" %>
	
<div id="wrapper">
	<div id="content">
		<div style="clear: both;">&nbsp;</div>
		<h2 class="title">Discussions:</h2>
		<div style="clear: both;">&nbsp;</div>
		<!-- start #themes -->

		<ul>
			<%  List<Theme> themes = (List<Theme>)request.getAttribute(com.model.Theme.entityGroup); %>
			
			<% for (Theme theme : themes) {
	        	if(theme != null) {
	 		%>
					<li>
						<a href="<%= request.getContextPath() %>/theme?<%=Const.THEME_KEY %>=<%= theme.getId() %>">
							<%=theme.getContent() %> (<%= theme.getAuthor().getEmail() %>)
						</a>&nbsp;
						<a>(<%=theme.getPro() %> : <%=theme.getContra() %>)</a>
						&nbsp;
						<a href="<%=  request.getContextPath() %>?delete=<%= theme.getId() %>">[x]</a></li>
			<%
	         	}
	        }
			%>
		</ul>

	<% if(session.getAttribute("email") != null) { %>
	
			<div style="clear: both;">&nbsp;</div>
			<form action="<%=  request.getContextPath() %>/themes" method="post">
				<div>
					<textarea name="<%= Theme.field.content %>" rows="4" cols="50"></textarea>
				</div>
				<div>
					<input type="submit" value="Post argument" />
				</div>
			</form>
			
	<% } %>

	</div>
	
</div>
<!-- end #content -->

<%@include file="parts/footer.jsp"%>

</body>

</html>