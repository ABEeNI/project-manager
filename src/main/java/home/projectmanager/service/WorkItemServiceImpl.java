package home.projectmanager.service;

import home.projectmanager.dto.BugItemDto;
import home.projectmanager.dto.UserDto;
import home.projectmanager.dto.WorkItemCommentDto;
import home.projectmanager.dto.WorkItemDto;
import home.projectmanager.entity.*;
import home.projectmanager.exception.board.BoardNotFoundException;
import home.projectmanager.exception.bugitem.BugItemNotFoundException;
import home.projectmanager.exception.user.UserNotFoundException;
import home.projectmanager.exception.workitem.WorkItemNotFoundException;
import home.projectmanager.exception.workitem.WorkItemTitleNotProvidedException;
import home.projectmanager.repository.BoardRepository;
import home.projectmanager.repository.BugItemRepository;
import home.projectmanager.repository.UserRepository;
import home.projectmanager.repository.WorkItemRepository;
import home.projectmanager.service.accesscontrol.AccessDecisionVoter;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkItemServiceImpl implements WorkItemService {

    private final WorkItemRepository workItemRepository;
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final AccessDecisionVoter accessDecisionVoter;
    private final BugItemRepository bugitemRepository;

    @Override
    @Transactional
    public WorkItemDto createWorkItem(WorkItemDto workItemDto) {
        if(workItemDto.title() == null || workItemDto.title().isBlank()) {
            throw new WorkItemTitleNotProvidedException("Title not provided");
        }
        if(workItemDto.boardId() == null) {
           throw new BoardNotFoundException("Board id not provided");//Could be IllegalArgumentException
        }

        Board board = boardRepository.findById(workItemDto.boardId())
                .orElseThrow(() -> new BoardNotFoundException("Board with id " + workItemDto.boardId() + " not found"));
        if(!accessDecisionVoter.hasPermission(board)) {
            throw new AccessDeniedException("User does not have permission to board with id " + workItemDto.boardId());
        }

        WorkItem parentWorkItem = null;
        if (workItemDto.parentWorkItemId() != null) {
            parentWorkItem = workItemRepository.findById(workItemDto.parentWorkItemId())
                    .orElseThrow(() -> new WorkItemNotFoundException("Parent work item not found"));
            if(!parentWorkItem.getBoardId().equals(workItemDto.boardId())) {
                throw new WorkItemNotFoundException("Parent work item is not in the same board");
            }
        }

        User assignedUser = null;
        if (workItemDto.assignedUser() != null && workItemDto.assignedUser().id() != null) {
            assignedUser = userRepository.findById(workItemDto.assignedUser().id())
                    .orElseThrow(() -> new UserNotFoundException("Assigned user not found"));
            if(!accessDecisionVoter.hasPermission(board, assignedUser)) {
                throw new AccessDeniedException("User with id " + assignedUser.getId() + " does not have permission to board with id " + workItemDto.boardId());
            }
        }

        WorkItem workItem = WorkItem.builder()
                .title(workItemDto.title())
                .description(workItemDto.description())
                .points(workItemDto.points())
                .status(workItemDto.status() != null ? workItemDto.status() : WorkItemStatus.NEW)
                .boardId(board.getId())
                .projectId(board.getProjectId())
                .parentWorkItem(parentWorkItem)
                .assignedUser(assignedUser)
                .build();

        WorkItem savedWorkItem = workItemRepository.save(workItem);
        return WorkItemDto.builder()
                .id(savedWorkItem.getId())
                .title(savedWorkItem.getTitle())
                .description(savedWorkItem.getDescription())
                .points(savedWorkItem.getPoints())
                .status(savedWorkItem.getStatus())
                .boardId(savedWorkItem.getBoardId())
                .parentWorkItemId(workItemDto.parentWorkItemId())
                .assignedUser(savedWorkItem.getAssignedUser() != null ? UserDto.builder()
                        .id(savedWorkItem.getAssignedUser().getId())
                        .email(savedWorkItem.getAssignedUser().getEmail())
                        .firstName(savedWorkItem.getAssignedUser().getFirstName())
                        .lastName(savedWorkItem.getAssignedUser().getLastName())
                        .build() : null)
                .build();
    }

    @Override
    public WorkItemDto getWorkItem(Long id) {
        WorkItem workItem = workItemRepository.findById(id)
                .orElseThrow(() -> new WorkItemNotFoundException("Work item not found"));
        if(!accessDecisionVoter.hasPermission(workItem)) {
            throw new AccessDeniedException("User does not have permission to work item with id " + id);
        }
        List<WorkItemComment> comments = workItem.getComments();
        List<WorkItemCommentDto> commentDtos = comments.stream()
                .map(comment -> WorkItemCommentDto.builder()
                        .id(comment.getId())
                        .comment(comment.getComment())
                        .commenter(comment.getCommenter() != null ? UserDto.builder()
                                .id(comment.getCommenter().getId())
                                .email(comment.getCommenter().getEmail())
                                .firstName(comment.getCommenter().getFirstName())
                                .lastName(comment.getCommenter().getLastName())
                                .build() : null)
                        .build())
                .collect(Collectors.toList());
        BugItem bugItem = workItem.getBugItem();
        BugItemDto bugItemDto = bugItem != null ? BugItemDto.builder()
                .id(bugItem.getId())
                .title(bugItem.getTitle())
                .description(bugItem.getDescription())
                .build() : null;

        return WorkItemDto.builder()
                .id(workItem.getId())
                .title(workItem.getTitle())
                .description(workItem.getDescription())
                .points(workItem.getPoints())
                .status(workItem.getStatus())
                .parentWorkItemId(workItem.getParentWorkItem() != null ? workItem.getParentWorkItem().getId() : null)
                .boardId(workItem.getBoardId())
                .subWorkItems(workItem.getSubWorkItems().stream()
                        .map(this::convertToDto)
                        .collect(Collectors.toList()))//what if null?
                .comments(commentDtos)
                .assignedUser(workItem.getAssignedUser() != null ? UserDto.builder()
                        .id(workItem.getAssignedUser().getId())
                        .email(workItem.getAssignedUser().getEmail())
                        .build() : null)
                .bugItemDto(bugItemDto)
                .build();
    }

    @Override
    @Transactional
    public void deleteWorkItem(Long id) {
        WorkItem workItem = workItemRepository.findById(id)
                .orElseThrow(() -> new WorkItemNotFoundException("Work item not found"));
        if(!accessDecisionVoter.hasPermission(workItem)) {
            throw new AccessDeniedException("User does not have permission to work item with id " + id);
        }
        workItemRepository.deleteById(id);
    }

    @Override
    @Transactional
    public WorkItemDto updateWorkItem(Long id, WorkItemDto workItemDto) {
        WorkItem workItem = workItemRepository.findById(id)
                .orElseThrow(() -> new WorkItemNotFoundException("Work item not found"));
        if(!accessDecisionVoter.hasPermission(workItem)) {
            throw new AccessDeniedException("User does not have permission to work item with id " + id);
        }

        if (workItemDto.title() != null && !workItemDto.title().isBlank()) {
            workItem.setTitle(workItemDto.title());
        }

        if (workItemDto.description() != null) {
            workItem.setDescription(workItemDto.description());
        }

        if (workItemDto.points() != null) {
            workItem.setPoints(workItemDto.points());
        }

        if (workItemDto.status() != null) {
            workItem.setStatus(workItemDto.status());
        }

        if (workItemDto.assignedUser() != null && workItemDto.assignedUser().id() != null) {
            User assignedUser = userRepository.findById(workItemDto.assignedUser().id())
                    .orElseThrow(() -> new UserNotFoundException("Assigned user not found"));
            workItem.setAssignedUser(assignedUser);
        }
        if(workItemDto.bugItemDto() != null && workItemDto.bugItemDto().id() != null) {
            BugItem bugItem = bugitemRepository.findById(workItemDto.bugItemDto().id())
                    .orElseThrow(() -> new BugItemNotFoundException("Bug item with id " + workItemDto.bugItemDto().id() + " not found"));
            BugItem previosBugItem = workItem.getBugItem();
            if(previosBugItem != null) {
                previosBugItem.setWorkItem(null);
                bugitemRepository.save(previosBugItem);
            }
            workItem.addBugItem(bugItem);
        }
        if(workItemDto.parentWorkItemId() != null) {
            WorkItem parentWorkItem = workItemRepository.findById(workItemDto.parentWorkItemId())
                    .orElseThrow(() -> new WorkItemNotFoundException("Parent work item not found"));
            workItem.setParentWorkItem(parentWorkItem);
        }
        WorkItem updatedWorkItem = workItemRepository.save(workItem);
        return convertToDto(updatedWorkItem);
    }

private WorkItemDto convertToDto(WorkItem workItem) {
    return WorkItemDto.builder()
            .id(workItem.getId())
            .title(workItem.getTitle())
            .description(workItem.getDescription())
            .points(workItem.getPoints())
            .status(workItem.getStatus())
            .boardId(workItem.getBoardId())
            .parentWorkItemId(workItem.getParentWorkItem() != null ? workItem.getParentWorkItem().getId() : null)
            .assignedUser(workItem.getAssignedUser() != null ? UserDto.builder()
                    .id(workItem.getAssignedUser().getId())
                    .email(workItem.getAssignedUser().getEmail())
                    .firstName(workItem.getAssignedUser().getFirstName())
                    .lastName(workItem.getAssignedUser().getLastName())
                    .build() : null)
            .bugItemDto(workItem.getBugItem() != null ? BugItemDto.builder()
                    .id(workItem.getBugItem().getId())
                    .title(workItem.getBugItem().getTitle())
                    .description(workItem.getBugItem().getDescription())
                    .build() : null)
            .build();
}
}
