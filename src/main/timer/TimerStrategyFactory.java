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
     * @param workMinutes The work duration in minutes
     * @param workSeconds & seconds
     * @param breakMinutes The break duration in minutes
     * @param breakSeconds & seconds
     * @return A custom TimerStrategy
     */
    public static TimerStrategy createCustomStrategy(
        final String name, 
        final String description, 
        final int workMinutes,
        final int workSeconds,
        final int breakMinutes,
        final int breakSeconds) {
    
            return new TimerStrategy() {
                @Override
                public int getWorkDuration() {
                    return workMinutes;
                }
                
                @Override
                public int getWorkDurationSeconds() {
                    return workSeconds;
                }

                @Override
                public int getBreakDuration() {
                    return breakMinutes;
                }
                
                @Override
                public int getBreakDurationSeconds() {
                    return breakSeconds;
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