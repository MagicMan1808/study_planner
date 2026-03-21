package storage;

import model.Task;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class StorageManager {
    
    private static final String FILE_PATH = "storage/data.json";

    public void saveTasks(List<Task> tasks) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            
            for (Task task : tasks) {
                String line = task.getName() + ";" + task.getDeadline() + ";" + task.getDifficulty() + ";" + task.getEstimatedHours();

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

                String name = parts[0];
                java.time.LocalDate deadline = java.time.LocalDate.parse(parts[1]);
                int difficulty = Integer.parseInt(parts[2]);
                int hours = Integer.parseInt(parts[3]);

                tasks.add(new Task(name, deadline, difficulty, hours));
            }
        
        } catch (IOException e) {
            e.printStackTrace();
        }

        return tasks;
    }
}
