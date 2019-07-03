<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix='c' uri='http://java.sun.com/jsp/jstl/core' %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page errorPage="error.jsp" %>
<html>
<head>
    <title>Title</title>
    <link rel="stylesheet" href="style/main.css">

    <link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.6.1/css/all.css"
          integrity="sha384-gfdkjb5BdAXd+lj+gudLWI+BXq4IuLW5IT+brZEZsLFm++aCMlF1V92rMkPaX4PP" crossorigin="anonymous">

    <style>
        i.fas.fa-check:hover {
            cursor: pointer;
        }
        i.far.fa-trash-alt:hover {
            cursor: pointer;
        }
    </style>
    <script language="JavaScript" type="application/javascript">
        function completeTodo(id) {
            var xmlHttpRequest = new XMLHttpRequest();
            xmlHttpRequest.open("POST", 'edit', true);
            xmlHttpRequest.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");

            xmlHttpRequest.onreadystatechange = function () {
                if (this.readyState === XMLHttpRequest.DONE && this.status === 200) {
                    window.location = "${pageContext.request.contextPath}/home";
                }
            }
            xmlHttpRequest.send("id=" + id + "&completed=true");
        }

        function deleteTodo(id) {
            var xmlHttpRequest = new XMLHttpRequest();
            xmlHttpRequest.open("POST", 'delete', true);
            xmlHttpRequest.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");

            xmlHttpRequest.onreadystatechange = function () {
                if (this.readyState === XMLHttpRequest.DONE && this.status === 200) {
                    window.location = "home";
                }
            }
            xmlHttpRequest.send("id=" + id);
        }
    </script>
</head>
<body>
<header>TODO</header>
<div id="main">
<%--<h2>Anz.ToDos sind : <c:out value="${fn:length(todos)}"></c:out></h2>--%>
<%--if there is no todo then redirect to add--%>
<c:if test="${(fn:length(todos)) < 1}">
    <c:redirect url="/add.jsp"/>
</c:if>

<c:if test="${isFiltered}">
    <a href="?"><i class="fas fa-filter"></i> Filter has been applied - remove</a>
</c:if>

<table>
    <tr>
        <th></th>
        <th>ToDo</th>
        <th>Category</th>
        <th>Due Date</th>
        <th></th>
        <th></th>
        <th></th>
        <th></th>
    </tr>
    <c:forEach var="todo" items="${todos}">

        <%--
            set css class name according to todo-status
        --%>
        <c:choose>
            <c:when test="${todo.completed}">
                <c:set value="completed" var="todo_row"></c:set>
            </c:when>
            <c:when test="${todo.isOverdue()}">
                <c:set value="overdue" var="todo_row"></c:set>
            </c:when>
            <c:otherwise>
                <c:set value="todo" var="todo_row"></c:set>
            </c:otherwise>
        </c:choose>


        <%-- Conditional css class--%>
        <tr class="${todo_row}">
            <td>
                <c:choose>
                    <c:when test="${todo.important}">
                        <i class="fas fa-exclamation-circle" style="color: red"></i>
                    </c:when>
                </c:choose>
            </td>
            <td><c:out value="${todo.title}"></c:out></td>
            <td>
                <c:if test="${not empty todo.category}">
                    <a href="?filterCategory=${todo.category}"><c:out value="${todo.category}"></c:out></a></td>
                </c:if>
            <td>
                <c:if test="${todo.isNotExpiring()}">
                    <c:out value="${todo.dueDate}"></c:out>
                </c:if>
            </td>
            <td><a href="add.jsp"> <i class="fas fa-plus"></i> </a></td>
            <td><a href="get?id=${todo.id}"><i class="far fa-edit"></i> </a></td>
            <td><i onclick="deleteTodo(${todo.id})" class="far fa-trash-alt fa-1x"></i></td>
            <td>
            <i onclick="completeTodo(${todo.id})" class="fas fa-check fa-1x"></i>

        <%--<button onclick="completeTodo(${todo.id})"><i class="fas fa-check fa-1x"></i></button>--%>
            </td>
        </tr>
    </c:forEach>


</table>
</div>
<footer>Footer</footer>
</body>
</html>
