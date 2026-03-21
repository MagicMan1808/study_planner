package planner;

import model.Task;
import model.StudySession;

import java.util.List;

public class PlanningService {
    
    private StudyPlannerAlgorithm algorithm;

    public PlanningService() {
        this.algorithm = new StudyPlannerAlgorithm();
    }

    public List<StudySession> createPlan(List<Task> tasks) {
        return algorithm.generatePlan(tasks);
    }
}
