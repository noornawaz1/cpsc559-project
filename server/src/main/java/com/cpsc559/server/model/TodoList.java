package com.cpsc559.server.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
public class TodoList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    private String name;

    // One-to-many relationship with TodoItem
    @Setter
    @JsonManagedReference
    @OneToMany(mappedBy = "todoList", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TodoItem> items = new ArrayList<>();

    // Constructors
    public TodoList() {}

    public TodoList(String name) {
        this.name = name;
    }

}