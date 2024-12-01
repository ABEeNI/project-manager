package home.projectmanager.controller;

import home.projectmanager.dto.BugItemCommentDto;
import home.projectmanager.service.BugItemCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bugitem")
@RequiredArgsConstructor
public class BugItemCommentController {

    private final BugItemCommentService bugItemCommentService;

    @PostMapping("/{bugItemId}/bugitemcomments")
    public ResponseEntity<BugItemCommentDto> createComment(
            @PathVariable Long bugItemId,
            @RequestBody BugItemCommentDto bugItemCommentDto) {
        BugItemCommentDto createdComment = bugItemCommentService.createComment(bugItemId, bugItemCommentDto);
        return ResponseEntity.ok(createdComment);
    }

    @PutMapping("bugitemcomments/{commentId}")
    public ResponseEntity<BugItemCommentDto> updateComment(
            @PathVariable Long commentId,
            @RequestBody BugItemCommentDto bugItemCommentDto) {
        BugItemCommentDto updatedComment = bugItemCommentService.updateComment(commentId, bugItemCommentDto);
        return ResponseEntity.ok(updatedComment);
    }

    @DeleteMapping("bugitemcomments/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long commentId) {
        bugItemCommentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }
}