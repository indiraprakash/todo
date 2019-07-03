package ch.bfh.sd.five.todo.servlets;

public class NotAuthorizedException extends Exception {

    public NotAuthorizedException(String message) {
        super(message);
    }

}
