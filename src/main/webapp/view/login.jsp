<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
    <title>Title</title>
    <link href="/CSS/main.css" rel="stylesheet" type="text/css">

</head>
<body>

    <div class="login">

        <div class="login-title">Sign In</div>

        <div class="login-body">
            <form action="${pageContext.request.contextPath}/login" method="POST">
                <div>
                    <div>
                        <!-- Check for login error -->
                        <c:if test="${param.error != null}">
                            <div>
                                Invalid username and password.
                            </div>
                        </c:if>
                        <!-- Check for logout -->
                        <c:if test="${param.logout != null}">
                            <div>
                                You have been logged out.
                            </div>
                        </c:if>
                    </div>
                </div>
                <div class="form-group">

                    <input type="text" name="username" placeholder="username" class="form-control">
                </div>
                <div class="form-group">


                    <input type="password" name="password" placeholder="password" class="form-control">
                </div>
                <button type="submit">Login</button>
                 <input type="hidden"
                       name="${_csrf.parameterName}"
                        value="${_csrf.token}"/>
            </form>
        </div>
    </div>

</body>
</html>
