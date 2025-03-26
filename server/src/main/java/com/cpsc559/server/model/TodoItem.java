package com.cpsc559.server.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class TodoItem {

    @Id
    private Long id;

    private String title;
    private boolean completed;

    // Many-to-one relationship: each TodoItem belongs to one TodoList
    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "todo_list_id")
    private TodoList todoList;

    @PrePersist
    public void ensureId() {
        if (this.id == null) {
            this.id = System.currentTimeMillis();
        }
    }

    // Default constructor for JPA
    public TodoItem() { }

    // Constructor for convenience
    public TodoItem(String title, boolean completed) {
        this.title = title;
        this.completed = completed;
    }
}
