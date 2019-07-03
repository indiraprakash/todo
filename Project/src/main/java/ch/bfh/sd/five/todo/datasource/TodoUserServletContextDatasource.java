package ch.bfh.sd.five.todo.datasource;

import ch.bfh.sd.five.todo.helper.TodoLogger;
import ch.bfh.sd.five.todo.model.Todo;
import ch.bfh.sd.five.todo.model.TodoSeverity;
import ch.bfh.sd.five.todo.model.TodoMessageBag;
import ch.bfh.sd.five.todo.model.TodoUser;

import javax.servlet.ServletContext;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class TodoUserServletContextDatasource implements TodoUserDatasource {

    private HashMap<String, TodoUser> localTodoUsers;
    private final ServletContext SERVLET_CONTEXT;

    public TodoUserServletContextDatasource(ServletContext servletcontext) {
        SERVLET_CONTEXT = servletcontext;
    }

    @Override
    public void addTodoUser(TodoUser todouser) {
        localTodoUsers.put(todouser.getUsername(), todouser);
    }

    @Override
    public TodoUser getTodoUser(String username) {
        return localTodoUsers.get(username);
    }

    @Override
    public void removeTodoUser(TodoUser todouser) {
        localTodoUsers.remove(todouser.getUsername());
    }


    @Override
    public Todo createEmptyTodo() {
        return new Todo(((AtomicInteger) SERVLET_CONTEXT.getAttribute("TodoPKGenerator")).incrementAndGet());
    }

    @Override
    public void initialize() {

        TodoLogger.log("Create new hashmap for storing the users");
        localTodoUsers = new HashMap<String, TodoUser>();
        SERVLET_CONTEXT.setAttribute("todoUsers", localTodoUsers);

        TodoLogger.log("Create generator for getting primary keys");
        SERVLET_CONTEXT.setAttribute("TodoPKGenerator", new AtomicInteger());
    }

    @Override
    public void persist() {
        // we cannot persist this data
        TodoLogger.log(new TodoMessageBag("TodoUsers are saved in ServletContext - they cannot be Persisted", TodoSeverity.WARNING));
    }
}
