package com.elyashevich.card_manager.controller;

import com.elyashevich.card_manager.api.controller.UserController;
import com.elyashevich.card_manager.api.dto.filter.FilterDto;
import com.elyashevich.card_manager.api.mapper.UserMapper;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
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
        Page<User> emptyPage = new PageImpl<>(Collections.emptyList());
        when(userService.findAll(any(FilterDto.class), any(PageRequest.class)))
            .thenReturn(emptyPage);

        // Act & Assert
        mockMvc.perform(get("/api/v1/users")
                .param("page", "2")
                .param("pageSize", "5")
                .param("sort", "desc")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(userService).findAll(
            new FilterDto("desc"),
            PageRequest.of(2, 5)
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