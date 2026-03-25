package storage;

import model.Task;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class StorageManager {
    
    private static final String FILE_PATH = "storage/data.json";

    public void saveTasks(List<Task> tasks) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            
            for (Task task : tasks) {
                String line = task.getId() + ";" + task.getName() + ";" + task.getDeadline() + ";" + task.getDifficulty() + ";" + task.getEstimatedHours();

                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Task> loadTasks() {
        List<Task> tasks = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;

            while ((line = reader.readLine()) != null) {

                String[] parts = line.split(";");

                UUID id = UUID.fromString(parts[0]);
                String name = parts[1];
                java.time.LocalDate deadline = java.time.LocalDate.parse(parts[2]);
                int difficulty = Integer.parseInt(parts[3]);
                int hours = Integer.parseInt(parts[4]);

                tasks.add(new Task(id, name, deadline, difficulty, hours));
            }
        
        } catch (IOException e) {
            e.printStackTrace();
        }

        return tasks;
    }
}
