package ch.bfh.sd.five.todo.servlets;


import ch.bfh.sd.five.todo.datasource.TodoUserDatasource;
import ch.bfh.sd.five.todo.datasource.TodoUserFileDatasource;
import ch.bfh.sd.five.todo.datasource.TodoUserServletContextDatasource;
import ch.bfh.sd.five.todo.datasource.TodoUserXmlDatasource;
import ch.bfh.sd.five.todo.helper.TodoDatasource;
import ch.bfh.sd.five.todo.helper.TodoLogger;
import ch.bfh.sd.five.todo.model.TodoSeverity;
import ch.bfh.sd.five.todo.model.TodoMessageBag;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.io.File;

@WebListener
public class DatasourceRegistrar implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        ServletContext servletContext = servletContextEvent.getServletContext();
        TodoLogger.log("Initializing Todo application");

        try {
            String todoDatasourceType = servletContext.getInitParameter("TodoDatasourceType");


            TodoUserDatasource todoUserDatasource;

            switch (todoDatasourceType) {
                case "ServletContext": {
                    todoUserDatasource = new TodoUserServletContextDatasource(servletContext);
                    break;
                }
                case "File": {
                    File datasourceFile = new File(servletContextEvent.getServletContext().getRealPath("/") + servletContext.getInitParameter("TodoFileDatasource"));
                    todoUserDatasource = new TodoUserFileDatasource(datasourceFile, servletContext);
                    break;
                }
                case "Xml": {
                    File datasourceFile = new File(servletContextEvent.getServletContext().getRealPath("/") + servletContext.getInitParameter("TodoXmlDatasource"));
                    todoUserDatasource = new TodoUserXmlDatasource(datasourceFile, servletContext);
                    break;
                }
                default: {
                    todoUserDatasource = new TodoUserServletContextDatasource(servletContext);
                }
            }

            TodoLogger.log("Initializing Todo Datasource of type " + todoDatasourceType);
            todoUserDatasource.initialize();

            TodoLogger.log("Registering new TodoDatasource");
            new TodoDatasource(todoUserDatasource);

        } catch (Exception e) {
            TodoLogger.log(new TodoMessageBag("Could not initialize the Todo Datasource: " + e.getLocalizedMessage(), TodoSeverity.ERROR));
        }

        TodoLogger.log("Initialization of Todo application complete!");
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        TodoLogger.log("Shutting down Todo application");

        try {
            TodoLogger.log("Persisting TodoUsers...");
            TodoDatasource.persist();
        } catch (Exception e) {
            TodoLogger.log(new TodoMessageBag("Could not persist the TodoUsers: " + e.getLocalizedMessage(), TodoSeverity.ERROR));
        }
        TodoLogger.log("Shutting down Todo application complete!");
    }
}
