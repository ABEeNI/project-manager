package home.projectmanager.service;

import home.projectmanager.dto.BugItemDto;
import home.projectmanager.dto.UserDto;
import home.projectmanager.dto.WorkItemCommentDto;
import home.projectmanager.dto.WorkItemDto;
import home.projectmanager.entity.*;
import home.projectmanager.exception.board.BoardNotFoundException;
import home.projectmanager.exception.workitem.WorkItemNotFoundException;
import home.projectmanager.exception.workitem.WorkItemTitleNotProvidedException;
import home.projectmanager.repository.BoardRepository;
import home.projectmanager.repository.BugItemRepository;
import home.projectmanager.repository.UserRepository;
import home.projectmanager.repository.WorkItemRepository;
import home.projectmanager.service.accesscontrol.AccessDecisionVoter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorkItemServiceTest {

    @Mock
    private WorkItemRepository workItemRepository;

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BugItemRepository bugItemRepository;

    @Mock
    private AccessDecisionVoter accessDecisionVoter;

    @InjectMocks
    private WorkItemServiceImpl workItemService;

    private WorkItemDto workItemDto;
    private Board board;
    private WorkItem workItem;
    private User user;


    @Test
    void createWorkItem_ShouldReturnWorkItem_WhenValidData() {
        board = Board.builder()
                .id(1L)
                .boardName("Board")
                .projectId(1L)
                .build();
        user = User.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("test@email.com")
                .build();
        UserDto userdto = UserDto.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("test@email.com")
                .build();
        workItemDto = WorkItemDto.builder()
                .title("Test Work Item")
                .description("Test Work Item")
                .boardId(board.getId())
                .assignedUser(userdto)
                .build();

        workItem = WorkItem.builder()
                .id(1L)
                .title("Test Work Item")
                .description("Test Work Item")
                .status(WorkItemStatus.NEW)
                .boardId(board.getId())
                .projectId(board.getProjectId())
                .assignedUser(user)
                .build();

        WorkItemDto expectedWorkItemDto = WorkItemDto.builder()
                .title("Test Work Item") //Do not give value for the id since repository is mocked and returning what WorkItem was given to it, not adding the generated Id
                .description("Test Work Item")
                .status(WorkItemStatus.NEW)
                .boardId(board.getId())
                .parentWorkItemId(null)
                .assignedUser(userdto)
                .build();
        when(boardRepository.findById(board.getId())).thenReturn(Optional.of(board));
        when(accessDecisionVoter.hasPermission(board)).thenReturn(true);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(accessDecisionVoter.hasPermission(board, user)).thenReturn(true);
        when(workItemRepository.save(any(WorkItem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        WorkItemDto result = workItemService.createWorkItem(workItemDto);

        assertEquals(expectedWorkItemDto, result);
    }

    @Test
    void createWorkItem_ShouldThrowException_WhenTitleIsMissing() {
        board = Board.builder()
                .id(1L)
                .boardName("Board")
                .projectId(1L)
                .build();
        workItemDto = WorkItemDto.builder().boardId(board.getId()).build();

        assertThrows(WorkItemTitleNotProvidedException.class, () -> workItemService.createWorkItem(workItemDto));
        verify(workItemRepository, never()).save(any());
    }

    @Test
    void createWorkItem_ShouldThrowException_WhenBoardNotFound() {
        workItemDto = WorkItemDto.builder()
                .title("Test Work Item")
                .description("Test Work Item")
                .boardId(1L)
                .build();
        when(boardRepository.findById(workItemDto.boardId())).thenReturn(Optional.empty());

        assertThrows(BoardNotFoundException.class, () -> workItemService.createWorkItem(workItemDto));
        verify(workItemRepository, never()).save(any());
    }

    @Test
    void createWorkItem_ShouldThrowException_WhenUserAccessDenied() {
        long boardId = 1L;
        board = Board.builder()
                .id(boardId)
                .boardName("Board")
                .projectId(boardId)
                .build();
        workItemDto = WorkItemDto.builder()
                .title("Test Work Item")
                .description("Test Work Item")
                .boardId(boardId)
                .build();
        when(boardRepository.findById(workItemDto.boardId())).thenReturn(Optional.of(board));
        when(accessDecisionVoter.hasPermission(board)).thenReturn(false);

        assertThrows(AccessDeniedException.class, () -> workItemService.createWorkItem(workItemDto));
        verify(workItemRepository, never()).save(any());
    }

    @Test
    void getWorkItem_ShouldReturnWorkItem_WhenValidId() {
        BugItem bugItem = BugItem.builder()
                .id(1L)
                .title("Bug 1")
                .description("Test Bug")
                .build();

        WorkItem parentWorkItem = WorkItem.builder()
                .id(2L)
                .title("Parent Work Item")
                .description("Parent Description")
                .boardId(1L)
                .build();

        WorkItem mainWorkItem = WorkItem.builder()
                .id(1L)
                .title("Main Work Item")
                .description("Main Work Item Description")
                .points(5)
                .status(WorkItemStatus.IN_PROGRESS)
                .boardId(1L)
                .parentWorkItem(parentWorkItem)
                .bugItem(bugItem)
                .build();

        WorkItem subWorkItem1 = WorkItem.builder()
                .id(3L)
                .title("Sub Work Item 1")
                .description("Sub Description 1")
                .boardId(1L)
                .parentWorkItem(mainWorkItem)
                .build();

        WorkItem subWorkItem2 = WorkItem.builder()
                .id(4L)
                .title("Sub Work Item 2")
                .description("Sub Description 2")
                .boardId(1L)
                .parentWorkItem(mainWorkItem)
                .build();

        mainWorkItem.setSubWorkItems(List.of(subWorkItem1, subWorkItem2));

        WorkItemComment comment1 = WorkItemComment.builder()
                .id(1L)
                .comment("First Comment")
                .commenter(User.builder().email("user1@email.com").build())
                .build();

        WorkItemComment comment2 = WorkItemComment.builder()
                .id(2L)
                .comment("Second Comment")
                .commenter(User.builder().email("user2@email.com").build())
                .build();

        mainWorkItem.setComments(List.of(comment1, comment2));

        when(workItemRepository.findById(mainWorkItem.getId())).thenReturn(Optional.of(mainWorkItem));
        when(accessDecisionVoter.hasPermission(mainWorkItem)).thenReturn(true);

        WorkItemDto expectedWorkItemDto = WorkItemDto.builder()
                .id(mainWorkItem.getId())
                .title(mainWorkItem.getTitle())
                .description(mainWorkItem.getDescription())
                .points(mainWorkItem.getPoints())
                .status(mainWorkItem.getStatus())
                .boardId(mainWorkItem.getBoardId())
                .parentWorkItemId(parentWorkItem.getId())
                .subWorkItems(List.of(
                        WorkItemDto.builder()
                                .id(subWorkItem1.getId())
                                .title(subWorkItem1.getTitle())
                                .description(subWorkItem1.getDescription())
                                .boardId(subWorkItem1.getBoardId())
                                .parentWorkItemId(mainWorkItem.getId())
                                .build(),
                        WorkItemDto.builder()
                                .id(subWorkItem2.getId())
                                .title(subWorkItem2.getTitle())
                                .description(subWorkItem2.getDescription())
                                .boardId(subWorkItem2.getBoardId())
                                .parentWorkItemId(mainWorkItem.getId())
                                .build()
                ))
                .comments(List.of(
                        WorkItemCommentDto.builder()
                                .id(comment1.getId())
                                .comment(comment1.getComment())
                                .commenter(comment1.getCommenter().getEmail())
                                .build(),
                        WorkItemCommentDto.builder()
                                .id(comment2.getId())
                                .comment(comment2.getComment())
                                .commenter(comment2.getCommenter().getEmail())
                                .build()
                ))
                .bugItemDto(BugItemDto.builder()
                        .id(bugItem.getId())
                        .title(bugItem.getTitle())
                        .description(bugItem.getDescription())
                        .build())
                .build();

        WorkItemDto result = workItemService.getWorkItem(mainWorkItem.getId());

        assertEquals(expectedWorkItemDto, result);
    }


    @Test
    void getWorkItem_ShouldThrowException_WhenWorkItemNotFound() {
        Long workItemId = 1L;

        when(workItemRepository.findById(workItemId)).thenReturn(Optional.empty());

        assertThrows(WorkItemNotFoundException.class, () -> workItemService.getWorkItem(workItemId));
    }

    @Test
    void deleteWorkItem_ShouldDeleteWorkItem_WhenValidId() {
        workItem = WorkItem.builder()
                .id(1L)
                .title("Test Work Item")
                .description("Test Work Item")
                .boardId(1L)
                .build();
        when(workItemRepository.findById(workItem.getId())).thenReturn(Optional.of(workItem));
        when(accessDecisionVoter.hasPermission(workItem)).thenReturn(true);

        workItemService.deleteWorkItem(workItem.getId());

        verify(workItemRepository, times(1)).deleteById(workItem.getId());
    }

    @Test
    void deleteWorkItem_ShouldThrowException_WhenWorkItemNotFound() {
        Long workItemId = 1L;
        when(workItemRepository.findById(workItemId)).thenReturn(Optional.empty());

        assertThrows(WorkItemNotFoundException.class, () -> workItemService.deleteWorkItem(workItemId));
    }

    @Test
    void updateWorkItem_ShouldUpdateWorkItem_WhenValidData() {
        BugItem bugItem = BugItem.builder()
                .id(1L)
                .title("Bug 1")
                .description("Test Bug")
                .build();

        WorkItem parentWorkItem = WorkItem.builder()
                .id(2L)
                .title("Parent Work Item")
                .description("Parent Description")
                .boardId(1L)
                .build();

        user = User.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("test@email.com")
                .build();
        WorkItem existingWorkItem = WorkItem.builder()
                .id(1L)
                .title("Old Title")
                .description("Old Description")
                .points(5)
                .status(WorkItemStatus.NEW)
                .boardId(1L)
                .bugItem(null)
                .parentWorkItem(null)
                .build();

        UserDto userToAssign = UserDto.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("test@email.com")
                .build();
        BugItemDto bugItemDto = BugItemDto.builder()
                .id(bugItem.getId())
                .title(bugItem.getTitle())
                .description(bugItem.getDescription())
                .build();
        WorkItemDto updatedWorkItemDto = WorkItemDto.builder()
                .title("Updated Title")
                .description("Updated Description")
                .points(10)
                .status(WorkItemStatus.READY)
                .bugItemDto(bugItemDto)
                .parentWorkItemId(parentWorkItem.getId())
                .assignedUser(userToAssign)
                .build();

        when(workItemRepository.findById(existingWorkItem.getId())).thenReturn(Optional.of(existingWorkItem));
        when(accessDecisionVoter.hasPermission(existingWorkItem)).thenReturn(true);
        when(userRepository.findById(userToAssign.id())).thenReturn(Optional.of(user));
        when(bugItemRepository.findById(bugItem.getId())).thenReturn(Optional.of(bugItem));
        when(workItemRepository.findById(parentWorkItem.getId())).thenReturn(Optional.of(parentWorkItem));
        when(workItemRepository.save(any(WorkItem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        WorkItemDto expectedWorkItemDto = WorkItemDto.builder()
                .id(existingWorkItem.getId())
                .title(updatedWorkItemDto.title())
                .description(updatedWorkItemDto.description())
                .points(updatedWorkItemDto.points())
                .status(updatedWorkItemDto.status())
                .boardId(existingWorkItem.getBoardId())
                .bugItemDto(bugItemDto)
                .parentWorkItemId(parentWorkItem.getId())
                .assignedUser(userToAssign)
                .build();

        WorkItemDto result = workItemService.updateWorkItem(existingWorkItem.getId(), updatedWorkItemDto);

        assertNotNull(result);
        assertEquals(expectedWorkItemDto, result);

        verify(workItemRepository, times(1)).save(existingWorkItem);
        verify(workItemRepository, times(1)).findById(existingWorkItem.getId());
        verify(bugItemRepository, times(1)).findById(bugItem.getId());
        verify(workItemRepository, times(1)).findById(parentWorkItem.getId());
    }


    @Test
    void updateWorkItem_ShouldThrowException_WhenWorkItemNotFound() {
        Long workItemId = 1L;
        when(workItemRepository.findById(workItemId)).thenReturn(Optional.empty());

        assertThrows(WorkItemNotFoundException.class, () -> workItemService.updateWorkItem(workItemId, workItemDto));
    }
}
