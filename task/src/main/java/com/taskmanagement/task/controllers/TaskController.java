package com.taskmanagement.task.controllers;


import com.taskmanagement.task.dtos.TaskDTO;
import com.taskmanagement.task.services.interfaces.ItaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@Slf4j
public class TaskController {
    private  final ItaskService itaskService;

    @Operation(summary = "Get paginated list of tasks", description = "Returns a page of tasks, optionally filtered by completion status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping()
    public ResponseEntity<Page<TaskDTO>> getPageableTasks(
            @RequestParam(required = false) Boolean completed,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size){
        log.info("getPageableTasks - start processing (page: {}, size: {})", page, size);
        try{
            Page<TaskDTO> taskPage = itaskService.getTaskPage(page, size, completed);
            log.info("getPageableTasks - end processing, found {} elements", taskPage.getTotalElements());
            return ResponseEntity.ok(taskPage);

        }catch (Exception e){
            log.error("getPageableTasks - exception occurred: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }


    @Operation(summary = "Get task by ID", description = "Returns the task for the provided ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task found"),
            @ApiResponse(responseCode = "404", description = "Task not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/id/{id}")
    public ResponseEntity<TaskDTO> getTaskById(@PathVariable("id") Long id){
        log.info("getTaskById - start processing (taskId: {})", id);
        try{
            TaskDTO task = itaskService.getTaskById(id);
            log.info("getTaskById - end processing");
            return ResponseEntity.ok(task);

        }catch (EntityNotFoundException notFoundException){
            log.error("getTaskById - exception occurred: {} Task not found", notFoundException.getMessage());
            return ResponseEntity.notFound().build();
        }
        catch (Exception e){
            log.error("getTaskById - exception occurred: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }


    @Operation(summary = "Create a new task", description = "Adds a new task to the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Task created successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping()
    public ResponseEntity<Void> createTask(@RequestBody TaskDTO dto){
        log.info("createTask - start processing");
        try{
            TaskDTO saved = itaskService.saveTask(dto);
            URI location = URI.create("/api/tasks/" + saved.getId());
            log.info("createTask - end processing");
            return ResponseEntity.created(location).build();

        }catch (Exception e){
            log.error("createTask - exception occurred: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary = "Update existing task", description = "Updates task information by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task updated successfully"),
            @ApiResponse(responseCode = "404", description = "Task not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/{id}")
    public ResponseEntity<TaskDTO> updateTask(@PathVariable Long id, @RequestBody TaskDTO dto) {
        log.info("updateTask - start processing");
        try {
            dto.setId(id);
            TaskDTO updated = itaskService.updatedTask(dto);
            log.info("updateTask - end processing");
            return ResponseEntity.ok(updated);
        }catch (EntityNotFoundException notFoundException){
            log.error("updateTask - exception occurred: {} Task not found", notFoundException.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("updateTask - exception occurred: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }


}
