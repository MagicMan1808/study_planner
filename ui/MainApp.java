package ui;

import model.Task;
import model.StudySession;
import planner.PlanningService;
//import planner.StudyPlannerAlgorithm;
import storage.StorageManager;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
//import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

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

        TextField deleteField = new TextField();
        deleteField.setPromptText("Name zum Löschen");

        String fieldStyle = "-fx-font-size: 13px; -fx-background-radius: 8;";

        String buttonStyle = 
            "-fx-font-size: 14px;" +
            "-fx-background-radius: 10" +
            "-fx-padding: 8 15 8 15";

        nameField.setStyle(fieldStyle);
        deadlineField.setStyle(fieldStyle);
        difficultyField.setStyle(fieldStyle);
        hoursField.setStyle(fieldStyle);
        deleteField.setStyle(fieldStyle);

        Button button = new Button("Plan anzeigen");

        button.setStyle(
            "-fx-font-size: 14px;" +
            "-fx-background-radius: 10;" +
            "-fx-background-color: #4CAF50;" +
            "-fx-text-fill: white;" +
            "-fx-padding: 8 15 8 15;"
        );
        //TextArea output = new TextArea();
        ListView<String> listView = new ListView<>();
        listView.setPrefHeight(250);
        listView.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: lightgray;" +
            "-fx-border-radius: 10;" +
            "-fx-background-radius: 10;" +
            "-fx-padding: 5;"
        );

        listView.setCellFactory(lv -> new javafx.scene.control.ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);

                    if (isSelected()) {
                        // 🔥 ausgewählt (sichtbar machen)
                        setStyle(
                            "-fx-padding: 10;" +
                            "-fx-background-radius: 8;" +
                            "-fx-background-color: #2196F3;" +
                            "-fx-text-fill: white;"
                        );
                    } else {
                        // normal
                        setStyle(
                            "-fx-padding: 10;" +
                            "-fx-background-radius: 8;" +
                            "-fx-border-radius: 8;" +
                            "-fx-border-color: #e0e0e0;" +
                            "-fx-background-color: white;" +
                            "-fx-text-fill: black;"
                        );
                    }
                }
            }
        });

        Button addButton = new Button("Task hinzufügen");

        Button showTasksButton = new Button("Tasks anzeigen");

        Button deleteButton = new Button("Task löschen");

        addButton.setStyle(buttonStyle);
        showTasksButton.setStyle(buttonStyle);
        deleteButton.setStyle(
            "-fx-font-size: 14px;" +
            "-fx-background-radius: 10;" +
            "-fx-background-color: #e74c3c;" +
            "-fx-text-fill: white;" +
            "-fx-padding: 8 15 8 15;"
        );

        Label title = new Label("Study Planner");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

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
                listView.getItems().clear();
                listView.getItems().add("Keine verfügbaren Tasks!");
                return;

                //tasks = new ArrayList<>();
                //tasks.add(new Task("Mathe", LocalDate.now().plusDays(3), 5, 10));
                //tasks.add(new Task("Prog", LocalDate.now().plusDays(7), 3, 8));
            }

            // Plan berechnen
            PlanningService service = new PlanningService();
            List<StudySession> plan = service.createPlan(tasks);

            // Ausgabe vorbereiten
            //StringBuilder sb = new StringBuilder();
            //for (StudySession session : plan) {
            //    sb.append(session).append("\n");
            //}

            // Text anzeigen
            //output.setText(sb.toString());
            listView.getItems().clear();

            for (StudySession session : plan) {
                listView.getItems().add(session.toString());
            }
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

                //output.setText("Task gespeichert!");
                listView.getItems().clear();
                listView.getItems().add("Task gespeichert!");

                // Felder leeren
                nameField.clear();
                deadlineField.clear();
                difficultyField.clear();
                hoursField.clear();

            } catch (Exception ex) {
                //output.setText("Fehler bei Eingabe!");
                listView.getItems().clear();
                listView.getItems().add("Fehler bei Eingabe!");
            }
        });

        showTasksButton.setOnAction(e -> {

            StorageManager storage = new StorageManager();
            List<Task> tasks = storage.loadTasks();

            listView.getItems().clear();

            if (tasks.isEmpty()) {
                listView.getItems().add("Keine Tasks vorhanden.");
                return;
            }

            for (Task task : tasks) {
                listView.getItems().add(task.toString());
            }
        });

        deleteButton.setOnAction(e -> {

            String nameToDelete = deleteField.getText();

            StorageManager storage = new StorageManager();
            List<Task> tasks = storage.loadTasks();

            boolean removed = tasks.removeIf(task ->
                task.getName().equalsIgnoreCase(nameToDelete)
            );

            listView.getItems().clear();

            if (removed) {
                storage.saveTasks(tasks);
                //output.setText("Task gelöscht!");
                listView.getItems().add("Task gelöscht!");
                //listView.getItems().clear();
                //for (Task task : tasks) {
                //    listView.getItems().add(task.toString());
                //}
            } else {
                //output.setText("Task nicht gefunden!");
                listView.getItems().add("Task nicht gefunden!");
            }

            deleteField.clear();
        });


        HBox inputRow = new HBox(10,
                nameField,
                deadlineField,
                difficultyField,
                hoursField
        );

        HBox deleteRow = new HBox(10,
                deleteField,
                deleteButton
        );

        HBox buttonRow = new HBox(10,
                addButton,
                showTasksButton,
                button
        );

        VBox root = new VBox(15,
                title,
                new Label("Task hinzufügen:"),
                inputRow,
                addButton,

                new Label("Task löschen:"),
                deleteRow,

                new Label("Aktionen:"),
                buttonRow,

                new Label("Ausgabe:"),
                listView
        );

        root.setStyle("-fx-padding: 20; -fx-background-color: #f5f5f5;");

        Scene scene = new Scene(root, 500, 500);

        stage.setTitle("Study Planner");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
