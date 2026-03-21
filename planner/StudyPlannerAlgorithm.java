package planner;

import model.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class StudyPlannerAlgorithm {

    private double calculatePriority(Task task, LocalDate today) {
        long daysLeft = java.time.temporal.ChronoUnit.DAYS.between(today, task.getDeadline());
        if (daysLeft <= 0) {
                return Double.MAX_VALUE;
            }
        return (double) task.getDifficulty() / daysLeft;
    }

    public List<StudySession> generatePlan(List<Task> tasks) {
        Map<Task, Integer> remainingHours = new HashMap<>();

        for (Task task : tasks) {
            remainingHours.put(task, task.getEstimatedHours());
        }

        List<StudySession> plan = new ArrayList<>();

        LocalDate today = LocalDate.now();
        int maxDays = 30;      // wie weit wir planen
        int dailyHours = 4;    // max Lernzeit pro Tag

        for (int day = 0; day < maxDays; day++) {

            LocalDate currentDate = today.plusDays(day);

            // 1. Filter: nur Tasks die noch relevant sind
            List<Task> activeTasks = new ArrayList<>();
            for (Task task : tasks) {
                if (!task.getDeadline().isBefore(currentDate) && remainingHours.get(task) > 0) {
                    activeTasks.add(task);
                }
            }
            if (activeTasks.isEmpty()) {
                continue;
            }

            // 2. Filter: sortiere nach Priorität
            activeTasks.sort((a, b) -> Double.compare(calculatePriority(b, currentDate), calculatePriority(a, currentDate)));

            int remainingHoursPerDay = dailyHours;

            double totalPriority = 0;

            for (Task task : activeTasks) {
                totalPriority += calculatePriority(task, currentDate);
            }

            // 3. Filter: verteile Zeit auf Tasks
            for (Task task : activeTasks) {
                if (remainingHoursPerDay <= 0) {
                    break;
                }

                double priority = calculatePriority(task, currentDate);

                int hours = (int) Math.round((priority / totalPriority) * dailyHours);
                int remaining = remainingHours.get(task);

                hours = Math.min(hours, remaining);
                hours = Math.min(hours, remainingHoursPerDay);
                hours = Math.max(1, hours);

                plan.add(new StudySession(currentDate, task.getName(), hours));

                remainingHours.put(task, remaining - hours);

                remainingHoursPerDay -= hours;
            }
        }

        return plan;
    }
}