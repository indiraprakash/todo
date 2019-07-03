package ch.bfh.sd.five.todo.servlets;

import ch.bfh.sd.five.todo.helper.TodoLogger;
import ch.bfh.sd.five.todo.helper.TodoPassword;
import ch.bfh.sd.five.todo.helper.TodoSession;
import ch.bfh.sd.five.todo.model.TodoSeverity;
import ch.bfh.sd.five.todo.model.TodoMessageBag;
import ch.bfh.sd.five.todo.model.TodoUser;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

@WebServlet("/login")
public class LoginController extends HttpServlet {

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        try {
            // Get parameter
            List<String> requestParameterNames = Collections.list(request.getParameterNames());

            // check if user and password are sent with the request
            if (! (requestParameterNames.contains("username") && requestParameterNames.contains("password"))) {
                throw new NotAuthorizedException("You have to specify Username and Password!");
            }

            String hashedPassword = TodoPassword.getHashedPassword(request.getParameter("password"));

            // Check if User exists
            TodoUser todoUser = TodoSession.getOrCreateTodoUser(request.getSession() ,request.getParameter("username"), hashedPassword);

            if (! TodoPassword.compare(request.getParameter("password"), todoUser.getPassword())) {
                throw new NotAuthorizedException(request.getParameter("username") + ": You have entered an invalid username or password");
            }
            TodoSession.registerTodoUserInSession(request.getSession(), todoUser);
            TodoLogger.log(new TodoMessageBag(request.getParameter("username") + " has logged in!", TodoSeverity.DEBUG));


            // redirect to home servlets
            response.sendRedirect(request.getContextPath()+"/home");

        } catch (NotAuthorizedException e) {
            // if not authorized, forward to login page
            TodoLogger.log(new TodoMessageBag(e.getLocalizedMessage(), TodoSeverity.WARNING));
            request.setAttribute("message", new TodoMessageBag(e.getLocalizedMessage(), TodoSeverity.ERROR));
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        } catch (Exception e) {
            // other errors, show error page
            TodoMessageBag returnMessage = new TodoMessageBag("The login failed with the following message: " + e.getLocalizedMessage() + " of type '" + e.getClass() + "'", TodoSeverity.ERROR);
            TodoLogger.log(returnMessage);
            request.setAttribute("message", returnMessage);
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Get requests to the login page are forwarded to the login page
        request.setAttribute("message", new TodoMessageBag("You are not logged in!", TodoSeverity.INFO));
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }
}