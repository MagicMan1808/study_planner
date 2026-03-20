package planner;

import model.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class StudyPlannerAlgorithm {

    public List<StudySession> generatePlan(List<Task> tasks) {
        List<StudySession> plan = new ArrayList<>();

        LocalDate today = LocalDate.now();

        for (Task task : tasks) {
            long daysLeft = java.time.temporal.ChronoUnit.DAYS.between(today, task.getDeadline());

            if (daysLeft <= 0) {
                continue;
            }

            int hoursPerDay = task.getEstimatedHours() / (int) daysLeft;

            for (int i = 0; i < daysLeft; i++) {
                LocalDate date = today.plusDays(i);

                plan.add(new StudySession(date, task.getName(), hoursPerDay));
            }
        }

        return plan;
    }
}