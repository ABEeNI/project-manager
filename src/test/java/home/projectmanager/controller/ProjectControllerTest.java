package home.projectmanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import home.projectmanager.dto.ProjectDto;
import home.projectmanager.service.ProjectService;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class ProjectControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProjectService projectService;

    @Test
    void should_create_project() throws Exception {
        String projectName = "Project Thesis";
        String description = "Thesis project";
        ProjectDto projectDto = ProjectDto.builder()
                .projectName(projectName)
                .projectDescription(description)
                .build();

        Long teamId = 1L;
        ProjectDto createdProjectDto = ProjectDto.builder()
                .id(1L)
                .projectName(projectName)
                .projectDescription(description)
                .build();

        when(projectService.createProject(any(ProjectDto.class), eq(teamId))).thenReturn(createdProjectDto);

        mockMvc.perform(post("/api/projects/teams/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(projectDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.projectName").value("Project Thesis"));
    }

    @Test
    void addTeamToProject_ShouldReturnOk_WhenTeamIsAdded() throws Exception {
        Long projectId = 1L;
        Long teamId = 2L;

        mockMvc.perform(put("/api/projects/{projectId}/teams/{teamId}", projectId, teamId))
                .andExpect(status().isOk());
    }

    @Test
    void removeTeamFromProject_ShouldReturnOk_WhenTeamIsRemoved() throws Exception {
        Long projectId = 1L;
        Long teamId = 2L;

        mockMvc.perform(delete("/api/projects/{projectId}/teams/{teamId}", projectId, teamId))
                .andExpect(status().isOk());
    }

    @Test
    void getProject_ShouldReturnOk_WhenProjectExists() throws Exception {
        Long projectId = 1L;
        ProjectDto projectDto = ProjectDto.builder()
                .id(projectId)
                .projectName("Project Thesis")
                .projectDescription("Thesis project")
                .build();

        when(projectService.getProject(projectId)).thenReturn(projectDto);

        mockMvc.perform(get("/api/projects/{id}", projectId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(projectId))
                .andExpect(jsonPath("$.projectName").value("Project Thesis"))
                .andExpect(jsonPath("$.projectDescription").value("Thesis project"));
    }

    @Test
    void getProjectsByUserId_ShouldReturnOk_WhenProjectsExistForUser() throws Exception {
        Long userId = 1L;
        List<ProjectDto> projects = Arrays.asList(
                ProjectDto.builder().id(1L).projectName("Project A").projectDescription("Description A").build(),
                ProjectDto.builder().id(2L).projectName("Project B").projectDescription("Description B").build()
        );

        when(projectService.getProjectsByUserId(userId)).thenReturn(projects);

        mockMvc.perform(get("/api/projects/users/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].projectName").value("Project A"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].projectName").value("Project B"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getProjects_ShouldReturnOk_WhenProjectsExist() throws Exception {
        List<ProjectDto> projects = Arrays.asList(
                ProjectDto.builder().id(1L).projectName("Project A").projectDescription("Description A").build(),
                ProjectDto.builder().id(2L).projectName("Project B").projectDescription("Description B").build()
        );

        when(projectService.getProjects()).thenReturn(projects);

        mockMvc.perform(get("/api/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].projectName").value("Project A"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].projectName").value("Project B"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteProject_ShouldReturnNoContent_WhenProjectIsDeletedByAdmin() throws Exception {
        Long projectId = 1L;

        doNothing().when(projectService).deleteProject(projectId);

        mockMvc.perform(delete("/api/projects/{id}", projectId))
                .andExpect(status().isNoContent());
    }

    @Test
    void updateProject_ShouldReturnOk_WhenProjectIsUpdatedSuccessfully() throws Exception {
        Long projectId = 1L;
        ProjectDto projectDto = ProjectDto.builder()
                .id(projectId)
                .projectName("Updated Project")
                .projectDescription("Updated description")
                .build();

        ProjectDto updatedProjectDto = ProjectDto.builder()
                .id(projectId)
                .projectName("Updated Project")
                .projectDescription("Updated description")
                .build();

        when(projectService.updateProject(eq(projectId), any(ProjectDto.class))).thenReturn(updatedProjectDto);

        mockMvc.perform(put("/api/projects/{id}", projectId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(projectDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(projectId))
                .andExpect(jsonPath("$.projectName").value("Updated Project"))
                .andExpect(jsonPath("$.projectDescription").value("Updated description"));
    }

}
