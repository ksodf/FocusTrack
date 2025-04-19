package main.timer;

// Long break timer - 50 minutes work, 10 minutes break
public class LongBreakTimer implements TimerStrategy {
    @Override
    public int getWorkDuration() {
        return 50;
    }

    @Override
    public int getBreakDuration() {
        return 10;
    }

    @Override
    public String getName() {
        return "Long Break";
    }

    @Override
    public String getDescription() {
        return "50 minutes of focused work followed by a 10-minute break";
    }
}