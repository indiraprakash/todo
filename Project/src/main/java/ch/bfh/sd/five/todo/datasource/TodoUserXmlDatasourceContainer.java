package ch.bfh.sd.five.todo.datasource;

import ch.bfh.sd.five.todo.model.Todo;
import ch.bfh.sd.five.todo.model.TodoUser;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement
@XmlSeeAlso({TodoUser.class,Todo.class})
public class TodoUserXmlDatasourceContainer {
    private List<TodoUser> todoUsers = new ArrayList<>();

    public TodoUserXmlDatasourceContainer() {}

    @XmlElementWrapper(name = "TodoUsers")
    @XmlElement (name = "TodoUser")
    public List<TodoUser> getTodoUsers() {
        return todoUsers;
    }

    public void setTodoUsers(List<TodoUser> todoUserList) {
        this.todoUsers = new ArrayList<>(todoUserList);
    }
}