// Kompilieren vom Ordner study_planner:
// javac --module-path "C:\javafx-sdk-26\lib" --add-modules javafx.controls -sourcepath . ui/MainApp.java
// java --module-path "C:\javafx-sdk-26\lib" --add-modules javafx.controls ui.MainApp

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

    private void refreshTasks(ListView<Task> listView) {
        StorageManager storage = new StorageManager();
        List<Task> tasks = storage.loadTasks();

        listView.getItems().clear();
        listView.getItems().addAll(tasks);
    }

    @Override
    public void start(Stage stage) {

        TextField nameField = new TextField();
        nameField.setPromptText("Name");

        TextField deadlineField = new TextField();
        deadlineField.setPromptText("YYYY-MM-DD");

        TextField difficultyField = new TextField();
        difficultyField.setPromptText("Difficulty (1-5)");

        TextField hoursField = new TextField();
        hoursField.setPromptText("Hours");



        String fieldStyle = 
            "-fx-font-size: 13px;" +
            "-fx-background-radius: 8;" +
            "-fx-background-color: #1e1e1e;" +
            "-fx-text-fill: white;" +
            "-fx-prompt-text-fill: #888;" +
            "-fx-border-color: #333;" +
            "-fx-border-radius: 8;" +
            "-fx-padding: 8;";

        String buttonStyle = 
            "-fx-font-size: 14px;" +
            "-fx-background-radius: 10;" +
            "-fx-background-color: #1f1f1f;" +
            "-fx-text-fill: white;" +
            "-fx-padding: 8 15 8 15;";

        nameField.setStyle(fieldStyle);
        deadlineField.setStyle(fieldStyle);
        difficultyField.setStyle(fieldStyle);
        hoursField.setStyle(fieldStyle);

        nameField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                nameField.setStyle(fieldStyle + "-fx-border-color: #2979ff;");
            } else {
                nameField.setStyle(fieldStyle);
            }
        });

        deadlineField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                deadlineField.setStyle(fieldStyle + "-fx-border-color: #2979ff;");
            } else {
                deadlineField.setStyle(fieldStyle);
            }
        });

        difficultyField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                difficultyField.setStyle(fieldStyle + "-fx-border-color: #2979ff;");
            } else {
                difficultyField.setStyle(fieldStyle);
            }
        });

        hoursField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                hoursField.setStyle(fieldStyle + "-fx-border-color: #2979ff;");
            } else {
                hoursField.setStyle(fieldStyle);
            }
        });

        Button button = new Button("Plan anzeigen");

        button.setStyle(
            "-fx-font-size: 14px;" +
            "-fx-background-radius: 10;" +
            "-fx-background-color: #4CAF50;" +
            "-fx-text-fill: white;" +
            "-fx-padding: 8 15 8 15;"
        );

        button.setOnMouseEntered(e -> button.setStyle(
            "-fx-font-size: 14px;" +
            "-fx-background-radius: 10;" +
            "-fx-background-color: #5ed662;" +
            "-fx-text-fill: white;" +
            "-fx-padding: 8 15 8 15;"
        ));
        button.setOnMouseExited(e -> button.setStyle(
            "-fx-font-size: 14px;" +
            "-fx-background-radius: 10;" +
            "-fx-background-color: #4CAF50;" +
            "-fx-text-fill: white;" +
            "-fx-padding: 8 15 8 15;"
        ));
        //TextArea output = new TextArea();
        ListView<Task> listView = new ListView<>();
        listView.setPrefHeight(300);
        listView.setStyle(
            "-fx-background-color: #1e1e1e;" +
            "-fx-control-inner-background: #1e1e1e;" +
            "-fx-border-color: #333;" +
            "-fx-border-radius: 10;" +
            "-fx-background-radius: 10;" +
            "-fx-padding: 5;"
        );

        ListView<StudySession> planListView = new ListView<>();
        planListView.setPrefHeight(300);

        planListView.setStyle(
            "-fx-background-color: #1e1e1e;" +
            "-fx-control-inner-background: #1e1e1e;" +
            "-fx-border-color: #333;" +
            "-fx-border-radiius: 10;" +
            "-fx-background-radius: 10;" +
            "-fx-padding: 5;"
        );

        listView.setCellFactory(lv -> new javafx.scene.control.ListCell<>() {
            @Override
            protected void updateItem(Task item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    String text = item.toString();
                    setText(text);

                    if (isSelected()) {
                        // 🔥 ausgewählt (sichtbar machen)
                        setStyle(
                            "-fx-padding: 10;" +
                            "-fx-background-radius: 8;" +
                            "-fx-background-color: #2979ff;" +
                            "-fx-text-fill: white;"
                        );
                    } else if (text.startsWith("✅")) {
                        setStyle(
                            "-fx-padding: 10;" +
                            "-fx-background-radius: 8;" +
                            "-fx-background-color: #e8f5e9;" +
                            "-fx-text-fill: #2e7d32;" +
                            "-fx-font-weight: bold;"
                        );
                    } else if (text.startsWith("❌")) {
                        setStyle(
                            "-fx-padding: 10;" +
                            "-fx-background-radius: 8;" +
                            "-fx-background-color: #ffebee;" +
                            "-fx-text-fill: #c62828;" +
                            "-fx-font-weight: bold;"
                        );
                    } else {
                        // normal
                        setStyle(
                            "-fx-padding: 10;" +
                            "-fx-background-radius: 8;" +
                            "-fx-border-radius: 8;" +
                            "-fx-border-color: #333;" +
                            "-fx-background-color: #1e1e1e;" +
                            "-fx-text-fill: white;"
                        );
                    }
                }
            }
        });

        planListView.setCellFactory(lv -> new javafx.scene.control.ListCell<>() {
            @Override
            protected void updateItem(StudySession item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText("📚 " + item.toString());

                    if (isSelected()) {
                        setStyle(
                            "-fx-padding: 10;" +
                            "-fx-background-radius: 8;" +
                            "-fx-background-color: #2972ff;" +
                            "-fx-text-fill: white;"
                        );
                    } else {
                        setStyle(
                            "-fx-padding: 10;" +
                            "-fx-background-radius: 8;" +
                            "-fx-background-color: #1e1e1e;" +
                            "-fx-border-color: #333;" +
                            "-fx-border-radius: 8;" + 
                            "-fx-text-fill: white;"
                        );
                    }
                }
            }
        });

        Button addButton = new Button("Task hinzufügen");

        addButton.setOnMouseEntered(e -> addButton.setStyle(
            "-fx-font-size: 14px;" +
            "-fx-background-radius: 10;" +
            "-fx-background-color: #333333;" +
            "-fx-text-fill: white;" +
            "-fx-padding: 8 15 8 15;"
        ));
        addButton.setOnMouseExited(e -> addButton.setStyle(buttonStyle));

        Button showTasksButton = new Button("Tasks aktualisieren");

        showTasksButton.setOnMouseEntered(e -> showTasksButton.setStyle(
            "-fx-font-size: 14px;" +
            "-fx-background-radius: 10;" +
            "-fx-background-color: #333333;" +
            "-fx-text-fill: white;" +
            "-fx-padding: 8 15 8 15;"
        ));
        showTasksButton.setOnMouseExited(e -> showTasksButton.setStyle(buttonStyle));

        Button deleteButton = new Button("Task löschen");

        addButton.setStyle(buttonStyle);
        showTasksButton.setStyle(buttonStyle);
        deleteButton.setStyle(
            "-fx-font-size: 14px;" +
            "-fx-background-radius: 10;" +
            "-fx-background-color: #c0392b;" +
            "-fx-text-fill: white;" +
            "-fx-padding: 8 15 8 15;"
        );

        deleteButton.setOnMouseEntered(e -> deleteButton.setStyle(
            "-fx-font-size: 14px;" +
            "-fx-background-radius: 10;" +
            "-fx-background-color: #e74c3c;" +
            "-fx-text-fill: white;" +
            "-fx-padding: 8 15 8 15;"
        ));
        deleteButton.setOnMouseExited(e -> deleteButton.setStyle(
            "-fx-font-size: 14px;" +
            "-fx-background-radius: 10;" +
            "-fx-background-color: #c0392b;" +
            "-fx-text-fill: white;" +
            "-fx-padding: 8 15 8 15;"
        ));

        Label title = new Label("Study Planner");
        title.setStyle("-fx-font-size: 25px; -fx-font-weight: bold; -fx-text-fill: white");

        Label statusLabel = new Label();
        statusLabel.setStyle(
            "-fx-font-size: 13px;" +
            "-fx-padding: 8;" +
            "-fx-background-radius: 8;" +
            "-fx-text-fill: white;"
        );

        button.setOnAction(e -> {
            // Demo Tasks (später aus Storage)
            //List<Task> tasks = new ArrayList<>();
            //tasks.add(new Task("Mathe", LocalDate.now().plusDays(3), 5, 10));
            //tasks.add(new Task("Prog", LocalDate.now().plusDays(7), 3, 8));
            //tasks.add(new Task("GBS", LocalDate.now().plusDays(10), 2, 5));

            statusLabel.setText("");
            
            StorageManager storage = new StorageManager();
            List<Task> tasks = storage.loadTasks();

            // Fallback falls Storage leer
            if (tasks.isEmpty()) {
                planListView.getItems().clear();
                statusLabel.setText("❌ Keine verfügbaren Tasks!");
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
            planListView.getItems().clear();

            for (StudySession session : plan) {
                planListView.getItems().add(session);
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

                boolean exists = tasks.stream()
                    .anyMatch(t -> t.getName().equalsIgnoreCase(name));

                if (exists) {
                    statusLabel.setText("❌ Task mit diesem Namen existiert bereits!");
                    return;
                }

                tasks.add(newTask);
                storage.saveTasks(tasks);

                //output.setText("Task gespeichert!");

                refreshTasks(listView);
                statusLabel.setText("✅ Task gespeichert!");

                // Felder leeren
                nameField.clear();
                deadlineField.clear();
                difficultyField.clear();
                hoursField.clear();

            } catch (Exception ex) {
                //output.setText("Fehler bei Eingabe!");
                statusLabel.setText("❌ Fehler bei Eingabe!");
            }
        });

        showTasksButton.setOnAction(e -> {

            refreshTasks(listView);

            if (listView.getItems().isEmpty()) {
                statusLabel.setText("❌ Keine Tasks vorhanden.");
                return;
            }
        });

        deleteButton.setOnAction(e -> {

            Task selectedTask = listView.getSelectionModel().getSelectedItem();

            if (selectedTask == null) {
                statusLabel.setText("❌ Bitte wähle einen Task aus!");
                return;
            }

            StorageManager storage = new StorageManager();
            List<Task> tasks = storage.loadTasks();

            boolean removed = tasks.removeIf(t ->
                t.getName().equals(selectedTask.getName()) &&
                t.getDeadline().equals(selectedTask.getDeadline())
            );

            if (removed) {
                storage.saveTasks(tasks);

                refreshTasks(listView);

                statusLabel.setText("✅ Task gelöscht!");
            } else {
                statusLabel.setText("❌ Fehler beim Löschen!");
            }
        });


        HBox inputRow = new HBox(10,
                nameField,
                deadlineField,
                difficultyField,
                hoursField
        );

        HBox buttonRow = new HBox(10,
                showTasksButton,
                button,
                deleteButton
        );

        VBox inputCard = new VBox(10, inputRow, addButton);
        inputCard.setStyle(
            "-fx-background-color: #2d2d2d;" +
            "-fx-padding: 15;" +
            "-fx-background-radius: 10;"
        );

        Label taskLabel = new Label("Task hinzufügen:");
        taskLabel.setStyle("-fx-text-fill: #bbbbbb;");

        Label actionLabel = new Label("Aktionen:");
        actionLabel.setStyle("-fx-text-fill: #bbbbbb;");

        Label tasksLabel = new Label("Tasks:");
        tasksLabel.setStyle("-fx-text-fill: #bbbbbb;");

        Label planLabel = new Label("Lernplan:");
        planLabel.setStyle("-fx-text-fill: #bbbbbb;");

        VBox root = new VBox(15,
                title,
                statusLabel,
                taskLabel,
                inputCard,

                actionLabel,
                buttonRow,

                tasksLabel,
                listView,

                planLabel,
                planListView
        );

        root.setStyle("-fx-padding: 20; -fx-background-color: #121212;");

        Scene scene = new Scene(root, 500, 700);

        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());

        stage.setTitle("Study Planner");
        stage.setScene(scene);
        stage.show();
        refreshTasks(listView);
    }

    public static void main(String[] args) {
        launch();
    }
}
