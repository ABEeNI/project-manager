package home.projectmanager.service;

import home.projectmanager.dto.UserDto;

public interface UserService {
    UserDto getUserById(Long userId);

    UserDto updateUser(Long userId, UserDto userDto);
}
