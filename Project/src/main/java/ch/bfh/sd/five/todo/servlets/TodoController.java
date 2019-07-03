package ch.bfh.sd.five.todo.servlets;

import ch.bfh.sd.five.todo.helper.TodoConverter;
import ch.bfh.sd.five.todo.helper.TodoDatasource;
import ch.bfh.sd.five.todo.helper.TodoLogger;
import ch.bfh.sd.five.todo.helper.TodoSession;
import ch.bfh.sd.five.todo.model.Todo;
import ch.bfh.sd.five.todo.model.TodoSeverity;
import ch.bfh.sd.five.todo.model.TodoMessageBag;
import ch.bfh.sd.five.todo.model.TodoUser;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

@WebServlet(urlPatterns = {"/add", "/delete", "/edit", "/get"})
public class TodoController extends HttpServlet {
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        try {
            request.setCharacterEncoding("UTF-8");
            TodoMessageBag todoMessageBag = new TodoMessageBag("", TodoSeverity.INFO);
            TodoUser todoUser = TodoSession.getTodoUser(request.getSession());

            switch (request.getServletPath()) {
                case "/delete": {
                    if (!Collections.list(request.getParameterNames()).contains("id")) {
                        throw new Exception("Delete Todo: You have to specify an id!");
                    }

                    Todo currentTodo = todoUser.getTodoByIndex(Integer.parseInt(request.getParameter("id")));
                    if (currentTodo != null) {
                        todoUser.removeTodo(currentTodo.getId());
                        todoMessageBag.setMessage("Todo with ID " + String.valueOf(currentTodo.getId()) + " removed.");
                        break;
                    }

                    todoMessageBag.setStatus(TodoSeverity.WARNING);
                    todoMessageBag.setMessage("Todo with ID " + String.valueOf(currentTodo.getId()) + " not found!");

                    break;
                }
                case "/add": {
                    Todo currentTodo = TodoDatasource.createEmptyTodo();
                    TodoConverter.convert(request, currentTodo);
                    todoUser.addTodo(currentTodo);
                    todoMessageBag.setMessage("New Todo with id " + currentTodo.getId() + " created!");
                    break;
                }
                case "/edit": {
                    if (!Collections.list(request.getParameterNames()).contains("id")) {
                        throw new Exception("Edit Todo: You have to specify an id!");
                    }

                    Todo currentTodo = todoUser.getTodoByIndex(Integer.parseInt(request.getParameter("id").trim()));
                    if (currentTodo == null) {
                        throw new Exception("Edit Todo: Todo with id " + request.getParameter("id") + " not found!");
                    }

                    TodoConverter.convert(request, currentTodo);
                    todoMessageBag.setMessage("Todo with id " + currentTodo.getId() + " modified!");

                    break;
                }
                case "/get": {
                    throw new Exception("Route '/get' is not supported in POST request");
                }
                default: {
                    throw new Exception("There must be something wrong with your routes!");
                }
            }

            TodoLogger.log(todoUser.getUsername() + ": " + todoMessageBag.getMessage(), TodoSeverity.DEBUG.toString());
            request.setAttribute("message", todoMessageBag);
            response.sendRedirect("home");

        } catch (Exception e) {
            TodoLogger.log(new TodoMessageBag(e.getLocalizedMessage(), TodoSeverity.ERROR));
        }

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {

            // get TodoUser
            TodoUser todoUser = TodoSession.getTodoUser(request.getSession());

            // get single instance and forward to the edit page
            if (Collections.list(request.getParameterNames()).contains("id")) {
                Todo currentTodo = todoUser.getTodoByIndex(Integer.parseInt(request.getParameter("id")));
                request.setAttribute("currentTodo", currentTodo);
                request.getRequestDispatcher("/edit.jsp").forward(request, response);
                return;
            }

            // filter and sort list of todos and save it in the request
            if (Collections.list(request.getParameterNames()).contains("filterCategory")) {
                request.setAttribute("todos",
                        todoUser.getTodos().stream()
                                .filter(t -> t.getCategory().matches(request.getParameter("filterCategory")))
                                .sorted((o1, o2) -> o1.getDueDate().compareTo(o2.getDueDate()))
                                .toArray()
                );
                request.setAttribute("isFiltered", true);
            } else {
                request.setAttribute("todos",
                        todoUser.getTodos().stream()
                                .sorted((o1, o2) -> o1.getDueDate().compareTo(o2.getDueDate()))
                                .toArray()
                );
            }

            request.setAttribute("categories", todoUser.getCategories());
            request.getRequestDispatcher("home.jsp").forward(request, response);

        } catch (Exception e) {
            TodoLogger.log(new TodoMessageBag(e.getLocalizedMessage(), TodoSeverity.ERROR));
            throw new ServletException(e.getLocalizedMessage());
        }
    }
}
