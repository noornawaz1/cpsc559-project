package com.cpsc559.server.service;

import com.cpsc559.server.model.TodoListResponse;
import com.cpsc559.server.repository.TodoListRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class TodoListService {
    private final TodoListRepository todoListRepository;

    public TodoListService(TodoListRepository todoListRepository) {
        this.todoListRepository = todoListRepository;
    }

    public TodoListResponse getTodoListById(Long id) {
        return todoListRepository.findById(id)
                .map(TodoListResponse::new)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "TodoList not found"));
    }
}
