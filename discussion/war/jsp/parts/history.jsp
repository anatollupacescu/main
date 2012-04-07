<div id="menu">
	<ul>
	
		<% String view = (String)request.getParameter(com.util.Const.THEME_KEY); %>
		
		<li <%= (null == view ? "class=\"active\"" : "") %>>
			<a href="<%= request.getContextPath() %>/themes" accesskey="1" title="">Themes</a>
		</li>

		<% java.util.Map<String, String> history = com.util.HistoryUtils.get(request); %>
		<% for(String key : history.keySet()) { %>
		<li <%= (key.equals(view) ? "class=\"active\"" : "") %>>
			<a href="/discussion/theme?<%=com.util.Const.THEME_KEY%>=<%= key %>&<%=com.util.Const.history%>"><%= history.get(key) %>...</a>
		</li>
		<% } %>

	</ul>
</div>