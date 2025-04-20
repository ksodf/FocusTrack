package main.app;

import main.model.Task;
import main.model.TaskManager;
import main.sound.SoundManager;
import main.timer.*;
import main.ui.TimerUI;

public class App {
    public static void main(String[] args) throws Exception {
        System.out.println("TaskManager with Timer - Java OOP Final Project");
        System.out.println("==============================================");
        
        // Initialize sound manager
        SoundManager.getInstance();
        
        // Demo mode - create and show sample tasks
        if (args.length > 0 && args[0].equals("demo")) {
            runDemoMode();
        } else {
            // Interactive mode - start the UI
            TimerUI ui = new TimerUI();
            ui.start();
        }
    }
    
    private static void runDemoMode() throws InterruptedException {
        TaskManager manager = TaskManager.getInstance();
        TimerManager timerManager = TimerManager.getInstance();
        SoundManager soundManager = SoundManager.getInstance();
        
        // Add sample tasks
        Task reportTask = new Task("Finish report", "Due tomorrow");
        Task readingTask = new Task("Read chapter 4", "Pages 45–62");
        Task codingTask = new Task("Implement timer feature", "For the Java OOP project");
        
        manager.addTask(reportTask);
        manager.addTask(readingTask);
        manager.addTask(codingTask);
        
        // List all tasks
        System.out.println("\nCurrent tasks:");
        for (Task task : manager.getTasks()) {
            System.out.println(task);
        }
        
        // Demo timer with different strategies
        System.out.println("\nDemonstrating different timer strategies:");
        
        // Start a Pomodoro timer for report task
        System.out.println("\n1. Starting Pomodoro timer for 'Finish report' task");
        TimerStrategy pomodoroStrategy = new PomodoroTimer();
        System.out.println("Strategy: " + pomodoroStrategy.getName() + " - " + pomodoroStrategy.getDescription());
        
        TaskTimer reportTimer = timerManager.startTimer(reportTask, pomodoroStrategy);
        reportTimer.setListener(createDemoListener(reportTask, reportTimer, soundManager));
        
        // Simulate 5 seconds of timer running
        Thread.sleep(5000);
        System.out.println("\nPausing the timer...");
        timerManager.pauseTimer(reportTask);
        
        // Start a Short Break timer for reading task
        System.out.println("\n2. Starting Short Break timer for 'Read chapter 4' task");
        TimerStrategy shortBreakStrategy = new ShortBreakTimer();
        System.out.println("Strategy: " + shortBreakStrategy.getName() + " - " + shortBreakStrategy.getDescription());
        
        TaskTimer readingTimer = timerManager.startTimer(readingTask, shortBreakStrategy);
        readingTimer.setListener(createDemoListener(readingTask, readingTimer, soundManager));
        
        // Simulate 5 seconds of timer running
        Thread.sleep(5000);
        
        // Start a Long Break timer for coding task
        System.out.println("\n3. Starting Long Break timer for 'Implement timer feature' task");
        TimerStrategy longBreakStrategy = new LongBreakTimer();
        System.out.println("Strategy: " + longBreakStrategy.getName() + " - " + longBreakStrategy.getDescription());
        
        TaskTimer codingTimer = timerManager.startTimer(codingTask, longBreakStrategy);
        codingTimer.setListener(createDemoListener(codingTask, codingTimer, soundManager));
        
        // Simulate 5 seconds of timer running
        Thread.sleep(5000);
        
        // End demo
        timerManager.clearAllTimers();
        System.out.println("\nDemo completed. Timers canceled.");
        
        // Complete a task
        manager.completeTask(0);
        
        // Show updated task list and progress
        System.out.println("\nFinal task status:");
        for (Task task : manager.getTasks()) {
            System.out.println(task);
        }
        
        System.out.println("\nProgress: " + manager.getCompletionRate() + "%");
        System.out.println("\nRun the application without parameters to start in interactive mode.");
    }
    
    private static TaskTimer.TimerListener createDemoListener(final Task task, final TaskTimer timer, final SoundManager soundManager) {
        return new TaskTimer.TimerListener() {
            @Override
            public void onTick(int seconds) {
                System.out.print("\r" + task.getTitle() + " - " + timer.getPhaseText() + " phase: " + timer.getFormattedTime());
            }
            
            @Override
            public void onPhaseComplete(boolean wasWorkPhase) {
                System.out.println("\n" + (wasWorkPhase ? "Work" : "Break") + " phase complete!");
                if (wasWorkPhase) {
                    task.incrementPomodoros();
                    // Play work complete sound
                    soundManager.playSound(SoundManager.SoundType.WORK_COMPLETE);
                } else {
                    // Play break complete sound
                    soundManager.playSound(SoundManager.SoundType.BREAK_COMPLETE);
                }
            }
            
            @Override
            public void onTimerComplete() {
                System.out.println("\nTimer cycle completed!");
                // Play timer complete sound
                soundManager.playSound(SoundManager.SoundType.TIMER_COMPLETE);
            }
        };
    }
}