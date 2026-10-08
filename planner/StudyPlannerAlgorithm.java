package planner;

import model.StudySession;
import model.Task;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudyPlannerAlgorithm {

    private static final int MAX_PLANNING_DAYS = 90;
    private static final int MAX_DAILY_HOURS = 6;
    private static final int MAX_HOURS_PER_TASK_PER_DAY = 3;

    /*
     * Higher values move a larger share of study time toward the exam.
     * 0.0 gives an even distribution; 2.0 gives a noticeable gradual increase.
     */
    private static final double BACKLOADING_FACTOR = 2.0;

    private int getDailyStudyHours(LocalDate date, List<Task> activeTasks) {
        if (activeTasks.isEmpty()) {
            return 0;
        }

        double totalUrgency = 0.0;
        for (Task task : activeTasks) {
            long daysLeft = ChronoUnit.DAYS.between(date, task.getDeadline());
            if (daysLeft > 0) {
                totalUrgency += 1.0 / daysLeft;
            }
        }

        int dailyHours;
        if (totalUrgency > 0.3) {
            dailyHours = 6;
        } else if (totalUrgency >= 0.1) {
            dailyHours = 4;
        } else {
            dailyHours = 2;
        }

        if (date.getDayOfWeek().getValue() >= 6) {
            dailyHours = Math.max(1, dailyHours - 1);
        }

        return Math.min(dailyHours, MAX_DAILY_HOURS);
    }

    /**
     * Returns the cumulative weighted share of a task's study hours that
     * should have been scheduled by the end of the given date.
     *
     * Early days have lower weights and later days higher weights. Using a
     * cumulative target (rather than only a priority score) ensures that a
     * single-task plan also gets progressively heavier toward the exam.
     */
    private int getTargetCumulativeHours(
            Task task,
            LocalDate date,
            LocalDate today) {

        int totalHours = Math.max(0, task.getEstimatedHours());
        if (totalHours == 0 || !date.isBefore(task.getDeadline())) {
            return 0;
        }

        long totalStudyDays = ChronoUnit.DAYS.between(today, task.getDeadline());
        if (totalStudyDays <= 0) {
            return 0;
        }

        long elapsedStudyDays = ChronoUnit.DAYS.between(today, date) + 1;
        elapsedStudyDays = Math.max(0, Math.min(elapsedStudyDays, totalStudyDays));

        double totalWeight = 0.0;
        double elapsedWeight = 0.0;

        for (long day = 0; day < totalStudyDays; day++) {
            double progress = totalStudyDays <= 1
                    ? 1.0
                    : (double) day / (totalStudyDays - 1);

            double weight = 1.0 + BACKLOADING_FACTOR * progress * progress;
            totalWeight += weight;

            if (day < elapsedStudyDays) {
                elapsedWeight += weight;
            }
        }

        if (totalWeight == 0.0) {
            return 0;
        }

        return (int) Math.round(totalHours * elapsedWeight / totalWeight);
    }

    public List<StudySession> generatePlan(List<Task> tasks) {
        List<StudySession> plan = new ArrayList<>();
        if (tasks == null || tasks.isEmpty()) {
            return plan;
        }

        LocalDate today = LocalDate.now();
        Map<Task, Integer> remainingHours = new HashMap<>();
        Map<Task, Integer> plannedHours = new HashMap<>();
        Map<LocalDate, Map<Task, Integer>> allocationsByDate = new HashMap<>();

        for (Task task : tasks) {
            if (task != null) {
                remainingHours.put(task, Math.max(0, task.getEstimatedHours()));
                plannedHours.put(task, 0);
            }
        }

        for (int day = 0; day < MAX_PLANNING_DAYS; day++) {
            LocalDate currentDate = today.plusDays(day);
            List<Task> activeTasks = new ArrayList<>();

            for (Task task : tasks) {
                if (task != null
                        && remainingHours.getOrDefault(task, 0) > 0
                        && currentDate.isBefore(task.getDeadline())) {
                    activeTasks.add(task);
                }
            }

            if (activeTasks.isEmpty()) {
                continue;
            }

            int dailyBudget = getDailyStudyHours(currentDate, activeTasks);
            Map<Task, Integer> todaysAllocations = allocationsByDate.computeIfAbsent(
                    currentDate,
                    ignored -> new HashMap<>()
            );

            while (dailyBudget > 0) {
                Task selectedTask = null;
                int bestDeficit = 0;
                long bestDaysLeft = Long.MAX_VALUE;

                for (Task task : activeTasks) {
                    int remaining = remainingHours.getOrDefault(task, 0);
                    int alreadyToday = todaysAllocations.getOrDefault(task, 0);

                    if (remaining <= 0 || alreadyToday >= MAX_HOURS_PER_TASK_PER_DAY) {
                        continue;
                    }

                    int targetCumulative = getTargetCumulativeHours(
                            task,
                            currentDate,
                            today
                    );
                    int alreadyPlanned = plannedHours.getOrDefault(task, 0);
                    int deficit = targetCumulative - alreadyPlanned;

                    // Do not pull all of a task's future hours into early days.
                    if (deficit <= 0) {
                        continue;
                    }

                    long daysLeft = ChronoUnit.DAYS.between(
                            currentDate,
                            task.getDeadline()
                    );

                    if (deficit > bestDeficit
                            || (deficit == bestDeficit && daysLeft < bestDaysLeft)) {
                        selectedTask = task;
                        bestDeficit = deficit;
                        bestDaysLeft = daysLeft;
                    }
                }

                if (selectedTask == null) {
                    break;
                }

                todaysAllocations.merge(selectedTask, 1, Integer::sum);
                remainingHours.put(
                        selectedTask,
                        remainingHours.get(selectedTask) - 1
                );
                plannedHours.put(
                        selectedTask,
                        plannedHours.get(selectedTask) + 1
                );
                dailyBudget--;
            }
        }

        // Return one session per task per day, in chronological order.
        for (int day = 0; day < MAX_PLANNING_DAYS; day++) {
            LocalDate date = today.plusDays(day);
            Map<Task, Integer> dailyAllocations = allocationsByDate.get(date);

            if (dailyAllocations == null) {
                continue;
            }

            for (Task task : tasks) {
                if (task == null) {
                    continue;
                }

                int duration = dailyAllocations.getOrDefault(task, 0);
                if (duration > 0) {
                    plan.add(new StudySession(date, task.getName(), duration));
                }
            }
        }

        return plan;
    }
}
