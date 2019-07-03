package ch.bfh.sd.five.todo.model;

import ch.bfh.sd.five.todo.datasource.TodoUserXmlDatasourceLocalDateAdapter;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.time.LocalDate;

public class Todo implements Serializable {
    private int id;
    private String title = "";
    private String category = "";
    private LocalDate dueDate = LocalDate.MAX;
    private Boolean important = false;
    private Boolean completed = false;

    // constructors
    public Todo() {}
    public Todo(int todoid) {
        id = todoid;
    }

    // getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @XmlJavaTypeAdapter(value = TodoUserXmlDatasourceLocalDateAdapter.class)
    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public Boolean getImportant() {
        return important;
    }

    public void setImportant(Boolean important) {
        this.important = important;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    // calculated properties
    public Boolean isOverdue() {
        return LocalDate.now().isAfter(this.dueDate);
    }

    public Boolean isNotExpiring() {
        return !this.dueDate.isEqual(LocalDate.MAX);
    }

}
