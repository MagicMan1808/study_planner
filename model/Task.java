package model;

import java.time.LocalDate;
import java.util.UUID;

public class Task {
    
    private UUID id;
    private String name;
    private LocalDate deadline;
    private int difficulty;
    private int estimatedHours;

    public Task(String name, LocalDate deadline, int difficulty, int estimatedHours) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.deadline = deadline;
        this.difficulty = difficulty;
        this.estimatedHours = estimatedHours; // benötigte Zeit
    }

    public Task(UUID id, String name, LocalDate deadline, int difficulty, int estimatedHours) {
        this.id = id;
        this.name = name;
        this.deadline = deadline;
        this.difficulty = difficulty;
        this.estimatedHours = estimatedHours; // benötigte Zeit
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public int getEstimatedHours() {
        return estimatedHours;
    }

    @Override
    public String toString() {
        return name + " | Deadline: " + deadline + " | Difficulty: " + difficulty + " | Hours: " + estimatedHours;
    }
}