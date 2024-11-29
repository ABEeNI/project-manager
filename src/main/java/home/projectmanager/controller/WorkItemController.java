package home.projectmanager.controller;

import home.projectmanager.dto.WorkItemDto;
import home.projectmanager.service.WorkItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/workitems")
@RequiredArgsConstructor
public class WorkItemController {

    private final WorkItemService workItemService;

    @PostMapping
    public ResponseEntity<WorkItemDto> createWorkItem(@RequestBody WorkItemDto workItemDto) {
        WorkItemDto createdWorkItem = workItemService.createWorkItem(workItemDto);
        return ResponseEntity.ok(createdWorkItem);
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkItemDto> getWorkItem(@PathVariable Long id) {
        WorkItemDto workItem = workItemService.getWorkItem(id);
        return ResponseEntity.ok(workItem);
    }

    @PutMapping("/{id}")
    public ResponseEntity<WorkItemDto> updateWorkItem(@PathVariable Long id, @RequestBody WorkItemDto workItemDto) {
        WorkItemDto updatedWorkItem = workItemService.updateWorkItem(id, workItemDto);
        return ResponseEntity.ok(updatedWorkItem);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWorkItem(@PathVariable Long id) {
        workItemService.deleteWorkItem(id);
        return ResponseEntity.noContent().build();
    }
}
