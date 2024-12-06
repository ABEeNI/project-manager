package home.projectmanager.service;

import home.projectmanager.dto.BugItemCommentDto;
import home.projectmanager.dto.BugItemDto;
import home.projectmanager.dto.UserDto;
import home.projectmanager.dto.WorkItemDto;
import home.projectmanager.entity.*;
import home.projectmanager.exception.bugitem.BugItemTitleNotProvidedError;
import home.projectmanager.exception.project.ProjectNotFoundException;
import home.projectmanager.repository.BugItemRepository;
import home.projectmanager.repository.ProjectRepository;
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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BugItemServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private BugItemRepository bugItemRepository;

    @Mock
    private WorkItemRepository workItemRepository;

    @Mock
    private AccessDecisionVoter accessDecisionVoter;

    @Mock
    private AuthenticationFacade authenticationFacade;

    @InjectMocks
    private BugItemServiceImpl bugItemService;

    private Project project;
    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .role(Role.USER)
                .build();

        project = new Project();
        project.setId(1L);
        project.setProjectName("Test Project");
        project.setBugItems(List.of());
    }

    @Test
    void createBugItem_ShouldCreateBugItem_WhenValidRequestIsProvided() {
        BugItemDto bugItemDto = BugItemDto.builder()
                .title("Bug Title")
                .description("Bug Description")
                .projectId(1L)
                .status(BugItemStatus.REPORTED)
                .build();

        BugItem savedBugItem = BugItem.builder()
                .id(1L)
                .title("Bug Title")
                .description("Bug Description")
                .projectId(1L)
                .status(BugItemStatus.REPORTED)
                .reporter(user)
                .build();

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(accessDecisionVoter.hasPermission(project)).thenReturn(true);
        when(authenticationFacade.getCurrentUser()).thenReturn(user);
        when(bugItemRepository.save(any(BugItem.class))).thenReturn(savedBugItem);

        BugItemDto result = bugItemService.createBugItem(bugItemDto);

        assertNotNull(result);
        assertEquals("Bug Title", result.title());
        assertEquals("Bug Description", result.description());
        assertEquals(1L, result.projectId());
        verify(bugItemRepository, times(1)).save(any(BugItem.class));
    }

    @Test
    void createBugItem_ShouldThrowException_WhenTitleIsMissing() {
        BugItemDto bugItemDto = BugItemDto.builder()
                .description("Bug Description")
                .projectId(1L)
                .build();

        assertThrows(BugItemTitleNotProvidedError.class, () -> bugItemService.createBugItem(bugItemDto));
    }

    @Test
    void createBugItem_ShouldThrowException_WhenProjectIdIsMissing() {
        
        BugItemDto bugItemDto = BugItemDto.builder()
                .title("Bug Title")
                .description("Bug Description")
                .build();

        assertThrows(ProjectNotFoundException.class, () -> bugItemService.createBugItem(bugItemDto));
    }

    @Test
    void createBugItem_ShouldThrowException_WhenAccessDenied() {
        BugItemDto bugItemDto = BugItemDto.builder()
                .title("Bug Title")
                .description("Bug Description")
                .projectId(1L)
                .build();

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(accessDecisionVoter.hasPermission(project)).thenReturn(false);

        assertThrows(AccessDeniedException.class, () -> bugItemService.createBugItem(bugItemDto));
        verify(bugItemRepository, never()).save(any());
    }

    @Test
    void getBugItem_ShouldReturnBugItem_WhenBugItemExists() {
        
        BugItem bugItem = BugItem.builder()
                .id(1L)
                .title("Bug Title")
                .description("Bug Description")
                .projectId(1L)
                .reporter(user)
                .build();

        when(bugItemRepository.findById(1L)).thenReturn(Optional.of(bugItem));
        when(accessDecisionVoter.hasPermission(bugItem)).thenReturn(true);

        BugItemDto result = bugItemService.getBugItem(1L);

        assertNotNull(result);
        assertEquals("Bug Title", result.title());
        assertEquals(1L, result.projectId());
        verify(bugItemRepository, times(1)).findById(1L);
    }

    @Test
    void getBugItem_ShouldThrowException_WhenBugItemDoesNotExist() {
        
        when(bugItemRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ProjectNotFoundException.class, () -> bugItemService.getBugItem(1L));
    }

    @Test
    void getBugItemsByProject_ShouldReturnBugItems_WhenProjectExists() {
        
        BugItem bugItem = BugItem.builder()
                .id(1L)
                .title("Bug Title")
                .description("Bug Description")
                .projectId(1L)
                .reporter(user)
                .build();
        project.setBugItems(List.of(bugItem));

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(accessDecisionVoter.hasPermission(project)).thenReturn(true);


        List<BugItemDto> result = bugItemService.getBugItemsByProject(1L);


        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Bug Title", result.get(0).title());
        verify(projectRepository, times(1)).findById(1L);
    }

    @Test
    void getBugItem_ShouldThrowException_WhenAccessDenied() {
        BugItem bugItem = BugItem.builder()
                .id(1L)
                .title("Bug Title")
                .description("Bug Description")
                .projectId(1L)
                .reporter(user)
                .build();

        when(bugItemRepository.findById(1L)).thenReturn(Optional.of(bugItem));
        when(accessDecisionVoter.hasPermission(bugItem)).thenReturn(false);

        assertThrows(AccessDeniedException.class, () -> bugItemService.getBugItem(1L));
    }

    @Test
    void deleteBugItem_ShouldDeleteBugItem_WhenBugItemExists() {
        
        BugItem bugItem = BugItem.builder()
                .id(1L)
                .title("Bug Title")
                .description("Bug Description")
                .projectId(1L)
                .reporter(user)
                .build();

        when(bugItemRepository.findById(1L)).thenReturn(Optional.of(bugItem));
        when(accessDecisionVoter.hasPermission(bugItem)).thenReturn(true);

        bugItemService.deleteBugItem(1L);

        verify(bugItemRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteBugItem_ShouldThrowException_WhenAccessDenied() {
        BugItem bugItem = BugItem.builder()
                .id(1L)
                .title("Bug Title")
                .description("Bug Description")
                .projectId(1L)
                .reporter(user)
                .build();

        when(bugItemRepository.findById(1L)).thenReturn(Optional.of(bugItem));
        when(accessDecisionVoter.hasPermission(bugItem)).thenReturn(false);

        assertThrows(AccessDeniedException.class, () -> bugItemService.deleteBugItem(1L));
        verify(bugItemRepository, never()).deleteById(anyLong());
    }

    @Test
    void updateBugItem_ShouldUpdateBugItem_WhenValidRequestIsProvided() {
        BugItem bugItem = BugItem.builder()
                .id(1L)
                .title("Old Title")
                .description("Old Description")
                .projectId(1L)
                .reporter(user)
                .build();

        BugItemDto bugItemDto = BugItemDto.builder()
                .title("New Title")
                .description("New Description")
                .build();

        when(bugItemRepository.findById(1L)).thenReturn(Optional.of(bugItem));
        when(accessDecisionVoter.hasPermission(bugItem)).thenReturn(true);
        when(bugItemRepository.save(any(BugItem.class))).thenReturn(bugItem);

        BugItemDto result = bugItemService.updateBugItem(1L, bugItemDto);

        assertNotNull(result);
        assertEquals("New Title", bugItem.getTitle());
        assertEquals("New Description", bugItem.getDescription());
        verify(bugItemRepository, times(1)).save(bugItem);
    }

    @Test
    void updateBugItem_ShouldThrowException_WhenAccessDenied() {
        BugItem bugItem = BugItem.builder()
                .id(1L)
                .title("Old Title")
                .description("Old Description")
                .projectId(1L)
                .reporter(user)
                .build();

        BugItemDto bugItemDto = BugItemDto.builder()
                .title("New Title")
                .description("New Description")
                .build();

        when(bugItemRepository.findById(1L)).thenReturn(Optional.of(bugItem));
        when(accessDecisionVoter.hasPermission(bugItem)).thenReturn(false);

        assertThrows(AccessDeniedException.class, () -> bugItemService.updateBugItem(1L, bugItemDto));
        verify(bugItemRepository, never()).save(any());
    }

    @Test
    void getBugItem_ShouldReturnFullyConvertedBugItemDto_WhenBugItemExists() {
        User reporter = User.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .build();

        WorkItem workItem = WorkItem.builder()
                .id(2L)
                .title("Work Item Title")
                .description("Work Item Description")
                .build();

        BugItemComment comment1 = BugItemComment.builder()
                .id(3L)
                .comment("First comment")
                .commenter(User.builder()
                        .id(4L)
                        .firstName("Alice")
                        .lastName("Smith")
                        .email("alice.smith@example.com")
                        .build())
                .build();

        BugItemComment comment2 = BugItemComment.builder()
                .id(5L)
                .comment("Second comment")
                .commenter(User.builder()
                        .id(6L)
                        .firstName("Bob")
                        .lastName("Johnson")
                        .email("bob.johnson@example.com")
                        .build())
                .build();

        BugItem bugItem = BugItem.builder()
                .id(1L)
                .title("Bug Title")
                .description("Bug Description")
                .status(BugItemStatus.REPORTED)
                .projectId(1L)
                .reporter(reporter)
                .workItem(workItem)
                .comments(List.of(comment1, comment2))
                .build();

        BugItemDto expectedBugItemDto = BugItemDto.builder()
                .id(1L)
                .title("Bug Title")
                .description("Bug Description")
                .status(BugItemStatus.REPORTED)
                .projectId(1L)
                .reporter(UserDto.builder()
                        .id(1L)
                        .email("john.doe@example.com")
                        .firstName("John")
                        .lastName("Doe")
                        .build())
                .workItemDto(WorkItemDto.builder()
                        .id(2L)
                        .title("Work Item Title")
                        .build())
                .comments(List.of(
                        BugItemCommentDto.builder()
                                .id(3L)
                                .comment("First comment")
                                .commenter(UserDto.builder()
                                        .id(4L)
                                        .firstName("Alice")
                                        .lastName("Smith")
                                        .email("alice.smith@example.com")
                                        .build())
                                .build(),
                        BugItemCommentDto.builder()
                                .id(5L)
                                .comment("Second comment")
                                .commenter(UserDto.builder()
                                        .id(6L)
                                        .firstName("Bob")
                                        .lastName("Johnson")
                                        .email("bob.johnson@example.com")
                                        .build())
                                .build()
                ))
                .build();

        when(bugItemRepository.findById(1L)).thenReturn(Optional.of(bugItem));
        when(accessDecisionVoter.hasPermission(bugItem)).thenReturn(true);

        BugItemDto result = bugItemService.getBugItem(1L);

        assertEquals(expectedBugItemDto, result);
        verify(bugItemRepository, times(1)).findById(1L);
    }
}
