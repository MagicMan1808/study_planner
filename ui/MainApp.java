package ui;

import model.Task;
import model.StudySession;
import planner.PlanningService;
import planner.StudyPlannerAlgorithm;
import storage.StorageManager;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) {

        TextField nameField = new TextField();
        nameField.setPromptText("Name");

        TextField deadlineField = new TextField();
        deadlineField.setPromptText("Deadline (YYYY-MM-DD)");

        TextField difficultyField = new TextField();
        difficultyField.setPromptText("Difficulty (1-5)");

        TextField hoursField = new TextField();
        hoursField.setPromptText("Hours");

        Button button = new Button("Plan anzeigen");
        TextArea output = new TextArea();

        Button addButton = new Button("Task hinzufügen");

        button.setOnAction(e -> {
            // Demo Tasks (später aus Storage)
            //List<Task> tasks = new ArrayList<>();
            //tasks.add(new Task("Mathe", LocalDate.now().plusDays(3), 5, 10));
            //tasks.add(new Task("Prog", LocalDate.now().plusDays(7), 3, 8));
            //tasks.add(new Task("GBS", LocalDate.now().plusDays(10), 2, 5));

            StorageManager storage = new StorageManager();
            List<Task> tasks = storage.loadTasks();

            // Fallback falls Storage leer
            if (tasks.isEmpty()) {
                tasks = new ArrayList<>();
                tasks.add(new Task("Mathe", LocalDate.now().plusDays(3), 5, 10));
                tasks.add(new Task("Prog", LocalDate.now().plusDays(7), 3, 8));
            }

            // Plan berechnen
            PlanningService service = new PlanningService();
            List<StudySession> plan = service.createPlan(tasks);

            // Ausgabe vorbereiten
            StringBuilder sb = new StringBuilder();
            for (StudySession session : plan) {
                sb.append(session).append("\n");
            }

            // Text anzeigen
            output.setText(sb.toString());
        });

        addButton.setOnAction(e -> {
            try {
                String name = nameField.getText();
                LocalDate deadline = LocalDate.parse(deadlineField.getText());
                int difficulty = Integer.parseInt(difficultyField.getText());
                int hours = Integer.parseInt(hoursField.getText());

                Task newTask = new Task(name, deadline, difficulty, hours);

                StorageManager storage = new StorageManager();
                List<Task> tasks = storage.loadTasks();

                tasks.add(newTask);
                storage.saveTasks(tasks);

                output.setText("Task gespeichert!");

                // Felder leeren
                nameField.clear();
                deadlineField.clear();
                difficultyField.clear();
                hoursField.clear();

            } catch (Exception ex) {
                output.setText("Fehler bei Eingabe!");
            }
        });

        VBox root = new VBox(10,
                new Label("Neuer Task:"),
                nameField,
                deadlineField,
                difficultyField,
                hoursField,
                addButton,
                button,
                output
        );

        Scene scene = new Scene(root, 400, 300);

        stage.setTitle("Study Planner");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
