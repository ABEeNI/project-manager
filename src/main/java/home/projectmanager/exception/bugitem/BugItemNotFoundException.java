package home.projectmanager.exception.bugitem;

public class BugItemNotFoundException extends RuntimeException {
    public BugItemNotFoundException(String message) {
        super(message);
    }
}
