package home.projectmanager.exception;

public class TeamNameNotProvidedException extends RuntimeException {
    public TeamNameNotProvidedException(String message) {
        super(message);
    }
}
