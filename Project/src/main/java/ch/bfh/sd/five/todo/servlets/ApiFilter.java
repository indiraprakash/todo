package ch.bfh.sd.five.todo.servlets;

import ch.bfh.sd.five.todo.helper.TodoLogger;
import ch.bfh.sd.five.todo.helper.TodoSession;
import ch.bfh.sd.five.todo.model.TodoMessageBag;
import ch.bfh.sd.five.todo.model.TodoSeverity;
import ch.bfh.sd.five.todo.model.TodoUser;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(filterName = "APIFilter", urlPatterns = "/api/*")
public class ApiFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        try {
            if (! (httpRequest.getMethod().equalsIgnoreCase("POST") && httpRequest.getRequestURI().matches(".*\\/api\\/users(\\/)?$"))) {
                TodoUser todoUser = TodoSession.getTodoUser(httpRequest);
            }

            chain.doFilter(request, response);

        } catch (NotAuthorizedException e) {
            ((HttpServletResponse)response).sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        } catch (Exception e) {
            TodoLogger.log(new TodoMessageBag(e.getLocalizedMessage(), TodoSeverity.ERROR));
            ((HttpServletResponse)response).sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }
    }

    @Override
    public void destroy() {}
}
