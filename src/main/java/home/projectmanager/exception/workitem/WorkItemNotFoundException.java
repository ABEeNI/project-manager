package home.projectmanager.exception.workitem;

public class WorkItemNotFoundException extends RuntimeException {
    public WorkItemNotFoundException(String message) {
        super(message);
    }
}
