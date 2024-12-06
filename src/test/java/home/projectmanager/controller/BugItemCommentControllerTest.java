package home.projectmanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import home.projectmanager.dto.*;
import home.projectmanager.service.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import home.projectmanager.dto.UserDto;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class BugItemCommentControllerTest {

    @MockBean
    private BugItemCommentService bugItemCommentService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createComment_ShouldReturnCreatedComment_WhenValidBugItemIdAndRequestAreProvided() throws Exception {
        Long bugItemId = 1L;
        Long commentId = 1L;
        String commentText = "This is a test comment";
        UserDto commenter = UserDto.builder()
                .id(1L)
                .email("commenter@example.com")
                .firstName("Jane")
                .lastName("Doe")
                .build();

        BugItemCommentDto bugItemCommentDto = BugItemCommentDto.builder()
                .id(commentId)
                .comment(commentText)
                .commenter(commenter)
                .build();

        when(bugItemCommentService.createComment(bugItemId, bugItemCommentDto)).thenReturn(bugItemCommentDto);

        mockMvc.perform(post("/api/bugitem/{bugItemId}/bugitemcomments", bugItemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bugItemCommentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(commentId))
                .andExpect(jsonPath("$.comment").value(commentText))
                .andExpect(jsonPath("$.commenter.id").value(1L))
                .andExpect(jsonPath("$.commenter.email").value("commenter@example.com"))
                .andExpect(jsonPath("$.commenter.firstName").value("Jane"))
                .andExpect(jsonPath("$.commenter.lastName").value("Doe"));

        verify(bugItemCommentService, times(1)).createComment(bugItemId, bugItemCommentDto);
    }

    @Test
    void updateComment_ShouldReturnUpdatedComment_WhenValidCommentIdAndRequestAreProvided() throws Exception {
        Long commentId = 1L;
        String updatedCommentText = "Updated comment text";
        UserDto updatedCommenter = UserDto.builder()
                .id(1L)
                .email("updated@example.com")
                .firstName("Jane")
                .lastName("Doe")
                .build();

        BugItemCommentDto updatedCommentDto = BugItemCommentDto.builder()
                .id(commentId)
                .comment(updatedCommentText)
                .commenter(updatedCommenter)
                .build();

        when(bugItemCommentService.updateComment(commentId, updatedCommentDto)).thenReturn(updatedCommentDto);

        mockMvc.perform(put("/api/bugitem/bugitemcomments/{commentId}", commentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedCommentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(commentId))
                .andExpect(jsonPath("$.comment").value(updatedCommentText))
                .andExpect(jsonPath("$.commenter.id").value(1L))
                .andExpect(jsonPath("$.commenter.email").value("updated@example.com"))
                .andExpect(jsonPath("$.commenter.firstName").value("Jane"))
                .andExpect(jsonPath("$.commenter.lastName").value("Doe"));

        verify(bugItemCommentService, times(1)).updateComment(commentId, updatedCommentDto);
    }

    @Test
    void deleteComment_ShouldReturnNoContent_WhenValidCommentIdIsProvided() throws Exception {
        Long commentId = 1L;
        doNothing().when(bugItemCommentService).deleteComment(commentId);

        mockMvc.perform(delete("/api/bugitem/bugitemcomments/{commentId}", commentId))
                .andExpect(status().isNoContent());

        verify(bugItemCommentService, times(1)).deleteComment(commentId);
    }

}
