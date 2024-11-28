package home.projectmanager.controller;

import home.projectmanager.dto.ProjectDto;
import home.projectmanager.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/project")
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping("/team/{teamId}")
    public ResponseEntity<ProjectDto> createProject(@RequestBody ProjectDto projectDto, @PathVariable Long teamId) {
        ProjectDto createdProjectDto = projectService.createProject(projectDto, teamId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProjectDto);
    }
    @PutMapping("/{projectId}/team/{teamId}")
    public ResponseEntity<Void> addTeamToProject(@PathVariable Long projectId, @PathVariable Long teamId) {
        projectService.addTeamToProject(projectId, teamId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
    @DeleteMapping("/{projectId}/team/{teamId}")
    public ResponseEntity<Void> removeTeamFromProject(@PathVariable Long projectId, @PathVariable Long teamId) {
        projectService.removeTeamFromProject(projectId, teamId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectDto> getProject(@PathVariable Long id) {
        ProjectDto projectDto = projectService.getProject(id);
        return ResponseEntity.ok(projectDto);
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<List<ProjectDto>> getProjectsByUserId(@PathVariable Long userId) {
        List<ProjectDto> projects = projectService.getProjectsByUserId(userId);
        return ResponseEntity.ok(projects);
    }

    @GetMapping
    public ResponseEntity<List<ProjectDto>> getProjects() {
        List<ProjectDto> projects = projectService.getProjects();
        return ResponseEntity.ok(projects);
    }

    //TODO Just ADMIN Role should be able to delete a project
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjectDto> updateProject(@PathVariable Long id, @RequestBody ProjectDto projectDto) {
        ProjectDto updatedProjectDto = projectService.updateProject(id, projectDto);
        return ResponseEntity.ok(updatedProjectDto);
    }
}
