package main.ui;

import main.model.Task;
import main.model.TaskManager;
import main.model.TaskStatus;
import main.sound.SoundManager;
import main.timer.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
        
        // Create sample tasks if there are none
        if (taskManager.getTasks().isEmpty()) {
            createSampleTasks();
        }
        
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
        
        // Timer display
        timerLabel = new JLabel("25:00", JLabel.CENTER);
        timerLabel.setFont(new Font("SansSerif", Font.BOLD, 80));
        
        // Progress bar
        progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setPreferredSize(new Dimension(WIDTH - 50, 15));
        
        // Buttons panel
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new GridLayout(1, 2, 20, 0));
        buttonsPanel.setBackground(Color.WHITE);
        
        startButton = new JButton("Start Timer");
        startButton.addActionListener(e -> startTimer());
        
        pauseButton = new JButton("Pause Timer");
        pauseButton.addActionListener(e -> pauseTimer());
        pauseButton.setEnabled(false);
        
        buttonsPanel.add(startButton);
        buttonsPanel.add(pauseButton);
        
        // Add components to timer panel
        panel.add(timerLabel, BorderLayout.NORTH);
        panel.add(progressBar, BorderLayout.CENTER);
        panel.add(buttonsPanel, BorderLayout.SOUTH);
        
        return panel;
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
        
        // Task details
        JLabel taskTitle = new JLabel(task.getTitle());
        taskTitle.setFont(new Font("SansSerif", Font.PLAIN, 16));
        
        // Task status
        String statusText = (task.getStatus() == TaskStatus.COMPLETED) ? "Completed" : "Pending";
        JLabel taskStatus = new JLabel(statusText);
        taskStatus.setHorizontalAlignment(JLabel.RIGHT);
        taskStatus.setForeground(task.getStatus() == TaskStatus.COMPLETED ? new Color(100, 150, 100) : Color.GRAY);
        
        panel.add(taskTitle, BorderLayout.WEST);
        panel.add(taskStatus, BorderLayout.EAST);
        
        // Make task selectable
        panel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                selectTask(task);
                panel.setBackground(new Color(240, 240, 255));
                
                // Deselect others
                for (Component c : taskListPanel.getComponents()) {
                    if (c != panel && c instanceof JPanel) {
                        c.setBackground(null);
                    }
                }
            }
        });
        
        return panel;
    }
    
    private void selectTask(Task task) {
        currentTask = task;
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
        
        // Reset UI
        startButton.setEnabled(false);
        pauseButton.setEnabled(true);
        
        // Cancel existing timer if any
        if (activeTimer != null) {
            timerManager.cancelTimer(currentTask);
        }
        
        // Start new timer
        int totalSeconds = currentStrategy.getWorkDuration() * 60;
        activeTimer = timerManager.startTimer(currentTask, currentStrategy);
        
        // Setup timer listener
        activeTimer.setListener(new TaskTimer.TimerListener() {
            @Override
            public void onTick(int seconds) {
                SwingUtilities.invokeLater(() -> {
                    // Update timer display
                    timerLabel.setText(formatTime(seconds));
                    
                    // Update progress bar
                    double progress = 1.0 - ((double) seconds / totalSeconds);
                    progressBar.setValue((int) (progress * 100));
                });
            }
            
            @Override
            public void onPhaseComplete(boolean wasWorkPhase) {
                SwingUtilities.invokeLater(() -> {
                    if (wasWorkPhase) {
                        // Work phase complete
                        currentTask.incrementPomodoros();
                        soundManager.playSound(SoundManager.SoundType.WORK_COMPLETE);
                        JOptionPane.showMessageDialog(FocusTrackUI.this, 
                            "Work phase complete! Take a break.", "Phase Complete", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        // Break phase complete
                        soundManager.playSound(SoundManager.SoundType.BREAK_COMPLETE);
                        JOptionPane.showMessageDialog(FocusTrackUI.this, 
                            "Break complete! Ready to work again?", "Phase Complete", JOptionPane.INFORMATION_MESSAGE);
                    }
                });
            }
            
            @Override
            public void onTimerComplete() {
                SwingUtilities.invokeLater(() -> {
                    soundManager.playSound(SoundManager.SoundType.TIMER_COMPLETE);
                    startButton.setEnabled(true);
                    pauseButton.setEnabled(false);
                    progressBar.setValue(100);
                    
                    // Ask if task is complete
                    int response = JOptionPane.showConfirmDialog(FocusTrackUI.this, 
                        "Timer cycle completed! Is this task finished?", 
                        "Timer Complete", JOptionPane.YES_NO_OPTION);
                    
                    if (response == JOptionPane.YES_OPTION) {
                        completeTask(currentTask);
                        updateTaskList();
                    }
                });
            }
        });
    }
    
    private void pauseTimer() {
        if (activeTimer != null && activeTimer.isRunning()) {
            timerManager.pauseTimer(currentTask);
            pauseButton.setText("Resume Timer");
            startButton.setEnabled(true);
        } else if (activeTimer != null) {
            // Resume timer
            timerManager.resumeTimer(currentTask);
            pauseButton.setText("Pause Timer");
            startButton.setEnabled(false);
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
        if (timerManager.hasActiveTimer(task)) {
            timerManager.cancelTimer(task);
            if (task == currentTask) {
                startButton.setEnabled(true);
                pauseButton.setEnabled(false);
                pauseButton.setText("Pause Timer");
            }
        }
        
        // Mark task as completed
        task.markCompleted();
        
        // Reset current task if it was the completed one
        if (task == currentTask) {
            currentTask = null;
        }
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
        taskManager.addTask(new Task("Complete assignment", "Due tomorrow"));
        taskManager.addTask(new Task("Read book", "Chapter 5"));
        
        Task exerciseTask = new Task("Exercise", "30 minutes");
        exerciseTask.markCompleted();
        taskManager.addTask(exerciseTask);
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
        JTextField workField = new JTextField("25");
        JTextField breakField = new JTextField("5");
        
        JPanel panel = new JPanel(new GridLayout(0, 2));
        panel.add(new JLabel("Timer name:"));
        panel.add(nameField);
        panel.add(new JLabel("Work duration (minutes):"));
        panel.add(workField);
        panel.add(new JLabel("Break duration (minutes):"));
        panel.add(breakField);
        
        int result = JOptionPane.showConfirmDialog(this, panel, 
                "Custom Timer", JOptionPane.OK_CANCEL_OPTION);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                String name = nameField.getText();
                int workDuration = Integer.parseInt(workField.getText());
                int breakDuration = Integer.parseInt(breakField.getText());
                
                if (workDuration <= 0 || breakDuration <= 0) {
                    throw new NumberFormatException("Durations must be positive");
                }
                
                currentStrategy = TimerStrategyFactory.createCustomStrategy(
                    name,
                    workDuration + " minutes of focused work followed by a " + breakDuration + "-minute break",
                    workDuration,
                    breakDuration
                );
                
                // Update timer display
                timerLabel.setText(String.format("%02d:00", workDuration));
                
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, 
                    "Please enter valid positive numbers for durations.",
                    "Invalid Input", 
                    JOptionPane.ERROR_MESSAGE);
            }
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