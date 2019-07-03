<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page isErrorPage="true" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>TODO-Error</title>
</head>
<body>


<c:choose>
    <c:when test="${not empty message}">
        <h1>${message.status}</h1>
        ${message.message}
    </c:when>
    <c:otherwise>
        <c:set var="exception" value="${requestScope['java.lang.Exception']}"/>
        <jsp:scriptlet>

              exception.printStackTrace(new java.io.PrintWriter(out));

        </jsp:scriptlet>
    </c:otherwise>
</c:choose>
</body>
</html>
