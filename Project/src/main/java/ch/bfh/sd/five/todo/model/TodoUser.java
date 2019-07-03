package ch.bfh.sd.five.todo.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class TodoUser implements Serializable {

    private String username;
    private String password;
    private LinkedList<Todo> todos = new LinkedList<Todo>();

    // constructors
    public TodoUser(){};

    public TodoUser(String username, String password) {
        setUsername(username);
        setPassword(password);
    }

    // getters and setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @XmlElementWrapper(name = "Todos")
    @XmlElement(name = "Todo")
    public LinkedList<Todo> getTodos() {
        return todos;
    }

    public void setTodos(LinkedList<Todo> todos) {
        this.todos = todos;
    }

    // data methods (CRUD)
    public void addTodo(Todo todo) {
        todos.add(todo);
    }

    public void removeTodo(int id) {
        for (int i = 0; i < todos.size(); i++) {
            if (todos.get(i).getId() == id) {
                todos.remove(i);
                return;
            }
        }
    }
    //filter
    public Todo getTodoByIndex(int id) {
        return getTodos().stream().filter(t -> t.getId() == id).findAny().orElse(null);
    }

    // calculated properties
    public List<String> getCategories() {
        return todos.stream().map(Todo::getCategory).distinct().collect(Collectors.toList());
    }

}
