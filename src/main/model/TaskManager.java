package main.model;

import java.util.ArrayList;
import java.util.List;

public class TaskManager {
    private static TaskManager instance = null;
    private List<Task> tasks;

    private TaskManager() {
        tasks = new ArrayList<>();
    }

    // Singleton access method
    public static TaskManager getInstance() {
        if (instance == null) {
            instance = new TaskManager();
        }
        return instance;
    }

    // Add a new task
    public void addTask(Task task) {
        tasks.add(task);
    }

    // Edit task by index
    public void editTask(int index, String newTitle, String newDescription) {
        if (index >= 0 && index < tasks.size()) {
            Task task = tasks.get(index);
            task.setTitle(newTitle);
            task.setDescription(newDescription);
        }
    }

    // Delete task by index
    public void deleteTask(int index) {
        if (index >= 0 && index < tasks.size()) {
            tasks.remove(index);
        }
    }

    // Mark a task as completed
    public void completeTask(int index) {
        if (index >= 0 && index < tasks.size()) {
            tasks.get(index).markCompleted();
        }
    }

    // Get task list
    public List<Task> getTasks() {
        return tasks;
    }

    // Progress: percentage of tasks completed
    public double getCompletionRate() {
        if (tasks.isEmpty()) return 0.0;
        long completed = tasks.stream().filter(t -> t.getStatus().name().equals("COMPLETED")).count();
        return (completed * 100.0) / tasks.size();
    }
}
