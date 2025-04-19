package com.taskmanagement.task.services.implimentation;

import com.taskmanagement.task.mapper.ItaskMapper;
import com.taskmanagement.task.repositories.ItaskRepository;
import com.taskmanagement.task.services.interfaces.ItaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class taskService implements ItaskService {
    private final ItaskRepository repository;
    private final ItaskMapper mapper;

}
