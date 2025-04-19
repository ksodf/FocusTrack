package main.timer;

public interface TimerStrategy {
    int getWorkDuration();
    int getBreakDuration();
    String getName();
    String getDescription();
}
