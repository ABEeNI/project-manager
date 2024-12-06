package home.projectmanager.service;

import home.projectmanager.dto.UserDto;
import home.projectmanager.dto.WorkItemCommentDto;
import home.projectmanager.entity.User;
import home.projectmanager.entity.WorkItem;
import home.projectmanager.entity.WorkItemComment;
import home.projectmanager.exception.workitem.WorkItemNotFoundException;
import home.projectmanager.exception.workitemcomment.WorkItemCommentNotFoundException;
import home.projectmanager.exception.workitemcomment.WorkItemCommentNotProvided;
import home.projectmanager.repository.WorkItemCommentRepository;
import home.projectmanager.repository.WorkItemRepository;
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
class WorkItemCommentServiceTest {

    @Mock
    private WorkItemRepository workItemRepository;

    @Mock
    private WorkItemCommentRepository workItemCommentRepository;

    @Mock
    private AuthenticationFacade authenticationFacade;

    @Mock
    private AccessDecisionVoter accessDecisionVoter;

    @InjectMocks
    private WorkItemCommentServiceImpl workItemCommentService;

    private User currentUser;
    private WorkItem workItem;
    private WorkItemComment comment;

    @BeforeEach
    void setUp() {
        currentUser = User.builder()
                .id(1L)
                .email("user@example.com")
                .firstName("John")
                .lastName("Doe")
                .build();

        workItem = WorkItem.builder()
                .id(1L)
                .boardId(1L)
                .projectId(10L)
                .build();

        comment = WorkItemComment.builder()
                .id(1L)
                .comment("Test Comment")
                .commenter(currentUser)
                .workItem(workItem)
                .projectId(10L)
                .build();
    }

    @Test
    void createComment_ShouldReturnCreatedComment_WhenValidData() {
        Long workItemId = 1L;
        WorkItemCommentDto commentDto = WorkItemCommentDto.builder()
                .comment("New Comment")
                .build();

        UserDto commenterDto = UserDto.builder()
                .id(currentUser.getId())
                .email(currentUser.getEmail())
                .firstName(currentUser.getFirstName())
                .lastName(currentUser.getLastName())
                .build();
        WorkItemCommentDto expectedComment = WorkItemCommentDto.builder()
                .comment("New Comment")
                .commenter(commenterDto)
                .build();

        when(workItemRepository.findById(workItemId)).thenReturn(Optional.of(workItem));
        when(accessDecisionVoter.hasPermission(workItem.getParentProjectId())).thenReturn(true);
        when(authenticationFacade.getCurrentUser()).thenReturn(currentUser);
        when(workItemCommentRepository.save(any(WorkItemComment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        WorkItemCommentDto result = workItemCommentService.createComment(workItem.getId(), commentDto);


        assertEquals(expectedComment, result);
        verify(workItemCommentRepository, times(1)).save(any(WorkItemComment.class));
    }

    @Test
    void createComment_ShouldThrowException_WhenCommentIsNull() {
        Long workItemId = 1L;
        WorkItemCommentDto commentDto = WorkItemCommentDto.builder().build();

        assertThrows(WorkItemCommentNotProvided.class,
                () -> workItemCommentService.createComment(workItemId, commentDto));
    }
    @Test
    void createComment_ShouldThrowException_WhenCommentIsBlank() {
        Long workItemId = 1L;
        WorkItemCommentDto commentDto = WorkItemCommentDto.builder().comment("").build();

        assertThrows(WorkItemCommentNotProvided.class,
                () -> workItemCommentService.createComment(workItemId, commentDto));
    }


    @Test
    void createComment_ShouldThrowException_WhenWorkItemNotFound() {
        Long workItemId = 1L;
        WorkItemCommentDto commentDto = WorkItemCommentDto.builder()
                .comment("New Comment")
                .build();

        when(workItemRepository.findById(workItemId)).thenReturn(Optional.empty());

        assertThrows(WorkItemNotFoundException.class,
                () -> workItemCommentService.createComment(workItemId, commentDto));
    }

    @Test
    void createComment_ShouldThrowException_WhenAccessDenied() {
        Long workItemId = 1L;
        WorkItemCommentDto commentDto = WorkItemCommentDto.builder()
                .comment("New Comment")
                .build();

        when(workItemRepository.findById(workItemId)).thenReturn(Optional.of(workItem));
        when(accessDecisionVoter.hasPermission(workItem.getParentProjectId())).thenReturn(false);

        assertThrows(AccessDeniedException.class,
                () -> workItemCommentService.createComment(workItemId, commentDto));
    }

    @Test
    void updateComment_ShouldReturnUpdatedComment_WhenValidData() {
        WorkItemCommentDto updatedCommentDto = WorkItemCommentDto.builder()
                .comment("Updated Comment")
                .build();
        UserDto commenterDto = UserDto.builder()
                .id(currentUser.getId())
                .email(currentUser.getEmail())
                .firstName(currentUser.getFirstName())
                .lastName(currentUser.getLastName())
                .build();
        WorkItemCommentDto expectedComment = WorkItemCommentDto.builder()
                .id(comment.getId())
                .comment("Updated Comment")
                .commenter(commenterDto)
                .build();

        when(workItemCommentRepository.findById(comment.getId())).thenReturn(Optional.of(comment));
        when(accessDecisionVoter.hasPermission(comment)).thenReturn(true);
        when(workItemCommentRepository.save(any(WorkItemComment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        WorkItemCommentDto result = workItemCommentService.updateComment(comment.getId(), updatedCommentDto);

        assertEquals(expectedComment, result);
    }

    @Test
    void updateComment_ShouldThrowException_WhenCommentIsNull() {
        Long commentId = 1L;
        WorkItemCommentDto commentDto = WorkItemCommentDto.builder().build();

        assertThrows(WorkItemCommentNotProvided.class,
                () -> workItemCommentService.updateComment(commentId, commentDto));
    }
    @Test
    void updateComment_ShouldThrowException_WhenCommentIsBlank() {
        Long commentId = 1L;
        WorkItemCommentDto commentDto = WorkItemCommentDto.builder().comment("").build();

        assertThrows(WorkItemCommentNotProvided.class,
                () -> workItemCommentService.updateComment(commentId, commentDto));
    }
    @Test
    void updateComment_ShouldThrowException_WhenCommentNotFound() {
        Long commentId = 1L;
        WorkItemCommentDto updatedCommentDto = WorkItemCommentDto.builder()
                .comment("Updated Comment")
                .build();

        when(workItemCommentRepository.findById(commentId)).thenReturn(Optional.empty());

        assertThrows(WorkItemCommentNotFoundException.class,
                () -> workItemCommentService.updateComment(commentId, updatedCommentDto));
    }
    @Test
    void updateComment_ShouldThrowException_WhenAccessDenied() {
        Long commentId = 1L;
        WorkItemCommentDto updatedCommentDto = WorkItemCommentDto.builder()
                .comment("Updated Comment")
                .build();

        when(workItemCommentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(accessDecisionVoter.hasPermission(comment)).thenReturn(false);

        assertThrows(AccessDeniedException.class,
                () -> workItemCommentService.updateComment(commentId, updatedCommentDto));
    }

    @Test
    void deleteComment_ShouldDeleteComment_WhenValidData() {
        Long commentId = 1L;

        when(workItemCommentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(accessDecisionVoter.hasPermission(comment)).thenReturn(true);

        workItemCommentService.deleteComment(commentId);

        verify(workItemCommentRepository, times(1)).delete(comment);
    }

    @Test
    void deleteComment_ShouldThrowException_WhenCommentNotFound() {
        Long commentId = 1L;

        when(workItemCommentRepository.findById(commentId)).thenReturn(Optional.empty());

        assertThrows(WorkItemCommentNotFoundException.class, () -> workItemCommentService.deleteComment(commentId));
    }

    @Test
    void deleteComment_ShouldThrowException_WhenAccessDenied() {
        Long commentId = 1L;

        when(workItemCommentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(accessDecisionVoter.hasPermission(comment)).thenReturn(false);

        assertThrows(AccessDeniedException.class, () -> workItemCommentService.deleteComment(commentId));
    }
}