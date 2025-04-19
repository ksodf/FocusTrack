package main.timer;

// Pomodoro timer - 25 minutes work, 5 minutes break
public class PomodoroTimer implements TimerStrategy {
    @Override
    public int getWorkDuration() {
        return 25;
    }

    @Override
    public int getBreakDuration() {
        return 5;
    }

    @Override
    public String getName() {
        return "Pomodoro";
    }

    @Override
    public String getDescription() {
        return "25 minutes of focused work followed by a 5-minute break";
    }
}