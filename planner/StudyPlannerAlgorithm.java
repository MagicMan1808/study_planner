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
     * Controls how strongly study time is weighted toward the exam.
     * 0.0 means no additional back-loading; 2.0 gives a noticeable,
     * but not extreme, increase in priority as the deadline approaches.
     */
    private static final double BACKLOADING_FACTOR = 2.0;

    /**
     * Determines the total study-time budget for a day.
     * The budget increases as the active deadlines get closer.
     */
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

        // Slightly lower workload on weekends.
        if (date.getDayOfWeek().getValue() >= 6) {
            dailyHours = Math.max(1, dailyHours - 1);
        }

        return Math.min(dailyHours, MAX_DAILY_HOURS);
    }

    /**
     * Calculates a task's priority for one hour of study on the given date.
     * More remaining work and a closer deadline increase priority. The
     * back-loading factor gradually raises priority later in the study window.
     */
    private double calculatePriority(
            Task task,
            LocalDate date,
            int remainingHours,
            int alreadyScheduledToday) {

        long daysAvailable = ChronoUnit.DAYS.between(date, task.getDeadline());

        if (remainingHours <= 0 || daysAvailable <= 0
                || alreadyScheduledToday >= MAX_HOURS_PER_TASK_PER_DAY) {
            return -1.0;
        }

        long totalStudyDays = ChronoUnit.DAYS.between(
                dateForPlanningStart(date),
                task.getDeadline()
        );

        // Progress is measured from today, so it remains bounded from 0 to 1.
        long totalDaysFromToday = ChronoUnit.DAYS.between(
                LocalDate.now(),
                task.getDeadline()
        );
        if (totalDaysFromToday <= 0) {
            return -1.0;
        }

        long elapsedDays = ChronoUnit.DAYS.between(LocalDate.now(), date);
        double progress = Math.max(
                0.0,
                Math.min(1.0, (double) elapsedDays / totalDaysFromToday)
        );

        double backloadingWeight = 1.0
                + BACKLOADING_FACTOR * progress * progress;
        double workloadPressure = (double) remainingHours / daysAvailable;
        double difficultyWeight = 1.0 + Math.max(0, task.getDifficulty()) / 20.0;

        return workloadPressure * backloadingWeight * difficultyWeight;
    }

    /*
     * Kept as a separate method to make the start of the planning window
     * explicit and to avoid embedding date logic in the priority formula.
     */
    private LocalDate dateForPlanningStart(LocalDate date) {
        return LocalDate.now().isAfter(date) ? date : LocalDate.now();
    }

    /**
     * Creates a plan with a progressively stronger focus on upcoming exams.
     *
     * Each task is scheduled only before its deadline. The planner respects
     * the daily total limit and the per-task daily limit. If the total workload
     * cannot fit within those limits, some estimated hours may remain unscheduled.
     */
    public List<StudySession> generatePlan(List<Task> tasks) {
        List<StudySession> plan = new ArrayList<>();

        if (tasks == null || tasks.isEmpty()) {
            return plan;
        }

        LocalDate today = LocalDate.now();
        Map<Task, Integer> remainingHours = new HashMap<>();

        for (Task task : tasks) {
            if (task != null) {
                remainingHours.put(task, Math.max(0, task.getEstimatedHours()));
            }
        }

        // Store allocations separately so each task has one session per day,
        // rather than several duplicate one-hour entries.
        Map<LocalDate, Map<Task, Integer>> allocationsByDate = new HashMap<>();

        for (int day = 0; day < MAX_PLANNING_DAYS; day++) {
            LocalDate currentDate = today.plusDays(day);
            List<Task> activeTasks = new ArrayList<>();

            for (Task task : tasks) {
                if (task == null || remainingHours.getOrDefault(task, 0) <= 0) {
                    continue;
                }

                // Do not schedule study time on or after the exam date.
                if (currentDate.isBefore(task.getDeadline())) {
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
                double bestPriority = -1.0;

                for (Task task : activeTasks) {
                    int remaining = remainingHours.getOrDefault(task, 0);
                    int alreadyToday = todaysAllocations.getOrDefault(task, 0);

                    double priority = calculatePriority(
                            task,
                            currentDate,
                            remaining,
                            alreadyToday
                    );

                    if (priority > bestPriority) {
                        bestPriority = priority;
                        selectedTask = task;
                    }
                }

                if (selectedTask == null || bestPriority < 0.0) {
                    break;
                }

                todaysAllocations.merge(selectedTask, 1, Integer::sum);
                remainingHours.put(
                        selectedTask,
                        remainingHours.get(selectedTask) - 1
                );
                dailyBudget--;
            }
        }

        // Convert the allocations to displayable sessions in date order.
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
