package home.projectmanager.service;

import home.projectmanager.dto.BoardDto;
import home.projectmanager.entity.Board;
import home.projectmanager.entity.Project;
import home.projectmanager.exception.board.BoardNotFoundException;
import home.projectmanager.exception.project.ProjectNotFoundException;
import home.projectmanager.repository.BoardRepository;
import home.projectmanager.repository.ProjectRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

    private final BoardRepository boardRepository;
    private final ProjectRepository projectRepository;

    @Override
    @Transactional
    public BoardDto createBoard(BoardDto boardDto) {
        Project project = projectRepository.findById(boardDto.projectId()) //?? Should it be from pathvariable and in the ProjectService?
                .orElseThrow(() -> new ProjectNotFoundException("Project not found"));

        Board board = Board.builder()
                .boardName(boardDto.boardName())
                .build();

        project.addBoard(board);
        projectRepository.save(project);//TODO Cascade?
        Board savedBoard = boardRepository.save(board);
        log.info("Board created: {}", savedBoard);
        return convertToDto(savedBoard);
    }

    @Override
    public BoardDto getBoard(Long id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new BoardNotFoundException("Board not found"));
        return convertToDto(board);
    }

    @Override
    public List<BoardDto> getBoards() {
        List<Board> boards = boardRepository.findAll();
        return boards.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteBoard(Long id) {
        if (!boardRepository.existsById(id)) {
            throw new BoardNotFoundException("Board not found");
        }
        boardRepository.deleteById(id);
    }

    @Override
    @Transactional
    public BoardDto updateBoard(Long id, BoardDto boardDto) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new BoardNotFoundException("Board with id " + id + " not found"));

        if (boardDto.boardName() != null && !boardDto.boardName().isBlank()) {
            board.setBoardName(boardDto.boardName());
        }

        Board updatedBoard = boardRepository.save(board);
        return convertToDto(updatedBoard);
    }

    @Override
    public List<BoardDto> getBoardsByProject(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException("Project not found"));
        List<Board> boards = project.getBoards();
        return boards.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private BoardDto convertToDto(Board board) {
        return new BoardDto(
                board.getId(),
                board.getBoardName(),
                board.getProject().getId()
        );
    }
}
