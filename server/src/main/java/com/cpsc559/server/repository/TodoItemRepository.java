package com.cpsc559.server.repository;

import com.cpsc559.server.model.TodoItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TodoItemRepository extends JpaRepository<TodoItem, Long> {
    // Spring Data JPA will automatically implement common CRUD methods.
}