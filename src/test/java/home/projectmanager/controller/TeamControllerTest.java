package home.projectmanager.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import home.projectmanager.dto.TeamDto;
import home.projectmanager.exception.TeamAlreadyExistsException;
import home.projectmanager.exception.TeamNameNotProvidedException;
import home.projectmanager.service.TeamService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
    void testCreateTeam() throws Exception {

        String teamName = "Backend";
        TeamDto expectedTeam = TeamDto.builder()
                .teamName(teamName)
                .build();

        when(teamService.createTeam(any(TeamDto.class))).thenReturn(expectedTeam);

        mockMvc.perform(post("/api/team")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(expectedTeam)))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.teamName").value(teamName));
    }

    @Test
    void testCreateTeamWithEmptyName() throws Exception {

        String teamName = "";
        TeamDto teamDto = TeamDto.builder()
                .teamName(teamName)
                .build();

        when(teamService.createTeam(any(TeamDto.class))).thenThrow(new TeamNameNotProvidedException("Team name cannot be empty"));

        mockMvc.perform(post("/api/team")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(teamDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Team name cannot be empty"));
    }

    @Test
    void testCreateTeamWhenTeamAlreadyExists() throws Exception {

        String teamName = "Backend";
        TeamDto teamDto = TeamDto.builder()
                .teamName(teamName)
                .build();

        when(teamService.createTeam(any(TeamDto.class))).thenThrow(new TeamAlreadyExistsException("Team with name " + teamName + " already exists"));

        mockMvc.perform(post("/api/team")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(teamDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Team with name " + teamName + " already exists"));
    }

    @Test
    void testGetTeamWhenIdIsProvided() throws Exception {

        Long teamId = 1L;
        String teamName = "Backend";
        TeamDto expectedTeam = TeamDto.builder()
                .teamName(teamName)
                .build();

        when(teamService.getTeam(teamId)).thenReturn(expectedTeam);

        mockMvc.perform(get("/api/team/" + teamId))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.teamName").value(teamName));
    }
}
