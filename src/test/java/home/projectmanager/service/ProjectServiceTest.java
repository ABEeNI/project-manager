package home.projectmanager.service;

import home.projectmanager.dto.ProjectDto;
import home.projectmanager.entity.Project;
import home.projectmanager.repository.ProjectRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {

    @InjectMocks
    private ProjectServiceImpl projectService;

    @Mock
    private ProjectRepository projectRepository;

    @Test
    void testCreateProject() {
        ProjectDto projectDto = ProjectDto.builder()
                .projectName("Project")
                .projectDescription("Description")
                .build();

        Project project = Project.builder()
                .id(1L)
                .projectName("Project")
                .projectDescription("Description")
                .build();

        when(projectRepository.findByProjectName(projectDto.projectName())).thenReturn(Optional.empty());
        when(projectRepository.save(any(Project.class))).thenReturn(project);
    }
}
