package home.projectmanager.service;

import home.projectmanager.dto.BoardDto;
import home.projectmanager.entity.Board;
import home.projectmanager.entity.Project;
import home.projectmanager.entity.WorkItem;
import home.projectmanager.exception.board.BoardNameNotProvidedException;
import home.projectmanager.exception.board.BoardNotFoundException;
import home.projectmanager.exception.project.ProjectNotFoundException;
import home.projectmanager.repository.BoardRepository;
import home.projectmanager.repository.ProjectRepository;
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
class BoardServiceTest {

    @InjectMocks
    private BoardServiceImpl boardService;

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private AccessDecisionVoter accessDecisionVoter;

    private Project project;
    private Board board;

    @BeforeEach
    void setUp() {
        project = new Project();
        project.setId(1L);
        project.setProjectName("Project");
        project.setProjectDescription("Description");

        board = new Board();
        board.setId(1L);
        board.setBoardName("Board");
        board.setProjectId(project.getId());
    }

    @Test
    void createBoard_ShouldReturnBoardDto_WhenProjectExistsAndUserHasPermission() {
        BoardDto boardDto = BoardDto.builder()
                .boardName("New Board")
                .projectId(1L)
                .build();

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(accessDecisionVoter.hasPermission(project)).thenReturn(true);
        when(boardRepository.save(any(Board.class))).thenReturn(board);

        BoardDto result = boardService.createBoard(boardDto);

        assertEquals("Board", result.boardName());
        verify(boardRepository).save(any(Board.class));
    }

    @Test
    void createBoard_ShouldThrowException_WhenProjectDoesNotExist() {
        BoardDto boardDto = BoardDto.builder()
                .boardName("New Board")
                .projectId(1L)
                .build();

        when(projectRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ProjectNotFoundException.class, () -> boardService.createBoard(boardDto));
    }

    @Test
    void createBoard_ShouldThrowException_WhenUserDoesNotHavePermission() {
        BoardDto boardDto = BoardDto.builder()
                .boardName("New Board")
                .projectId(1L)
                .build();

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(accessDecisionVoter.hasPermission(project)).thenReturn(false);

        assertThrows(AccessDeniedException.class, () -> boardService.createBoard(boardDto));
    }

    @Test
    void createBoard_ShouldThrowException_WhenBoardNameIsBlank() {
        BoardDto boardDto = BoardDto.builder()
                .boardName("")
                .projectId(1L)
                .build();

        assertThrows(BoardNameNotProvidedException.class, () -> boardService.createBoard(boardDto));
    }

    @Test
    void createBoard_ShouldThrowException_WhenBoardNameIsNull() {
        BoardDto boardDto = BoardDto.builder()
                .projectId(1L)
                .build();

        assertThrows(BoardNameNotProvidedException.class, () -> boardService.createBoard(boardDto));
    }

    @Test
    void createBoard_ShouldThrowException_WhenProjectIdIsNull() {
        BoardDto boardDto = BoardDto.builder()
                .boardName("New Board")
                .build();

        assertThrows(ProjectNotFoundException.class, () -> boardService.createBoard(boardDto));
    }

    @Test
    void getBoard_ShouldReturnBoardDto_WhenBoardExistsAndUserHasPermission() {
        WorkItem mockWorkItem = new WorkItem();
        mockWorkItem.setId(1L);
        mockWorkItem.setTitle("Work Item");
        board.setWorkItems(List.of(mockWorkItem));

        when(boardRepository.findById(1L)).thenReturn(Optional.of(board));
        when(accessDecisionVoter.hasPermission(board)).thenReturn(true);

        BoardDto result = boardService.getBoard(1L);

        assertEquals("Board", result.boardName());
        assertEquals(1, result.workItemDtos().size());
        assertEquals("Work Item", result.workItemDtos().get(0).title());
    }

    @Test
    void getBoard_ShouldThrowException_WhenBoardDoesNotExist() {
        when(boardRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(BoardNotFoundException.class, () -> boardService.getBoard(1L));
    }

    @Test
    void getBoard_ShouldThrowException_WhenUserDoesNotHavePermission() {
        when(boardRepository.findById(1L)).thenReturn(Optional.of(board));
        when(accessDecisionVoter.hasPermission(board)).thenReturn(false);

        assertThrows(AccessDeniedException.class, () -> boardService.getBoard(1L));
    }

    @Test
    void deleteBoard_ShouldDeleteBoard_WhenBoardExists() {
        when(boardRepository.existsById(1L)).thenReturn(true);

        boardService.deleteBoard(1L);

        verify(boardRepository).deleteById(1L);
    }

    @Test
    void deleteBoard_ShouldThrowException_WhenBoardDoesNotExist() {
        when(boardRepository.existsById(1L)).thenReturn(false);

        assertThrows(BoardNotFoundException.class, () -> boardService.deleteBoard(1L));
    }

    @Test
    void updateBoard_ShouldUpdateBoardName_WhenValidDataProvided() {
        BoardDto boardDto = BoardDto.builder()
                .boardName("Updated Board")
                .build();

        when(boardRepository.findById(1L)).thenReturn(Optional.of(board));
        when(accessDecisionVoter.hasPermission(board)).thenReturn(true);
        when(boardRepository.save(any(Board.class))).thenReturn(board);

        BoardDto result = boardService.updateBoard(1L, boardDto);

        assertEquals("Updated Board", result.boardName());
    }

    @Test
    void updateBoard_ShouldThrowException_WhenBoardDoesNotExist() {
        BoardDto boardDto = BoardDto.builder()
                .boardName("Updated Board")
                .build();

        when(boardRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(BoardNotFoundException.class, () -> boardService.updateBoard(1L, boardDto));
        verifyNoInteractions(accessDecisionVoter);
    }

    @Test
    void getBoardsByProject_ShouldReturnBoardDtos_WhenProjectExistsAndUserHasPermission() {
        project.getBoards().add(board);

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(accessDecisionVoter.hasPermission(project)).thenReturn(true);

        List<BoardDto> result = boardService.getBoardsByProject(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Board", result.get(0).boardName());
    }

    @Test
    void getBoardsByProject_ShouldThrowException_WhenProjectDoesNotExist() {
        when(projectRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ProjectNotFoundException.class, () -> boardService.getBoardsByProject(1L));
    }

    @Test
    void getBoardsByProject_ShouldThrowException_WhenUserDoesNotHavePermission() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(accessDecisionVoter.hasPermission(project)).thenReturn(false);

        assertThrows(AccessDeniedException.class, () -> boardService.getBoardsByProject(1L));
    }
}
