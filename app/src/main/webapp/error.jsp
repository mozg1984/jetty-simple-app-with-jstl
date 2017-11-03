<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" isErrorPage="true" %> 
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="en">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Simple web app</title>
</head>
<body>
<% if (response.getStatus() == 500) { %>
	<font color="red">Error: <%=exception.getMessage()%></font><br/>
<%@ include file="index.jsp" %>
<% } else { %>
Hi There, error code is <%=response.getStatus()%><br/>
Please go to <a href="/">home page</a>
<% } %>
</body>
</html>