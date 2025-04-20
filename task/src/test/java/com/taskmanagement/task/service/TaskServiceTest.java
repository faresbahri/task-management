package com.taskmanagement.task.service;

import com.taskmanagement.task.dtos.TaskDTO;
import com.taskmanagement.task.entities.Task;
import com.taskmanagement.task.mapper.ItaskMapper;
import com.taskmanagement.task.repositories.ItaskRepository;
import com.taskmanagement.task.services.implimentation.TaskService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {
    @Mock
    private ItaskRepository taskRepository;

    @Mock
    private ItaskMapper taskMapper;

    @InjectMocks
    private TaskService taskService;

    @Test
    void getTaskPage_shouldReturnPageOfAllTasks() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        List<Task> taskList = List.of(new Task());
        Page<Task> taskPage = new PageImpl<>(taskList);

        Mockito.when(taskRepository.findAll(pageable)).thenReturn(taskPage);
        Mockito.when(taskMapper.toDtoPage(taskPage)).thenReturn(new PageImpl<>(List.of(new TaskDTO())));

        Page<TaskDTO> result = taskService.getTaskPage(0, 10, null);

        Assertions.assertEquals(1, result.getContent().size());
    }

    @Test
    void getTaskPage_shouldReturnPageOfCompletedTasks() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        List<Task> taskListCompleted = List.of(new Task(1L, "task 1", true));
        Page<Task> taskPage = new PageImpl<>(taskListCompleted);

        Mockito.when(taskRepository.findByCompleted(true, pageable)).thenReturn(taskPage);
        Mockito.when(taskMapper.toDtoPage(taskPage)).thenReturn(new PageImpl<>(List.of(new TaskDTO(1L, "task 1", true))));

        Page<TaskDTO> result = taskService.getTaskPage(0, 10, true);

        Assertions.assertEquals(1, result.getContent().size());
    }

    @Test
    void getTaskById_shouldReturnTaskDTO_whenTaskExist(){
        Task entity = new Task(1L, "task 1", true);
        TaskDTO dto = new TaskDTO(1L, "task 1", true);
        Long id = 1L;

        Mockito.when(taskRepository.findById(id)).thenReturn(Optional.of(entity));
        Mockito.when(taskMapper.toDto(entity)).thenReturn(dto);

        TaskDTO obj = taskService.getTaskById(id);

        Assertions.assertEquals(id, obj.getId());
        Mockito.verify(taskRepository, times(1)).findById(id);
    }

    @Test
    void getTaskById_shouldThrowException_whenTaskNotExist(){
        Long id = 1L;

        Mockito.when(taskRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            taskService.getTaskById(id);
        });

        Mockito.verify(taskRepository, times(1)).findById(id);
    }

    @Test
    void saveTask_shouldReturnTaskDTO_whenTaskIsValid() throws Exception {
        TaskDTO taskDTO = new TaskDTO(null, "task 1", true);

        Task taskToBeSaved = new Task(null, "task 1", true);
        Task taskSaved = new Task(1L, "task 1", true);
        TaskDTO taskDTOSaved = new TaskDTO(1L, "task 1", true);

        Mockito.when(taskMapper.toEntity(taskDTO)).thenReturn(taskToBeSaved);
        Mockito.when(taskRepository.saveAndFlush(taskToBeSaved)).thenReturn(taskSaved);
        Mockito.when(taskMapper.toDto(taskSaved)).thenReturn(taskDTOSaved);

        TaskDTO result = taskService.saveTask(taskDTO);

        Assertions.assertNotNull(result);

        Mockito.verify(taskRepository, times(1)).saveAndFlush(Mockito.any());
    }

    @Test
    void saveTask_shouldThrowException_whenTaskDtoHasId() {
        TaskDTO taskDTO = new TaskDTO(2L, "task 2", true);
        Exception exception = assertThrows(Exception.class, () -> {
            taskService.saveTask(taskDTO);
        });

        Assertions.assertEquals("ID should be null when creating a new task.", exception.getMessage());

        Mockito.verify(taskRepository, times(0)).saveAndFlush(Mockito.any(Task.class));
    }

    @Test
    void updatedTask_shouldUpdateAndReturnUpdatedTaskDTO() {
        Long taskId = 1L;
        TaskDTO inputDto = new TaskDTO(1L, "Updated description", true);

        Task existingTask = new Task(1L, "Old description", false);

        Task updatedTask = new Task(1L, "Updated description", true);

        Mockito.when(taskRepository.findById(taskId)).thenReturn(Optional.of(existingTask));
        Mockito.when(taskRepository.saveAndFlush(existingTask)).thenReturn(updatedTask);
        Mockito.when(taskMapper.toDto(updatedTask)).thenReturn(inputDto);

        TaskDTO result = taskService.updatedTask(inputDto);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1L, result.getId());
        Assertions.assertEquals("Updated description", result.getDescription());
        Assertions.assertTrue(result.getCompleted());

        Mockito.verify(taskRepository).findById(taskId);
        Mockito.verify(taskRepository).saveAndFlush(existingTask);
        Mockito.verify(taskMapper).toDto(updatedTask);
    }
    @Test
    void updatedTask_shouldThrowEntityNotFoundException_whenTaskDoesNotExist() {
        Long taskId = 42L;
        TaskDTO inputDto = new TaskDTO(42L, null, null);

        Mockito.when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            taskService.updatedTask(inputDto);
        });

        Assertions.assertEquals("Task with id 42 not found", exception.getMessage());

        Mockito.verify(taskRepository, times(1)).findById(taskId);
        Mockito.verify(taskRepository, Mockito.never()).saveAndFlush(Mockito.any());
    }

}
