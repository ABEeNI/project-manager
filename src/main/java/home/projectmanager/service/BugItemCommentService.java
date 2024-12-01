package home.projectmanager.service;

import home.projectmanager.dto.BugItemCommentDto;

public interface BugItemCommentService {
    BugItemCommentDto createComment(Long bugItemId, BugItemCommentDto bugItemCommentDto);

    BugItemCommentDto updateComment(Long commentId, BugItemCommentDto bugItemCommentDto);

    void deleteComment(Long commentId);
}
