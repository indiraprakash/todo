package ch.bfh.sd.five.todo.datasource;

import javax.servlet.ServletContext;

public final class LoggerServletContextDatasource implements LoggerDatasource {

    private final ServletContext SERVLET_CONTEXT;

    public LoggerServletContextDatasource(ServletContext servletcontext) {
        SERVLET_CONTEXT = servletcontext;
    }

    @Override
    public void log(String message) {
        SERVLET_CONTEXT.log(message);
    }
}
