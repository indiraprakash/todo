package ch.bfh.sd.five.todo.datasource;

import ch.bfh.sd.five.todo.helper.TodoLogger;
import ch.bfh.sd.five.todo.model.Todo;
import ch.bfh.sd.five.todo.model.TodoSeverity;
import ch.bfh.sd.five.todo.model.TodoMessageBag;
import ch.bfh.sd.five.todo.model.TodoUser;

import javax.servlet.ServletContext;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileOutputStream;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

public class TodoUserXmlDatasource implements TodoUserDatasource {

    private File xmlDataSource;
    private LinkedList<TodoUser> localTodoUsers;
    private final ServletContext SERVLET_CONTEXT;

    public TodoUserXmlDatasource(File todouserfile, ServletContext servletcontext) {
        SERVLET_CONTEXT = servletcontext;
        xmlDataSource = todouserfile;
    }

    @Override
    public void addTodoUser(TodoUser todouser) {
        localTodoUsers.add(todouser);
    }

    @Override
    public TodoUser getTodoUser(String username) {
        return localTodoUsers.stream().filter(todoUser -> todoUser.getUsername().equals(username)).findFirst().orElse(null);
    }

    @Override
    public void removeTodoUser(TodoUser todouser) {
        localTodoUsers.remove(todouser);
    }

    @Override
    public Todo createEmptyTodo() {
        return new Todo(((AtomicInteger) SERVLET_CONTEXT.getAttribute("TodoPKGenerator")).incrementAndGet());
    }

    @Override
    public void initialize() {
        if (xmlDataSource.exists()) {
            TodoLogger.log("File datasource exists, loading " + xmlDataSource.getAbsolutePath());

            try {
                JAXBContext jaxbContext = JAXBContext.newInstance(TodoUserXmlDatasourceContainer.class);
                Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
                localTodoUsers = new LinkedList<>();

                TodoUserXmlDatasourceContainer todoUserXmlDatasourceContainer = (TodoUserXmlDatasourceContainer) unmarshaller.unmarshal(xmlDataSource);
                TodoLogger.log("There are " + todoUserXmlDatasourceContainer.getTodoUsers().size() + " TodoUsers in the XML datasource.");

                localTodoUsers.addAll(todoUserXmlDatasourceContainer.getTodoUsers());
            } catch (Exception e) {
                TodoLogger.log(new TodoMessageBag("Could not initialize Todo Users: " + e.getLocalizedMessage(), TodoSeverity.ERROR));
            }
        } else {
            TodoLogger.log("File datasource does not exist, creating empty Todo Datasource in memory.");
            localTodoUsers = new LinkedList<>();
        }

        SERVLET_CONTEXT.setAttribute("todoUsers", localTodoUsers);

        // create generator for getting primary keys
        int maxTodos = localTodoUsers.stream()
                .mapToInt(u ->
                        u.getTodos().stream().mapToInt(t -> t.getId()).max().orElse(0)
                )
                .max().orElse(0);
        SERVLET_CONTEXT.setAttribute("TodoPKGenerator", new AtomicInteger(maxTodos));
    }

    @Override
    public void persist() {

        try {

            TodoUserXmlDatasourceContainer todoUserXmlDatasourceContainer = new TodoUserXmlDatasourceContainer();
            todoUserXmlDatasourceContainer.setTodoUsers(localTodoUsers);

            JAXBContext jaxbContext = JAXBContext.newInstance(TodoUserXmlDatasourceContainer.class);
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(todoUserXmlDatasourceContainer, new FileOutputStream(xmlDataSource));

        } catch (Exception e) {
            TodoLogger.log(new TodoMessageBag("Could not persist Todo Users: " + e.getLocalizedMessage(), TodoSeverity.ERROR));
        }
    }
}