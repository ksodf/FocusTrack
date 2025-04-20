package main.timer;

import main.model.Task;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

// Another Singleton pattern instance
public class TimerManager {
    private static TimerManager instance;
    private Map<Task, TaskTimer> activeTimers;
    private boolean timerUpdateDisplayEnabled = true;
    
    private TimerManager() {
        activeTimers = new HashMap<>();
    }
    
    public static TimerManager getInstance() {
        if (instance == null) {
            instance = new TimerManager();
        }
        return instance;
    }
    
    public TaskTimer startTimer(Task task, TimerStrategy strategy) {
        // Cancel existing timer for this task if any
        if (activeTimers.containsKey(task)) {
            activeTimers.get(task).cancel();
        }
        
        TaskTimer timer = new TaskTimer(task, strategy);
        activeTimers.put(task, timer);
        timer.start();
        
        return timer;
    }
    
    public void pauseTimer(Task task) {
        if (activeTimers.containsKey(task)) {
            activeTimers.get(task).pause();
        }
    }
    
    public void resumeTimer(Task task) {
        if (activeTimers.containsKey(task)) {
            activeTimers.get(task).resume();
        }
    }
    
    public void cancelTimer(Task task) {
        if (activeTimers.containsKey(task)) {
            activeTimers.get(task).cancel();
            activeTimers.remove(task);
        }
    }

    public void resetTimer(Task task) {
        if (activeTimers.containsKey(task)) {
            activeTimers.get(task).reset();
        }
    }
    
    public TaskTimer getTimerForTask(Task task) {
        return activeTimers.get(task);
    }
    
    public boolean hasActiveTimer(Task task) {
        return activeTimers.containsKey(task) && activeTimers.get(task).isRunning();
    }
    
    public void clearAllTimers() {
        for (TaskTimer timer : activeTimers.values()) {
            timer.cancel();
        }
        activeTimers.clear();
    }
    
    public List<TaskTimer> getAllActiveTimers() {
        List<TaskTimer> result = new ArrayList<>();
        for (TaskTimer timer : activeTimers.values()) {
            if (timer.isRunning()) {
                result.add(timer);
            }
        }
        return result;
    }
    
    public void setTimerUpdateDisplayEnabled(boolean enabled) {
        this.timerUpdateDisplayEnabled = enabled;
    }
    
    public boolean isTimerUpdateDisplayEnabled() {
        return timerUpdateDisplayEnabled;
    }
    
    public String getTimerStatusSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("\n===== Active Timers =====\n");
        
        List<TaskTimer> timers = getAllActiveTimers();
        if (timers.isEmpty()) {
            summary.append("No active timers.\n");
        } else {
            for (int i = 0; i < timers.size(); i++) {
                TaskTimer timer = timers.get(i);
                summary.append(i + 1).append(". ")
                       .append(timer.getStatusDisplay())
                       .append("\n");
            }
        }
        
        return summary.toString();
    }
}