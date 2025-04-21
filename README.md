# **FocusTrack**

## **Project Overview**

FocusTrack is a simple and effective desktop application built with Java Swing to help users manage their tasks and stay focused using a countdown timer.

## **Features**

- Add, delete, and save personal tasks
- Countdown timer with adjustable minutes and seconds
- Save and load tasks from a local file (resources/tasks.txt)
- Reset timer button to original set time

## **Real-World Use Case**

FocusTrack helps users — students and professionals — to improve productivity by combining task management and focus sessions in a lightweight application.

## **Design Patterns Implemented**

| **Pattern** | **Application** |
| --- | --- |
| Singleton | Ensures a single instance of TaskManager |
| Observer | Updates TaskManagerPanel automatically when tasks change |
| Strategy | Provides flexibility for timer countdown behaviors |

## **OOP Principles Applied**

- **Encapsulation:** Task and timer states are managed internally.
- **Abstraction:** Interfaces such as TaskListener and TimerStrategy.
- **Inheritance:** Custom panels extend JPanel.
- **Polymorphism:** Timer behavior changes dynamically without modifying main timer logic.
## Folder Structure

## **How to Run**

Compile and run FocusTrackLauncher.java. Tasks will automatically be saved in and loaded from resources/tasks.txt.

The workspace contains two folders by default, where:

- `src`: the folder to maintain sources
- `lib`: the folder to maintain dependencies

Meanwhile, the compiled output files will be generated in the `bin` folder by default.

> If you want to customize the folder structure, open `.vscode/settings.json` and update the related settings there.

## Dependency Management

The `JAVA PROJECTS` view allows you to manage your dependencies. More details can be found [here](https://github.com/microsoft/vscode-java-dependency#manage-dependencies).
