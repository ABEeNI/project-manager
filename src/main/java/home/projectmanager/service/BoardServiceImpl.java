package home.projectmanager.service;

import home.projectmanager.dto.BoardDto;
import home.projectmanager.dto.WorkItemDto;
import home.projectmanager.entity.Board;
import home.projectmanager.entity.Project;
import home.projectmanager.entity.WorkItem;
import home.projectmanager.exception.board.BoardNotFoundException;
import home.projectmanager.exception.project.ProjectNotFoundException;
import home.projectmanager.repository.BoardRepository;
import home.projectmanager.repository.ProjectRepository;
import home.projectmanager.service.accesscontrol.AccessDecisionVoter;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

    private final BoardRepository boardRepository;
    private final ProjectRepository projectRepository;
    private final AccessDecisionVoter accessDecisionVoter;

    @Override
    @Transactional//needed
    public BoardDto createBoard(BoardDto boardDto) {
        if(boardDto.boardName() == null || boardDto.boardName().isBlank()) {
            throw new BoardNotFoundException("Board name is not provided");
        }
        if(boardDto.projectId() == null) {
            throw new ProjectNotFoundException("Project id is not provided");
        }
        Project project = projectRepository.findById(boardDto.projectId()) //?? Should it be from pathvariable and in the ProjectService?
                .orElseThrow(() -> new ProjectNotFoundException("Project not found"));
        if(!accessDecisionVoter.hasPermission(project)) {
            throw new AccessDeniedException("User does not have permission to project with id " + boardDto.projectId());
        }

        Board newBoard = Board.builder()
                .boardName(boardDto.boardName())
                .projectId(boardDto.projectId())
                .build();

        project.addBoard(newBoard);//needed? Not even saved later. Only the value of the foreign key is relevant

        Board savedBoard = boardRepository.save(newBoard);
        //projectRepository.save(project);//cascade? Do I need to save project? Probably not
        log.info("Board created: {}", savedBoard);
        return convertToDto(savedBoard);
    }

    @Override
    @Transactional
    public BoardDto getBoard(Long id) {//needed//it returns all the workItemDtos as well. maybe rename?
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new BoardNotFoundException("Board with id " + id + " not found"));
        if(!accessDecisionVoter.hasPermission(board)) {
            throw new AccessDeniedException("User does not have permission to board with id " + id);
        }
        List<WorkItem> workItems = board.getWorkItems();
        List<WorkItemDto> workItemDtos = workItems.stream()
                .map(workItem -> WorkItemDto.builder()
                        .id(workItem.getId())
                        .title(workItem.getTitle())
                        .description(workItem.getDescription())
                        .points(workItem.getPoints())
                        .status(workItem.getStatus())
                        .build())
                .collect(Collectors.toList());//TODO .toList() everywhere

        return BoardDto.builder()
                .id(board.getId())
                .boardName(board.getBoardName())
                .projectId(board.getProjectId())
                .workItemDtos(workItemDtos)
                .build();
    }

    @Override
    public List<BoardDto> getBoards() {//extra/ maybe leave for ADMIN, getBoardByProjectId could be enough
        List<Board> boards = boardRepository.findAll();
        return boards.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteBoard(Long id) {//ADMIN or USER? if User then AccessDecisionVoter needed to be added
        if (!boardRepository.existsById(id)) {
            throw new BoardNotFoundException("Board not found");
        }
        boardRepository.deleteById(id);
    }

    @Override
    @Transactional
    public BoardDto updateBoard(Long id, BoardDto boardDto) {//needed
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new BoardNotFoundException("Board with id " + id + " not found"));
        if(!accessDecisionVoter.hasPermission(board)) {
            throw new AccessDeniedException("User does not have permission to board with id " + id);
        }//could extract to fetchBoardById

        if (boardDto.boardName() != null && !boardDto.boardName().isBlank()) {
            board.setBoardName(boardDto.boardName());
        }

        Board updatedBoard = boardRepository.save(board);
        return convertToDto(updatedBoard);
    }
    //board could be extended with Sprints in future plans

    @Override
    public List<BoardDto> getBoardsByProject(Long projectId) {//needed
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException("Project not found"));
        if(!accessDecisionVoter.hasPermission(project)) {
            throw new AccessDeniedException("User does not have permission to project with id " + projectId);
        }
        List<Board> boards = project.getBoards();
        return boards.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private BoardDto convertToDto(Board board) {
        return BoardDto.builder()
                .id(board.getId())
                .boardName(board.getBoardName())
                .projectId(board.getProjectId())
                .build();
    }
}
