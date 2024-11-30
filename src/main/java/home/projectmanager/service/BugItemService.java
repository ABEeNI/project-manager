package home.projectmanager.service;

import home.projectmanager.dto.BugItemDto;

import java.util.List;

public interface BugItemService {
    BugItemDto createBugItem(BugItemDto bugItemDto);

    BugItemDto getBugItem(Long id);

    List<BugItemDto> getBugItemsByProject(Long projectId);

    BugItemDto updateBugItem(Long id, BugItemDto bugItemDto);

    void deleteBugItem(Long id);
}
