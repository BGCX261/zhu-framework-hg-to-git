<%@ include file="../templates/header.jsp" %>
<h2>login</h2>
<%@ include file="../templates/user.jsp" %>

<%if (request.getParameter("auth") != null && request.getParameter("auth").equals("false")) {%>
<h3><font color="red">authorization failed</font></h3>
<%}%>
<form name="login" action="/admin/" method="post">
	<table cellspacing="0" cellpadding="0" border="0">
		<tr><td>login:</td> <td><input type="text" name="f_login" /></td></tr>
		<tr><td>password:</td> <td><input type="password" name="f_password" /></td></tr>
		<tr><td colspan="2" align="right"><input type="submit" value="login"></td></tr>
	</table>
</form>

<%@ include file="../templates/footer.jsp" %>