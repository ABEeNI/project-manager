package home.projectmanager.exception.bugitem;

public class BugItemTitleNotProvidedError extends RuntimeException {
    public BugItemTitleNotProvidedError(String message) {
        super(message);
    }
}
