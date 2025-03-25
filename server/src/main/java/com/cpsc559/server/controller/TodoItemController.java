package com.cpsc559.server.controller;

import com.cpsc559.server.model.TodoItem;
import com.cpsc559.server.model.TodoList;
import com.cpsc559.server.repository.TodoItemRepository;
import com.cpsc559.server.repository.TodoListRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import com.cpsc559.server.service.ReplicationService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/todolists/{listId}/items")
public class TodoItemController {
    @Value("${replication.primary:false}")
    private Boolean primaryServer;

    @Autowired
    private TodoListRepository todoListRepository;

    @Autowired
    private TodoItemRepository todoItemRepository;

    @Autowired
    private ReplicationService ReplicationService;

    // GET /api/todolists/{listId}/items - get all items in a list
    @GetMapping
    public List<TodoItem> getItems(@PathVariable Long listId) {
        TodoList list = todoListRepository.findById(listId)
                .orElseThrow(() -> new RuntimeException("TodoList not found"));
        return list.getItems();
    }

    // GET /api/todolists/{listId}/items/{itemId} - get a specific item in a list
    @GetMapping("/{itemId}")
    public TodoItem getItem(@PathVariable Long listId, @PathVariable Long itemId) {
        // Ensure the item belongs to the list
        TodoList list = todoListRepository.findById(listId)
                .orElseThrow(() -> new RuntimeException("TodoList not found"));
        return todoItemRepository.findById(itemId)
                .filter(item -> item.getTodoList().getId().equals(list.getId()))
                .orElseThrow(() -> new RuntimeException("TodoItem not found"));
    }

    // POST /api/todolists/{listId}/items - add a new item to a list
    @PostMapping
    public TodoItem createItem(@PathVariable Long listId, @RequestBody TodoItem item, @RequestHeader HttpHeaders headers) {
        TodoList list = todoListRepository.findById(listId)
                .orElseThrow(() -> new RuntimeException("TodoList not found"));
        item.setTodoList(list);
        TodoItem savedTodoItem = todoItemRepository.save(item);
        
        // Forward the write to the other replicas
        if (primaryServer){
          String path = "/api/todolists/" + listId + "/items" ;
          ReplicationService.replicate("POST", path, item, headers);
        };

        return savedTodoItem;
    }

    // PUT /api/todolists/{listId}/items/{itemId} - update an existing item in a list
    @PutMapping("/{itemId}")
    public TodoItem updateItem(@PathVariable Long listId,
                               @PathVariable Long itemId,
                               @RequestBody TodoItem itemDetails, 
                               @RequestHeader HttpHeaders headers) {
        TodoList list = todoListRepository.findById(listId)
                .orElseThrow(() -> new RuntimeException("TodoList not found"));
        return todoItemRepository.findById(itemId).map(item -> {
            if (!item.getTodoList().getId().equals(list.getId())) {
                throw new RuntimeException("Item does not belong to the specified list");
            }
            item.setTitle(itemDetails.getTitle());
            item.setCompleted(itemDetails.isCompleted());
            TodoItem updatedItem = todoItemRepository.save(item);
            
            // Forward the write to the other replicas
            if (primaryServer){
              String path = "/api/todolists/" + listId + "/items/" + itemId;
              ReplicationService.replicate("PUT", path, itemDetails, headers);
            };
            
            return updatedItem;
        }).orElseThrow(() -> new RuntimeException("TodoItem not found"));
    }

    // DELETE /api/todolists/{listId}/items/{itemId} - delete an item from a list
    @DeleteMapping("/{itemId}")
    public void deleteItem(@PathVariable Long listId, @PathVariable Long itemId, @RequestHeader HttpHeaders headers) {
        TodoList list = todoListRepository.findById(listId)
                .orElseThrow(() -> new RuntimeException("TodoList not found"));
        TodoItem item = todoItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("TodoItem not found"));
        if (!item.getTodoList().getId().equals(list.getId())) {
            throw new RuntimeException("Item does not belong to the specified list");
        }
        todoItemRepository.delete(item);
        if (primaryServer){
          // Forward the write to the other replicas
          String path = "/api/todolists/" + listId + "/items/" + itemId;
          ReplicationService.replicate("DELETE", path, null, headers);
        };
    }
}
