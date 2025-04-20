package main.ui;

import main.model.Task;
import main.model.TaskManager;
import main.sound.SoundManager;
import main.timer.*;

import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * UI class for the timer application
 * Separates UI concerns from the timer logic
 */
public class TimerUI {
    private final TaskManager taskManager;
    private final TimerManager timerManager;
    private final TimerDisplayManager displayManager;
    private final SoundManager soundManager;
    private final Scanner scanner;
    private final ScheduledExecutorService timerDisplayExecutor;
    private boolean running;
    
    public TimerUI() {
        this.taskManager = TaskManager.getInstance();
        this.timerManager = TimerManager.getInstance();
        this.displayManager = new TimerDisplayManager();
        this.soundManager = SoundManager.getInstance();
        this.scanner = new Scanner(System.in);
        this.timerDisplayExecutor = Executors.newSingleThreadScheduledExecutor();
        this.running = false;
    }
    
    public void start() {
        running = true;
        
        // Start a separate thread for displaying timer updates
        startTimerDisplayUpdater();
        
        while (running) {
            // Display menu
            displayMenu();
            
            // Process user input
            int choice = getIntInput();
            
            // Temporarily disable timer display updates during menu interaction
            timerManager.setTimerUpdateDisplayEnabled(false);
            
            processMenuChoice(choice);
            
            // Re-enable timer display updates
            timerManager.setTimerUpdateDisplayEnabled(true);
        }
        
        // Clean up
        shutdown();
    }
    
    private void processMenuChoice(int choice) {
        switch (choice) {
            case 1:
                addTask();
                break;
            case 2:
                listTasks();
                waitForEnter();
                break;
            case 3:
                startTimer();
                break;
            case 4:
                pauseTimer();
                break;
            case 5:
                resumeTimer();
                break;
            case 6:
                cancelTimer();
                break;
            case 7:
                completeTask();
                break;
            case 8:
                soundSettings();
                break;
            case 9:
                running = false;
                System.out.println("Exiting application...");
                break;
            default:
                System.out.println("Invalid option. Please try again.");
                soundManager.playSound(SoundManager.SoundType.ERROR);
                waitForEnter();
        }
    }
    
    private void displayMenu() {
        displayManager.clearConsole();
        System.out.println("\n===== Task Timer Application =====");
        System.out.println("1. Add Task");
        System.out.println("2. List Tasks");
        System.out.println("3. Start Timer for Task");
        System.out.println("4. Pause Timer");
        System.out.println("5. Resume Timer");
        System.out.println("6. Cancel Timer");
        System.out.println("7. Mark Task as Completed");
        System.out.println("8. Sound Settings");
        System.out.println("9. Exit");
        
        // Display sound status
        System.out.println("\nSound: " + (soundManager.isSoundEnabled() ? "ON" : "OFF") + 
                          " (Volume: " + Math.round(soundManager.getVolume() * 100) + "%)");
        
        // Display timer status if any active timers
        if (!timerManager.getAllActiveTimers().isEmpty()) {
            System.out.println(displayManager.getTimerStatusSummary(timerManager));
        }
        
        System.out.print("Choose an option: ");
    }
    
    private void startTimerDisplayUpdater() {
        timerDisplayExecutor.scheduleAtFixedRate(() -> {
            if (timerManager.isTimerUpdateDisplayEnabled() && !timerManager.getAllActiveTimers().isEmpty()) {
                displayManager.clearConsole();
                displayMenu();
            }
        }, 1, 1, TimeUnit.SECONDS);
    }
    
    private void addTask() {
        System.out.print("Enter task title: ");
        String title = scanner.nextLine();
        
        System.out.print("Enter task description: ");
        String description = scanner.nextLine();
        
        Task task = new Task(title, description);
        taskManager.addTask(task);
        
        System.out.println("Task added successfully!");
        waitForEnter();
    }
    
    private void listTasks() {
        System.out.println("\n===== Task List =====");
        
        if (taskManager.getTasks().isEmpty()) {
            System.out.println("No tasks available.");
            return;
        }
        
        for (int i = 0; i < taskManager.getTasks().size(); i++) {
            Task task = taskManager.getTasks().get(i);
            String timerStatus = timerManager.hasActiveTimer(task) ? " [⏱️ Active]" : "";
            System.out.println(i + ": " + task.getTitle() + " - " + task.getStatus() + timerStatus);
            if (task.getCompletedPomodoros() > 0) {
                System.out.println("   Completed pomodoros: " + task.getCompletedPomodoros());
            }
        }
        
        System.out.println("\nProgress: " + taskManager.getCompletionRate() + "%");
    }
    
    private void soundSettings() {
        boolean settingsActive = true;
        
        while (settingsActive) {
            displayManager.clearConsole();
            System.out.println("\n===== Sound Settings =====");
            System.out.println("1. " + (soundManager.isSoundEnabled() ? "Disable" : "Enable") + " Sound");
            System.out.println("2. Increase Volume");
            System.out.println("3. Decrease Volume");
            System.out.println("4. Test Sounds");
            System.out.println("5. Back to Main Menu");
            System.out.println("\nCurrent status: Sound " + (soundManager.isSoundEnabled() ? "ON" : "OFF") + 
                              ", Volume: " + Math.round(soundManager.getVolume() * 100) + "%");
            System.out.print("\nChoose an option: ");
            
            int choice = getIntInput();
            
            switch (choice) {
                case 1:
                    soundManager.setSoundEnabled(!soundManager.isSoundEnabled());
                    System.out.println("Sound " + (soundManager.isSoundEnabled() ? "enabled" : "disabled"));
                    break;
                case 2:
                    soundManager.setVolume(soundManager.getVolume() + 0.1f);
                    System.out.println("Volume increased to " + Math.round(soundManager.getVolume() * 100) + "%");
                    break;
                case 3:
                    soundManager.setVolume(soundManager.getVolume() - 0.1f);
                    System.out.println("Volume decreased to " + Math.round(soundManager.getVolume() * 100) + "%");
                    break;
                case 4:
                    testSounds();
                    break;
                case 5:
                    settingsActive = false;
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
            
            if (settingsActive && choice != 5) {
                waitForEnter();
            }
        }
    }
    
    private void testSounds() {
        System.out.println("\nPlaying test sounds...");
        
        System.out.println("Work complete sound:");
        soundManager.playSound(SoundManager.SoundType.WORK_COMPLETE);
        try { Thread.sleep(1500); } catch (InterruptedException e) { }
        
        System.out.println("Break complete sound:");
        soundManager.playSound(SoundManager.SoundType.BREAK_COMPLETE);
        try { Thread.sleep(1500); } catch (InterruptedException e) { }
        
        System.out.println("Timer complete sound:");
        soundManager.playSound(SoundManager.SoundType.TIMER_COMPLETE);
        try { Thread.sleep(1500); } catch (InterruptedException e) { }
        
        System.out.println("Error sound:");
        soundManager.playSound(SoundManager.SoundType.ERROR);
    }
    
    private void startTimer() {
        // Show task list
        listTasks();
        
        System.out.print("Enter task number to start timer: ");
        int taskIndex = getIntInput();
        
        if (!isValidTaskIndex(taskIndex)) {
            return;
        }
        
        Task task = taskManager.getTasks().get(taskIndex);
        TimerStrategy strategy = selectTimerStrategy();
        
        if (strategy == null) {
            System.out.println("Timer setup cancelled.");
            waitForEnter();
            return;
        }
        
        // Start the timer
        final TaskTimer timer = timerManager.startTimer(task, strategy);
        setupTimerListener(timer, task);
        
        System.out.println("Timer started for task: " + task.getTitle());
        System.out.println("Strategy: " + strategy.getName() + " - " + strategy.getDescription());
        waitForEnter();
    }
    
    private TimerStrategy selectTimerStrategy() {
        System.out.println("\nSelect timer strategy:");
        System.out.println("1. Pomodoro (25-5)");
        System.out.println("2. Short Break (15-3)");
        System.out.println("3. Long Break (50-10)");
        System.out.println("4. Custom Timer");
        System.out.print("Choose a strategy (or 0 to cancel): ");
        
        int strategyChoice = getIntInput();
        
        if (strategyChoice == 0) {
            return null;
        }
        
        if (strategyChoice == 4) {
            return createCustomStrategy();
        }
        
        TimerStrategyType type;
        switch (strategyChoice) {
            case 1:
                type = TimerStrategyType.POMODORO;
                break;
            case 2:
                type = TimerStrategyType.SHORT_BREAK;
                break;
            case 3:
                type = TimerStrategyType.LONG_BREAK;
                break;
            default:
                System.out.println("Invalid strategy, using Pomodoro by default.");
                soundManager.playSound(SoundManager.SoundType.ERROR);
                type = TimerStrategyType.POMODORO;
        }
        
        return TimerStrategyFactory.createStrategy(type);
    }
    
    private TimerStrategy createCustomStrategy() {
        System.out.println("\n=== Custom Timer Setup ===");
        
        System.out.print("Enter a name for this timer: ");
        String name = scanner.nextLine();
        
        System.out.print("Enter a description: ");
        String description = scanner.nextLine();
        
        System.out.print("Enter work duration (minutes): ");
        int workDuration = getPositiveIntInput(25); // Default to 25 if invalid
        
        System.out.print("Enter work duration (minutes): ");
        int workSeconds = getPositiveIntInput(0);
        
        System.out.print("Enter break duration (minutes): ");
        int breakDuration = getPositiveIntInput(5); // Default to 5 if invalid

        System.out.print("Enter work duration (minutes): ");
        int breakSeconds = getPositiveIntInput(0);
        
        return TimerStrategyFactory.createCustomStrategy(name, description, workDuration, workSeconds, breakDuration, breakSeconds);
    }
    
    private void setupTimerListener(final TaskTimer timer, final Task task) {
        timer.setListener(new TaskTimer.TimerListener() {
            @Override
            public void onTick(int seconds) {
                // Nothing to do here - updates handled by display updater
            }
            
            @Override
            public void onPhaseComplete(boolean wasWorkPhase) {
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
                // Timer cycle completed - play completion sound
                soundManager.playSound(SoundManager.SoundType.TIMER_COMPLETE);
            }
        });
    }
    
    private void pauseTimer() {
        // Show task list
        listTasks();
        
        System.out.print("Enter task number to pause timer: ");
        int taskIndex = getIntInput();
        
        if (!isValidTaskIndex(taskIndex)) {
            return;
        }
        
        Task task = taskManager.getTasks().get(taskIndex);
        
        if (!timerManager.hasActiveTimer(task)) {
            System.out.println("No active timer for this task.");
            soundManager.playSound(SoundManager.SoundType.ERROR);
            waitForEnter();
            return;
        }
        
        timerManager.pauseTimer(task);
        System.out.println("Timer paused for task: " + task.getTitle());
        waitForEnter();
    }
    
    private void resumeTimer() {
        // Show task list
        listTasks();
        
        System.out.print("Enter task number to resume timer: ");
        int taskIndex = getIntInput();
        
        if (!isValidTaskIndex(taskIndex)) {
            return;
        }
        
        Task task = taskManager.getTasks().get(taskIndex);
        TaskTimer timer = timerManager.getTimerForTask(task);
        
        if (timer == null || timer.isRunning()) {
            System.out.println("No paused timer for this task.");
            soundManager.playSound(SoundManager.SoundType.ERROR);
            waitForEnter();
            return;
        }
        
        timerManager.resumeTimer(task);
        System.out.println("Timer resumed for task: " + task.getTitle());
        waitForEnter();
    }
    
    private void cancelTimer() {
        // Show task list
        listTasks();
        
        System.out.print("Enter task number to cancel timer: ");
        int taskIndex = getIntInput();
        
        if (!isValidTaskIndex(taskIndex)) {
            return;
        }
        
        Task task = taskManager.getTasks().get(taskIndex);
        
        if (timerManager.getTimerForTask(task) == null) {
            System.out.println("No timer for this task.");
            soundManager.playSound(SoundManager.SoundType.ERROR);
            waitForEnter();
            return;
        }
        
        timerManager.cancelTimer(task);
        System.out.println("Timer cancelled for task: " + task.getTitle());
        waitForEnter();
    }
    
    private void completeTask() {
        // Show task list
        listTasks();
        
        System.out.print("Enter task number to mark as completed: ");
        int taskIndex = getIntInput();
        
        if (!isValidTaskIndex(taskIndex)) {
            return;
        }
        
        taskManager.completeTask(taskIndex);
        System.out.println("Task marked as completed.");
        
        // Cancel any timers for this task
        Task task = taskManager.getTasks().get(taskIndex);
        if (timerManager.getTimerForTask(task) != null) {
            timerManager.cancelTimer(task);
        }
        
        waitForEnter();
    }
    
    private boolean isValidTaskIndex(int taskIndex) {
        if (taskIndex < 0 || taskIndex >= taskManager.getTasks().size()) {
            System.out.println("Invalid task number.");
            soundManager.playSound(SoundManager.SoundType.ERROR);
            waitForEnter();
            return false;
        }
        return true;
    }
    
    private int getIntInput() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            soundManager.playSound(SoundManager.SoundType.ERROR);
            return -1;
        }
    }
    
    private int getPositiveIntInput(int defaultValue) {
        try {
            int value = Integer.parseInt(scanner.nextLine().trim());
            return value > 0 ? value : defaultValue;
        } catch (NumberFormatException e) {
            soundManager.playSound(SoundManager.SoundType.ERROR);
            return defaultValue;
        }
    }
    
    private void waitForEnter() {
        System.out.println("Press Enter to continue...");
        scanner.nextLine();
    }
    
    private void shutdown() {
        timerDisplayExecutor.shutdown();
        try {
            if (!timerDisplayExecutor.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                timerDisplayExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            timerDisplayExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
        
        timerManager.clearAllTimers();
        soundManager.cleanup();
        scanner.close();
    }
    
    public static void main(String[] args) {
        TimerUI ui = new TimerUI();
        ui.start();
    }
}