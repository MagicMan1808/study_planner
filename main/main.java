// Kompilieren vom study_planner Ordner:
// javac -sourcepath . main/Main.java
// java main.Main

package main;

import model.Task;
import model.StudySession;
import planner.StudyPlannerAlgorithm;


import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;



public class Main {
    public static void main(String[] args) {
        Task task = new Task("Prog I", LocalDate.of(2026, 7, 30), 4, 20);
        System.out.println(task);

        StudySession session = new StudySession(LocalDate.of(2026, 1, 3), "Prog II", 5);
        System.out.println(session);

        List<Task> tasks = new ArrayList<>();
        tasks.add(new Task("Mathe", LocalDate.now().plusDays(5), 5, 10));
        tasks.add(new Task("Prog", LocalDate.now().plusDays(3),  3, 7));

        StudyPlannerAlgorithm algo = new StudyPlannerAlgorithm();
        List<StudySession> plan = algo.generatePlan(tasks);

        plan.forEach(System.out::println);
    }
}
