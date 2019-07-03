package ch.bfh.sd.five.todo.datasource;

import ch.bfh.sd.five.todo.helper.TodoLogger;
import ch.bfh.sd.five.todo.model.Todo;
import ch.bfh.sd.five.todo.model.TodoSeverity;
import ch.bfh.sd.five.todo.model.TodoMessageBag;
import ch.bfh.sd.five.todo.model.TodoUser;

import javax.servlet.ServletContext;
import java.io.*;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class TodoUserFileDatasource implements TodoUserDatasource {

    private File fileDataSource;
    private HashMap<String, TodoUser> localTodoUsers;
    private final ServletContext SERVLET_CONTEXT;

    public TodoUserFileDatasource(File todouserfile, ServletContext servletcontext) {
        SERVLET_CONTEXT = servletcontext;
        fileDataSource = todouserfile;
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
        if (fileDataSource.exists()) {
            TodoLogger.log("File datasource exists, loading " + fileDataSource.getAbsolutePath());

            // load todos from file
            try (ObjectInputStream in = new ObjectInputStream(
                    new FileInputStream(fileDataSource))) {
                localTodoUsers = (HashMap<String, TodoUser>) in.readObject();
            } catch (Exception e) {
                TodoLogger.log(new TodoMessageBag("Could not initialize Todo Users: " + e.getLocalizedMessage(), TodoSeverity.ERROR));
            }
        } else {
            TodoLogger.log("File datasource does not exist, creating empty Todo Datasource in memory.");
            localTodoUsers = new HashMap<String, TodoUser>();
        }

        SERVLET_CONTEXT.setAttribute("todoUsers", localTodoUsers);

        // create generator for getting primary keys
        int maxTodos = localTodoUsers.values().stream()
                .mapToInt(u ->
                        u.getTodos().stream().mapToInt(t -> t.getId()).max().orElse(0)
                )
                .max().orElse(0);
        SERVLET_CONTEXT.setAttribute("TodoPKGenerator", new AtomicInteger(maxTodos));
    }

    @Override
    public void persist() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileDataSource))) {
            out.writeObject(localTodoUsers);
        } catch (Exception e) {
            TodoLogger.log(new TodoMessageBag("Could not persist Todo Users: " + e.getLocalizedMessage(), TodoSeverity.ERROR));
        }
    }
}
