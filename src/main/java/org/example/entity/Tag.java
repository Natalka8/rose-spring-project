package org.example.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tags")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "tasks")
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String name;

    @Column(length = 100)
    private String description;

    @Column(length = 7) // HEX color, e.g.: #FF0000
    private String color;

    @ManyToMany(mappedBy = "tags")
    @Builder.Default
    private Set<Task> tasks = new HashSet<>();

    public void addTask(Task task) {
        if (tasks == null) {
            tasks = new HashSet<>();
        }
        this.tasks.add(task);
        if (task.getTags() != null) {
            task.getTags().add(this);
        }
    }

    public void removeTask(Task task) {
        if (tasks != null) {
            this.tasks.remove(task);
        }
        if (task != null && task.getTags() != null) {
            task.getTags().remove(this);
        }
    }
}