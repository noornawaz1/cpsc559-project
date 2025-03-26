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
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import com.cpsc559.server.service.ReplicationService;
import java.util.List;

@RestController
@RequestMapping("/api/todolists")
public class TodoListController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TodoListRepository todoListRepository;

    @Autowired
    private TodoListService todoListService;

    @Autowired
    private ReplicationService ReplicationService;

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
    public TodoList createList(@RequestBody TodoList list, @RequestHeader HttpHeaders headers) { 
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
        
        TodoList savedList = todoListRepository.save(list);
        ReplicationService.replicate("POST", "/api/todolists/replica", list, headers);

        return savedList;
    }

    // PUT /api/todolists/{id} - update an existing list
    @PutMapping("/{id}")
    public TodoList updateList(@PathVariable Long id, @RequestBody TodoList listDetails, @RequestHeader HttpHeaders headers) {
        TodoList updatedList = todoListRepository.findById(id).map(list -> {
            list.setName(listDetails.getName());
            return todoListRepository.save(list);
        }).orElseThrow(() -> new RuntimeException("TodoList not found"));
        
        // Forward the request to all replicas
        String path = "/api/todolists/" + id + "/replica";
        ReplicationService.replicate("PUT", path, listDetails, headers);

        return updatedList;
    }

    // DELETE /api/todolists/{id} - delete a list
    @DeleteMapping("/{id}")
    public void deleteList(@PathVariable Long id, @RequestHeader HttpHeaders headers) {
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
        
        // Forward the request to all replicas
        String path = "/api/todolists/" + id + "/replica";
        ReplicationService.replicate("DELETE", path, null, headers);
        
    }
  
    // REPLICATION ENDPOINTS

    // POST /api/todolists/replica - create a replica's new list
    @PostMapping("/replica")
    public ResponseEntity<String> createListReplica(@RequestBody TodoList list, @RequestHeader HttpHeaders headers) { 
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
        
        todoListRepository.save(list);

        return ResponseEntity.ok("ACK");
    }

    // PUT /api/todolists/{id}/replica - update a replica's existing list
    @PutMapping("/{id}/replica")
    public ResponseEntity<String> updateListReplica(@PathVariable Long id, @RequestBody TodoList listDetails, @RequestHeader HttpHeaders headers) {
        TodoList updatedList = todoListRepository.findById(id).map(list -> {
            list.setName(listDetails.getName());
            return todoListRepository.save(list);
        }).orElseThrow(() -> new RuntimeException("TodoList not found"));
        
        return ResponseEntity.ok("ACK");
    }

    // DELETE /api/todolists/{id} - delete a replica's list
    @DeleteMapping("/{id}/replica")
    public ResponseEntity<String> deleteListReplica(@PathVariable Long id, @RequestHeader HttpHeaders headers) {
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
        return ResponseEntity.ok("ACK");
    }

}
