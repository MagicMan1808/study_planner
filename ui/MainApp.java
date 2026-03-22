package ui;

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
