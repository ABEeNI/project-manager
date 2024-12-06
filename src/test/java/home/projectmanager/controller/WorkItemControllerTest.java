package home.projectmanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import home.projectmanager.dto.WorkItemDto;
import home.projectmanager.entity.WorkItemStatus;
import home.projectmanager.service.WorkItemService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class WorkItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private WorkItemService workItemService;

    @Test
    void createWorkItem_ShouldReturnCreatedWorkItem_WhenValidRequestIsMade() throws Exception {
        WorkItemDto workItemDto = WorkItemDto.builder()
                .id(1L)
                .title("Work Item 1")
                .description("Work Item Description")
                .points(5)
                .status(WorkItemStatus.NEW)
                .parentWorkItemId(null)
                .boardId(1L)
                .subWorkItems(Collections.emptyList())
                .comments(Collections.emptyList())
                .assignedUser(null)
                .bugItemDto(null)
                .build();

        when(workItemService.createWorkItem(any(WorkItemDto.class))).thenReturn(workItemDto);

        mockMvc.perform(post("/api/workitems")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(workItemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Work Item 1"))
                .andExpect(jsonPath("$.description").value("Work Item Description"))
                .andExpect(jsonPath("$.points").value(5))
                .andExpect(jsonPath("$.status").value("NEW"));
    }

    @Test
    void getWorkItem_ShouldReturnWorkItem_WhenValidIdIsProvided() throws Exception {
        Long workItemId = 1L;
        WorkItemDto workItemDto = WorkItemDto.builder()
                .id(workItemId)
                .title("Work Item 1")
                .description("Work Item Description")
                .points(5)
                .status(WorkItemStatus.NEW)
                .parentWorkItemId(null)
                .boardId(1L)
                .subWorkItems(Collections.emptyList())
                .comments(Collections.emptyList())
                .assignedUser(null)
                .bugItemDto(null)
                .build();

        when(workItemService.getWorkItem(workItemId)).thenReturn(workItemDto);

        mockMvc.perform(get("/api/workitems/{id}", workItemId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(workItemId))
                .andExpect(jsonPath("$.title").value("Work Item 1"))
                .andExpect(jsonPath("$.description").value("Work Item Description"))
                .andExpect(jsonPath("$.points").value(5))
                .andExpect(jsonPath("$.status").value("NEW"));
    }

    @Test
    void updateWorkItem_ShouldReturnUpdatedWorkItem_WhenValidRequestIsMade() throws Exception {
        Long workItemId = 1L;
        WorkItemDto workItemDto = WorkItemDto.builder()
                .id(workItemId)
                .title("Updated Work Item")
                .description("Updated description")
                .points(8)
                .status(WorkItemStatus.IN_PROGRESS)
                .parentWorkItemId(null)
                .boardId(1L)
                .subWorkItems(Collections.emptyList())
                .comments(Collections.emptyList())
                .assignedUser(null)
                .bugItemDto(null)
                .build();

        when(workItemService.updateWorkItem(eq(workItemId), any(WorkItemDto.class))).thenReturn(workItemDto);

        mockMvc.perform(put("/api/workitems/{id}", workItemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(workItemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(workItemId))
                .andExpect(jsonPath("$.title").value("Updated Work Item"))
                .andExpect(jsonPath("$.description").value("Updated description"))
                .andExpect(jsonPath("$.points").value(8))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    }

    @Test
    void deleteWorkItem_ShouldReturnNoContent_WhenValidIdIsProvided() throws Exception {
        Long workItemId = 1L;

        doNothing().when(workItemService).deleteWorkItem(workItemId);

        mockMvc.perform(delete("/api/workitems/{id}", workItemId))
                .andExpect(status().isNoContent());
    }
}
