package home.projectmanager.controller;

import home.projectmanager.dto.BoardDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import home.projectmanager.service.BoardService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class BoardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BoardService boardService;

    @Test
    void createBoard_ShouldReturnCreatedBoard_WhenValidBoardDtoIsProvided() throws Exception {
        BoardDto boardDto = BoardDto.builder()
                .boardName("Project Board")
                .projectId(1L)
                .workItemDtos(Collections.emptyList())
                .build();

        BoardDto createdBoardDto = BoardDto.builder()
                .id(1L)
                .boardName("Project Board")
                .projectId(1L)
                .workItemDtos(Collections.emptyList())
                .build();

        when(boardService.createBoard(any(BoardDto.class))).thenReturn(createdBoardDto);

        mockMvc.perform(post("/api/boards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(boardDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.boardName").value("Project Board"))
                .andExpect(jsonPath("$.projectId").value(1L));
    }

    @Test
    void getBoard_ShouldReturnBoard_WhenValidBoardIdIsProvided() throws Exception {
        Long boardId = 1L;
        BoardDto boardDto = BoardDto.builder()
                .id(boardId)
                .boardName("Project Board")
                .projectId(1L)
                .workItemDtos(Collections.emptyList())
                .build();

        when(boardService.getBoard(boardId)).thenReturn(boardDto);

        mockMvc.perform(get("/api/boards/{id}", boardId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(boardId))
                .andExpect(jsonPath("$.boardName").value("Project Board"))
                .andExpect(jsonPath("$.projectId").value(1L));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getBoards_ShouldReturnListOfBoards_WhenCurrentUserIsAdmin() throws Exception {
        List<BoardDto> boardDtos = Arrays.asList(
                BoardDto.builder().id(1L).boardName("Board 1").projectId(1L).workItemDtos(Collections.emptyList()).build(),
                BoardDto.builder().id(2L).boardName("Board 2").projectId(1L).workItemDtos(Collections.emptyList()).build()
        );

        when(boardService.getBoards()).thenReturn(boardDtos);

        mockMvc.perform(get("/api/boards"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].boardName").value("Board 1"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].boardName").value("Board 2"));
    }

    @Test
    void getBoardsByProject_ShouldReturnListOfBoards_WhenValidProjectIdIsProvided() throws Exception {
        Long projectId = 1L;
        List<BoardDto> boardDtos = Arrays.asList(
                BoardDto.builder().id(1L).boardName("Board 1").projectId(projectId).workItemDtos(Collections.emptyList()).build(),
                BoardDto.builder().id(2L).boardName("Board 2").projectId(projectId).workItemDtos(Collections.emptyList()).build()
        );

        when(boardService.getBoardsByProject(projectId)).thenReturn(boardDtos);

        mockMvc.perform(get("/api/boards/projects/{projectId}", projectId))
                .andExpect(status().isOk())  // Expect 200 OK
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].boardName").value("Board 1"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].boardName").value("Board 2"));
    }

    @Test
    void updateBoard_ShouldReturnUpdatedBoard_WhenValidRequestIsMade() throws Exception {
        Long boardId = 1L;
        BoardDto boardDto = BoardDto.builder()
                .id(boardId)
                .boardName("Updated Board")
                .projectId(1L)
                .workItemDtos(Collections.emptyList())
                .build();

        when(boardService.updateBoard(eq(boardId), any(BoardDto.class))).thenReturn(boardDto);

        mockMvc.perform(put("/api/boards/{id}", boardId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(boardDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(boardId))
                .andExpect(jsonPath("$.boardName").value("Updated Board"))
                .andExpect(jsonPath("$.projectId").value(1L));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteBoard_ShouldReturnNoContent_WhenValidRequestIsMade() throws Exception {

        Long boardId = 1L;

        doNothing().when(boardService).deleteBoard(boardId);

        mockMvc.perform(delete("/api/boards/{id}", boardId))
                .andExpect(status().isNoContent());
    }
}
