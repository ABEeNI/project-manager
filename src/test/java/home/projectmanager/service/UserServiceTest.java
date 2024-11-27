package home.projectmanager.service;

import home.projectmanager.dto.UserDto;
import home.projectmanager.entity.User;
import home.projectmanager.exception.user.UserNotFoundException;
import home.projectmanager.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthenticationFacade authenticationFacade;

    @InjectMocks
    private UserServiceImpl userService;

    private User currentUser;
    private UserDto clientUserDto;

    @BeforeEach
    void setUp() {
        Long id = 1L;
        String email = "test@example.com";
        String firstname = "John";
        String lastName = "Doe";
        currentUser = new User();
        currentUser.setId(id);
        currentUser.setEmail(email);
        currentUser.setFirstName(firstname);
        currentUser.setLastName(lastName);

    }

    @Test
    void getUserById_ShouldReturnUser_WhenUserExists() {
        Long userId = 1L;
        String email = "test@example.com";
        String firstname = "John";
        String lastName = "Doe";
        UserDto expectedUserDto = UserDto.builder()
                .id(userId)
                .email(email)
                .firstName(firstname)
                .lastName(lastName)
                .build();
        when(authenticationFacade.getCurrentUser()).thenReturn(currentUser);
        when(userRepository.findById(userId)).thenReturn(Optional.of(currentUser));

        UserDto result = userService.getUserById(userId);

        assertNotNull(result);
        assertEquals(expectedUserDto, result);
    }

    @Test
    void getUserById_ShouldThrowException_WhenUserDoesNotExist() {
        Long userId = 1L;

        when(authenticationFacade.getCurrentUser()).thenReturn(currentUser);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserById(userId));
    }

    @Test
    void getUserById_ShouldThrowException_WhenAccessingOtherUsersData() {
        Long otherUserId = 2L;

        when(authenticationFacade.getCurrentUser()).thenReturn(currentUser);

        assertThrows(AccessDeniedException.class, () -> userService.getUserById(otherUserId));
    }

    @Test
    void updateUser_ShouldReturnUpdatedUser_WhenUserExists() {
        Long userId = 1L;

        String newFirstName = "Jane";
        String newLastName = "Smith";
        clientUserDto = UserDto.builder()
                .firstName(newFirstName)
                .lastName(newLastName)
                .build();

        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setFirstName(newFirstName);
        updatedUser.setLastName(newLastName);
        updatedUser.setEmail(currentUser.getEmail());

        UserDto expectedUserDto = UserDto.builder()
                .id(userId)
                .firstName(newFirstName)
                .lastName(newLastName)
                .email(currentUser.getEmail())
                .build();

        when(authenticationFacade.getCurrentUser()).thenReturn(currentUser);
        when(userRepository.findById(userId)).thenReturn(Optional.of(currentUser));
        when(userRepository.save(currentUser)).thenReturn(updatedUser);

        UserDto updatedUserDto = userService.updateUser(userId, clientUserDto);

        verify(userRepository, times(1)).save(currentUser);
        assertEquals(clientUserDto.firstName(), currentUser.getFirstName());
        assertEquals(clientUserDto.lastName(), currentUser.getLastName());
        assertEquals(expectedUserDto, updatedUserDto); //Could leave just this assertion
    }

    @Test
    void updateUser_ShouldThrowException_WhenUserDoesNotExist() {
        Long userId = 1L;

        when(authenticationFacade.getCurrentUser()).thenReturn(currentUser);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.updateUser(userId, clientUserDto));
    }

    @Test
    void updateUser_ShouldThrowException_WhenAccessingOtherUsersData() {
        Long otherUserId = 2L;

        when(authenticationFacade.getCurrentUser()).thenReturn(currentUser);

        assertThrows(AccessDeniedException.class, () -> userService.updateUser(otherUserId, clientUserDto));
    }
}
