package home.projectmanager.exception;


import home.projectmanager.exception.board.BoardNameNotProvidedException;
import home.projectmanager.exception.board.BoardNotFoundException;
import home.projectmanager.exception.bugitem.BugItemTitleNotProvidedError;
import home.projectmanager.exception.bugitemcomment.BugItemCommentNotFoundException;
import home.projectmanager.exception.bugitemcomment.BugItemCommentNotProvided;
import home.projectmanager.exception.project.ProjectAlreadyExistsException;
import home.projectmanager.exception.project.ProjectNameNotProvidedException;
import home.projectmanager.exception.project.ProjectNotFoundException;
import home.projectmanager.exception.team.TeamAlreadyExistsException;
import home.projectmanager.exception.team.TeamNameNotProvidedException;
import home.projectmanager.exception.team.TeamNotFoundException;
import home.projectmanager.exception.user.UserAlreadyExistsException;
import home.projectmanager.exception.user.UserNotFoundException;
import home.projectmanager.exception.workitem.WorkItemNotFoundException;
import home.projectmanager.exception.workitem.WorkItemTitleNotProvidedException;
import home.projectmanager.exception.workitemcomment.WorkItemCommentNotFoundException;
import home.projectmanager.exception.workitemcomment.WorkItemCommentNotProvided;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalControllerExceptionHandler {

    @ExceptionHandler(ProjectNameNotProvidedException.class)
    public ResponseEntity<String> handleNameNotProvided(ProjectNameNotProvidedException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ProjectAlreadyExistsException.class)
    public ResponseEntity<String> handleProjectAlreadyExists(ProjectAlreadyExistsException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ProjectNotFoundException.class)
    public ResponseEntity<String> handleProjectNotFound(ProjectNotFoundException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(TeamNameNotProvidedException.class)
    public ResponseEntity<String> handleNameNotProvided(TeamNameNotProvidedException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TeamAlreadyExistsException.class)
    public ResponseEntity<String> handleTeamAlreadyExists(TeamAlreadyExistsException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TeamNotFoundException.class)
    public ResponseEntity<String> handleTeamNotFound(TeamNotFoundException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFound(UserNotFoundException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<String> handleUserAlreadyExists(UserAlreadyExistsException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BoardNotFoundException.class)
    public ResponseEntity<String> handleBoardNotFound(BoardNotFoundException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BoardNameNotProvidedException.class)
    public ResponseEntity<String> handleBoardNameNotProvided(BoardNameNotProvidedException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(WorkItemNotFoundException.class)
    public ResponseEntity<String> handleWorkItemNotFound(WorkItemNotFoundException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(WorkItemTitleNotProvidedException.class)
    public ResponseEntity<String> handleWorkItemTitleNotProvided(WorkItemTitleNotProvidedException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> handleAccessDenied(AccessDeniedException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(WorkItemCommentNotFoundException.class)
    public ResponseEntity<String> handleWorkItemCommentNotFound(WorkItemCommentNotFoundException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(WorkItemCommentNotProvided.class)
    public ResponseEntity<String> handleWorkItemCommentNotProvided(WorkItemCommentNotProvided exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BugItemTitleNotProvidedError.class)
    public ResponseEntity<String> handleBugItemTitleNotProvided(BugItemTitleNotProvidedError exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BugItemCommentNotProvided.class)
    public ResponseEntity<String> handleBugItemCommentNotProvided(BugItemCommentNotProvided exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BugItemCommentNotFoundException.class)
    public ResponseEntity<String> handleBugItemCommentNotFound(BugItemCommentNotFoundException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
    }
}
