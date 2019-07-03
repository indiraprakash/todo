<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix='c' uri='http://java.sun.com/jsp/jstl/core' %>

<html>
<head>
    <title>Edit Todo</title>

    <!-- FORM POST TO /edit -->
    <!-- will be processed and sent to /home -->
    <link rel="stylesheet" href="style/main.css">

    <style>
    .container
    {
        display: flex;
        align-items: center;
        justify-content: center;
        height: 100vh;
        background-color: rgba(180, 180, 180, 0.05);

    }
</style>


</head>
<body>
<header>TODO</header>

<div class="container">

        <form name="edit" action="edit" method="post">
            <input type="hidden" name="id" value="<c:out value="${currentTodo.id}"/> ">
            TODO:<br>
            <input type="text" name="title" value="<c:out value="${currentTodo.title}"/>" >
            <br>
            Category:<br>
            <input type="text" name="category" value="<c:out value="${currentTodo.category}"/>">
            <br>
            Due Date:<br>
            <input type="text" name="duedate" value="<c:out value="${currentTodo.dueDate}"/>">
            <br>
            <input type="hidden" name="completed_hidden" value="false"/>
            <c:choose>
                <c:when test="${currentTodo.completed}">
                    <input type="checkbox" name="completed" checked/>
                </c:when>
                <c:otherwise>
                    <input type="checkbox" name="completed" />
                </c:otherwise>
            </c:choose> Complete
            <br>
            <input type="hidden" name="important_hidden" value="false"/>
            <c:choose>
                <c:when test="${currentTodo.important}">
                    <input type="checkbox" name="important" checked/>
                </c:when>
                <c:otherwise>
                    <input type="checkbox" name="important"/>
                </c:otherwise>
            </c:choose> Important
            <br>
            <input type="submit" value="Save">

        </form>

</div>
<footer>Footer</footer>


</body>
</html>
