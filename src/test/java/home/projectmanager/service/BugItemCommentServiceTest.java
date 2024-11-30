package home.projectmanager.service;

import home.projectmanager.dto.UserDto;
import home.projectmanager.dto.BugItemCommentDto;
import home.projectmanager.entity.User;
import home.projectmanager.entity.BugItem;
import home.projectmanager.entity.BugItemComment;
import home.projectmanager.exception.bugitem.BugItemNotFoundException;
import home.projectmanager.exception.bugitemcomment.BugItemCommentNotFoundException;
import home.projectmanager.exception.bugitemcomment.BugItemCommentNotProvided;
import home.projectmanager.repository.BugItemCommentRepository;
import home.projectmanager.repository.BugItemRepository;
import home.projectmanager.service.accesscontrol.AccessDecisionVoter;
import home.projectmanager.service.accesscontrol.AuthenticationFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BugItemCommentServiceTest {

    @Mock
    private BugItemRepository bugItemRepository;

    @Mock
    private BugItemCommentRepository bugItemCommentRepository;

    @Mock
    private AuthenticationFacade authenticationFacade;

    @Mock
    private AccessDecisionVoter accessDecisionVoter;

    @InjectMocks
    private BugItemCommentServiceImpl bugItemCommentService;

    private User currentUser;
    private BugItem bugItem;
    private BugItemComment comment;

    @BeforeEach
    void setUp() {
        currentUser = User.builder()
                .id(1L)
                .email("user@example.com")
                .firstName("John")
                .lastName("Doe")
                .build();

        bugItem = BugItem.builder()
                .id(1L)
                .projectId(10L)
                .build();

        comment = BugItemComment.builder()
                .id(1L)
                .comment("Test Comment")
                .commenter(currentUser)
                .bugItem(bugItem)
                .projectId(10L)
                .build();
    }

    @Test
    void createComment_ShouldReturnCreatedComment_WhenValidData() {
        Long bugItemId = 1L;
        BugItemCommentDto commentDto = BugItemCommentDto.builder()
                .comment("New Comment")
                .build();

        UserDto commenterDto = UserDto.builder()
                .id(currentUser.getId())
                .email(currentUser.getEmail())
                .firstName(currentUser.getFirstName())
                .lastName(currentUser.getLastName())
                .build();
        BugItemCommentDto expectedComment = BugItemCommentDto.builder()
                .comment("New Comment")
                .commenter(commenterDto)
                .build();

        when(bugItemRepository.findById(bugItemId)).thenReturn(Optional.of(bugItem));
        when(accessDecisionVoter.hasPermission(bugItem.getParentProjectId())).thenReturn(true);
        when(authenticationFacade.getCurrentUser()).thenReturn(currentUser);
        when(bugItemCommentRepository.save(any(BugItemComment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BugItemCommentDto result = bugItemCommentService.createComment(bugItem.getId(), commentDto);


        assertEquals(expectedComment, result);
        verify(bugItemCommentRepository, times(1)).save(any(BugItemComment.class));
    }

    @Test
    void createComment_ShouldThrowException_WhenCommentIsNull() {
        Long bugItemId = 1L;
        BugItemCommentDto commentDto = BugItemCommentDto.builder().build();

        assertThrows(BugItemCommentNotProvided.class,
                () -> bugItemCommentService.createComment(bugItemId, commentDto));
    }
    @Test
    void createComment_ShouldThrowException_WhenCommentIsBlank() {
        Long bugItemId = 1L;
        BugItemCommentDto commentDto = BugItemCommentDto.builder().comment("").build();

        assertThrows(BugItemCommentNotProvided.class,
                () -> bugItemCommentService.createComment(bugItemId, commentDto));
    }


    @Test
    void createComment_ShouldThrowException_WhenBugItemNotFound() {
        Long bugItemId = 1L;
        BugItemCommentDto commentDto = BugItemCommentDto.builder()
                .comment("New Comment")
                .build();

        when(bugItemRepository.findById(bugItemId)).thenReturn(Optional.empty());

        assertThrows(BugItemNotFoundException.class,
                () -> bugItemCommentService.createComment(bugItemId, commentDto));
    }

    @Test
    void createComment_ShouldThrowException_WhenAccessDenied() {
        Long bugItemId = 1L;
        BugItemCommentDto commentDto = BugItemCommentDto.builder()
                .comment("New Comment")
                .build();

        when(bugItemRepository.findById(bugItemId)).thenReturn(Optional.of(bugItem));
        when(accessDecisionVoter.hasPermission(bugItem.getParentProjectId())).thenReturn(false);

        assertThrows(AccessDeniedException.class,
                () -> bugItemCommentService.createComment(bugItemId, commentDto));
    }

    @Test
    void updateComment_ShouldReturnUpdatedComment_WhenValidData() {
        BugItemCommentDto updatedCommentDto = BugItemCommentDto.builder()
                .comment("Updated Comment")
                .build();
        UserDto commenterDto = UserDto.builder()
                .id(currentUser.getId())
                .email(currentUser.getEmail())
                .firstName(currentUser.getFirstName())
                .lastName(currentUser.getLastName())
                .build();
        BugItemCommentDto expectedComment = BugItemCommentDto.builder()
                .id(comment.getId())
                .comment("Updated Comment")
                .commenter(commenterDto)
                .build();

        when(bugItemCommentRepository.findById(comment.getId())).thenReturn(Optional.of(comment));
        when(accessDecisionVoter.hasPermission(comment)).thenReturn(true);
        when(bugItemCommentRepository.save(any(BugItemComment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BugItemCommentDto result = bugItemCommentService.updateComment(comment.getId(), updatedCommentDto);

        assertEquals(expectedComment, result);
    }

    @Test
    void updateComment_ShouldThrowException_WhenCommentIsNull() {
        Long commentId = 1L;
        BugItemCommentDto commentDto = BugItemCommentDto.builder().build();

        assertThrows(BugItemCommentNotProvided.class,
                () -> bugItemCommentService.updateComment(commentId, commentDto));
    }
    @Test
    void updateComment_ShouldThrowException_WhenCommentIsBlank() {
        Long commentId = 1L;
        BugItemCommentDto commentDto = BugItemCommentDto.builder().comment("").build();

        assertThrows(BugItemCommentNotProvided.class,
                () -> bugItemCommentService.updateComment(commentId, commentDto));
    }
    @Test
    void updateComment_ShouldThrowException_WhenCommentNotFound() {
        Long commentId = 1L;
        BugItemCommentDto updatedCommentDto = BugItemCommentDto.builder()
                .comment("Updated Comment")
                .build();

        when(bugItemCommentRepository.findById(commentId)).thenReturn(Optional.empty());

        assertThrows(BugItemCommentNotFoundException.class,
                () -> bugItemCommentService.updateComment(commentId, updatedCommentDto));
    }

    @Test
    void deleteComment_ShouldDeleteComment_WhenValidData() {
        Long commentId = 1L;

        when(bugItemCommentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(accessDecisionVoter.hasPermission(comment)).thenReturn(true);

        bugItemCommentService.deleteComment(commentId);

        verify(bugItemCommentRepository, times(1)).delete(comment);
    }

    @Test
    void deleteComment_ShouldThrowException_WhenCommentNotFound() {
        Long commentId = 1L;

        when(bugItemCommentRepository.findById(commentId)).thenReturn(Optional.empty());

        assertThrows(BugItemCommentNotFoundException.class, () -> bugItemCommentService.deleteComment(commentId));
    }

    @Test
    void deleteComment_ShouldThrowException_WhenAccessDenied() {
        Long commentId = 1L;

        when(bugItemCommentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(accessDecisionVoter.hasPermission(comment)).thenReturn(false);

        assertThrows(AccessDeniedException.class, () -> bugItemCommentService.deleteComment(commentId));
    }
}