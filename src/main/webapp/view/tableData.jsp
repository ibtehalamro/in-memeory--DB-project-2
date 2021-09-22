<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Table Content</title>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
    <link href="/CSS/main.css" rel="stylesheet" type="text/css">
</head>
<body>
${errorMessage}

<div class="container">

    <%@ include file="../view/Header.jsp" %>
    <%@ include file="../view/QueryForm.jsp" %>
    <%@ include file="../view/Table.jsp" %>
</div>

</body>
</html>
