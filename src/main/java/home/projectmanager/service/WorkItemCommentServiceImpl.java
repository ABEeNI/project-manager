package home.projectmanager.service;

import home.projectmanager.dto.UserDto;
import home.projectmanager.dto.WorkItemCommentDto;
import home.projectmanager.entity.WorkItem;
import home.projectmanager.entity.WorkItemComment;
import home.projectmanager.exception.workitem.WorkItemNotFoundException;
import home.projectmanager.exception.workitemcomment.WorkItemCommentNotFoundException;
import home.projectmanager.exception.workitemcomment.WorkItemCommentNotProvided;
import home.projectmanager.repository.WorkItemCommentRepository;
import home.projectmanager.repository.WorkItemRepository;
import home.projectmanager.service.accesscontrol.AccessDecisionVoter;
import home.projectmanager.service.accesscontrol.AuthenticationFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkItemCommentServiceImpl implements WorkItemCommentService {

    private final WorkItemRepository workItemRepository;
    private final WorkItemCommentRepository workItemCommentRepository;
    private final AuthenticationFacade authenticationFacade;
    private final AccessDecisionVoter accessDecisionVoter;

    @Override
    public WorkItemCommentDto createComment(Long workItemId, WorkItemCommentDto workItemCommentDto) {
        if(workItemCommentDto.comment() == null || workItemCommentDto.comment().isBlank()) {
            throw new WorkItemCommentNotProvided("Comment cannot be null or blank");
        }

        WorkItem workitem = workItemRepository.findById(workItemId)
                .orElseThrow(() -> new WorkItemNotFoundException("WorkItem not found"));
        Long parentProjectId = workitem.getParentProjectId();
        if(!accessDecisionVoter.hasPermission(parentProjectId)) {
            throw new AccessDeniedException("User has no access to project with id " + parentProjectId);
        }

        WorkItemComment newWorkItemComment = WorkItemComment.builder()
                .comment(workItemCommentDto.comment())
                .commenter(authenticationFacade.getCurrentUser())
                .workItem(workitem)
                .projectId(parentProjectId)
                .build();

        WorkItemComment savedWorkItemComment = workItemCommentRepository.save(newWorkItemComment);
        log.info("Comment created with id {}", savedWorkItemComment.getId());
        return convertToDto(savedWorkItemComment);
    }

    @Override
    public WorkItemCommentDto updateComment(Long commentId, WorkItemCommentDto workItemCommentDto) {
        if(workItemCommentDto.comment() == null || workItemCommentDto.comment().isBlank()) {
            throw new WorkItemCommentNotProvided("Comment cannot be null");
        }

        WorkItemComment workItemComment = workItemCommentRepository.findById(commentId)
                .orElseThrow(() -> new WorkItemCommentNotFoundException("Comment with id " + commentId + " not found"));
        if(!accessDecisionVoter.hasPermission(workItemComment)) {
            throw new AccessDeniedException("User has no access to project with id " + workItemComment.getParentProjectId());
        }

        workItemComment.setComment(workItemCommentDto.comment());

        WorkItemComment updatedWorkItemComment = workItemCommentRepository.save(workItemComment);
        log.info("Comment with id {} updated", updatedWorkItemComment.getId());

        return convertToDto(updatedWorkItemComment);
    }

    @Override
    public void deleteComment(Long commentId) {
        WorkItemComment workItemComment = workItemCommentRepository.findById(commentId)
                .orElseThrow(() -> new WorkItemCommentNotFoundException("Comment with id " + commentId + " not found"));
        if(!accessDecisionVoter.hasPermission(workItemComment)) {
            throw new AccessDeniedException("User has no access to project with id " + workItemComment.getParentProjectId());
        }

        workItemCommentRepository.delete(workItemComment);
        log.info("Comment with id {} deleted", commentId);
    }

    private WorkItemCommentDto convertToDto(WorkItemComment workItemComment) {
        return WorkItemCommentDto.builder()
                .id(workItemComment.getId())
                .comment(workItemComment.getComment())
                .commenter(UserDto.builder()
                        .id(workItemComment.getCommenter().getId())
                        .email(workItemComment.getCommenter().getEmail())
                        .firstName(workItemComment.getCommenter().getFirstName())
                        .lastName(workItemComment.getCommenter().getLastName())
                        .build())
                .build();
    }
}
