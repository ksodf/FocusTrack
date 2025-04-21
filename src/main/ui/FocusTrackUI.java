package main.ui;

import main.model.Task;
import main.model.TaskManager;
import main.model.TaskStatus;
import main.sound.SoundManager;
import main.timer.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

/**
 * Swing UI for the FocusTrack application
 */
public class FocusTrackUI extends JFrame {
    private static final String APP_TITLE = "FocusTrack";
    private static final int WIDTH = 450;
    private static final int HEIGHT = 600;
    
    // Main components
    private TaskManager taskManager;
    private TimerManager timerManager;
    private SoundManager soundManager;
    
    // UI components
    private JLabel timerLabel;
    private JProgressBar progressBar;
    private JButton startButton;
    private JButton pauseButton;
    private JButton addTaskButton;
    private JButton completeTaskButton;
    private JButton resetButton;
    private JButton changeStrategyButton;
    private JPanel taskListPanel;
    private Task currentTask;
    private TimerStrategy currentStrategy;
    private TaskTimer activeTimer;
    
    public FocusTrackUI() {
        super(APP_TITLE);
        
        // Initialize managers
        taskManager = TaskManager.getInstance();
        timerManager = TimerManager.getInstance();
        soundManager = SoundManager.getInstance();
        
        // Default timer strategy
        currentStrategy = new PomodoroTimer();
        
        initUI();
        
        // Window closing handler
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                shutdown();
                System.exit(0);
            }
        });
    }
    
    private void initUI() {
        // Set window properties
        setSize(WIDTH, HEIGHT);
        setMinimumSize(new Dimension(WIDTH, HEIGHT));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(20, 20));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);
        
        // Timer panel
        JPanel timerPanel = createTimerPanel();
        
        // Task panel
        JPanel taskPanel = createTaskPanel();
        
        // Add panels to main
        mainPanel.add(timerPanel, BorderLayout.NORTH);
        mainPanel.add(taskPanel, BorderLayout.CENTER);
        
        // Set content
        setContentPane(mainPanel);
    }
    
    private JPanel createTimerPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(10, 20));
        panel.setBackground(Color.WHITE);

        changeStrategyButton = new JButton("Change Strategy");
        changeStrategyButton.addActionListener(e -> changeTimerStrategy());
        changeStrategyButton.setEnabled(false);
        
        // Timer display
        timerLabel = new JLabel("25:00", JLabel.CENTER);
        timerLabel.setFont(new Font("SansSerif", Font.BOLD, 80));
        
        // Progress bar
        progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setPreferredSize(new Dimension(WIDTH - 50, 15));
        
        // Buttons panel
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new GridLayout(2, 2, 10, 10)); // Change to 2x2 grid
        buttonsPanel.setBackground(Color.WHITE);
        
        startButton = new JButton("Start Timer");
        startButton.addActionListener(e -> startTimer());
        
        pauseButton = new JButton("Pause Timer");
        pauseButton.addActionListener(e -> pauseTimer());
        pauseButton.setEnabled(false);
        
        // New reset button
        resetButton = new JButton("Reset Timer");
        resetButton.addActionListener(e -> resetTimer());
        resetButton.setEnabled(false);
        
        buttonsPanel.add(startButton);
        buttonsPanel.add(pauseButton);
        buttonsPanel.add(resetButton);
        buttonsPanel.add(changeStrategyButton);

        panel.add(timerLabel, BorderLayout.NORTH);
        panel.add(progressBar, BorderLayout.CENTER);
        panel.add(buttonsPanel, BorderLayout.SOUTH);
        
        return panel;
    }

    private void changeTimerStrategy() {
        if (currentTask == null) {
            JOptionPane.showMessageDialog(this, "Please select a task first.", "No Task Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Show strategy dialog
        showTimerStrategyDialog();
        
        // If there's an active timer, change its strategy
        TaskTimer timer = timerManager.getTimerForTask(currentTask);
        if (timer != null) {
            timer.changeStrategy(currentStrategy);
            
            // Update UI
            updateTimerUI();
            updateTaskList();
        }
    }
    
    private JPanel createTaskPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(0, 10));
        panel.setBackground(Color.WHITE);
        
        // Task header
        JLabel tasksHeader = new JLabel("Tasks");
        tasksHeader.setFont(new Font("SansSerif", Font.BOLD, 16));
        
        // Task list panel with scroll
        taskListPanel = new JPanel();
        taskListPanel.setLayout(new BoxLayout(taskListPanel, BoxLayout.Y_AXIS));
        taskListPanel.setBackground(Color.WHITE);
        
        JScrollPane scrollPane = new JScrollPane(taskListPanel);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        // Create task action buttons
        JPanel actionPanel = new JPanel();
        actionPanel.setLayout(new GridLayout(1, 2, 20, 0));
        actionPanel.setBackground(Color.WHITE);
        
        addTaskButton = new JButton("Add Task");
        addTaskButton.addActionListener(e -> showAddTaskDialog());
        
        completeTaskButton = new JButton("Complete Task");
        completeTaskButton.addActionListener(e -> completeSelectedTask());
        
        actionPanel.add(addTaskButton);
        actionPanel.add(completeTaskButton);
        
        // Add to task panel
        panel.add(tasksHeader, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(actionPanel, BorderLayout.SOUTH);
        
        // Populate task list
        updateTaskList();
        
        return panel;
    }
    
    private void updateTaskList() {
        taskListPanel.removeAll();
        List<Task> tasks = taskManager.getTasks();
        
        for (Task task : tasks) {
            JPanel taskItem = createTaskItem(task);
            taskListPanel.add(taskItem);
        }
        
        taskListPanel.revalidate();
        taskListPanel.repaint();
    }
    
    private JPanel createTaskItem(Task task) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(10, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
        panel.setMaximumSize(new Dimension(WIDTH - 50, 40));
        panel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        // Set background for already selected task
        if (currentTask != null && task.equals(currentTask)) {
            panel.setBackground(new Color(240, 240, 255));
        } else {
            panel.setBackground(UIManager.getColor("Panel.background"));
        }
        
        // Task details
        String title = task.getTitle();
        
        // Add indicator for active timer
        if (timerManager.getTimerForTask(task) != null) {
            TaskTimer taskTimer = timerManager.getTimerForTask(task);
            String runningIndicator = taskTimer.isRunning() ? "▶️" : "⏸️";
            title = runningIndicator + " " + title;
        }
        
        JPanel taskInfoPanel = new JPanel(new BorderLayout(5, 3));
        taskInfoPanel.setOpaque(false);
        
        JLabel taskTitle = new JLabel(title);
        taskTitle.setFont(new Font("SansSerif", Font.BOLD, 16));
        
        // Add description if it exists
        String desc = task.getDescription();
        JLabel taskDesc = null;
        if (desc != null && !desc.trim().isEmpty()) {
            taskDesc = new JLabel(desc);
            taskDesc.setFont(new Font("SansSerif", Font.ITALIC, 12));
            taskDesc.setForeground(Color.DARK_GRAY);
        }
        
        taskInfoPanel.add(taskTitle, BorderLayout.NORTH);
        if (taskDesc != null) {
            taskInfoPanel.add(taskDesc, BorderLayout.SOUTH);
        }
        
        // Add to main panel
        panel.add(taskInfoPanel, BorderLayout.CENTER);
        
        // Task status
        String statusText = (task.getStatus() == TaskStatus.COMPLETED) ? "Completed" : "Pending";
        JLabel taskStatus = new JLabel(statusText);
        taskStatus.setForeground(task.getStatus() == TaskStatus.COMPLETED ? new Color(100, 150, 100) : Color.GRAY);
        
        // Delete button
        JButton deleteButton = new JButton("×");
        deleteButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        deleteButton.setForeground(Color.RED);
        deleteButton.setBorderPainted(false);
        deleteButton.setContentAreaFilled(false);
        deleteButton.setFocusPainted(false);
        deleteButton.addActionListener(e -> deleteTask(task));
        
        // Add components to panel
        panel.add(taskTitle, BorderLayout.WEST);
        
        JPanel rightPanel = new JPanel(new BorderLayout(5, 0));
        rightPanel.setOpaque(false);
        rightPanel.add(taskStatus, BorderLayout.CENTER);
        rightPanel.add(deleteButton, BorderLayout.EAST);
        
        panel.add(rightPanel, BorderLayout.EAST);
        
        // Make task selectable
        panel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                selectTask(task);
                updateTaskList(); // Update the entire list to reflect selection
            }
        });
        
        return panel;
    }
    
    private void deleteTask(Task task) {
        // Ask for confirmation
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to delete task: " + task.getTitle() + "?",
            "Confirm Deletion",
            JOptionPane.YES_NO_OPTION
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            // Find the task's index
            int index = taskManager.getTasks().indexOf(task);
            if (index != -1) {
                // Cancel any timer for this task
                if (timerManager.hasActiveTimer(task)) {
                    timerManager.cancelTimer(task);
                    
                    // Reset UI if this was the active task
                    if (task.equals(currentTask)) {
                        startButton.setEnabled(true);
                        pauseButton.setEnabled(false);
                        resetButton.setEnabled(false);
                        progressBar.setValue(0);
                        timerLabel.setText(formatTime(currentStrategy.getTotalWorkSeconds()));
                        currentTask = null;
                    }
                }
                
                // Delete the task
                taskManager.deleteTask(index);
                updateTaskList();
            }
        }
    }
    
    private void selectTask(Task task) {
        // Store the previously selected task
        Task previousTask = currentTask;
        currentTask = task;
        
        // If we're switching tasks and there was a previous task with a running timer
        if (previousTask != null && !previousTask.equals(currentTask) && 
            timerManager.hasActiveTimer(previousTask)) {
            // Ask user if they want to pause the previous timer
            int response = JOptionPane.showConfirmDialog(
                this,
                "You have an active timer for task: " + previousTask.getTitle() + "\nDo you want to pause it?",
                "Switch Task",
                JOptionPane.YES_NO_OPTION
            );
            
            if (response == JOptionPane.YES_OPTION) {
                timerManager.pauseTimer(previousTask);
            }
        }
        
        // Update UI to reflect current task's timer state
        updateTimerUI();
    }
    
    // New method to get the timer UI based on current task
    private TaskTimer getCurrentTaskTimer() {
        return currentTask != null ? timerManager.getTimerForTask(currentTask) : null;
    }

    private void updateTimerUI() {
        activeTimer = getCurrentTaskTimer();
        
        if (activeTimer != null) {
            // We have a timer for this task
            if (activeTimer.isRunning()) {
                // Timer is running
                startButton.setEnabled(false);
                pauseButton.setEnabled(true);
                pauseButton.setText("Pause Timer");
                resetButton.setEnabled(true);
                
                // Update display
                timerLabel.setText(formatTime(activeTimer.getRemainingSeconds()));
                
                // Update progress bar based on current phase
                int totalSeconds = activeTimer.isWorkPhase() ? 
                    activeTimer.getStrategy().getTotalWorkSeconds() :
                    activeTimer.getStrategy().getTotalBreakSeconds();
                
                double progress = 1.0 - ((double) activeTimer.getRemainingSeconds() / totalSeconds);
                progressBar.setValue((int) (progress * 100));
            } else {
                // Timer exists but is paused
                startButton.setEnabled(true);
                pauseButton.setEnabled(true);
                pauseButton.setText("Resume Timer");
                resetButton.setEnabled(true);
                
                // Update display
                timerLabel.setText(formatTime(activeTimer.getRemainingSeconds()));
            }
        } else {
            // No timer for this task
            startButton.setEnabled(true);
            pauseButton.setEnabled(false);
            pauseButton.setText("Pause Timer");
            resetButton.setEnabled(false);
            
            // Reset timer display to strategy default
            timerLabel.setText(formatTime(currentStrategy.getTotalWorkSeconds()));
            progressBar.setValue(0);
        }

        changeStrategyButton.setEnabled(currentTask != null);
    }
    
    private void startTimer() {
        if (currentTask == null) {
            JOptionPane.showMessageDialog(this, "Please select a task first.", "No Task Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Check if task is already completed
        if (currentTask.getStatus() == TaskStatus.COMPLETED) {
            JOptionPane.showMessageDialog(this, "Task is already completed.", "Task Completed", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Get existing timer if any
        TaskTimer existingTimer = timerManager.getTimerForTask(currentTask);
        
        if (existingTimer != null && !existingTimer.isRunning()) {
            // Resume existing paused timer
            timerManager.resumeTimer(currentTask);
            activeTimer = existingTimer;
            
            // Update UI
            startButton.setEnabled(false);
            pauseButton.setEnabled(true);
            pauseButton.setText("Pause Timer");
            resetButton.setEnabled(true);
            
            updateTaskList(); // Update task list to show the timer is running
            return;
        }
        
        // Reset UI for new timer
        startButton.setEnabled(false);
        pauseButton.setEnabled(true);
        pauseButton.setText("Pause Timer");
        resetButton.setEnabled(true);
        
        // Start new timer
        activeTimer = timerManager.startTimer(currentTask, currentStrategy);
        
        // Setup timer listener
        activeTimer.setListener(new TaskTimer.TimerListener() {
            @Override
            public void onTick(int seconds) {
                SwingUtilities.invokeLater(() -> {
                    // Only update if this is still the current task
                    if (currentTask != null && currentTask.equals(activeTimer.getTask())) {
                        // Update timer display
                        timerLabel.setText(formatTime(seconds));
                        
                        // Update progress bar
                        int totalSeconds = activeTimer.isWorkPhase() ? 
                            activeTimer.getStrategy().getTotalWorkSeconds() :
                            activeTimer.getStrategy().getTotalBreakSeconds();
                        
                        double progress = 1.0 - ((double) seconds / totalSeconds);
                        progressBar.setValue((int) (progress * 100));
                    }
                    
                    // Always update the task list to show running timers
                    updateTaskList();
                });
            }
            
            @Override
            public void onPhaseComplete(boolean wasWorkPhase) {
                SwingUtilities.invokeLater(() -> {
                    Task timerTask = activeTimer.getTask();
                    if (wasWorkPhase) {
                        // Work phase complete
                        timerTask.incrementPomodoros();
                        soundManager.playSound(SoundManager.SoundType.WORK_COMPLETE);
                        
                        String taskName = timerTask.getTitle();
                        JOptionPane.showMessageDialog(FocusTrackUI.this, 
                            "Work phase complete for task: " + taskName + "! Take a break.", 
                            "Phase Complete", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        // Break phase complete
                        soundManager.playSound(SoundManager.SoundType.BREAK_COMPLETE);
                        
                        String taskName = timerTask.getTitle();
                        JOptionPane.showMessageDialog(FocusTrackUI.this, 
                            "Break complete for task: " + taskName + "! Ready to work again?", 
                            "Phase Complete", JOptionPane.INFORMATION_MESSAGE);
                    }
                    
                    // Update UI if this is still the current task
                    if (currentTask != null && currentTask.equals(timerTask)) {
                        updateTimerUI();
                    }
                    
                    // Update task list
                    updateTaskList();
                });
            }
            
            @Override
            public void onTimerComplete() {
                SwingUtilities.invokeLater(() -> {
                    Task timerTask = activeTimer.getTask();
                    soundManager.playSound(SoundManager.SoundType.TIMER_COMPLETE);
                    
                    // Update UI if this is still the current task
                    if (currentTask != null && currentTask.equals(timerTask)) {
                        startButton.setEnabled(true);
                        pauseButton.setEnabled(false);
                        resetButton.setEnabled(false);
                        progressBar.setValue(100);
                    }
                    
                    // Ask if task is complete
                    String taskName = timerTask.getTitle();
                    int response = JOptionPane.showConfirmDialog(FocusTrackUI.this, 
                        "Timer cycle completed for task: " + taskName + "! Is this task finished?", 
                        "Timer Complete", JOptionPane.YES_NO_OPTION);
                    
                    if (response == JOptionPane.YES_OPTION) {
                        completeTask(timerTask);
                    }
                    
                    // Update the task list
                    updateTaskList();
                });
            }
        });
        
        // Update task list to show the timer is running
        updateTaskList();
    }
    
    private void pauseTimer() {
        if (currentTask == null) {
            JOptionPane.showMessageDialog(this, "No task selected.", "No Task", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        TaskTimer timer = timerManager.getTimerForTask(currentTask);
        
        if (timer != null) {
            if (timer.isRunning()) {
                // Pause the timer
                timerManager.pauseTimer(currentTask);
                pauseButton.setText("Resume Timer");
                startButton.setEnabled(true);
            } else {
                // Resume the timer
                timerManager.resumeTimer(currentTask);
                pauseButton.setText("Pause Timer");
                startButton.setEnabled(false);
            }
            
            // Update task list to reflect timer state change
            updateTaskList();
        } else {
            JOptionPane.showMessageDialog(this, "No active timer for the selected task.", 
                "No Timer", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void resetTimer() {
        if (currentTask == null) {
            JOptionPane.showMessageDialog(this, "No task selected.", 
                "No Task", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        TaskTimer timer = timerManager.getTimerForTask(currentTask);
        
        if (timer != null) {
            timerManager.resetTimer(currentTask);
            
            // Update UI
            timerLabel.setText(formatTime(timer.getRemainingSeconds()));
            
            // Update progress bar
            progressBar.setValue(0);
            
            // Update task list
            updateTaskList();
        } else {
            JOptionPane.showMessageDialog(this, "No active timer to reset.", 
                "No Timer", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void completeSelectedTask() {
        if (currentTask == null) {
            JOptionPane.showMessageDialog(this, "Please select a task first.", "No Task Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        completeTask(currentTask);
        updateTaskList();
    }
    
    private void completeTask(Task task) {
        // Cancel any running timer
        if (timerManager.getTimerForTask(task) != null) {
            timerManager.cancelTimer(task);
            
            // Update UI if this was the current task
            if (task.equals(currentTask)) {
                updateTimerUI();
            }
        }
        
        // Mark task as completed
        task.markCompleted();
        
        // Reset current task if it was the completed one
        if (task.equals(currentTask)) {
            currentTask = null;
        }
        
        // Update task list
        updateTaskList();
    }
    
    private void showAddTaskDialog() {
        JTextField titleField = new JTextField();
        JTextField descField = new JTextField();
        
        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Task title:"));
        panel.add(titleField);
        panel.add(new JLabel("Description:"));
        panel.add(descField);
        
        int result = JOptionPane.showConfirmDialog(null, panel, 
                "Add New Task", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION && !titleField.getText().trim().isEmpty()) {
            Task newTask = new Task(titleField.getText().trim(), descField.getText().trim());
            taskManager.addTask(newTask);
            updateTaskList();
        }
    }
    
    private void createSampleTasks() {
        if (taskManager.getTasks().isEmpty()) {
            taskManager.addTask(new Task("Complete assignment", "Due tomorrow"));
            taskManager.addTask(new Task("Read book", "Chapter 5"));
            
            Task exerciseTask = new Task("Exercise", "30 minutes");
            exerciseTask.markCompleted();
            taskManager.addTask(exerciseTask);
        }
    }

    private String formatTime(int seconds) {
        int minutes = seconds / 60;
        int secs = seconds % 60;
        return String.format("%02d:%02d", minutes, secs);
    }
    
    private void showTimerStrategyDialog() {
        String[] options = {
            "Pomodoro (25 min work, 5 min break)",
            "Short Break (15 min work, 3 min break)",
            "Long Break (50 min work, 10 min break)",
            "Custom Timer"
        };
        
        int choice = JOptionPane.showOptionDialog(this,
            "Select timer strategy:",
            "Timer Strategy",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]);
        
        switch (choice) {
            case 0:
                currentStrategy = new PomodoroTimer();
                break;
            case 1:
                currentStrategy = new ShortBreakTimer();
                break;
            case 2:
                currentStrategy = new LongBreakTimer();
                break;
            case 3:
                showCustomTimerDialog();
                break;
        }
        
        // Update timer display to reflect new strategy
        if (choice >= 0 && choice <= 2) {
            timerLabel.setText(String.format("%02d:00", currentStrategy.getWorkDuration()));
        }
    }
    
    private void showCustomTimerDialog() {
        JTextField nameField = new JTextField("Custom Timer");
        JTextField workMinutesField = new JTextField("25", 3);
        JTextField workSecondsField = new JTextField("0", 3); 
        JTextField breakMinutesField = new JTextField("5", 3); 
        JTextField breakSecondsField = new JTextField("0", 3);
        
        JPanel panel = new JPanel(new GridLayout(0, 2));
        panel.add(new JLabel("Timer name:"));
        panel.add(nameField);
        
        JPanel workPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        workPanel.add(workMinutesField);
        workPanel.add(new JLabel("min"));
        workPanel.add(workSecondsField);
        workPanel.add(new JLabel("sec"));
        
        JPanel breakPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        breakPanel.add(breakMinutesField);
        breakPanel.add(new JLabel("min"));
        breakPanel.add(breakSecondsField);
        breakPanel.add(new JLabel("sec"));
        
        panel.add(new JLabel("Work duration:"));
        panel.add(workPanel);
        panel.add(new JLabel("Break duration:"));
        panel.add(breakPanel);
        
        int result = JOptionPane.showConfirmDialog(this, panel, 
                "Custom Timer", JOptionPane.OK_CANCEL_OPTION);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                String name = nameField.getText();
                int workMinutes = Integer.parseInt(workMinutesField.getText().trim());
                int workSeconds = Integer.parseInt(workSecondsField.getText().trim());
                int breakMinutes = Integer.parseInt(breakMinutesField.getText().trim());
                int breakSeconds = Integer.parseInt(breakSecondsField.getText().trim());
                
                // Validate inputs
                if (workMinutes < 0 || workSeconds < 0 || workSeconds >= 60 || 
                    breakMinutes < 0 || breakSeconds < 0 || breakSeconds >= 60 ||
                    (workMinutes == 0 && workSeconds == 0) || 
                    (breakMinutes == 0 && breakSeconds == 0)) {
                    throw new IllegalArgumentException("Invalid time values");
                }
                
                // Create description with minutes and seconds
                String workDesc = formatTimeDescription(workMinutes, workSeconds);
                String breakDesc = formatTimeDescription(breakMinutes, breakSeconds);
                String description = workDesc + " of focused work followed by a " + breakDesc + " break";
                
                currentStrategy = TimerStrategyFactory.createCustomStrategy(
                    name,
                    description,
                    workMinutes,
                    workSeconds,
                    breakMinutes,
                    breakSeconds
                );
                
                // Update timer display
                int totalSeconds = currentStrategy.getTotalWorkSeconds();
                timerLabel.setText(formatTime(totalSeconds));
                
            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(this, 
                    "Please enter valid positive numbers for durations.\n" +
                    "Seconds must be between 0-59.\n" +
                    "At least one duration must be greater than 0.",
                    "Invalid Input", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    // Helper method to format time descriptions
    private String formatTimeDescription(int minutes, int seconds) {
        if (minutes > 0 && seconds > 0) {
            return minutes + " minutes and " + seconds + " seconds";
        } else if (minutes > 0) {
            return minutes + " minute" + (minutes == 1 ? "" : "s");
        } else {
            return seconds + " second" + (seconds == 1 ? "" : "s");
        }
    }
    
    private void showSettingsDialog() {
        JCheckBox soundEnabledBox = new JCheckBox("Enable sounds", soundManager.isSoundEnabled());
        JSlider volumeSlider = new JSlider(0, 100, (int)(soundManager.getVolume() * 100));
        volumeSlider.setMajorTickSpacing(20);
        volumeSlider.setMinorTickSpacing(5);
        volumeSlider.setPaintTicks(true);
        volumeSlider.setPaintLabels(true);
        
        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(soundEnabledBox);
        panel.add(new JLabel("Volume:"));
        panel.add(volumeSlider);
        
        int result = JOptionPane.showConfirmDialog(this, panel, 
                "Settings", JOptionPane.OK_CANCEL_OPTION);
        
        if (result == JOptionPane.OK_OPTION) {
            soundManager.setSoundEnabled(soundEnabledBox.isSelected());
            soundManager.setVolume(volumeSlider.getValue() / 100.0f);
        }
    }
    
    private void shutdown() {
        // Clean up resources
        if (timerManager != null) {
            timerManager.clearAllTimers();
        }
        if (soundManager != null) {
            soundManager.cleanup();
        }
        
        // Save tasks before exit
        taskManager.saveTasks();
    }
    
    public static void main(String[] args) {
        // Set look and feel to system
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Launch UI
        SwingUtilities.invokeLater(() -> {
            FocusTrackUI ui = new FocusTrackUI();
            ui.setVisible(true);
            
            // Add menu bar
            JMenuBar menuBar = new JMenuBar();
            
            JMenu fileMenu = new JMenu("File");
            JMenuItem exitItem = new JMenuItem("Exit");
            exitItem.addActionListener(e -> {
                ui.shutdown();
                System.exit(0);
            });
            fileMenu.add(exitItem);
            
            JMenu timerMenu = new JMenu("Timer");
            JMenuItem strategyItem = new JMenuItem("Change Timer Strategy");
            strategyItem.addActionListener(e -> ui.showTimerStrategyDialog());
            timerMenu.add(strategyItem);
            
            JMenu settingsMenu = new JMenu("Settings");
            JMenuItem soundItem = new JMenuItem("Sound Settings");
            soundItem.addActionListener(e -> ui.showSettingsDialog());
            settingsMenu.add(soundItem);
            
            menuBar.add(fileMenu);
            menuBar.add(timerMenu);
            menuBar.add(settingsMenu);
            
            ui.setJMenuBar(menuBar);
        });
    }
}