package ch.bfh.sd.five.todo.servlets;

import ch.bfh.sd.five.todo.helper.*;
import ch.bfh.sd.five.todo.model.Todo;
import ch.bfh.sd.five.todo.model.TodoMessageBag;
import ch.bfh.sd.five.todo.model.TodoSeverity;
import ch.bfh.sd.five.todo.model.TodoUser;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;

@WebServlet(urlPatterns = {"/api/*"})
public class RestController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // check accept header
        if (!Collections.list(request.getHeaderNames()).contains("accept") || !request.getHeader("accept").equals("application/json")) {
            TodoLogger.log(new TodoMessageBag("accept header missing", TodoSeverity.ERROR));
            response.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE);
            return;
        }

        // prepare response
        String returnData = "";

        try {

            if (request.getRequestURI().matches(".*\\/api\\/todos(\\/)?$")) {
                TodoUser todoUser = TodoSession.getTodoUser(request);
                List<Todo> todos = todoUser.getTodos();

                if (Collections.list(request.getParameterNames()).contains("category")) {
                    final String todoFilter = (String) request.getParameter("category");
                    todos = todos.stream()
                            .filter(t -> t.getCategory().matches(todoFilter))
                            .collect(Collectors.toList());
                }

                returnData = JsonConverter.getJsonFromList(todos);

            } else if (request.getRequestURI().matches(".*\\/api\\/todos\\/(\\d+)?$")) {
                TodoUser todoUser = TodoSession.getTodoUser(request);

                String[] parts = request.getRequestURI().split("\\/");

                int todoId = Integer.parseInt(parts[parts.length - 1]);

                Todo todo = todoUser.getTodoByIndex(todoId);

                if (todo == null) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }

                returnData = JsonConverter.getJson(todo);

            } else if (request.getRequestURI().matches(".*\\/api\\/categories(\\/)?$")) {
                TodoUser todoUser = TodoSession.getTodoUser(request);

                returnData = JsonConverter.getJsonFromList(todoUser.getCategories());
            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            }

        } catch (NotAuthorizedException e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        } catch (Exception e) {
            TodoLogger.log(new TodoMessageBag(e.getLocalizedMessage(), TodoSeverity.ERROR));
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

        response.setHeader("Content-Type", "application/json");
        try (PrintWriter out = response.getWriter()) {
            response.setContentType("application/json");
            out.println(returnData);
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        if (!request.getRequestURI().matches(".*\\/api\\/todos\\/(\\d+)?$")) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            String[] parts = request.getRequestURI().split("\\/");
            int todoId = Integer.parseInt(parts[parts.length - 1]);

            TodoUser todoUser = TodoSession.getTodoUser(request);

            Todo changedTodo = JsonConverter.getObject(request.getReader().lines().collect(Collectors.joining()), Todo.class);
            Todo currentTodo = todoUser.getTodoByIndex(todoId);

            if (currentTodo == null) {
                //todoitem not found
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            if (changedTodo.getId() != todoId) {
                //invalid data
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            TodoConverter.convert(currentTodo, changedTodo);

            //todoitem updated
            response.sendError(HttpServletResponse.SC_NO_CONTENT);

        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!request.getRequestURI().matches(".*\\/api\\/todos\\/(\\d+)?$")) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }

        try {
            String[] parts = request.getRequestURI().split("\\/");

            int todoId = Integer.parseInt(parts[parts.length - 1]);

            if (TodoSession.getTodoUser(request).getTodoByIndex(todoId) == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            TodoSession.getTodoUser(request).removeTodo(todoId);

            response.sendError(HttpServletResponse.SC_NO_CONTENT);

        } catch (Exception e) {
            TodoLogger.log(new TodoMessageBag(e.getLocalizedMessage(), TodoSeverity.ERROR));
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // check content type
        if (!request.getContentType().equals("application/json")) {
            response.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
            return;
        }

        try {
            // register user
            if (request.getRequestURI().matches(".*\\/api\\/users(\\/)?$")) {

                TodoCredential todoCredential = (TodoCredential) JsonConverter.getObject(request.getReader().lines().collect(Collectors.joining()), TodoCredential.class);
                if (!todoCredential.isValid()) {
                    //invalid user data
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                    return;
                }

                if (TodoDatasource.getTodoUser(todoCredential.getName()) != null) {
                    // user already exists
                    TodoLogger.log(new TodoMessageBag("User: " + todoCredential.getName() + " already exist!", TodoSeverity.ERROR));
                    response.sendError(HttpServletResponse.SC_CONFLICT);
                    return;
                }

                TodoDatasource.addTodoUser(new TodoUser(todoCredential.getName(), TodoPassword.getHashedPassword(todoCredential.getPassword())));

                response.sendError(HttpServletResponse.SC_CREATED);
            } else if (request.getRequestURI().matches(".*\\/api\\/todos(\\/)?$")) {
                Todo todo = (Todo) JsonConverter.getObject(request.getReader().lines().collect(Collectors.joining()), Todo.class);

                if (todo.getTitle() == null || todo.getTitle().isEmpty()) {
                    //invalid tododata
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                    return;
                }

                todo.setId(TodoDatasource.getDatasource().createEmptyTodo().getId());
                TodoSession.getTodoUser(request).addTodo(todo);

                // todoitem created

                Map<String, Integer> returnObject = new HashMap<String, Integer>();
                returnObject.put("id", Integer.valueOf(todo.getId()));

                String returnData = JsonConverter.getJson(returnObject);
                response.setHeader("Content-Type", "application/json");
                response.setStatus(201);
                try (PrintWriter out = response.getWriter()) {
                    response.setContentType("application/json");
                    out.println(returnData);
                }

            }
        } catch  (Exception e) {
            TodoLogger.log(new TodoMessageBag(e.getLocalizedMessage(), TodoSeverity.ERROR));
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

}
