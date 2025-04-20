package main.io;

import main.model.Task;
import main.model.TaskStatus;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles saving and loading tasks to/from a file
 * Implements the Data Access Object pattern
 */
public class TaskFileHandler {
    private static final String DEFAULT_TASKS_PATH = "src/main/resources/tasks.txt";
    private String filePath;
    
    public TaskFileHandler() {
        this(DEFAULT_TASKS_PATH);
    }
    
    public TaskFileHandler(String filePath) {
        this.filePath = filePath;
        // Ensure the directory exists
        File file = new File(filePath);
        file.getParentFile().mkdirs();
    }
    
    /**
     * Save a list of tasks to the file
     */
    public void saveTasks(List<Task> tasks) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (Task task : tasks) {
                writer.write(String.format("%s|%s|%s|%d",
                        task.getTitle(),
                        task.getDescription(),
                        task.getStatus(),
                        task.getCompletedPomodoros()));
                writer.newLine();
            }
            System.out.println("Tasks saved successfully to " + filePath);
        } catch (IOException e) {
            System.err.println("Error saving tasks: " + e.getMessage());
        }
    }
    
    /**
     * Load tasks from the file
     */
    public List<Task> loadTasks() {
        List<Task> tasks = new ArrayList<>();
        File file = new File(filePath);
        
        if (!file.exists()) {
            System.out.println("No tasks file found. Starting with empty task list.");
            return tasks;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 3) {
                    String title = parts[0];
                    String description = parts[1];
                    TaskStatus status = TaskStatus.valueOf(parts[2]);
                    
                    Task task = new Task(title, description);
                    task.setStatus(status);
                    
                    // Set completed pomodoros if available
                    if (parts.length >= 4) {
                        try {
                            int pomodoros = Integer.parseInt(parts[3]);
                            for (int i = 0; i < pomodoros; i++) {
                                task.incrementPomodoros();
                            }
                        } catch (NumberFormatException e) {
                            // Ignore if pomodoro count is invalid
                        }
                    }
                    
                    tasks.add(task);
                }
            }
            System.out.println("Loaded " + tasks.size() + " tasks from " + filePath);
        } catch (IOException e) {
            System.err.println("Error loading tasks: " + e.getMessage());
        }
        
        return tasks;
    }
}