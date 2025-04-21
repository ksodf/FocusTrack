package main.model;

import main.io.TaskFileHandler;

import java.util.List;

public class TaskManager {
    private static TaskManager instance = null;
    private List<Task> tasks;
    private TaskFileHandler fileHandler;
    private boolean autoSave;

    private TaskManager() {
        fileHandler = new TaskFileHandler();
        autoSave = true;
        loadTasks();
    }

    // Singleton access method
    public static TaskManager getInstance() {
        if (instance == null) {
            instance = new TaskManager();
        }
        return instance;
    }
    
    /**
     * Load tasks from file
     */
    public void loadTasks() {
        tasks = fileHandler.loadTasks();
    }
    
    /**
     * Save tasks to file
     */
    public void saveTasks() {
        if (tasks != null) {
            fileHandler.saveTasks(tasks);
        }
    }
    
    /**
     * Enable or disable auto-save
     */
    public void setAutoSave(boolean autoSave) {
        this.autoSave = autoSave;
    }
    
    /**
     * Check if auto-save is enabled
     */
    public boolean isAutoSave() {
        return autoSave;
    }

    // Add a new task
    public void addTask(Task task) {
        tasks.add(task);
        if (autoSave) saveTasks();
    }

    // Edit task by index
    public void editTask(int index, String newTitle, String newDescription) {
        if (index >= 0 && index < tasks.size()) {
            Task task = tasks.get(index);
            task.setTitle(newTitle);
            task.setDescription(newDescription);
            if (autoSave) saveTasks();
        }
    }

    // Delete task by index
    public void deleteTask(int index) {
        if (index >= 0 && index < tasks.size()) {
            tasks.remove(index);
            if (autoSave) saveTasks();
        }
    }

    // Mark a task as completed
    public void completeTask(int index) {
        if (index >= 0 && index < tasks.size()) {
            tasks.get(index).markCompleted();
            if (autoSave) saveTasks();
        }
    }

    // Get task list
    public List<Task> getTasks() {
        return tasks;
    }

    // Progress: percentage of tasks completed
    public double getCompletionRate() {
        if (tasks.isEmpty()) return 0.0;
        long completed = tasks.stream().filter(t -> t.getStatus() == TaskStatus.COMPLETED).count();
        return (completed * 100.0) / tasks.size();
    }
}
