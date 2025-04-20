package main.timer;

import main.model.Task;

import java.util.Timer;
import java.util.TimerTask;

public class TaskTimer {
    private Task task;
    private TimerStrategy strategy;
    private Timer timer;
    private boolean isRunning;
    private boolean isWorkPhase;
    private int remainingSeconds;
    private TimerListener listener;
    private boolean isConsoleMode; // Flag to control console output

    public TaskTimer(Task task, TimerStrategy strategy) {
        this.task = task;
        this.strategy = strategy;
        this.isRunning = false;
        this.isWorkPhase = true;
        this.remainingSeconds = strategy.getTotalWorkSeconds(); // Use the new helper method
        this.isConsoleMode = false;
    }

    public interface TimerListener {
        void onTick(int seconds);
        void onPhaseComplete(boolean wasWorkPhase);
        void onTimerComplete();
    }

    public void setListener(TimerListener listener) {
        this.listener = listener;
    }

    public void setConsoleMode(boolean consoleMode) {
        this.isConsoleMode = consoleMode;
    }

    public void start() {
        if (isRunning) return;
        
        isRunning = true;
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (remainingSeconds > 0) {
                    remainingSeconds--;
                    if (listener != null) {
                        listener.onTick(remainingSeconds);
                    }
                } else {
                    // Phase complete
                    if (listener != null) {
                        listener.onPhaseComplete(isWorkPhase);
                    }
                    
                    // Switch phases
                    isWorkPhase = !isWorkPhase;
                    
                    // If work phase just ended and there's a break, start break
                    if (!isWorkPhase) {
                        remainingSeconds = strategy.getTotalBreakSeconds(); // Use the new helper method
                    } else {
                        // If break just ended, reset to work phase
                        remainingSeconds = strategy.getTotalWorkSeconds(); // Use the new helper method
                        if (listener != null) {
                            listener.onTimerComplete();
                        }
                        // Auto-cancel after completing a full work-break cycle
                        cancel();
                    }
                }
            }
        }, 1000, 1000); // Update every second
    }

    public void pause() {
        if (!isRunning) return;
        
        isRunning = false;
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public void resume() {
        if (isRunning) return;
        
        start();
    }

    public void cancel() {
        pause();
        isWorkPhase = true;
        remainingSeconds = strategy.getTotalWorkSeconds(); 
    }

    // In TaskTimer.java, add a reset method
    public void reset() {
        boolean wasRunning = isRunning;
        
        if (wasRunning) {
            pause();
        }
        
        // Reset to the beginning of the current phase
        if (isWorkPhase) {
            remainingSeconds = strategy.getTotalWorkSeconds();
        } else {
            remainingSeconds = strategy.getTotalBreakSeconds();
        }
        
        if (wasRunning) {
            resume();
        }
        
        if (listener != null) {
            listener.onTick(remainingSeconds);
        }
    }

    public void changeStrategy(TimerStrategy newStrategy) {
        boolean wasRunning = isRunning;
        
        if (wasRunning) {
            pause();
        }
        
        this.strategy = newStrategy;
        this.isWorkPhase = true;
        this.remainingSeconds = newStrategy.getTotalWorkSeconds(); 
        
        if (wasRunning) {
            resume();
        }
    }

    public Task getTask() {
        return task;
    }

    public TimerStrategy getStrategy() {
        return strategy;
    }

    public boolean isRunning() {
        return isRunning;
    }
    
    public boolean isConsoleMode() {
        return isConsoleMode;
    }

    public boolean isWorkPhase() {
        return isWorkPhase;
    }

    public int getRemainingSeconds() {
        return remainingSeconds;
    }
    
    public String getFormattedTime() {
        int minutes = remainingSeconds / 60;
        int seconds = remainingSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
    
    public String getPhaseText() {
        return isWorkPhase ? "Work" : "Break";
    }
    
    public String getStatusDisplay() {
        return task.getTitle() + " - " + getPhaseText() + ": " + getFormattedTime();
    }
}