package com.cpsc559.server.controller;

import com.cpsc559.server.model.TodoItem;
import com.cpsc559.server.model.TodoList;
import com.cpsc559.server.model.TodoListResponse;
import com.cpsc559.server.model.User;
import com.cpsc559.server.repository.TodoListRepository;
import com.cpsc559.server.repository.UserRepository;
import com.cpsc559.server.service.TodoListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping("/api/todolists")
public class TodoListController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TodoListRepository todoListRepository;

    @Autowired
    private TodoListService todoListService;

    // GET /api/todolists - get all lists
    @GetMapping
    public List<TodoListResponse> getAllLists() {
        return todoListService.getAllTodoLists();
    }

    // GET /api/todolists/{id} - get a specific list by id
    @GetMapping("/{id}")
    public TodoListResponse getListById(@PathVariable Long id) {
        return todoListService.getTodoListById(id);
    }

    // POST /api/todolists - create a new list
    @PostMapping
    public TodoList createList(@RequestBody TodoList list) {
        // Set the parent reference for each TodoItem in the list
        if (list.getItems() != null) {
            for (TodoItem item : list.getItems()) {
                item.setTodoList(list);
            }
        }

        String currentUserName = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(currentUserName)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));
        list.setAuthor(currentUser);
        return todoListRepository.save(list);
    }

    // PUT /api/todolists/{id} - update an existing list
    @PutMapping("/{id}")
    public TodoList updateList(@PathVariable Long id, @RequestBody TodoList listDetails) {
        return todoListRepository.findById(id).map(list -> {
            list.setName(listDetails.getName());
            return todoListRepository.save(list);
        }).orElseThrow(() -> new RuntimeException("TodoList not found"));
    }

    // DELETE /api/todolists/{id} - delete a list
    @DeleteMapping("/{id}")
    public void deleteList(@PathVariable Long id) {
        TodoList list = todoListRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("TodoList not found"));

        String currentUserName = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(currentUserName)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));

        Long currentUserId = currentUser.getId();
        Long listAuthorId = list.getAuthor().getId();
        if (listAuthorId.equals(currentUserId)) {
            todoListRepository.deleteById(id);
        }
        else {
            throw new RuntimeException("Unauthorized to delete this list");
        }
    }
}
