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

public class MainApp extends Application {

    @Override
    public void start(Stage stage) {

        Button button = new Button("Plan anzeigen");
        TextArea output = new TextArea();

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

        VBox root = new VBox(10, button, output);

        Scene scene = new Scene(root, 400, 300);

        stage.setTitle("Study Planner");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
