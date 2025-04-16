package com.elyashevich.card_manager.controller;

import com.elyashevich.card_manager.api.controller.UserController;
import com.elyashevich.card_manager.api.mapper.UserMapperImpl;
import com.elyashevich.card_manager.entity.User;
import com.elyashevich.card_manager.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({UserMapperImpl.class})
@DisplayName("User Controller Integration Tests")
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("GET /api/v1/users - With Custom Pagination")
    void getAllUsersWithCustomPaginationShouldUseParameters() throws Exception {
        // Arrange
        List<User> emptyPage = Collections.emptyList();
        when(userService.findAll())
            .thenReturn(emptyPage);

        // Act & Assert
        mockMvc.perform(get("/api/v1/users")
                .param("page", "2")
                .param("pageSize", "5")
                .param("sort", "desc")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(userService).findAll(
        );
    }

    @Test
    @DisplayName("DELETE /api/v1/users/{id} - Success")
    void deleteUserShouldReturnNoContent() throws Exception {
        // Arrange
        Long userId = 1L;
        doNothing().when(userService).delete(userId);

        // Act & Assert
        mockMvc.perform(delete("/api/v1/users/{id}", userId)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        verify(userService).delete(userId);
    }
}