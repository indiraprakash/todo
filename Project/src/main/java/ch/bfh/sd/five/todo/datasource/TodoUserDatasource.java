package ch.bfh.sd.five.todo.datasource;

import ch.bfh.sd.five.todo.model.Todo;
import ch.bfh.sd.five.todo.model.TodoUser;

public interface TodoUserDatasource {

    //manipulate TodoUser
    void addTodoUser(TodoUser todouser);
    TodoUser getTodoUser(String username);
    void removeTodoUser(TodoUser todouser);

    //create TodoObject
    Todo createEmptyTodo();

    // load and persist data (startup / shutdown)
    void initialize();
    void persist();

}
