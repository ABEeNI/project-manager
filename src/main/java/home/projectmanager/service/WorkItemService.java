package home.projectmanager.service;

import home.projectmanager.dto.WorkItemDto;


public interface WorkItemService {

    WorkItemDto createWorkItem(WorkItemDto workItemDto);

    WorkItemDto getWorkItem(Long id);

    void deleteWorkItem(Long id);

    WorkItemDto updateWorkItem(Long id, WorkItemDto workItemDto);
}
