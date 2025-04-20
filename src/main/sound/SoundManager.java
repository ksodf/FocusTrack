package main.sound;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Handles sound notifications for the application
 * Follows the Singleton pattern to ensure only one instance exists
 */
public class SoundManager {
    private static SoundManager instance;
    private boolean soundEnabled = true;
    private float volume = 1.0f;  // Volume level (0.0 to 1.0)
    
    // Define sound types
    public enum SoundType {
        WORK_COMPLETE,
        BREAK_COMPLETE,
        TIMER_COMPLETE,
        ERROR
    }
    
    // Map to store sound clips
    private Map<SoundType, Clip> soundClips;
    
    private SoundManager() {
        soundClips = new HashMap<>();
        initializeSounds();
    }
    
    public static SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }
        return instance;
    }
    
    /**
     * Initialize sound clips
     */
    private void initializeSounds() {
        try {
            // Load sounds from resources
            loadSound(SoundType.WORK_COMPLETE, "/sounds/work_complete.wav");
            loadSound(SoundType.BREAK_COMPLETE, "/sounds/break_complete.wav");
            loadSound(SoundType.TIMER_COMPLETE, "/sounds/timer_complete.wav");
            loadSound(SoundType.ERROR, "/sounds/error.wav");
        } catch (Exception e) {
            System.err.println("Error loading sounds: " + e.getMessage());
            // Continue without sounds if they can't be loaded
        }
    }
    
    /**
     * Load a sound clip from a resource file
     */
    private void loadSound(SoundType type, String resourcePath) {
        try {
            InputStream soundStream = getClass().getResourceAsStream(resourcePath);
            
            // If resource stream is null, try to find the file directly
            if (soundStream == null) {
                String fallbackPath = "src/main/resources" + resourcePath;
                File soundFile = new File(fallbackPath);
                if (soundFile.exists()) {
                    AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);
                    Clip clip = AudioSystem.getClip();
                    clip.open(audioStream);
                    soundClips.put(type, clip);
                } else {
                    System.err.println("Could not find sound file: " + resourcePath);
                }
                return;
            }
            
            // Load from resource stream
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundStream);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            soundClips.put(type, clip);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("Error loading sound " + resourcePath + ": " + e.getMessage());
        }
    }
    
    /**
     * Play a sound notification
     * 
     * @param type The type of sound to play
     */
    public void playSound(SoundType type) {
        if (!soundEnabled || !soundClips.containsKey(type)) {
            return;
        }
        
        try {
            Clip clip = soundClips.get(type);
            if (clip != null) {
                // Reset clip to start
                clip.stop();
                clip.setFramePosition(0);
                
                // Set volume
                if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                    FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                    // Convert linear volume (0.0 to 1.0) to dB scale
                    float dB = 20f * (float) Math.log10(volume);
                    gainControl.setValue(dB);
                }
                
                // Play the sound
                clip.start();
            }
        } catch (Exception e) {
            System.err.println("Error playing sound: " + e.getMessage());
        }
    }
    
    /**
     * Enable or disable sounds
     */
    public void setSoundEnabled(boolean enabled) {
        this.soundEnabled = enabled;
    }
    
    /**
     * Check if sound is enabled
     */
    public boolean isSoundEnabled() {
        return soundEnabled;
    }
    
    /**
     * Set the volume level (0.0 to 1.0)
     */
    public void setVolume(float volume) {
        if (volume < 0.0f) volume = 0.0f;
        if (volume > 1.0f) volume = 1.0f;
        this.volume = volume;
    }
    
    /**
     * Get the current volume level
     */
    public float getVolume() {
        return volume;
    }
    
    /**
     * Clean up resources
     */
    public void cleanup() {
        for (Clip clip : soundClips.values()) {
            clip.close();
        }
        soundClips.clear();
    }
}