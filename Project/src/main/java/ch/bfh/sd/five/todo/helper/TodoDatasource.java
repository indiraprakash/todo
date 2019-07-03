package ch.bfh.sd.five.todo.helper;

import ch.bfh.sd.five.todo.datasource.TodoUserDatasource;
import ch.bfh.sd.five.todo.model.Todo;
import ch.bfh.sd.five.todo.model.TodoUser;

public class TodoDatasource {
    private static TodoUserDatasource todoUserDatasource;

    public TodoDatasource(TodoUserDatasource todoUserDatasource) {
        this.todoUserDatasource = todoUserDatasource;
    }

    // helper methods
    public static TodoUserDatasource getDatasource() throws NotInitializedException {
        if (todoUserDatasource == null) {
            throw new NotInitializedException("You must first initialize the todo helper once on application startup!");
        }

        return todoUserDatasource;
    }

    // Wrapper on TodoUserDatasource Interface
    public static void addTodoUser(TodoUser todouser) throws NotInitializedException {
        TodoDatasource.getDatasource().addTodoUser(todouser);
    }

    public static TodoUser getTodoUser(String username) throws NotInitializedException {
        return TodoDatasource.getDatasource().getTodoUser(username);
    }

    public static void removeTodoUser(TodoUser todouser) throws NotInitializedException {
        TodoDatasource.getDatasource().removeTodoUser(todouser);
    }

    public static Todo createEmptyTodo() throws NotInitializedException {
        return TodoDatasource.getDatasource().createEmptyTodo();
    }

    public static void initialize() throws NotInitializedException {
        TodoDatasource.getDatasource().initialize();
    }

    public static void persist() throws NotInitializedException {
        TodoDatasource.getDatasource().persist();
    }
}
