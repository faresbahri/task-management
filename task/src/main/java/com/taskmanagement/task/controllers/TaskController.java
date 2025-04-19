package com.taskmanagement.task.controllers;


import com.taskmanagement.task.services.interfaces.ItaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {
    private  final ItaskService itaskService;
}
