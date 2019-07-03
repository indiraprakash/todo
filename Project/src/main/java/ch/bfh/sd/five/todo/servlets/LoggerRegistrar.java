package ch.bfh.sd.five.todo.servlets;

import ch.bfh.sd.five.todo.datasource.LoggerDatasource;
import ch.bfh.sd.five.todo.datasource.LoggerServletContextDatasource;
import ch.bfh.sd.five.todo.helper.TodoLogger;
import ch.bfh.sd.five.todo.model.TodoSeverity;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class LoggerRegistrar implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext servletContext = sce.getServletContext();
        String loggerDatasourceType = servletContext.getInitParameter("LoggerDatasourceType");
        String loggerLogLevel = servletContext.getInitParameter("LogLevel");

        LoggerDatasource todoLoggerDatasource = null;

        switch (loggerDatasourceType) {
            case "ServletContext" : {
                todoLoggerDatasource = new LoggerServletContextDatasource(servletContext);
                break;
            }
        }

        TodoLogger todoLogger = new TodoLogger(todoLoggerDatasource, TodoSeverity.valueOf(loggerLogLevel).ordinal());
        TodoLogger.log("Logger initialized");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }
}
