package model;

import java.time.LocalDate;

public class StudySession {
    
    private LocalDate date;
    private String subject;
    private int duration;

    public StudySession(LocalDate date, String subject, int duration) {
        this.date = date;
        this.subject = subject;
        this.duration = duration;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getSubject() {
        return subject;
    }

    public int getDuration() {
        return duration;
    }

    @Override
    public String toString() {
        return date + " | " + subject + " | " + duration + "h";
    }
}
