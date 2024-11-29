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
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
}
