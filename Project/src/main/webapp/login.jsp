<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>TODO-Login</title>
    <style>
        .container
        {
            background: #DCDCDC;
            border: 1px solid #4ce285;
            border-radius: 6px;
            height: 257px;
            margin: 20px auto 0;
            width: 298px;

        }
        input[type="password"], input[type="text"] {
            border: 1px solid #a1a3a3;
            border-radius: 4px;
            box-shadow: 0 1px #fff;
            box-sizing: border-box;
            color: #696969;
            height: 39px;
            margin: 31px 0 0 29px;
            padding-left: 37px;
            transition: box-shadow 0.3s;
            width: 240px;
        }
        input[type="password"]:focus, input[type="text"]:focus {
            box-shadow: 0 0 4px 1px rgba(55, 166, 155, 0.3);
            outline: 0;
        }


        input[type="submit"] {
            width:240px;
            height:35px;
            display:block;
            font-family:Arial, "Helvetica", sans-serif;
            font-size:16px;
            font-weight:bold;
            color:#fff;
            text-align:center;
            padding-top:6px;
            margin: 29px 0 0 29px;
            position:relative;
            cursor:pointer;
            border: none;
            background-color: #1fb44f;
            background-image: linear-gradient(top,#3db0a6,#3111);

        }

        input[type="submit"]:active {
            top:3px;
            box-shadow: inset 0px 1px 0px #2ab7ec, 0px 2px 0px 0px #31524d, 0px 5px 3px #999;
        }


    </style>
</head>
<body>
<div class="container">

    <form action="login" method="post">

            <input type="text" name="username" id="username" placeholder="Username">
            <br>
            <input type="password" name="password" id="password" placeholder="Password">
            <br>
            <input type="submit" value="Login">
    </form>
</div>
<p align="center">${message.status}: ${message.message}</p>

</body>
</html>
