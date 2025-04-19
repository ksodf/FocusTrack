package main.model;

public class Task {
    private String title;
    private String description;
    private TaskStatus status;
    private int completedPomodoros; // Number of completed pomodoro cycles
    
    public Task(String title, String description) {
        this.title = title;
        this.description = description;
        this.status = TaskStatus.PENDING;
        this.completedPomodoros = 0;
    }

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }
    
    public int getCompletedPomodoros() {
        return completedPomodoros;
    }
    
    public void incrementPomodoros() {
        this.completedPomodoros++;
    }

    public void markCompleted() {
        this.status = TaskStatus.COMPLETED;
    }

    @Override
    public String toString() {
        return "Task{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                ", completedPomodoros=" + completedPomodoros +
                '}';
    }
}