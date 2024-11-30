package home.projectmanager.controller;

import home.projectmanager.dto.WorkItemCommentDto;
import home.projectmanager.service.WorkItemCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/workitems")
@RequiredArgsConstructor
public class WorkItemCommentController {

    private final WorkItemCommentService workItemCommentService;

    @PostMapping("/{workItemId}/comments")
    public ResponseEntity<WorkItemCommentDto> createComment(
            @PathVariable Long workItemId,
            @RequestBody WorkItemCommentDto workItemCommentDto) {
        WorkItemCommentDto createdComment = workItemCommentService.createComment(workItemId, workItemCommentDto);
        return ResponseEntity.ok(createdComment);
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<WorkItemCommentDto> updateComment(
            @PathVariable Long commentId,
            @RequestBody WorkItemCommentDto workItemCommentDto) {
        WorkItemCommentDto updatedComment = workItemCommentService.updateComment(commentId, workItemCommentDto);
        return ResponseEntity.ok(updatedComment);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long commentId) {
        workItemCommentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }
}