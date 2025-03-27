package com.cpsc559.server.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TodoListResponse {

    private Long id;
    private String name;
    private String author;
    private List<TodoItem> items;

    public TodoListResponse(TodoList todoList) {
        this.id = todoList.getId();
        this.name = todoList.getName();
        this.author = todoList.getAuthor().getUsername();
        this.items = todoList.getItems();
    }
}
