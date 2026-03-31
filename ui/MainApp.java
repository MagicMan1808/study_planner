// Kompilieren vom Ordner study_planner:
// javac --module-path "C:\javafx-sdk-26\lib" --add-modules javafx.controls -sourcepath . planner/*.java model/*.java storage/*.java ui/*.java
// java --module-path "C:\javafx-sdk-26\lib" --add-modules javafx.controls ui.MainApp

package ui;

import model.Task;
import model.StudySession;
import planner.PlanExporter;
import planner.PlanningService;
import storage.StorageManager;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.layout.VBox;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
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

    private void showSuccess(Label label, String message) {
        label.setText(message);
        label.setStyle(
            "-fx-padding: 10;" +
            "-fx-background-radius: 8;" +
            "-fx-background-color: #e8f5e9;" +
            "-fx-text-fill: #2e7d32;" +
            "-fx-font-weight: bold;"
        );
    }

    private void showError(Label label, String message) {
        label.setText(message);
        label.setStyle(
            "-fx-padding: 10;" +
            "-fx-background-radius: 8;" +
            "-fx-background-color: #ffebee;" +
            "-fx-text-fill: #c62828;" +
            "-fx-font-weight: bold;"
        );
    }

    @Override
    public void start(Stage stage) {

        final Task[] selectedTaskRef = new Task[1];
        AtomicReference<List<StudySession>> currentPlan = new AtomicReference<>(null);

        TextField nameField = new TextField();
        nameField.setPromptText("Name");

        TextField deadlineField = new TextField();
        deadlineField.setPromptText("DD.MM.YYYY");

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
            "-fx-background-color: #2e2e2e;" +
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

        Button exportButton = new Button("Plan exportieren");
        exportButton.setStyle(
            "-fx-font-size: 14px;" +
            "-fx-background-radius: 10;" +
            "-fx-background-color: #856404;" +
            "-fx-text-fill: white;" +
            "-fx-padding: 8 15 8 15;"
        );

        exportButton.setOnMouseEntered(e -> exportButton.setStyle(
            "-fx-font-size: 14px;" +
            "-fx-background-radius: 10;" +
            "-fx-background-color: #c19205;" +
            "-fx-text-fill: white;" +
            "-fx-padding: 8 15 8 15;"
        ));
        exportButton.setOnMouseExited(e -> exportButton.setStyle(
            "-fx-font-size: 14px;" +
            "-fx-background-radius: 10;" +
            "-fx-background-color: #856404;" +
            "-fx-text-fill: white;" +
            "-fx-padding: 8 15 8 15;"
        ));

        Button planbutton = new Button("Plan anzeigen");

        planbutton.setStyle(
            "-fx-font-size: 14px;" +
            "-fx-background-radius: 10;" +
            "-fx-background-color: #4CAF50;" +
            "-fx-text-fill: white;" +
            "-fx-padding: 8 15 8 15;"
        );

        planbutton.setOnMouseEntered(e -> planbutton.setStyle(
            "-fx-font-size: 14px;" +
            "-fx-background-radius: 10;" +
            "-fx-background-color: #5ed662;" +
            "-fx-text-fill: white;" +
            "-fx-padding: 8 15 8 15;"
        ));
        planbutton.setOnMouseExited(e -> planbutton.setStyle(
            "-fx-font-size: 14px;" +
            "-fx-background-radius: 10;" +
            "-fx-background-color: #4CAF50;" +
            "-fx-text-fill: white;" +
            "-fx-padding: 8 15 8 15;"
        ));

        ListView<Task> listView = new ListView<>();
        listView.setPrefHeight(300);
        listView.setStyle(
            "-fx-background-color: #1e1e1e;" +
            "-fx-control-inner-background: #1e1e1e;" +
            "-fx-border-color: transparent;" +
            "-fx-border-radius: 10;" +
            "-fx-background-radius: 10;" +
            "-fx-padding: 5;"
        );

        ListView<StudySession> planListView = new ListView<>();
        planListView.setPrefHeight(300);

        planListView.setStyle(
            "-fx-background-color: #1e1e1e;" +
            "-fx-control-inner-background: #1e1e1e;" +
            "-fx-border-color: transparent;" +
            "-fx-border-radius: 10;" +
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
                        // ausgewählt
                        setStyle(
                            "-fx-padding: 10;" +
                            "-fx-background-radius: 8;" +
                            "-fx-background-color: #2979ff;" +
                            "-fx-text-fill: white;"
                        );
                    } else if (item.getDeadline().isBefore(LocalDate.now())) {
                        setStyle(
                            "-fx-padding: 10;" +
                            "-fx-background-radius: 8;" +
                            "-fx-border-radius: 8;" +
                            "-fx-background-color: #3a1e1e;" +
                            "-fx-text-fill: #f80707;"
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

        Button refreshTasksButton = new Button("Tasks aktualisieren");

        refreshTasksButton.setOnMouseEntered(e -> refreshTasksButton.setStyle(
            "-fx-font-size: 14px;" +
            "-fx-background-radius: 10;" +
            "-fx-background-color: #333333;" +
            "-fx-text-fill: white;" +
            "-fx-padding: 8 15 8 15;"
        ));
        refreshTasksButton.setOnMouseExited(e -> refreshTasksButton.setStyle(buttonStyle));

        Button deleteButton = new Button("Task löschen");

        addButton.setStyle(buttonStyle);
        refreshTasksButton.setStyle(buttonStyle);
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

        Button editButton = new Button("Task bearbeiten");
        editButton.setStyle(buttonStyle);

        editButton.setOnMouseEntered(e -> editButton.setStyle(
            "-fx-font-size: 14px;" +
            "-fx-background-radius: 10;" +
            "-fx-background-color: #333333;" +
            "-fx-text-fill: white;" +
            "-fx-padding: 8 15 8 15;"
        ));
        editButton.setOnMouseExited(e -> editButton.setStyle(buttonStyle));

        // Buttons zum Bearbeiten des generierten Plans
        Button editPlanEntryButton = new Button("Eintrag bearbeiten");
        Button deletePlanEntryButton = new Button("Eintrag löschen");
        Button addPlanEntryButton = new Button("Eintrag hinzufügen");

        editPlanEntryButton.setStyle(buttonStyle);
        deletePlanEntryButton.setStyle(buttonStyle);
        addPlanEntryButton.setStyle(buttonStyle);

        editPlanEntryButton.setOnMouseEntered(e -> editPlanEntryButton.setStyle(
            "-fx-font-size: 14px;" +
            "-fx-background-radius: 10;" +
            "-fx-background-color: #333333;" +
            "-fx-text-fill: white;" +
            "-fx-padding: 8 15 8 15;"
        ));
        editPlanEntryButton.setOnMouseExited(e -> editPlanEntryButton.setStyle(buttonStyle));

        deletePlanEntryButton.setOnMouseEntered(e -> deletePlanEntryButton.setStyle(
            "-fx-font-size: 14px;" +
            "-fx-background-radius: 10;" +
            "-fx-background-color: #333333;" +
            "-fx-text-fill: white;" +
            "-fx-padding: 8 15 8 15;"
        ));
        deletePlanEntryButton.setOnMouseExited(e -> deletePlanEntryButton.setStyle(buttonStyle));

        addPlanEntryButton.setOnMouseEntered(e -> addPlanEntryButton.setStyle(
            "-fx-font-size: 14px;" +
            "-fx-background-radius: 10;" +
            "-fx-background-color: #333333;" +
            "-fx-text-fill: white;" +
            "-fx-padding: 8 15 8 15;"
        ));
        addPlanEntryButton.setOnMouseExited(e -> addPlanEntryButton.setStyle(buttonStyle));

        Label title = new Label("Study Planner");
        title.setStyle("-fx-font-size: 25px; -fx-font-weight: bold; -fx-text-fill: white");

        Label statusLabel = new Label();
        statusLabel.setStyle(
            "-fx-font-size: 13px;" +
            "-fx-padding: 8;" +
            "-fx-background-radius: 8;" +
            "-fx-text-fill: white;"
        );

        editPlanEntryButton.setOnAction(e -> {
            StudySession selected = planListView.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showError(statusLabel, "❌ Bitte wähle einen Eintrag aus!");
                return;
            }

            Dialog<StudySession> dialog = new Dialog<>();
            dialog.setTitle("Eintrag bearbeiten");
            dialog.setHeaderText("Lerneinheit anpassen");

            TextField dateField = new TextField(selected.getDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
            TextField subjectField = new TextField(selected.getSubject());
            TextField hourField = new TextField(String.valueOf(selected.getDuration()));

            dateField.setPromptText("DD.MM.YYYY");
            subjectField.setPromptText("Fach / Aufgabe");
            hourField.setPromptText("Stunden");

            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.add(new Label("Datum:"), 0, 0);
            grid.add(dateField, 1, 0);
            grid.add(new Label("Fach:"), 0, 1);
            grid.add(subjectField, 1, 1);
            grid.add(new Label("Stunden:"), 0, 2);
            grid.add(hourField, 1, 2);

            dialog.getDialogPane().setContent(grid);
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            dialog.setResultConverter(btn -> {
                if (btn == ButtonType.OK) {
                    try {
                        LocalDate newDate = LocalDate.parse(dateField.getText().trim(), DateTimeFormatter.ofPattern("dd.MM.yyyy"));
                        String newSubject = subjectField.getText().trim();
                        int newHours = Integer.parseInt(hourField.getText().trim());
                        
                        if (newSubject.isEmpty()) {
                            throw new IllegalArgumentException("Fach darf nicht leer sein!");
                        }
                        if (newHours <= 0) {
                            throw new IllegalArgumentException("Stunden müssen positiv sein!");
                        }

                        return new StudySession(newDate, newSubject, newHours);
                    } catch (Exception ex) {
                        showError(statusLabel, "❌ Ungültige Eingabe: " + ex.getMessage());
                        return null;
                    }
                }
                return null;
            });

            dialog.showAndWait().ifPresent(newSession -> {
                List<StudySession> plan = currentPlan.get();
                int index = plan.indexOf(selected);
                plan.set(index, newSession);
                plan.sort((a, b) -> a.getDate().compareTo(b.getDate()));
                planListView.getItems().setAll(plan);
                showSuccess(statusLabel, "✅ Eintrag aktualisiert!");
            });
        });

        deletePlanEntryButton.setOnAction(e -> {
            StudySession selected = planListView.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showError(statusLabel, "❌ Bitte wähle einen Eintrag aus!");
                return;
            }

            List<StudySession> plan = currentPlan.get();
            plan.remove(selected);
            planListView.getItems().setAll(plan);
            showSuccess(statusLabel, "✅ Eintrag gelöscht!");
        });

        editButton.setOnAction(e -> {
            Task selected = listView.getSelectionModel().getSelectedItem();

            if (selected == null) {
                showError(statusLabel, "❌ Bitte wähle einen Task aus!");
                return;
            }

            selectedTaskRef[0] = selected;

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

            nameField.setText(selected.getName());
            deadlineField.setText(selected.getDeadline().format(formatter));
            difficultyField.setText(String.valueOf(selected.getDifficulty()));
            hoursField.setText(String.valueOf(selected.getEstimatedHours()));

            addButton.setText("Task aktualisieren");
            statusLabel.setText("✏️ Bearbeite Task...");
            statusLabel.setStyle(
                "-fx-padding: 10;" +
                "-fx-background-radius: 8;" +
                "-fx-background-color: #e3f2fd;" +
                "-fx-text-fill: #1565c0;" +
                "-fx-font-weight: bold;"
            );
        });

        addPlanEntryButton.setOnAction(e -> {
            Dialog<StudySession> dialog = new Dialog<>();
            dialog.setTitle("Neuen Eintrag hinzufügen");
            dialog.setHeaderText("Manuelle Lerneinheit einplanen");

            TextField dateField = new TextField(LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
            TextField subjectField = new TextField();
            TextField hourField = new TextField();

            dateField.setPromptText("DD.MM.YYYY");
            subjectField.setPromptText("Fach / Aufgabe");
            hourField.setPromptText("Stunden");

            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.add(new Label("Datum:"), 0, 0);
            grid.add(dateField, 1, 0);
            grid.add(new Label("Fach:"), 0, 1);
            grid.add(subjectField, 1, 1);
            grid.add(new Label("Stunden:"), 0, 2);
            grid.add(hourField, 1, 2);

            dialog.getDialogPane().setContent(grid);
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            dialog.setResultConverter(btn -> {
                if (btn == ButtonType.OK) {
                    try {
                        LocalDate newDate = LocalDate.parse(dateField.getText().trim(), DateTimeFormatter.ofPattern("dd.MM.yyyy"));
                        String newSubject = subjectField.getText().trim();
                        int newHours = Integer.parseInt(hourField.getText().trim());
                        
                        if (newSubject.isEmpty()) {
                            throw new IllegalArgumentException("Fach darf nicht leer sein!");
                        }
                        if (newHours <= 0) {
                            throw new IllegalArgumentException("Stunden müssen positiv sein!");
                        }

                        return new StudySession(newDate, newSubject, newHours);
                    } catch (Exception ex) {
                        showError(statusLabel, "❌ Ungültige Eingabe: " + ex.getMessage());
                        return null;
                    }
                }
                return null;
            });

            dialog.showAndWait().ifPresent(newSession -> {
                List<StudySession> plan = currentPlan.get();
                plan.add(newSession);
                plan.sort((a, b) -> a.getDate().compareTo(b.getDate()));
                planListView.getItems().setAll(plan);
                showSuccess(statusLabel, "✅ Eintrag hinzugefügt!");
            });
        });

        deletePlanEntryButton.setOnAction(e -> {
            StudySession selected = planListView.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showError(statusLabel, "❌ Bitte wähle einen Eintrag aus!");
                return;
            }

            List<StudySession> plan = currentPlan.get();
            plan.remove(selected);
            planListView.getItems().setAll(plan);
            showSuccess(statusLabel, "✅ Eintrag gelöscht!");
        });

        planbutton.setOnAction(e -> {
            statusLabel.setText("");
            statusLabel.setStyle("");
            
            
            StorageManager storage = new StorageManager();
            List<Task> tasks = storage.loadTasks();

            // Fallback falls Storage leer
            if (tasks.isEmpty()) {
                planListView.getItems().clear();
                showError(statusLabel, "❌ Keine verfügbaren Tasks!");
                return;
            }

            boolean hasExpired = tasks.stream()
                .anyMatch(t -> t.getDeadline().isBefore(LocalDate.now()));

            if (hasExpired) {
                statusLabel.setText("⚠️ Einige Tasks sind bereits abgelaufen!");
                statusLabel.setStyle(
                    "-fx-padding: 10;" +
                    "-fx-background-radius: 8;" +
                    "-fx-background-color: #fff3cd;" +
                    "-fx-text-fill: #856404;" +
                    "-fx-font-weight: bold;"
                );
            }

            // Plan berechnen
            PlanningService service = new PlanningService();
            List<StudySession> plan = service.createPlan(tasks);
            currentPlan.set(plan);
            planListView.getItems().clear();

            for (StudySession session : plan) {
                planListView.getItems().add(session);
            }
        });

        exportButton.setOnAction(e -> {
            if (currentPlan.get() == null || currentPlan.get().isEmpty()) {
                showError(statusLabel, "❌ Bitte zuerst einen Plan erstellen!");
                return;
            }

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Study Plan");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));

            File file = fileChooser.showSaveDialog(stage);

            if (file != null) {
                PlanExporter.exportToCSV(currentPlan.get(), file.getAbsolutePath());
                showSuccess(statusLabel, "✅ Plan exportiert!");
            }

            // PlanExporter.exportToCSV(plan, "study_plan.csv");
        });

        addButton.setOnAction(e -> {
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            
            try {
                String name = nameField.getText().trim();
                String deadlineStr = deadlineField.getText().trim();
                String difficultyStr = difficultyField.getText().trim();
                String hoursStr = hoursField.getText().trim();

                if (name.isEmpty()) {
                    showError(statusLabel, "❌ Bitte gib einen Namen ein!");
                    return;
                }
                if (deadlineStr.isEmpty()) {
                    showError(statusLabel, "❌ Bitte gib eine Deadline ein!");
                    return;
                }
                if (difficultyStr.isEmpty()) {
                    showError(statusLabel, "❌ Schwierigkeit angeben (1-5)!");
                    return;
                }
                if (hoursStr.isEmpty()) {
                    showError(statusLabel, "❌ Gib benötigte Stunden an (mind. 1)!");
                    return;
                }

                LocalDate deadline;
                try {
                    deadline = LocalDate.parse(deadlineStr, formatter);
                } catch (Exception ex) {
                    showError(statusLabel, "❌ Datum muss Format DD.MM.YYYY haben!");
                    return;
                }

                int difficulty;
                try {
                    difficulty = Integer.parseInt(difficultyStr);
                } catch (NumberFormatException ex) {
                    showError(statusLabel, "❌ Schwierigkeit muss eine Zahl sein (1-5)!");
                    return;
                }
                if (difficulty < 1 || difficulty > 5) {
                    showError(statusLabel, "❌ Schwierigkeit muss zwischen 1 und 5 liegen!");
                    return;
                }

                int hours;
                try {
                    hours = Integer.parseInt(hoursStr);
                } catch (NumberFormatException ex) {
                    showError(statusLabel, "❌ Stunden müssen eine Zahl sein!");
                    return;
                }
                if (hours <= 0) {
                    showError(statusLabel, "❌ Gib benötigte Stunden an (mind. 1)!");
                    return;
                }

                Task newTask;

                if (selectedTaskRef[0] != null) {
                    // EDIT -> gleiche ID behalten
                    newTask = new Task(
                        selectedTaskRef[0].getId(),
                        name,
                        deadline,
                        difficulty,
                        hours
                    );
                } else {
                    // NEU -> neue UUID
                    newTask = new Task(name, deadline, difficulty, hours);
                }

                StorageManager storage = new StorageManager();
                List<Task> tasks = storage.loadTasks();

                if (selectedTaskRef[0] != null) {
                    // Edit mode
                    Task oldTask = selectedTaskRef[0];

                    boolean exists = tasks.stream()
                        .anyMatch(t ->
                            t.getName().equalsIgnoreCase(name) &&
                            !t.getId().equals(oldTask.getId())
                        );

                    if (exists) {
                        showError(statusLabel, "❌ Task mit diesem Namen existiert bereits!");
                        return;
                    }

                    tasks.removeIf(t -> t.getId().equals(oldTask.getId()));

                    tasks.add(newTask);

                    showSuccess(statusLabel, "✏️ Task aktualisiert!");

                } else {

                    boolean exists = tasks.stream()
                        .anyMatch(t -> t.getName().equalsIgnoreCase(name));

                    if (exists) {
                        showError(statusLabel, "❌ Task mit diesem Namen existiert bereits!");
                        return;
                    }

                    tasks.add(newTask);
                    showSuccess(statusLabel, "✅ Task gespeichert!");
                }

                storage.saveTasks(tasks);
                refreshTasks(listView);
                

                // Felder leeren
                nameField.clear();
                deadlineField.clear();
                difficultyField.clear();
                hoursField.clear();

                selectedTaskRef[0] = null;
                addButton.setText("Task hinzufügen");

            } catch (Exception ex) {
                showError(statusLabel, "❌ Datum muss Format DD.MM.YYYY haben!");
            }

            currentPlan.set(null);
            planListView.getItems().clear();
        });

        refreshTasksButton.setOnAction(e -> {

            refreshTasks(listView);

            if (listView.getItems().isEmpty()) {
                showError(statusLabel, "❌ Keine Tasks vorhanden.");
                return;
            } else {
                showSuccess(statusLabel, "✅ Tasks aktualisiert!");
            }

            currentPlan.set(null);
            planListView.getItems().clear();
        });

        deleteButton.setOnAction(e -> {

            Task selectedTask = listView.getSelectionModel().getSelectedItem();

            currentPlan.set(null);
            planListView.getItems().clear();

            if (selectedTask == null) {
                showError(statusLabel, "❌ Bitte wähle einen Task aus!");
                return;
            }

            StorageManager storage = new StorageManager();
            List<Task> tasks = storage.loadTasks();

            boolean removed = tasks.removeIf(t ->
                t.getId().equals(selectedTask.getId())
            );

            if (removed) {
                storage.saveTasks(tasks);

                refreshTasks(listView);

                showSuccess(statusLabel, "✅ Task gelöscht!");

                 selectedTaskRef[0] = null;
                 nameField.clear();
                 deadlineField.clear();
                 difficultyField.clear();
                 hoursField.clear();
                addButton.setText("Task hinzufügen");

            } else {
                showError(statusLabel, "❌ Fehler beim Löschen!");
            }
        });


        HBox inputRow = new HBox(10,
                nameField,
                deadlineField,
                difficultyField,
                hoursField
        );

        HBox buttonRow1 = new HBox(10,
                refreshTasksButton,
                editButton
        );

        HBox buttonRow2 = new HBox(10,
                planbutton,
                exportButton,
                deleteButton
        );

        VBox inputCard = new VBox(10, inputRow, addButton);
        inputCard.setStyle(
            "-fx-background-color: #1e1e1e;" +
            "-fx-padding: 15;" +
            "-fx-background-radius: 10;"
        );

        HBox planEditBox = new HBox(10, editPlanEntryButton, deletePlanEntryButton, addPlanEntryButton);
        planEditBox.setStyle(
            "-fx-padding: 5 0 0 0;"
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
                buttonRow1,
                buttonRow2,

                tasksLabel,
                listView,

                planLabel,
                planListView,

                planEditBox
        );

        root.setStyle("-fx-padding: 20; -fx-background-color: #121212;");

        root.setOnMouseClicked(e -> {
            root.requestFocus();
           
            listView.getSelectionModel().clearSelection();
            planListView.getSelectionModel().clearSelection();
        });

        Scene scene = new Scene(root, 600, 750);

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
