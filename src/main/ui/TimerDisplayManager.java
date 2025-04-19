package main.ui;

import main.timer.TaskTimer;
import main.timer.TimerManager;

import java.util.List;

/**
 * Responsible for formatting and displaying timer information
 * Follows the Single Responsibility Principle
 */
public class TimerDisplayManager {
    private static final int CONSOLE_WIDTH = 80;
    
    /**
     * Formats all active timers into a readable string
     */
    public String formatTimerStatus(List<TaskTimer> timers) {
        StringBuilder summary = new StringBuilder();
        summary.append("\n===== Active Timers =====\n");
        
        if (timers.isEmpty()) {
            summary.append("No active timers.\n");
        } else {
            for (int i = 0; i < timers.size(); i++) {
                TaskTimer timer = timers.get(i);
                summary.append(i + 1).append(". ")
                       .append(formatSingleTimer(timer))
                       .append("\n");
            }
        }
        
        return summary.toString();
    }
    
    /**
     * Format a single timer's status
     */
    public String formatSingleTimer(TaskTimer timer) {
        return String.format("%s - %s: %s", 
                timer.getTask().getTitle(),
                timer.getPhaseText(),
                timer.getFormattedTime());
    }
    
    /**
     * Formats a progress bar for visual representation
     */
    public String formatProgressBar(int currentSeconds, int totalSeconds, int width) {
        if (totalSeconds <= 0) {
            totalSeconds = 1; // Prevent division by zero
        }
        int progress = (int) (((double) (totalSeconds - currentSeconds) / totalSeconds) * width);
        StringBuilder bar = new StringBuilder("[");
        
        for (int i = 0; i < width; i++) {
            if (i < progress) {
                bar.append("=");
            } else {
                bar.append(" ");
            }
        }
        
        bar.append("]");
        return bar.toString();
    }
    
    /**
     * Clear the console screen
     */
    public void clearConsole() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
    
    /**
     * Get a full summary of timer status from the TimerManager
     */
    public String getTimerStatusSummary(TimerManager timerManager) {
        return formatTimerStatus(timerManager.getAllActiveTimers());
    }
}