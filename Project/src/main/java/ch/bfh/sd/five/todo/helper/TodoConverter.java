package ch.bfh.sd.five.todo.helper;

import ch.bfh.sd.five.todo.model.Todo;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

public class TodoConverter {
    public static void convert(HttpServletRequest request, Todo currentTodo) {

        List<String> requestParameterNames = Collections.list(request.getParameterNames());

        //
        if (requestParameterNames.contains("title")
                && !request.getParameter("title").isEmpty()
                && !currentTodo.getTitle().equals(request.getParameter("title"))) {

            currentTodo.setTitle(request.getParameter("title"));
        }

        if (requestParameterNames.contains("category")
                && !request.getParameter("category").isEmpty()
                && !currentTodo.getCategory().equals(request.getParameter("category"))) {

            currentTodo.setCategory(request.getParameter("category"));
        }

        if (requestParameterNames.contains("duedate")
                && !request.getParameter("duedate").isEmpty()
                && !currentTodo.getCategory().equals(LocalDate.parse(request.getParameter("duedate")))) {
            currentTodo.setDueDate(LocalDate.parse(request.getParameter("duedate")));
        }

        if (requestParameterNames.contains("important") || requestParameterNames.contains("important_hidden")) {
            String importantSetting = requestParameterNames.contains("important") ? request.getParameter("important") : request.getParameter("important_hidden");
            switch (importantSetting) {
                case "on":
                case "true": {
                    if (currentTodo.getImportant().equals(false)) {
                        currentTodo.setImportant(true);
                    }
                    break;
                }
                case "off":
                case "false": {
                    if (currentTodo.getImportant().equals(true)) {
                        currentTodo.setImportant(false);
                    }
                    break;
                }
            }
        }

        if (requestParameterNames.contains("completed") || requestParameterNames.contains("completed_hidden")) {
            String completedSetting = requestParameterNames.contains("completed") ? request.getParameter("completed") : request.getParameter("completed_hidden");
            switch (completedSetting) {
                case "on":
                case "true": {
                    if (currentTodo.getCompleted().equals(false)) {
                        currentTodo.setCompleted(true);
                    }
                    break;
                }
                case "off":
                case "false": {
                    if (currentTodo.getCompleted().equals(true)) {
                        currentTodo.setCompleted(false);
                    }
                    break;
                }
            }
        }
    }

    public static void convert(Todo currentTodo, Todo changedTodo) {
        currentTodo.setCategory(changedTodo.getCategory());
        currentTodo.setCompleted(changedTodo.getCompleted());
        currentTodo.setImportant(changedTodo.getImportant());
        currentTodo.setDueDate(changedTodo.getDueDate());
        currentTodo.setTitle(changedTodo.getTitle());
    }
}
