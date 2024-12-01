package home.projectmanager.service;

import home.projectmanager.dto.UserDto;
import home.projectmanager.dto.BugItemCommentDto;
import home.projectmanager.entity.BugItem;
import home.projectmanager.entity.BugItemComment;
import home.projectmanager.exception.bugitem.BugItemNotFoundException;
import home.projectmanager.exception.bugitemcomment.BugItemCommentNotFoundException;
import home.projectmanager.exception.bugitemcomment.BugItemCommentNotProvided;
import home.projectmanager.repository.BugItemCommentRepository;
import home.projectmanager.repository.BugItemRepository;
import home.projectmanager.service.accesscontrol.AccessDecisionVoter;
import home.projectmanager.service.accesscontrol.AuthenticationFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class BugItemCommentServiceImpl implements BugItemCommentService {

    private final BugItemRepository bugItemRepository;
    private final BugItemCommentRepository bugItemCommentRepository;
    private final AuthenticationFacade authenticationFacade;
    private final AccessDecisionVoter accessDecisionVoter;

    @Override
    public BugItemCommentDto createComment(Long bugItemId, BugItemCommentDto bugItemCommentDto) {
        if(bugItemCommentDto.comment() == null || bugItemCommentDto.comment().isBlank()) {
            throw new BugItemCommentNotProvided("Comment cannot be null or blank");
        }

        BugItem workitem = bugItemRepository.findById(bugItemId)
                .orElseThrow(() -> new BugItemNotFoundException("BugItem not found"));
        Long parentProjectId = workitem.getParentProjectId();
        if(!accessDecisionVoter.hasPermission(parentProjectId)) {
            throw new AccessDeniedException("User has no access to project with id " + parentProjectId);
        }

        BugItemComment newBugItemComment = BugItemComment.builder()
                .comment(bugItemCommentDto.comment())
                .commenter(authenticationFacade.getCurrentUser())
                .bugItem(workitem)
                .projectId(parentProjectId)
                .build();

        BugItemComment savedBugItemComment = bugItemCommentRepository.save(newBugItemComment);
        log.info("Comment created with id {}", savedBugItemComment.getId());
        return convertToDto(savedBugItemComment);
    }

    @Override
    public BugItemCommentDto updateComment(Long commentId, BugItemCommentDto bugItemCommentDto) {
        if(bugItemCommentDto.comment() == null || bugItemCommentDto.comment().isBlank()) {
            throw new BugItemCommentNotProvided("Comment cannot be null");
        }
        BugItemComment bugItemComment = bugItemCommentRepository.findById(commentId)
                .orElseThrow(() -> new BugItemCommentNotFoundException("Comment with id " + commentId + " not found"));
        if(!accessDecisionVoter.hasPermission(bugItemComment)) {
            throw new AccessDeniedException("User has no access to project with id " + bugItemComment.getParentProjectId());
        }

        bugItemComment.setComment(bugItemCommentDto.comment());

        BugItemComment updatedBugItemComment = bugItemCommentRepository.save(bugItemComment);
        log.info("Comment with id {} updated", updatedBugItemComment.getId());

        return convertToDto(updatedBugItemComment);
    }

    @Override
    public void deleteComment(Long commentId) {
        BugItemComment bugItemComment = bugItemCommentRepository.findById(commentId)
                .orElseThrow(() -> new BugItemCommentNotFoundException("Comment with id " + commentId + " not found"));
        if(!accessDecisionVoter.hasPermission(bugItemComment)) {
            throw new AccessDeniedException("User has no access to project with id " + bugItemComment.getParentProjectId());
        }

        bugItemCommentRepository.delete(bugItemComment);
        log.info("Comment with id {} deleted", commentId);
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
