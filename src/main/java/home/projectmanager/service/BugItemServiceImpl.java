package home.projectmanager.service;

import home.projectmanager.dto.BugItemCommentDto;
import home.projectmanager.dto.BugItemDto;
import home.projectmanager.dto.UserDto;
import home.projectmanager.dto.WorkItemDto;
import home.projectmanager.entity.*;
import home.projectmanager.exception.bugitem.BugItemTitleNotProvidedError;
import home.projectmanager.exception.project.ProjectNotFoundException;
import home.projectmanager.repository.BugItemRepository;
import home.projectmanager.repository.ProjectRepository;
import home.projectmanager.service.accesscontrol.AccessDecisionVoter;
import home.projectmanager.service.accesscontrol.AuthenticationFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BugItemServiceImpl implements BugItemService {
    private final ProjectRepository projectRepository;
    private final AccessDecisionVoter accessDecisionVoter;
    private final BugItemRepository bugItemRepository;
    private final AuthenticationFacade authenticationFacade  ;

    @Override
    public BugItemDto createBugItem(BugItemDto bugItemDto) {
        if(bugItemDto.title() == null || bugItemDto.title().isBlank()) {
            throw new BugItemTitleNotProvidedError("Title is required");
        }
        if(bugItemDto.projectId() == null) {
            throw new ProjectNotFoundException("Project id is required");
        }
        Project project = projectRepository.findById(bugItemDto.projectId())
                .orElseThrow(() -> new ProjectNotFoundException("Project with id: " + bugItemDto.projectId() + " not found"));

        if(!accessDecisionVoter.hasPermission(project)) {
            throw new AccessDeniedException("User does not have permission to project with id " + bugItemDto.projectId());
        }
        User currenUser = authenticationFacade.getCurrentUser();
        BugItem newBugItem = BugItem.builder()
                .title(bugItemDto.title())
                .description(bugItemDto.description())
                .projectId(bugItemDto.projectId())
                .reporter(currenUser)
                .status(bugItemDto.status() == null ? BugItemStatus.REPORTED : bugItemDto.status())
                .build();
        //project.addBugItem(newBugItem); probably not needed
        BugItem savedBugItem = bugItemRepository.save(newBugItem);
        log.info("BugItem created: {}", savedBugItem);
        return convertToDto(savedBugItem);
    }

    @Override
    public BugItemDto getBugItem(Long id) {
        BugItem bugItem = bugItemRepository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException("BugItem with id: " + id + " not found"));
        if(!accessDecisionVoter.hasPermission(bugItem)) {
            throw new AccessDeniedException("User does not have permission to project with id " + bugItem.getProjectId());
        }
        return convertToDto(bugItem);
    }

    @Override
    public List<BugItemDto> getBugItemsByProject(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException("Project with id: " + projectId + " not found"));
        if(!accessDecisionVoter.hasPermission(project)) {
            throw new AccessDeniedException("User does not have permission to project with id " + projectId);
        }
        List<BugItem> bugItems = project.getBugItems();
        return bugItems.stream()
                .map(this::convertToDto)
                .toList();
    }

    @Override
    public BugItemDto updateBugItem(Long id, BugItemDto bugItemDto) {
        BugItem bugItem = bugItemRepository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException("BugItem with id: " + id + " not found"));
        if(!accessDecisionVoter.hasPermission(bugItem)) {
            throw new AccessDeniedException("User does not have permission to project with id " + bugItem.getProjectId());
        }
        if(bugItemDto.title() != null && !bugItemDto.title().isBlank()) {
            bugItem.setTitle(bugItemDto.title());
        }
        if (bugItemDto.description() != null) {
            bugItem.setDescription(bugItemDto.description());
        }
        if (bugItemDto.status() != null) {
            bugItem.setStatus(bugItemDto.status());
        }
        //you can only add BugItems to Workitems by updating WorkItem
        BugItem savedBugItem = bugItemRepository.save(bugItem);
        log.info("BugItem updated: {}", savedBugItem);
        return convertToDto(savedBugItem);

    }

    @Override
    public void deleteBugItem(Long id) {
        BugItem bugItem = bugItemRepository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException("BugItem with id: " + id + " not found"));
        if(!accessDecisionVoter.hasPermission(bugItem)) {
            throw new AccessDeniedException("User does not have permission to project with id " + bugItem.getProjectId());
        }
        bugItemRepository.deleteById(id);
        log.info("BugItem deleted: {}", bugItem);
    }
    private BugItemDto convertToDto(BugItem savedBugItem) {
        return BugItemDto.builder()
                .id(savedBugItem.getId())
                .title(savedBugItem.getTitle())
                .description(savedBugItem.getDescription())
                .status(savedBugItem.getStatus())
                .projectId(savedBugItem.getProjectId())
                .workItemDto(savedBugItem.getWorkItem() == null ? null : WorkItemDto.builder()
                        .id(savedBugItem.getWorkItem().getId())
                        .title(savedBugItem.getWorkItem().getTitle())
                        .build())
                .comments(savedBugItem.getComments() == null ? null : savedBugItem.getComments().stream()
                        .map(this::convertToDto)
                        .toList())
                .build();
    }
    private BugItemCommentDto convertToDto(BugItemComment bugItemComment) {
        return BugItemCommentDto.builder()
                .id(bugItemComment.getId())
                .comment(bugItemComment.getComment())
                .commenter(UserDto.builder()
                        .id(bugItemComment.getCommenter().getId())
                        .email(bugItemComment.getCommenter().getEmail())
                        .firstName(bugItemComment.getCommenter().getFirstName())
                        .lastName(bugItemComment.getCommenter().getLastName())
                        .build())
                .build();
    }
}
