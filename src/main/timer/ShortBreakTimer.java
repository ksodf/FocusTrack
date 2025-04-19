package main.timer;

// Short break timer - 15 minutes work, 3 minutes break
public class ShortBreakTimer implements TimerStrategy {
    @Override
    public int getWorkDuration() {
        return 15;
    }

    @Override
    public int getBreakDuration() {
        return 3;
    }

    @Override
    public String getName() {
        return "Short Break";
    }

    @Override
    public String getDescription() {
        return "15 minutes of focused work followed by a 3-minute break";
    }
}