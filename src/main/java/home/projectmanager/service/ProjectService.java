package home.projectmanager.service;

import home.projectmanager.dto.ProjectDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ProjectService {
    ProjectDto createProject(ProjectDto projectDto, Long teamId);

    ProjectDto getProject(Long id);

    List<ProjectDto> getProjects();

    void deleteProject(Long id);

    ProjectDto updateProject(Long id, ProjectDto projectDto);

    void addTeamToProject(Long projectId, Long teamId);

    void removeTeamFromProject(Long projectId, Long teamId);

    List<ProjectDto> getProjectsByUserId(Long userId);
}
