// Kompilieren vom study_planner Ordner:
// javac -sourcepath . main/Main.java
// java main.Main

package main;

import model.Task;
import model.StudySession;
import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        Task task = new Task("Prog I", LocalDate.of(2026, 7, 30), 4, 20);
        System.out.println(task);

        StudySession session = new StudySession(LocalDate.of(2026, 1, 3), "Prog II", 5);
        System.out.println(session);
    }
}
