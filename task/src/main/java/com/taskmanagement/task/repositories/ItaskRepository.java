package com.taskmanagement.task.repositories;

import com.taskmanagement.task.entities.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItaskRepository extends JpaRepository<Task, Long> {
}
