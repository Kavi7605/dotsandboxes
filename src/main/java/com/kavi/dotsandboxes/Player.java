package com.kavi.dotsandboxes;

import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import java.util.Map;

public class Player {
    private String name;
    private Color color;
    private int score;
    private Map<Line, Boolean> horizontalLines;
    private Map<Line, Boolean> verticalLines;
    private Line[][] horizontalLineArray;
    private Line[][] verticalLineArray;
    private int gridSize;

    public Player(String name, Color color) {
        this.name = name;
        this.color = color;
        this.score = 0;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public int getScore() {
        return score;
    }

    public void increaseScore(int points) {
        this.score += points;
    }

    public void setGameState(Map<Line, Boolean> horizontalLines, Map<Line, Boolean> verticalLines, 
                           Line[][] horizontalLineArray, Line[][] verticalLineArray, int gridSize) {
        this.horizontalLines = horizontalLines;
        this.verticalLines = verticalLines;
        this.horizontalLineArray = horizontalLineArray;
        this.verticalLineArray = verticalLineArray;
        this.gridSize = gridSize;
    }
}