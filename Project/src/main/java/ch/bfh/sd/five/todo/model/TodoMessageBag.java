package ch.bfh.sd.five.todo.model;

public class TodoMessageBag {
    private String message;
    private TodoSeverity status;

    public TodoMessageBag(String returnMessage, TodoSeverity returnStatus){
        setMessage(returnMessage);
        setStatus(returnStatus);
    }

    // getters
    public String getMessage() {
        return message;
    }

    public TodoSeverity getStatus() {
        return status;
    }


    // setters
    public void setMessage(String message) {
        this.message = message;
    }

    public void setStatus(TodoSeverity status) {
        this.status = status;
    }
}
