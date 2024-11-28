package home.projectmanager.service;

import home.projectmanager.dto.UserDto;
import home.projectmanager.entity.User;
import home.projectmanager.exception.user.UserNotFoundException;
import home.projectmanager.repository.UserRepository;
import home.projectmanager.service.accesscontrol.AuthenticationFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;

import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AuthenticationFacade authenticationFacade;

    @Override
    public UserDto getUserById(Long userId) {
        User currentUser = authenticationFacade.getCurrentUser();
        if (!currentUser.getId().equals(userId)) {
            throw new AccessDeniedException("You cannot access other users' data.");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with id " + userId + " not found"));
        return mapToDto(user);
    }

    @Override
    public UserDto updateUser(Long userId, UserDto userDto) {
        User currentUser = authenticationFacade.getCurrentUser();
        if (!currentUser.getId().equals(userId)) {
            throw new AccessDeniedException("You cannot access other users' data.");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with id " + userId + " not found"));
        user.setFirstName(userDto.firstName());
        user.setLastName(userDto.lastName());
        User updatedUser = userRepository.save(user);
        log.info("User with id {} updated", userId);
        return mapToDto(updatedUser);
    }

    private UserDto mapToDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .build();
    }
}