package ua.kpi.webcrawler.model;

import java.time.LocalDateTime;

public class ScanSession {
    private Long id;
    private String profileName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;

    public ScanSession(Long id, String profileName, LocalDateTime startTime) {
        this.id = id;
        this.profileName = profileName;
        this.startTime = startTime;
        this.status = "RUNNING";
    }

    public Long getId() {
        return id;
    }

    public String getProfileName() {
        return profileName;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public String getStatus() {
        return status;
    }

    public void markPaused() {
        this.status = "PAUSED";
        // endTime stays null: the session can be resumed
    }

    public void markResumed() {
        this.status = "RUNNING";
        this.endTime = null;
    }

    public void markCompleted() {
        this.status = "COMPLETED";
        this.endTime = LocalDateTime.now();
    }

    public void markFailed() {
        this.status = "FAILED";
        this.endTime = LocalDateTime.now();
    }
}
