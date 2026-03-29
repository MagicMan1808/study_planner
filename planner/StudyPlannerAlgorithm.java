package planner;

import model.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class StudyPlannerAlgorithm {

    private static final int MAX_PLANNING_DAYS = 90;
    private static final double URGENCY_THRESHOLD = 0.3;

    private double calculatePriority(Task task, LocalDate today) {
        long daysLeft = ChronoUnit.DAYS.between(today, task.getDeadline());
        
        if (daysLeft <= 0) {
            return Double.MAX_VALUE;
        }

        double urgency = 1.0;
        double difficultyFactor = task.getDifficulty() / 10.0;

        return urgency * (1 + difficultyFactor);
    }

    private int getDailyStudyHours(LocalDate date, List<Task> activeTasks) {
        if (activeTasks.isEmpty()) {
            return 0;
        }

        double totalUrgency = 0;
        for (Task task : activeTasks) {
            long daysLeft = ChronoUnit.DAYS.between(date, task.getDeadline());
            if (daysLeft > 0) {
                totalUrgency += 1.0 / daysLeft;
            }
        }

        int baseHours = 4;
        if (totalUrgency > URGENCY_THRESHOLD) {
            baseHours = 6;
        } else if (totalUrgency < 0.1) {
            baseHours = 2;
        }

        if (date.getDayOfWeek().getValue() >= 6) {
            baseHours = Math.max(1, baseHours - 1);
        }

        return baseHours;
    }

    private double calculateOptimalStudyInterval(Task task, LocalDate today) {
        long daysLeft = ChronoUnit.DAYS.between(today, task.getDeadline());
        int totalHours = task.getEstimatedHours();

        double urgency = 1.0 / Math.max(1, daysLeft);
        return urgency * totalHours;
    }

    public List<StudySession> generatePlan(List<Task> tasks) {
        Map<Task, Integer> remainingHours = new HashMap<>();
        Map<Task, LocalDate> lastStudyDate = new HashMap<>();
        Map<Task, LocalDate> startDates = new HashMap<>();

        for (Task task : tasks) {
            remainingHours.put(task, task.getEstimatedHours());
            lastStudyDate.put(task, LocalDate.now().minusDays(1));
        }

        List<StudySession> plan = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (Task task : tasks) {
            int baseStart = 7;
            int extra = task.getEstimatedHours() / 5;
            int startOffset = Math.min(14, baseStart + extra); // 7-14 Tage vor der Klausur starten

            LocalDate startDate = task.getDeadline().minusDays(startOffset);

            if (startDate.isAfter(today.plusDays(MAX_PLANNING_DAYS))) {
                startDate = today.plusDays(MAX_PLANNING_DAYS + 1);
            }

            startDates.put(task, startDate);
        }

        for (int day = 0; day < MAX_PLANNING_DAYS; day++) {
            LocalDate currentDate = today.plusDays(day);

            List<Task> activeTasks = new ArrayList<>();
            for (Task  task : tasks) {
                LocalDate startDate = startDates.get(task);
                if (!task.getDeadline().isBefore(currentDate) &&
                    !currentDate.isBefore(startDate)) {

                    activeTasks.add(task);
                }
            }

            if (activeTasks.isEmpty()) {
                continue;
            }

            Task nearestExam = null;
            long minDays = Long.MAX_VALUE;

            for (Task task : activeTasks) {
                long daysLeft = ChronoUnit.DAYS.between(currentDate, task.getDeadline());

                if (daysLeft >= 0 && daysLeft < minDays) {
                    minDays = daysLeft;
                    nearestExam = task;
                }
            }

            if (nearestExam != null) {
                long daysLeft = ChronoUnit.DAYS.between(currentDate, nearestExam.getDeadline());

                if (daysLeft <= 3) {
                    boolean hasCloseNext = false;

                    for (Task other : activeTasks) {
                        if (other == nearestExam) {
                            continue;
                        }

                        long diff = ChronoUnit.DAYS.between(nearestExam.getDeadline(), other.getDeadline());

                        if (diff > 0 && diff <= 3) {
                            hasCloseNext = true;
                            break;
                        }
                    }

                    if (!hasCloseNext) {
                        activeTasks.clear();
                        activeTasks.add(nearestExam);
                    }
                }
            }

            activeTasks.sort((a, b) -> {
                double priorityA = calculatePriority(a, currentDate);
                double priorityB = calculatePriority(b, currentDate);

                long daysSinceLastA = ChronoUnit.DAYS.between(lastStudyDate.get(a), currentDate);
                long daysSinceLastB = ChronoUnit.DAYS.between(lastStudyDate.get(b), currentDate);

                priorityA *= (1 + daysSinceLastA * 0.1);
                priorityB *= (1 + daysSinceLastB * 0.1);

                return Double.compare(priorityB, priorityA);
            });

            int dailyHours = getDailyStudyHours(currentDate, activeTasks);
            int remainingHoursPerDay = dailyHours;

            for (Task task : activeTasks) {
                if (remainingHoursPerDay <= 0) {
                    break;
                }

                double optimalHours = calculateOptimalStudyInterval(task, currentDate);

                int remaining = remainingHours.get(task);
                int recommendedHours;
                long daysLeft = ChronoUnit.DAYS.between(currentDate, task.getDeadline());

                if (remaining > 0) {
                    recommendedHours = (int) Math.max(1, Math.min(optimalHours, remaining));
                } else {

                    if (daysLeft <= 3) {
                        recommendedHours = 5;
                    } else {
                        recommendedHours = 1; // 1h Wiederholung pro Tag
                    }
                }

                if (daysLeft <= 3) {
                    recommendedHours = Math.min(recommendedHours, remainingHoursPerDay);
                } else {
                    recommendedHours = Math.min(recommendedHours, 5);
                }
                

                if (recommendedHours >= 1) {
                    plan.add(new StudySession(currentDate, task.getName(), recommendedHours));
                    remainingHours.put(task, remainingHours.get(task) - recommendedHours);
                    remainingHoursPerDay -= recommendedHours;
                    lastStudyDate.put(task, currentDate);
                }
            }

            boolean allCompleted = remainingHours.values().stream().allMatch(h -> h == 0);

            if (allCompleted) {
                break;
            }
        }
        return plan;
    }
}