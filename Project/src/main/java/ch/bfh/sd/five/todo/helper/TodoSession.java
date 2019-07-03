package ch.bfh.sd.five.todo.helper;

import ch.bfh.sd.five.todo.model.TodoSeverity;
import ch.bfh.sd.five.todo.model.TodoMessageBag;
import ch.bfh.sd.five.todo.model.TodoUser;
import ch.bfh.sd.five.todo.servlets.NotAuthorizedException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;

public class TodoSession {

    public static TodoUser getTodoUser(String username) throws NotInitializedException {
        return TodoDatasource.getTodoUser(username);
    }

    public static TodoUser getTodoUser(HttpSession session) throws NotInitializedException {
        if (!Collections.list(session.getAttributeNames()).contains("todoUser")) {
            throw new NotInitializedException("Your session has not been initialized!");

        }
        return (TodoUser) session.getAttribute("todoUser");
    }

    public static TodoUser getTodoUser(HttpServletRequest httpRequest) throws NotInitializedException, NotAuthorizedException {

        if (!Collections.list(httpRequest.getHeaderNames()).contains("authorization") || httpRequest.getHeader("authorization") == null || ! httpRequest.getHeader("authorization").toLowerCase().startsWith("basic")) {
            throw new NotAuthorizedException("Missing Authorization header!");
        }

        final String authorization = httpRequest.getHeader("authorization");
        String base64Credentials = authorization.substring("Basic".length()).trim();
        byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
        String credentials = new String(credDecoded, StandardCharsets.UTF_8);
        String[] splittedCredential = credentials.split(":", 2);

        if (splittedCredential.length != 2) {
            throw new NotAuthorizedException("You have to specify Username and Password!");
        }

        TodoUser todoUser = getTodoUser(splittedCredential[0]);
        if (todoUser == null) {
            throw new NotAuthorizedException("User not found!");
        }

        if (! TodoPassword.compare((splittedCredential[1]), todoUser.getPassword())) {
            throw new NotAuthorizedException("User " + todoUser.getUsername() + " is not authorized!");
        }
        return todoUser;
    }

    public static TodoUser getOrCreateTodoUser(HttpSession session, String username, String password) throws Exception {
        TodoUser todoUser = null;

        try {
            todoUser = getTodoUser(session);
            TodoLogger.log(new TodoMessageBag(username + ": TodoUser found in Session", TodoSeverity.DEBUG));
        } catch (NotInitializedException e) {
            TodoLogger.log(new TodoMessageBag(username + ": no TodoUser found in Session", TodoSeverity.DEBUG));
        }

        try {
            todoUser = getTodoUser(username);
            TodoLogger.log(new TodoMessageBag(username + ": no TodoUser found in Datasource", TodoSeverity.DEBUG));
        } catch (NotInitializedException e) {
            TodoLogger.log(new TodoMessageBag(username + ": TodoUser found in Datasource", TodoSeverity.DEBUG));
        }

        if (todoUser == null) {
            todoUser = new TodoUser(username, password);
            TodoDatasource.addTodoUser(todoUser);
            TodoLogger.log(new TodoMessageBag(username + ": Created a new TodoUser", TodoSeverity.DEBUG));
        }

        TodoLogger.log(new TodoMessageBag(username + ": Returning TodoUser", TodoSeverity.DEBUG));
        return todoUser;
    }

    public static void registerTodoUserInSession(HttpSession session, TodoUser todoUser) {
        session.setAttribute("todoUser", todoUser);
    }


}
