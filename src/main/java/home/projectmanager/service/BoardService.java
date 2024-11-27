package home.projectmanager.service;

import home.projectmanager.dto.BoardDto;

import java.util.List;

public interface BoardService {

    BoardDto createBoard(BoardDto boardDto);

    BoardDto getBoard(Long id);

    List<BoardDto> getBoards();

    void deleteBoard(Long id);

    BoardDto updateBoard(Long id, BoardDto boardDto);

    List<BoardDto> getBoardsByProject(Long projectId);
}