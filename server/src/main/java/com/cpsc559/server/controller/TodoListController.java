package com.cpsc559.server.controller;

import com.cpsc559.server.model.TodoItem;
import com.cpsc559.server.model.TodoList;
import com.cpsc559.server.repository.TodoListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/todolists")
public class TodoListController {

    @Autowired
    private TodoListRepository todoListRepository;

    // GET /api/todolists - get all lists
    @GetMapping
    public List<TodoList> getAllLists() {
        return todoListRepository.findAll();
    }

    // GET /api/todolists/{id} - get a specific list by id
    @GetMapping("/{id}")
    public TodoList getListById(@PathVariable Long id) {
        return todoListRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("TodoList not found"));
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
        todoListRepository.deleteById(id);
    }
}
