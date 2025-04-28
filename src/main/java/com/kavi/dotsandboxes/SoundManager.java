package com.kavi.dotsandboxes;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class SoundManager {
    private static SoundManager instance;
    private Map<String, MediaPlayer> soundPlayers;
    private boolean isMuted = false;

    private SoundManager() {
        soundPlayers = new HashMap<>();
        loadSounds();
    }

    public static SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }
        return instance;
    }

    private void loadSounds() {
        try {
            // Line drawing sound
            Media lineSound = new Media(new File("sounds/line.mp3").toURI().toString());
            soundPlayers.put("line", new MediaPlayer(lineSound));

            // Box completion sound
            Media boxSound = new Media(new File("sounds/box.mp3").toURI().toString());
            soundPlayers.put("box", new MediaPlayer(boxSound));

            // Game win sound
            Media winSound = new Media(new File("sounds/win.mp3").toURI().toString());
            soundPlayers.put("win", new MediaPlayer(winSound));

            // Game lose sound
            Media loseSound = new Media(new File("sounds/lose.mp3").toURI().toString());
            soundPlayers.put("lose", new MediaPlayer(loseSound));

            // Game tie sound
            Media tieSound = new Media(new File("sounds/tie.mp3").toURI().toString());
            soundPlayers.put("tie", new MediaPlayer(tieSound));

            // Button click sound
            Media buttonSound = new Media(new File("sounds/button.mp3").toURI().toString());
            soundPlayers.put("button", new MediaPlayer(buttonSound));

            // Set volume for all sounds
            for (MediaPlayer player : soundPlayers.values()) {
                player.setVolume(0.5);
            }
        } catch (Exception e) {
            System.err.println("Error loading sounds: " + e.getMessage());
        }
    }

    public void playSound(String soundName) {
        if (!isMuted && soundPlayers.containsKey(soundName)) {
            MediaPlayer player = soundPlayers.get(soundName);
            player.stop();
            player.play();
        }
    }

    public void toggleMute() {
        isMuted = !isMuted;
    }

    public boolean isMuted() {
        return isMuted;
    }

    public void setVolume(double volume) {
        for (MediaPlayer player : soundPlayers.values()) {
            player.setVolume(volume);
        }
    }
} 