package com.taskmanagement.task.controllers;


import com.taskmanagement.task.dtos.TaskDTO;
import com.taskmanagement.task.services.interfaces.ItaskService;
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

    @PostMapping()
    public ResponseEntity<Void> createTask(@RequestBody TaskDTO dto){
        log.info("createTask - start processing");
        try{
            TaskDTO saved = itaskService.saveTask(dto);
            URI location = URI.create("/tasks/" + saved.getId());
            log.info("createTask - end processing");
            return ResponseEntity.created(location).build();

        }catch (Exception e){
            log.error("createTask - exception occurred: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }


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
