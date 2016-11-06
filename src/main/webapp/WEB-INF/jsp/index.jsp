<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Spring Boot Demo</title>
<script type="text/javascript" src="${pageContext.request.contextPath }/webjarslocator/jquery/jquery.js"></script>
<script type="text/javascript">
$(function(){
	alert("ok");
})
</script>
</head>

<body>
    Time: ${time}
    <br>
    Message: ${message}
</body>
</html>