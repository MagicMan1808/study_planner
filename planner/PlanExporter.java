package planner;

import model.StudySession;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class PlanExporter {
    public static void exportToCSV(List<StudySession> plan, String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            // Header
            writer.append("Date,Task,Hours\n");

            // Daten
            for (StudySession session : plan) {
                writer.append(session.getDate().toString()).append(",");
                writer.append(session.getSubject()).append(",");
                writer.append(String.valueOf(session.getDuration())).append("\n");
            }

            System.out.println("Export erfolgreich: " + filePath);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
