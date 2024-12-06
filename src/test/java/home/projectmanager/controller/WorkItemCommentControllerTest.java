package home.projectmanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import home.projectmanager.dto.UserDto;
import home.projectmanager.dto.WorkItemCommentDto;
import home.projectmanager.service.WorkItemCommentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class WorkItemCommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private WorkItemCommentService workItemCommentService;

    @Test
    void createComment_ShouldReturnCreatedComment_WhenValidRequestIsMade() throws Exception {
        Long workItemId = 1L;
        WorkItemCommentDto workItemCommentDto = WorkItemCommentDto.builder()
                .id(1L)
                .comment("This is a comment.")
                .commenter(UserDto.builder()
                        .id(1L)
                        .email("user@example.com")
                        .firstName("John")
                        .lastName("Doe")
                        .build())
                .build();

        when(workItemCommentService.createComment(eq(workItemId), any(WorkItemCommentDto.class)))
                .thenReturn(workItemCommentDto);

        mockMvc.perform(post("/api/workitems/{workItemId}/comments", workItemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(workItemCommentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.comment").value("This is a comment."))
                .andExpect(jsonPath("$.commenter.id").value(1L))
                .andExpect(jsonPath("$.commenter.email").value("user@example.com"))
                .andExpect(jsonPath("$.commenter.firstName").value("John"))
                .andExpect(jsonPath("$.commenter.lastName").value("Doe"));
    }

    @Test
    void updateComment_ShouldReturnUpdatedComment_WhenValidRequestIsMade() throws Exception {
        Long commentId = 1L;
        WorkItemCommentDto workItemCommentDto = WorkItemCommentDto.builder()
                .id(commentId)
                .comment("Updated comment.")
                .commenter(UserDto.builder()
                        .id(1L)
                        .email("user@example.com")
                        .firstName("John")
                        .lastName("Doe")
                        .build())
                .build();

        when(workItemCommentService.updateComment(eq(commentId), any(WorkItemCommentDto.class)))
                .thenReturn(workItemCommentDto);

        mockMvc.perform(put("/api/workitems/workitemcomment/{commentId}", commentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(workItemCommentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(commentId))
                .andExpect(jsonPath("$.comment").value("Updated comment."))
                .andExpect(jsonPath("$.commenter.id").value(1L))
                .andExpect(jsonPath("$.commenter.email").value("user@example.com"))
                .andExpect(jsonPath("$.commenter.firstName").value("John"))
                .andExpect(jsonPath("$.commenter.lastName").value("Doe"));
    }

    @Test
    void deleteComment_ShouldReturnNoContent_WhenValidRequestIsMade() throws Exception {
        Long commentId = 1L;

        doNothing().when(workItemCommentService).deleteComment(commentId);

        mockMvc.perform(delete("/api/workitems/workitemcomment/{commentId}", commentId))
                .andExpect(status().isNoContent());
    }
}
