package com.candalf.lostbeneath;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * Manages sound effects in the game
 */
public class SoundManager implements Disposable {
    private static SoundManager instance;
    private ObjectMap<String, Sound> sounds;
    private boolean soundEnabled = true;
    private long backgroundMusicId = -1; // Store ID for background music
    
    private SoundManager() {
        sounds = new ObjectMap<>();
        loadSounds();
    }
    
    /**
     * Get the singleton instance
     */
    public static SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }
        return instance;
    }
    
    /**
     * Load all sounds used in the game
     */
    private void loadSounds() {
        try {
            // Load portal sound
            Sound portalSound = Gdx.audio.newSound(Gdx.files.internal("sounds/portal sesi (consolidated).wav"));
            sounds.put("portal", portalSound);
            
            // Load button sound
            Sound buttonSound = Gdx.audio.newSound(Gdx.files.internal("sounds/buton sesi (consolidated).wav"));
            sounds.put("button", buttonSound);

            Sound backgroundSound = Gdx.audio.newSound(Gdx.files.internal("sounds/background sesi (consolidated).wav"));
            sounds.put("background", backgroundSound);

            Sound araBolumSound = Gdx.audio.newSound(Gdx.files.internal("sounds/ara bolum sesi (consolidated).wav"));
            sounds.put("araBolum", araBolumSound);
            // Log successful loading
            Gdx.app.log("SoundManager", "Sounds loaded successfully");
        } catch (Exception e) {
            Gdx.app.error("SoundManager", "Error loading sounds: " + e.getMessage());
        }
    }
    
    /**
     * Play a sound with the given name
     * @param name The name of the sound to play
     * @return The ID of the sound instance or -1 if sound is disabled or not found
     */
    public long playSound(String name) {
        if (!soundEnabled) return -1;
        
        Sound sound = sounds.get(name);
        if (sound != null) {
            return sound.play(1.0f);
        } else {
            Gdx.app.error("SoundManager", "Sound not found: " + name);
            return -1;
        }
    }
    
    /**
     * Set whether sound is enabled
     * @param enabled True to enable sound, false to disable
     */
    public void setSoundEnabled(boolean enabled) {
        this.soundEnabled = enabled;
        
        // Handle background music based on sound enabled state
        if (enabled) {
            if (backgroundMusicId == -1) {
                // Restart background music if it was stopped
                startBackgroundMusic();
            } else {
                // Resume it if it was paused
                resumeBackgroundMusic();
            }
        } else {
            // Pause background music when sound is disabled
            pauseBackgroundMusic();
        }
    }
    
    /**
     * Check if sound is enabled
     * @return True if sound is enabled, false otherwise
     */
    public boolean isSoundEnabled() {
        return soundEnabled;
    }
    
    /**
     * Play a sound with the given name in a loop
     * @param name The name of the sound to play
     * @return The ID of the sound instance or -1 if sound is disabled or not found
     */
    public long playLoopingSound(String name) {
        if (!soundEnabled) {
            Gdx.app.log("SoundManager", "Sound is disabled, not playing: " + name);
            return -1;
        }
        
        Sound sound = sounds.get(name);
        if (sound != null) {
            long id = sound.loop(1.0f); // Loop the sound
            Gdx.app.log("SoundManager", "Started looping sound: " + name + " with ID: " + id);
            return id;
        } else {
            Gdx.app.error("SoundManager", "Sound not found: " + name);
            // Print out all available sounds for debugging
            Gdx.app.log("SoundManager", "Available sounds:");
            for (String key : sounds.keys()) {
                Gdx.app.log("SoundManager", " - " + key);
            }
            return -1;
        }
    }
    
    /**
     * Stop a specific sound instance 
     * @param name The name of the sound
     * @param soundId The ID of the sound instance to stop
     */
    public void stopSound(String name, long soundId) {
        if (soundId == -1) {
            Gdx.app.log("SoundManager", "Invalid sound ID (-1), not stopping: " + name);
            return;
        }
        
        Sound sound = sounds.get(name);
        if (sound != null) {
            sound.stop(soundId);
            Gdx.app.log("SoundManager", "Stopped sound: " + name + " with ID: " + soundId);
        } else {
            Gdx.app.error("SoundManager", "Cannot stop sound, not found: " + name);
        }
    }
    
    /**
     * Start playing background music
     * Should be called at the beginning of the application
     */
    public void startBackgroundMusic() {
        // Stop any existing background music first
        stopBackgroundMusic();
        
        // Start looping the background music
        backgroundMusicId = playLoopingSound("background");
        Gdx.app.log("SoundManager", "Started background music with ID: " + backgroundMusicId);
    }
    
    /**
     * Stop the background music if it's playing
     */
    public void stopBackgroundMusic() {
        if (backgroundMusicId != -1) {
            stopSound("background", backgroundMusicId);
            backgroundMusicId = -1;
        }
    }
    
    /**
     * Pause background music temporarily (can be resumed with resumeBackgroundMusic)
     */
    public void pauseBackgroundMusic() {
        if (backgroundMusicId != -1) {
            Sound bgMusic = sounds.get("background");
            if (bgMusic != null) {
                bgMusic.pause(backgroundMusicId);
                Gdx.app.log("SoundManager", "Paused background music");
            }
        }
    }
    
    /**
     * Resume background music after it was paused
     */
    public void resumeBackgroundMusic() {
        if (backgroundMusicId != -1) {
            Sound bgMusic = sounds.get("background");
            if (bgMusic != null) {
                bgMusic.resume(backgroundMusicId);
                Gdx.app.log("SoundManager", "Resumed background music");
            }
        }
    }
    
    /**
     * Dispose of all sounds
     */
    @Override
    public void dispose() {
        // Stop background music before disposing
        stopBackgroundMusic();
        
        for (Sound sound : sounds.values()) {
            sound.dispose();
        }
        sounds.clear();
    }
}
