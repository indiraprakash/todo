package ch.bfh.sd.five.todo.helper;

import ch.bfh.sd.five.todo.datasource.LoggerDatasource;
import ch.bfh.sd.five.todo.model.TodoSeverity;
import ch.bfh.sd.five.todo.model.TodoMessageBag;

import java.util.Arrays;

public class TodoLogger {
    private static LoggerDatasource logger;
    private static int logLevel;

    public TodoLogger(LoggerDatasource logger, int loglevel) {
        TodoLogger.logger = logger;
        logLevel = loglevel;
    }

    public static void log(TodoMessageBag returnMessage) {
        if (returnMessage.getStatus().ordinal() <= logLevel) {
            StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
            StackTraceElement stackTraceElement = Arrays.stream(Arrays.copyOfRange(stackTraceElements, 1, stackTraceElements.length))
                    .filter(s -> s.getClassName().matches("^(?!ch\\.bfh\\.sd\\.five\\.todo\\.helper\\.TodoLogger).+"))
                    .findFirst()
                    .get();

            String logmessage = "TodoLogger - " + stackTraceElement.getClassName() + " - " + stackTraceElement.getMethodName() + " - " + returnMessage.getStatus().toString() + " - " + returnMessage.getMessage();
            logger.log(logmessage);
        }
    }

    public static void log(String message) {
        log(message, "ANY");
    }

    public static void log(String message, String Severity) {
        log(new TodoMessageBag(message, TodoSeverity.valueOf(Severity)));
    }
}
