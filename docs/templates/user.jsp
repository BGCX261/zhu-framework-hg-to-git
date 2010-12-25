<div align="right">
<%HttpSession httpSession = request.getSession(true);%>
<%if (httpSession.getAttribute("login") != null) {%>
	Hi, <strong><%=(String)httpSession.getAttribute("login")%></strong>!<br />
	<a href="/admin/?logout=true">logout</a>
<%} else {%>
	Hi, <strong>Anonymous</strong>!
	<a href="/admin/">login</a>
<%}%>
</div>
