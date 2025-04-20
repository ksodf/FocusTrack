package main.timer;

public interface TimerStrategy {
    int getWorkDuration(); 
    int getBreakDuration(); 
    String getName();
    String getDescription();
    
    // Default implementations for backward compatibility
    default int getWorkDurationSeconds() {
        return 0;
    }
    
    default int getBreakDurationSeconds() {
        return 0;
    }
    
    // Helper methods to get total seconds
    default int getTotalWorkSeconds() {
        return getWorkDuration() * 60 + getWorkDurationSeconds();
    }
    
    default int getTotalBreakSeconds() {
        return getBreakDuration() * 60 + getBreakDurationSeconds();
    }
}