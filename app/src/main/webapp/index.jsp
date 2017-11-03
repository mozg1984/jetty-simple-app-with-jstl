<%@ page language="java" contentType="text/html; charset=UTF-8" 
	pageEncoding="UTF-8" session="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Simple web app</title>
    <style type="text/css">
    	h1 { margin: 2px; }
    	
    	.link {
    		margin: 5px;
    		padding: 2px;
    		width: 50px;
    		background-color: #ffffef;
    		text-align: center;
    	}

    	.login {
    		width: 300px;
    	}
    	
    	.login div {
    		margin: 3px;
    	}
    </style>
</head>
<body>
<h1>Simple web app</h1>

<c:choose> 
    <c:when test="${isAuth != 'true'}">
        <div class="login">
            <form action="/login" method="post">
                <fieldset>
                    <legend>Personal page:</legend>
                    <div><strong>User</strong>:<input type="text" name="user"></div>
                    <div><strong>Password</strong>:<input type="password" name="password"></div>
                    <div><input type="submit" value="Login"></div>
                </fieldset>
            </form>    
        </div>
    </c:when>
    <c:otherwise>
        <div>
            <div class="link"><a href="/about">About</a></div>
            <div class="link"><a href="/profile">Profile</a></div>
            <div class="link"><a href="/logout">Logout</a></div>
        </div>
    </c:otherwise>
</c:choose>
</body>
</html>