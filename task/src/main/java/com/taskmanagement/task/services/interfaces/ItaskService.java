package com.taskmanagement.task.services.interfaces;

import com.taskmanagement.task.dtos.TaskDTO;
import org.springframework.data.domain.Page;

public interface ItaskService {
    Page<TaskDTO> getTaskPage(int page, int size, Boolean completed);

    TaskDTO getTaskById(Long id);

    TaskDTO saveTask(TaskDTO dto) throws Exception;

    TaskDTO updatedTask(TaskDTO dto);
}
