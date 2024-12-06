package home.projectmanager.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import home.projectmanager.dto.UserDto;
import home.projectmanager.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getUser_ShouldReturnUser_WhenUserExists() throws Exception {
        Long userId = 1L;
        String email = "test@example.com";
        String firstName = "John";
        String lastName = "Doe";
        UserDto userDto = UserDto.builder()
                .id(userId)
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .build();

        when(userService.getUserById(userId)).thenReturn(userDto);

        mockMvc.perform(get("/api/users/" + userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.firstName").value(firstName))
                .andExpect(jsonPath("$.lastName").value(lastName));

        verify(userService, times(1)).getUserById(userId);
    }

    @Test
    void updateUser_ShouldUpdateUser_WhenUserExists() throws Exception {
        Long userId = 1L;
        String email = "test@example.com";
        String firstName = "John";
        String lastName = "Doe";
        UserDto clientUserDto = UserDto.builder()
                .firstName(firstName)
                .lastName(lastName)
                .build();
        UserDto userDto = UserDto.builder()
                .id(userId)
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .build();
        when(userService.updateUser(userId, clientUserDto)).thenReturn(userDto);

        mockMvc.perform(put("/api/users/" + userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clientUserDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.firstName").value(firstName))
                .andExpect(jsonPath("$.lastName").value(lastName));

        verify(userService, times(1)).updateUser(userId, clientUserDto);
    }

    @Test
    void getUsersByProjectId_ShouldReturnUsers_WhenProjectExists() throws Exception {
        Long projectId = 1L;
        Long userId1 = 1L;
        Long userId2 = 2L;
        String email1 = "user1@example.com";
        String email2 = "user2@example.com";
        String firstName1 = "Alice";
        String firstName2 = "Bob";
        String lastName1 = "Smith";
        String lastName2 = "Johnson";

        UserDto userDto1 = UserDto.builder()
                .id(userId1)
                .email(email1)
                .firstName(firstName1)
                .lastName(lastName1)
                .build();

        UserDto userDto2 = UserDto.builder()
                .id(userId2)
                .email(email2)
                .firstName(firstName2)
                .lastName(lastName2)
                .build();

        List<UserDto> userDtos = List.of(userDto1, userDto2);

        when(userService.getUsersByProjectId(projectId)).thenReturn(userDtos);

        mockMvc.perform(get("/api/users/project/" + projectId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(userId1))
                .andExpect(jsonPath("$[0].email").value(email1))
                .andExpect(jsonPath("$[0].firstName").value(firstName1))
                .andExpect(jsonPath("$[0].lastName").value(lastName1))
                .andExpect(jsonPath("$[1].id").value(userId2))
                .andExpect(jsonPath("$[1].email").value(email2))
                .andExpect(jsonPath("$[1].firstName").value(firstName2))
                .andExpect(jsonPath("$[1].lastName").value(lastName2));

        verify(userService, times(1)).getUsersByProjectId(projectId);
    }
}
