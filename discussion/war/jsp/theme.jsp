<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@page import="com.model.Theme"%>
<%@page import="java.util.*"%>
<%@page import="com.util.*"%>

<% Theme pageTheme = (Theme)request.getAttribute(Theme.field.id.toString()); %>

<html xmlns="http://www.w3.org/1999/xhtml" lang="en">

<%@ include file="parts/header.jsp" %>

<body>

<%@ include file="parts/history.jsp" %>
	
<div id="wrapper">

	<div id="content">
	
	<div style="clear: both;">&nbsp;</div>
	
		<div id="posts" style="float:left">
		<%
		for (Theme post : (List<Theme>) request.getAttribute(Theme.entityGroup)) {
			if (post != null && Theme.Side.PRO.equals(post.getSide())) {
		%>
			<div class="post">
				<div class="story">
					<p style="<%= (post.getPro()  < post.getContra()) ? "color:red" : "" %>"><%=post.getContent()%></p>
				</div>
				<div class="meta">
					
					<p class="date">Posted on <%=post.getDate()%> by <%=post.getAuthor().getName()%></p>
					
					<p class="file"> 
					
						<a href="<%= request.getContextPath() %>/theme?<%=Const.THEME_KEY%>=<%=post.getId() %>" title="pro : <%=post.getPro() %> contra: <%=post.getContra() %>">
							
							Discuss (<%= post.getArgumentCount() %>)
						
						</a>
						
					</p>
					
				</div>
			</div>
		<%
			}
		}
		%>
		
		<% if(session.getAttribute("email") != null) { %>
			<div class="story">
			<form action="<%= request.getContextPath() %>/theme" method="post">
			<input type="hidden" name="<%= Theme.field.id %>" value="<%= pageTheme.getId() %>" />
			<input type="hidden" name="<%= Theme.field.side %>" value="<%= Theme.Side.PRO %>" />
				<div>
					<textarea name="<%= Theme.field.content%>" rows="4"
						cols="30"></textarea>
				</div>
				<div>
					<input type="submit" value="Post argument" />
				</div>
			</form>
			</div>
		
		<% } %>
		
		</div>
		<div id="posts">
		<%
		for (Theme post : (List<Theme>) request.getAttribute(Theme.entityGroup)) {
			 if (post != null && Theme.Side.CONTRA.equals(post.getSide())) {
		%>
			<div class="post">
				<div class="story">
					<p style="<%= (post.getPro()  < post.getContra()) ? "color:red" : "" %>"><%= post.getContent() %></p>
				</div>
				<div class="meta">
					
					<p class="date">Posted on <%=post.getDate()%> by <%=post.getAuthor().getName()%></p>
						
					<p class="file"> 
					
						<a href="<%= request.getContextPath() %>/theme?<%=Const.THEME_KEY%>=<%=post.getId() %>" title="pro : <%=post.getPro() %> contra: <%=post.getContra() %>">
							
							Discuss (<%= post.getArgumentCount() %>)
						
						</a>
						
					</p>
				</div>
			</div>
		<%
			}
		}
		%>
		
		<% if(session.getAttribute("email") != null) { %>
			<div class="story">
			<form action="<%= request.getContextPath() %>/theme" method="post">
			<input type="hidden" name="<%= Theme.field.id %>" value="<%= pageTheme.getId() %>" />
			<input type="hidden" name="<%= Theme.field.side %>" value="<%= Theme.Side.CONTRA %>" />
				<div>
					<textarea name="<%=Theme.field.content%>" rows="4"
						cols="30"></textarea>
				</div>
				<div>
					<input type="submit" value="Post argument" />
				</div>
			</form>
			</div>
		<% } %>
		
		</div>
		<!-- end #links -->
		<div style="clear: both;">&nbsp;</div>
	</div>
</div>
<!-- end #content -->

<%@include file="parts/footer.jsp"%>

</body>

</html>