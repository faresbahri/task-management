package com.taskmanagement.task.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskmanagement.task.controllers.TaskController;
import com.taskmanagement.task.dtos.TaskDTO;
import com.taskmanagement.task.services.interfaces.ItaskService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
public class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItaskService itaskService;

    private String convertToJson(Object object) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(object);
    }

    private void performPostAndCheck(TaskDTO inputDto, int expectedStatus, String expectedLocation) throws Exception {
        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(convertToJson(inputDto)))
                .andExpect(status().is(expectedStatus))
                .andExpect(header().string("Location", expectedLocation));
    }

    private void performGetAndCheck(String url, int expectedStatus, String jsonPath, String expectedValue) throws Exception {
        mockMvc.perform(get(url)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(expectedStatus))
                .andExpect(jsonPath(jsonPath).value(expectedValue));
    }

    private void performPutAndCheck(Long id, TaskDTO inputDto, int expectedStatus, String jsonPath, String expectedValue) throws Exception {
        mockMvc.perform(put("/api/tasks/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(convertToJson(inputDto)))
                .andExpect(status().is(expectedStatus))
                .andExpect(jsonPath(jsonPath).value(expectedValue));
    }

    @Test
    @DisplayName("GET /tasks - should return paginated tasks")
    void getPageableTasks_shouldReturnOkWithTasks() throws Exception {
        TaskDTO task = new TaskDTO(1L, "Test Task", true);
        List<TaskDTO> tasks = List.of(task);
        Page<TaskDTO> page = new PageImpl<>(tasks);

        Mockito.when(itaskService.getTaskPage(0, 10, null)).thenReturn(page);

        performGetAndCheck("/api/tasks?page=0&size=10", 200, "$.content[0].description", "Test Task");
    }

    @Test
    @DisplayName("GET /api/task/id/{id} - should return task when found")
    void getTaskById_shouldReturnTask_whenFound() throws Exception {
        TaskDTO dto = new TaskDTO(1L, "My Task", true);
        Mockito.when(itaskService.getTaskById(1L)).thenReturn(dto);

        performGetAndCheck("/api/tasks/id/1", 200, "$.description", "My Task");
    }

    @Test
    @DisplayName("GET /api/task/id/{id} - should return 404 when not found")
    void getTaskById_shouldReturnNotFound_whenEntityNotFound() throws Exception {
        Mockito.when(itaskService.getTaskById(1L)).thenThrow(new EntityNotFoundException("Not found"));

        mockMvc.perform(get("/api/tasks/id/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/tasks - should create task and return 201 Created")
    void createTask_shouldReturnCreated_whenSuccess() throws Exception {
        TaskDTO inputDto = new TaskDTO(null, "New Task", false);
        TaskDTO savedDto = new TaskDTO(1L, "New Task", false);

        Mockito.when(itaskService.saveTask(Mockito.any(TaskDTO.class))).thenReturn(savedDto);

        performPostAndCheck(inputDto, 201, "/api/tasks/1");
    }

    @Test
    @DisplayName("POST /api/tasks - should return 500 on exception")
    void createTask_shouldReturnInternalServerError_whenExceptionThrown() throws Exception {
        TaskDTO inputDto = new TaskDTO(null, "New Task", false);

        Mockito.when(itaskService.saveTask(Mockito.any(TaskDTO.class)))
                .thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(convertToJson(inputDto)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("PUT /api/tasks/{id} - should update task and return 200 OK")
    void updateTask_shouldReturnOk_whenSuccess() throws Exception {
        TaskDTO inputDto = new TaskDTO(1L, "Updated Task", true);
        TaskDTO updatedDto = new TaskDTO(1L, "Updated Task", true);

        Mockito.when(itaskService.updatedTask(Mockito.any(TaskDTO.class))).thenReturn(updatedDto);

        performPutAndCheck(1L, inputDto, 200, "$.description", "Updated Task");
    }

    @Test
    @DisplayName("PUT /api/tasks/{id} - should return 404 when not found")
    void updateTask_shouldReturnNotFound_whenEntityNotFound() throws Exception {
        TaskDTO inputDto = new TaskDTO(1L, "Updated Task", true);

        Mockito.when(itaskService.updatedTask(Mockito.any(TaskDTO.class)))
                .thenThrow(new EntityNotFoundException("Not found"));

        mockMvc.perform(put("/api/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(convertToJson(inputDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /api/tasks/{id} - should return 500 on server error")
    void updateTask_shouldReturnInternalServerError_whenExceptionThrown() throws Exception {
        TaskDTO inputDto = new TaskDTO(1L, "Updated Task", true);

        Mockito.when(itaskService.updatedTask(Mockito.any(TaskDTO.class)))
                .thenThrow(new RuntimeException("Unexpected error"));

        mockMvc.perform(put("/api/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(convertToJson(inputDto)))
                .andExpect(status().isInternalServerError());
    }
}
