package home.projectmanager.controller;

import home.projectmanager.dto.TeamDto;
import home.projectmanager.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/team")
public class TeamController {

    @Autowired
    TeamService teamService;

    @PostMapping
    public ResponseEntity<TeamDto> createTeam(@RequestBody TeamDto teamDto) {
        TeamDto createdTeamDto = teamService.createTeam(teamDto);
        return new ResponseEntity<>(createdTeamDto, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TeamDto> getTeam(@PathVariable Long id) {
        TeamDto teamDto = teamService.getTeam(id);
        return ResponseEntity.ok(teamDto);
    }

}
