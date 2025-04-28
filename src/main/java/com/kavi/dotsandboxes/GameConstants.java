package com.kavi.dotsandboxes;

import javafx.scene.paint.Color;

public final class GameConstants {
    private GameConstants() {} // Private constructor for utility class

    // UI Constants
    public static final int DEFAULT_GRID_SIZE = 7;
    public static final int SPACING = 50;
    public static final int LINE_THICKNESS = 5;
    public static final int LOGIN_TIMEOUT_SECONDS = 30;

    // Colors
    public static final Color PLAYER1_COLOR = Color.rgb(255, 85, 85);
    public static final Color PLAYER2_COLOR = Color.rgb(85, 170, 255);
    public static final Color DARKSLATE_BLUE = Color.DARKSLATEBLUE;
    public static final Color TRANSPARENT = Color.TRANSPARENT;

    // UI Styles
    public static final String MAIN_BACKGROUND = "-fx-background-color: linear-gradient(to bottom, #1a237e, #0d47a1);";
    public static final String BUTTON_STYLE = "-fx-font-size: 18px; -fx-padding: 15px 30px; -fx-background-radius: 25; -fx-font-weight: bold;";
    public static final String TEXT_FIELD_STYLE = "-fx-font-size: 16px; -fx-padding: 12px; -fx-background-color: rgba(255, 255, 255, 0.9); " +
                                                "-fx-text-fill: #1a237e; -fx-background-radius: 10; -fx-border-radius: 10;";

    // Sound Effects
    public static final String SOUND_LINE = "line";
    public static final String SOUND_BOX = "box";
    public static final String SOUND_WIN = "win";
    public static final String SOUND_LOSE = "lose";
    public static final String SOUND_TIE = "tie";
    public static final String SOUND_BUTTON = "button";

    // Default Names
    public static final String DEFAULT_PLAYER1_NAME = "Player 1";
    public static final String DEFAULT_PLAYER2_NAME = "Player 2";
    public static final String DEFAULT_BOT_NAME = "Bot";
} 