package ch.bfh.sd.five.todo.servlets;

import ch.bfh.sd.five.todo.helper.NotInitializedException;
import ch.bfh.sd.five.todo.helper.TodoLogger;
import ch.bfh.sd.five.todo.helper.TodoSession;
import ch.bfh.sd.five.todo.model.TodoSeverity;
import ch.bfh.sd.five.todo.model.TodoMessageBag;
import ch.bfh.sd.five.todo.model.TodoUser;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@WebFilter(filterName = "AuthorisationFilter", urlPatterns = "/*")
public class LoginFilter implements Filter {

    private static final List<String> ALLOWED_ROUTES = Arrays.asList("\\/", ".*\\/favicon.ico$", ".*\\/login$", ".*\\/login\\.jsp$", ".*\\/error\\.jsp$", ".*\\/api(\\/)?.*");


    public void init(FilterConfig config) {
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;

        try {
            String allowedRoute = ALLOWED_ROUTES.stream().filter(r -> httpRequest.getRequestURI().matches(r)).findFirst().orElse("");

            if (allowedRoute.isEmpty()) {
                try {
                    TodoUser todoUser = TodoSession.getTodoUser(((HttpServletRequest) request).getSession());
                } catch (NotInitializedException e) {
                    throw new NotAuthorizedException("URL: " + httpRequest.getRequestURI() + ": No User registered in session!");
                }
            }

            chain.doFilter(request, response);

        } catch (NotAuthorizedException e) {
            TodoLogger.log(new TodoMessageBag(e.getLocalizedMessage(), TodoSeverity.ERROR));
            ((HttpServletResponse) response).sendRedirect(httpRequest.getContextPath() + "/login");
        } catch (Exception e) {
            String errorMessage = "Login Filter: Exception thrown " +
                    "of type: " + e.getClass() + "<br />" +
                    "on line: " + e.getStackTrace()[0].getLineNumber() + "<br />" +
                    "with cause: " + e.getCause() + "<br />" +
                    "and message: " + e.getLocalizedMessage();

            TodoMessageBag todoMessageBag = new TodoMessageBag(errorMessage, TodoSeverity.ERROR);
            TodoLogger.log(todoMessageBag);

            request.setAttribute("message", todoMessageBag);
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        }
    }

    public void destroy() {
    }
}