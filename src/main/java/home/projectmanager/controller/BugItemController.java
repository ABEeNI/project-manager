package home.projectmanager.controller;


import home.projectmanager.dto.BugItemDto;
import home.projectmanager.service.BugItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bugitems")
@RequiredArgsConstructor
public class BugItemController {

    private final BugItemService bugItemService;

    @PostMapping
    public ResponseEntity<BugItemDto> createBugItem(@RequestBody BugItemDto bugItemDto) {
        BugItemDto createdBugItem = bugItemService.createBugItem(bugItemDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBugItem);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BugItemDto> getBugItem(@PathVariable Long id) {
        BugItemDto bugItem = bugItemService.getBugItem(id);
        return ResponseEntity.ok(bugItem);
    }

    @GetMapping("/projects/{projectId}")
    public ResponseEntity<List<BugItemDto>> getBugItemsByProject(@PathVariable Long projectId) {
        List<BugItemDto> bugItems = bugItemService.getBugItemsByProject(projectId);
        return ResponseEntity.ok(bugItems);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BugItemDto> updateBugItem(@PathVariable Long id, @RequestBody BugItemDto bugItemDto) {
        BugItemDto updatedBugItem = bugItemService.updateBugItem(id, bugItemDto);
        return ResponseEntity.ok(updatedBugItem);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBugItem(@PathVariable Long id) {
        bugItemService.deleteBugItem(id);
        return ResponseEntity.noContent().build();
    }
}
