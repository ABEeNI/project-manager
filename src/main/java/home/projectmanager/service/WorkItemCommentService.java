package home.projectmanager.service;

import home.projectmanager.dto.WorkItemCommentDto;

public interface WorkItemCommentService {
    WorkItemCommentDto createComment(Long workItemId, WorkItemCommentDto workItemCommentDto);

    WorkItemCommentDto updateComment(Long commentId, WorkItemCommentDto workItemCommentDto);
    void deleteComment(Long commentId);
}
