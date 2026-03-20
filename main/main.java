// Kompilieren vom study_planner Ordner:
// javac -sourcepath . main/Main.java
// java main.Main

package main;

import model.Task;
import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        Task task = new Task("Prog I", LocalDate.of(2026, 7, 30), 4, 20);
        System.out.println(task);
    }
}
