package home.projectmanager.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import home.projectmanager.dto.ProjectDto;
import home.projectmanager.dto.TeamDto;
import home.projectmanager.dto.UserDto;
import home.projectmanager.exception.team.TeamAlreadyExistsException;
import home.projectmanager.exception.team.TeamNameNotProvidedException;
import home.projectmanager.exception.team.TeamNotFoundException;
import home.projectmanager.service.TeamService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class TeamControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TeamService teamService;

    @Test
    void createTeam_ShouldReturnCreatedTeam_WhenTeamIsValid() throws Exception {

        String teamName = "Backend";
        TeamDto expectedTeam = TeamDto.builder()
                .teamName(teamName)
                .build();

        when(teamService.createTeam(any(TeamDto.class))).thenReturn(expectedTeam);

        mockMvc.perform(post("/api/teams")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(expectedTeam)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.teamName").value(teamName));
    }

    @Test
    void createTeam_ShouldReturnBadRequest_WhenTeamNameIsEmpty() throws Exception {

        String teamName = "";
        TeamDto teamDto = TeamDto.builder()
                .teamName(teamName)
                .build();

        when(teamService.createTeam(any(TeamDto.class))).thenThrow(new TeamNameNotProvidedException("Team name cannot be empty"));

        mockMvc.perform(post("/api/teams")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(teamDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Team name cannot be empty"));
    }

    @Test
    void createTeam_ShouldReturnBadRequest_WhenTeamAlreadyExists() throws Exception {

        String teamName = "Backend";
        TeamDto teamDto = TeamDto.builder()
                .teamName(teamName)
                .build();

        when(teamService.createTeam(any(TeamDto.class))).thenThrow(new TeamAlreadyExistsException("Team with name " + teamName + " already exists"));

        mockMvc.perform(post("/api/teams")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(teamDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Team with name " + teamName + " already exists"));
    }

    @Test
    void getTeam_ShouldReturnTeam_WhenTeamExists() throws Exception {

        Long teamId = 1L;
        String teamName = "Backend";
        TeamDto expectedTeamDto = TeamDto.builder()
                .id(teamId)
                .teamName(teamName)
                .projects(List.of(ProjectDto.builder()
                        .id(1L)
                        .projectName("Project")
                        .projectDescription("Description")
                        .build()))
                .users(List.of(UserDto.builder()
                        .id(1L)
                        .firstName("John")
                        .lastName("Doe")
                        .email("")
                        .build()))
                .build();

        when(teamService.getTeam(teamId)).thenReturn(expectedTeamDto);

        mockMvc.perform(get("/api/teams/" + teamId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.teamName").value(teamName))
                .andExpect(jsonPath("$.projects[0].projectName").value("Project"))
                .andExpect(jsonPath("$.users[0].firstName").value("John"));
    }

    @Test
    void getTeam_ShouldReturnNotFound_WhenTeamDoesNotExist() throws Exception {

        Long teamId = 1L;

        when(teamService.getTeam(teamId)).thenThrow(new TeamNotFoundException("Team with id " + teamId + " not found"));

        mockMvc.perform(get("/api/teams/" + teamId))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Team with id " + teamId + " not found"));
    }

    @Test
    void getTeams_ShouldReturnListOfTeams_WhenTeamsExist() throws Exception {

        when(teamService.getTeams()).thenReturn(List.of(
                TeamDto.builder()
                        .id(1L)
                        .teamName("Backend")
                        .build(),
                TeamDto.builder()
                        .id(2L)
                        .teamName("Frontend")
                        .build()
        ));
        mockMvc.perform(get("/api/teams"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].teamName").value("Backend"))
                .andExpect(jsonPath("$[1].teamName").value("Frontend"));
    }

    @Test
    void getTeams_ShouldReturnEmptyList_WhenNoTeamsExist() throws Exception {

        when(teamService.getTeams()).thenReturn(List.of());

        mockMvc.perform(get("/api/teams"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void deleteTeam_ShouldReturnNoContent_WhenTeamIsDeleted() throws Exception {

        Long teamId = 1L;

        mockMvc.perform(delete("/api/teams/" + teamId))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteTeam_ShouldReturnNotFound_WhenTeamDoesNotExist() throws Exception {

        Long teamId = 1L;

        doThrow(new TeamNotFoundException("Team with id " + teamId + " not found")).when(teamService).deleteTeam(teamId);

        mockMvc.perform(delete("/api/teams/" + teamId))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Team with id " + teamId + " not found"));
    }

    @Test
    void updateTeam_ShouldReturnUpdatedTeam_WhenTeamIsValid() throws Exception {

        Long teamId = 1L;
        String teamName = "Backend";
        TeamDto teamDto = TeamDto.builder()
                .teamName(teamName)
                .build();

        when(teamService.updateTeam(teamId, teamDto)).thenReturn(teamDto);

        mockMvc.perform(put("/api/teams/" + teamId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(teamDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.teamName").value(teamName));
    }

    @Test
    void updateTeam_ShouldReturnNotFound_WhenTeamDoesNotExist() throws Exception {

        Long teamId = 1L;
        String teamName = "Backend";
        TeamDto teamDto = TeamDto.builder()
                .teamName(teamName)
                .build();

        when(teamService.updateTeam(teamId, teamDto)).thenThrow(new TeamNotFoundException("Team with id " + teamId + " not found"));

        mockMvc.perform(put("/api/teams/" + teamId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(teamDto)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Team with id " + teamId + " not found"));
    }

    @Test
    void addUserToTeam_ShouldReturnOk_WhenUserIsAdded() throws Exception {
        String userEmail = "john.doe@eamil.com";
        Long teamId = 1L;

        mockMvc.perform(put("/api/teams/" + teamId + "/users/" + userEmail))
                .andExpect(status().isOk());
    }

    @Test
    void addUserToTeam_ShouldReturnNotFound_WhenTeamDoesNotExist() throws Exception {
        String userEmail = "john.doe@eamil.com";
        Long teamId = 1L;

        doThrow(new TeamNotFoundException("Team with id " + teamId + " not found")).when(teamService).addUserToTeam(teamId, userEmail);

        mockMvc.perform(put("/api/teams/" + teamId + "/users/" + userEmail))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Team with id " + teamId + " not found"));
    }

    @Test
    void addUserToTeam_ShouldReturnNotFound_WhenUserDoesNotExist() throws Exception {
        String userEmail = "john.doe@eamil.com";
        Long teamId = 1L;

        doThrow(new TeamNotFoundException("User with id " + userEmail + " not found")).when(teamService).addUserToTeam(teamId, userEmail);

        mockMvc.perform(put("/api/teams/" + teamId + "/users/" + userEmail))
                .andExpect(status().isNotFound())
                .andExpect(content().string("User with id " + userEmail + " not found"));
    }

    @Test
    void getTeamsByUserId_ShouldReturnListOfTeams_WhenUserExists() throws Exception {
        Long userId = 1L;

        TeamDto team1 = TeamDto.builder().id(1L).teamName("Backend").build();
        TeamDto team2 = TeamDto.builder().id(2L).teamName("Frontend").build();

        when(teamService.getTeamsByUserId(userId)).thenReturn(List.of(team1, team2));

        mockMvc.perform(get("/api/teams/users/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].teamName").value("Backend"))
                .andExpect(jsonPath("$[1].teamName").value("Frontend"));
    }

    @Test
    void removeUserFromTeam_ShouldReturnOk_WhenUserIsRemoved() throws Exception {
        Long teamId = 1L;
        String userEmail = "john.doe@email.com";

        mockMvc.perform(delete("/api/teams/{teamId}/users/{userEmail}", teamId, userEmail))
                .andExpect(status().isOk());
    }


}
