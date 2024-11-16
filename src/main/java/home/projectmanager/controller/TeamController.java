package home.projectmanager.controller;

import home.projectmanager.dto.TeamDto;
import home.projectmanager.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/team")
public class TeamController {

    @Autowired
    TeamService teamService;

    @PostMapping
    public ResponseEntity<TeamDto> createTeam(@RequestBody TeamDto teamDto) {
        TeamDto createdTeamDto = teamService.createTeam(teamDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTeamDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TeamDto> getTeam(@PathVariable Long id) {
        TeamDto teamDto = teamService.getTeam(id);
        return ResponseEntity.status(HttpStatus.OK).body(teamDto);
    }

    @GetMapping
    public ResponseEntity<List<TeamDto>> getTeams() {
        List<TeamDto> teamDtos = teamService.getTeams();
        return ResponseEntity.status(HttpStatus.OK).body(teamDtos);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeam(@PathVariable Long id) {
        teamService.deleteTeam(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<TeamDto> updateTeam(@PathVariable Long id, @RequestBody TeamDto teamDto) {
        TeamDto updatedTeamDto = teamService.updateTeam(id, teamDto);
        return ResponseEntity.status(HttpStatus.OK).body(updatedTeamDto);
    }
}
