package home.projectmanager.service.accesscontrol;

import home.projectmanager.entity.Project;
import home.projectmanager.entity.ProjectObject;
import home.projectmanager.entity.Team;
import home.projectmanager.entity.User;
import home.projectmanager.exception.project.ProjectNotFoundException;
import home.projectmanager.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccessDecisionVoter {

    public final AuthenticationFacade authenticationFacade;
    public final ProjectRepository projectRepository;

    public boolean hasPermission(ProjectObject projectObject) {
        User user = authenticationFacade.getCurrentUser();

        List<Project> projects = projectRepository.findAllByUserId(user.getId());

        for (Project project : projects) {
            if (project.getId().equals(projectObject.getParentProjectId())) {
                return true;
            }
        }
        return false;
    }

    public boolean hasPermission(Project projectToAccess) {
        User user = authenticationFacade.getCurrentUser();

        List<Project> projects = projectRepository.findAllByUserId(user.getId());

        for (Project project : projects) {
            if (project.getId().equals(projectToAccess.getId())) {
                return true;
            }
        }
        return false;
    }

    public boolean hasPermission(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException("Project with id " + projectId + " not found"));
        return hasPermission(project);
    }

    public boolean hasPermission(Team team) {
        User user = authenticationFacade.getCurrentUser();
        return user.getTeams().contains(team);
    }

    public boolean hasPermission(ProjectObject projectObject, User user) {
        List<Project> projects = projectRepository.findAllByUserId(user.getId());

        for (Project project : projects) {
            if (project.getId().equals(projectObject.getParentProjectId())) {
                return true;
            }
        }
        return false;
    }
}
