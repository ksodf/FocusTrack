package main.app;

import main.model.Task;
import main.model.TaskManager;
public class App {
    public static void main(String[] args) throws Exception {
        TaskManager manager = TaskManager.getInstance();

        // Add tasks
        manager.addTask(new Task("Finish report", "Due tomorrow"));
        manager.addTask(new Task("Read chapter 4", "Pages 45–62"));

        // Complete task 0
        manager.completeTask(0);

        // Edit task 1
        manager.editTask(1, "Read chapter 4 & 5", "Pages 45-80");

        // Show tasks and progress
        for (Task task : manager.getTasks()) {
            System.out.println(task);
        }

        System.out.println("Progress: " + manager.getCompletionRate() + "%");
    }
}
