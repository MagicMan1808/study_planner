package model;

import java.time.LocalDate;

public class Task {
    
    private String name;
    private LocalDate deadline;
    private int difficulty;
    private int estimatedHours;
    // private boolean status;     // Status (Fertig / Offen)
    // private int progress;       // Fortschritt (in %)

    public Task(String name, LocalDate deadline, int difficulty, int estimatedHours) {
        this.name = name;
        this.deadline = deadline;
        this.difficulty = difficulty;
        this.estimatedHours = estimatedHours; // benötigte Zeit
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