package com.taskmanagement.task.mapper;

import com.taskmanagement.task.dtos.TaskDTO;
import com.taskmanagement.task.entities.Task;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ItaskMapper extends BaseMapper<Task, TaskDTO>{
}
