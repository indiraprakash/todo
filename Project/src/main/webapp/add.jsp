<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <link rel="stylesheet" href="style/main.css">


    <style>
        .addtodo {
            font-family: "Lucida Sans Unicode", "Lucida Grande", sans-serif;
            font-size: 0.8em;
            padding: 1em;
            border: 1px solid #ccc;
            border-radius: 3px;
        }
        .addtodo * {
            box-sizing: border-box;
        }

    </style>
</head>


<body>
<header></header>


<h2>Add ToDos</h2>



<!-- FORM POST TO /add -->
<!-- will be processed and sent to /home -->
        <form class="addtodo"method="post" action="add">
            <section>
                <label for="title"></label>
                <input type="text" id="title" name="title" required placeholder="Title">
                <label for="category"></label>
                <input type="text" id ="category" name="category"  placeholder="Category">
                <label for="duedate"></label>
                <input type="date" id= "duedate" name="duedate"  placeholder="Due Date">
                <label>
                    <input type="checkbox" name="important" value="true"> important
                </label>
                <%--<input type="text" id= "important" name="important" required placeholder="is important">--%>
                <button type="submit">Save</button>
            </section>
        </form>
<footer></footer>

</body>
</html>
