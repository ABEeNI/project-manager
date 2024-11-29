package home.projectmanager.service;

import home.projectmanager.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto getUserById(Long userId);

    UserDto updateUser(Long userId, UserDto userDto);

    List<UserDto> getUsersByProjectId(Long projectId);
}
