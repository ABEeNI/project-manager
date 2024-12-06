package home.projectmanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import home.projectmanager.dto.*;
import home.projectmanager.entity.BugItemStatus;
import home.projectmanager.entity.WorkItemStatus;
import home.projectmanager.service.BugItemService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class BugItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BugItemService bugItemService;

    @Test
    void createBugItem_ShouldReturnCreatedBugItem_WhenValidRequestIsMade() throws Exception {
        BugItemDto bugItemDto = BugItemDto.builder()
                .id(1L)
                .title("Bug Title")
                .description("Bug Description")
                .status(BugItemStatus.REPORTED)
                .comments(new ArrayList<>())
                .projectId(1L)
                .workItemDto(WorkItemDto.builder()
                        .id(1L)
                        .title("Work Item Title")
                        .description("Work Item Description")
                        .points(5)
                        .status(WorkItemStatus.NEW)
                        .parentWorkItemId(null)
                        .boardId(1L)
                        .subWorkItems(new ArrayList<>())
                        .comments(new ArrayList<>())
                        .assignedUser(UserDto.builder().id(1L).email("user@example.com").firstName("John").lastName("Doe").build())
                        .bugItemDto(null)
                        .build())
                .reporter(UserDto.builder().id(1L).email("user@example.com").firstName("John").lastName("Doe").build())
                .build();

        when(bugItemService.createBugItem(any(BugItemDto.class)))
                .thenReturn(bugItemDto);

        mockMvc.perform(post("/api/bugitems")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bugItemDto)))
                .andExpect(status().isCreated())  // Expect 201 CREATED status
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Bug Title"))
                .andExpect(jsonPath("$.description").value("Bug Description"))
                .andExpect(jsonPath("$.status").value("REPORTED"))
                .andExpect(jsonPath("$.projectId").value(1L))
                .andExpect(jsonPath("$.workItemDto.id").value(1L))
                .andExpect(jsonPath("$.workItemDto.title").value("Work Item Title"))
                .andExpect(jsonPath("$.workItemDto.description").value("Work Item Description"))
                .andExpect(jsonPath("$.workItemDto.points").value(5))
                .andExpect(jsonPath("$.workItemDto.status").value("NEW"))
                .andExpect(jsonPath("$.reporter.id").value(1L))
                .andExpect(jsonPath("$.reporter.email").value("user@example.com"))
                .andExpect(jsonPath("$.reporter.firstName").value("John"))
                .andExpect(jsonPath("$.reporter.lastName").value("Doe"));
    }

    @Test
    void getBugItem_ShouldReturnBugItem_WhenValidIdIsProvided() throws Exception {
        BugItemDto bugItemDto = BugItemDto.builder()
                .id(1L)
                .title("Bug Title")
                .description("Bug Description")
                .status(BugItemStatus.REPORTED)
                .comments(new ArrayList<>())
                .projectId(1L)
                .workItemDto(WorkItemDto.builder()
                        .id(1L)
                        .title("Work Item Title")
                        .description("Work Item Description")
                        .points(5)
                        .status(WorkItemStatus.NEW)
                        .parentWorkItemId(null)
                        .boardId(1L)
                        .subWorkItems(new ArrayList<>())
                        .comments(new ArrayList<>())
                        .assignedUser(UserDto.builder().id(1L).email("user@example.com").firstName("John").lastName("Doe").build())
                        .bugItemDto(null)
                        .build())
                .reporter(UserDto.builder().id(1L).email("user@example.com").firstName("John").lastName("Doe").build())
                .build();

        when(bugItemService.getBugItem(1L)).thenReturn(bugItemDto);

        mockMvc.perform(get("/api/bugitems/{id}", 1L))
                .andExpect(status().isOk())  // Expect 200 OK status
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Bug Title"))
                .andExpect(jsonPath("$.description").value("Bug Description"))
                .andExpect(jsonPath("$.status").value("REPORTED"))
                .andExpect(jsonPath("$.projectId").value(1L))
                .andExpect(jsonPath("$.workItemDto.id").value(1L))
                .andExpect(jsonPath("$.workItemDto.title").value("Work Item Title"))
                .andExpect(jsonPath("$.workItemDto.description").value("Work Item Description"))
                .andExpect(jsonPath("$.workItemDto.points").value(5))
                .andExpect(jsonPath("$.workItemDto.status").value("NEW"))
                .andExpect(jsonPath("$.reporter.id").value(1L))
                .andExpect(jsonPath("$.reporter.email").value("user@example.com"))
                .andExpect(jsonPath("$.reporter.firstName").value("John"))
                .andExpect(jsonPath("$.reporter.lastName").value("Doe"));
    }

    @Test
    void getBugItemsByProject_ShouldReturnBugItems_WhenValidProjectIdIsProvided() throws Exception {
        BugItemDto bugItemDto1 = BugItemDto.builder()
                .id(1L)
                .title("Bug Title 1")
                .description("Bug Description 1")
                .status(BugItemStatus.REPORTED)
                .comments(new ArrayList<>())
                .projectId(1L)
                .workItemDto(WorkItemDto.builder()
                        .id(1L)
                        .title("Work Item Title")
                        .description("Work Item Description")
                        .points(5)
                        .status(WorkItemStatus.NEW)
                        .parentWorkItemId(null)
                        .boardId(1L)
                        .subWorkItems(new ArrayList<>())
                        .comments(new ArrayList<>())
                        .assignedUser(UserDto.builder().id(1L).email("user@example.com").firstName("John").lastName("Doe").build())
                        .bugItemDto(null)
                        .build())
                .reporter(UserDto.builder().id(1L).email("user@example.com").firstName("John").lastName("Doe").build())
                .build();

        BugItemDto bugItemDto2 = BugItemDto.builder()
                .id(2L)
                .title("Bug Title 2")
                .description("Bug Description 2")
                .status(BugItemStatus.REPORTED)
                .comments(new ArrayList<>())
                .projectId(1L)
                .workItemDto(WorkItemDto.builder()
                        .id(2L)
                        .title("Work Item Title")
                        .description("Work Item Description")
                        .points(3)
                        .status(WorkItemStatus.NEW)
                        .parentWorkItemId(null)
                        .boardId(1L)
                        .subWorkItems(new ArrayList<>())
                        .comments(new ArrayList<>())
                        .assignedUser(UserDto.builder().id(2L).email("user2@example.com").firstName("Jane").lastName("Doe").build())
                        .bugItemDto(null)
                        .build())
                .reporter(UserDto.builder().id(2L).email("user2@example.com").firstName("Jane").lastName("Doe").build())
                .build();

        List<BugItemDto> bugItems = Arrays.asList(bugItemDto1, bugItemDto2);

        when(bugItemService.getBugItemsByProject(1L)).thenReturn(bugItems);

        mockMvc.perform(get("/api/bugitems/projects/{projectId}", 1L))
                .andExpect(status().isOk())  // Expect 200 OK status
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].title").value("Bug Title 1"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].title").value("Bug Title 2"))
                .andExpect(jsonPath("$[0].status").value("REPORTED"))
                .andExpect(jsonPath("$[1].status").value("REPORTED"));
    }

    @Test
    void updateBugItem_ShouldReturnUpdatedBugItem_WhenValidIdAndRequestAreProvided() throws Exception {
        BugItemDto bugItemDto = BugItemDto.builder()
                .id(1L)
                .title("Updated Bug Title")
                .description("Updated Bug Description")
                .status(BugItemStatus.CLOSED)
                .comments(new ArrayList<>())
                .projectId(1L)
                .workItemDto(WorkItemDto.builder()
                        .id(1L)
                        .title("Updated Work Item Title")
                        .description("Updated Work Item Description")
                        .points(5)
                        .status(WorkItemStatus.IN_PROGRESS)
                        .parentWorkItemId(null)
                        .boardId(1L)
                        .subWorkItems(new ArrayList<>())
                        .comments(new ArrayList<>())
                        .assignedUser(UserDto.builder().id(1L).email("user@example.com").firstName("John").lastName("Doe").build())
                        .bugItemDto(null)
                        .build())
                .reporter(UserDto.builder().id(1L).email("user@example.com").firstName("John").lastName("Doe").build())
                .build();

        when(bugItemService.updateBugItem(1L, bugItemDto)).thenReturn(bugItemDto);

        mockMvc.perform(put("/api/bugitems/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bugItemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Updated Bug Title"))
                .andExpect(jsonPath("$.description").value("Updated Bug Description"))
                .andExpect(jsonPath("$.status").value("CLOSED"))
                .andExpect(jsonPath("$.projectId").value(1L))
                .andExpect(jsonPath("$.workItemDto.id").value(1L))
                .andExpect(jsonPath("$.workItemDto.title").value("Updated Work Item Title"))
                .andExpect(jsonPath("$.workItemDto.description").value("Updated Work Item Description"))
                .andExpect(jsonPath("$.workItemDto.status").value("IN_PROGRESS"))
                .andExpect(jsonPath("$.reporter.id").value(1L))
                .andExpect(jsonPath("$.reporter.email").value("user@example.com"))
                .andExpect(jsonPath("$.reporter.firstName").value("John"))
                .andExpect(jsonPath("$.reporter.lastName").value("Doe"));
    }

    @Test
    void deleteBugItem_ShouldReturnNoContent_WhenValidIdIsProvided() throws Exception {
        doNothing().when(bugItemService).deleteBugItem(1L);

        mockMvc.perform(delete("/api/bugitems/{id}", 1L))
                .andExpect(status().isNoContent());
    }
}