package main.timer;

/**
 * Factory class for creating different timer strategies
 * Follows the Factory Method pattern
 */
public class TimerStrategyFactory {
    
    /**
     * Create a timer strategy based on the strategy type
     * 
     * @param strategyType The type of strategy to create
     * @return The created TimerStrategy instance
     */
    public static TimerStrategy createStrategy(TimerStrategyType strategyType) {
        switch (strategyType) {
            case POMODORO:
                return new PomodoroTimer();
            case SHORT_BREAK:
                return new ShortBreakTimer();
            case LONG_BREAK:
                return new LongBreakTimer();
            default:
                // Default to Pomodoro
                return new PomodoroTimer();
        }
    }
    
    /**
     * Create a custom timer strategy with specified durations
     * 
     * @param name The name of the strategy
     * @param description The description of the strategy
     * @param workDuration The work duration in minutes
     * @param breakDuration The break duration in minutes
     * @return A custom TimerStrategy
     */
    public static TimerStrategy createCustomStrategy(
            final String name, 
            final String description, 
            final int workDuration, 
            final int breakDuration) {
        
        return new TimerStrategy() {
            @Override
            public int getWorkDuration() {
                return workDuration;
            }

            @Override
            public int getBreakDuration() {
                return breakDuration;
            }

            @Override
            public String getName() {
                return name;
            }

            @Override
            public String getDescription() {
                return description;
            }
        };
    }
}