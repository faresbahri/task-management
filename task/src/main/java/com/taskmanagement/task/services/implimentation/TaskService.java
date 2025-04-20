package com.taskmanagement.task.services.implimentation;

import com.taskmanagement.task.dtos.TaskDTO;
import com.taskmanagement.task.entities.Task;
import com.taskmanagement.task.mapper.ItaskMapper;
import com.taskmanagement.task.repositories.ItaskRepository;
import com.taskmanagement.task.services.interfaces.ItaskService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TaskService implements ItaskService {
    private final ItaskRepository repository;
    private final ItaskMapper mapper;

    @Override
    public Page<TaskDTO> getTaskPage(final int page, final int size,final Boolean completed) {
        Sort sort = Sort.by(Sort.Order.by("id").with(Sort.Direction.fromString("asc")));
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Task> taskPage;
        if(completed != null){
            taskPage = repository.findByCompleted(completed, pageable);
        }else{
            taskPage = repository.findAll(pageable);
        }
        return mapper.toDtoPage(taskPage);
    }

    @Override
    public TaskDTO getTaskById(final Long id) {
        Task entity = getTaskOrThrowNotFoundException(id);
        return mapper.toDto(entity);
    }

    private Task getTaskOrThrowNotFoundException(Long id) {
        return repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Task with id " + id + " not found"));
    }

    @Override
    public TaskDTO saveTask(final TaskDTO dto) throws Exception {
        if(dto.getId() != null){
            throw new Exception("ID should be null when creating a new task.");
        }
        Task taskToBeSaved = mapper.toEntity(dto);
        return mapper.toDto(repository.saveAndFlush(taskToBeSaved));
    }

    @Override
    public TaskDTO updatedTask(final TaskDTO dto) {
        Task taskToBeUpdated = getTaskOrThrowNotFoundException(dto.getId());
        taskToBeUpdated.setCompleted(dto.getCompleted());
        taskToBeUpdated.setDescription(dto.getDescription());
        Task taskUpdated = repository.saveAndFlush(taskToBeUpdated);
        return mapper.toDto(taskUpdated);
    }
}
