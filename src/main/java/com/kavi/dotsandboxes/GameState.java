package com.kavi.dotsandboxes;

import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import java.util.HashMap;
import java.util.Map;

public class GameState {
    private final int gridSize;
    private final Line[][] hLines;
    private final Line[][] vLines;
    private final Rectangle[][] boxes;
    private final Map<Line, Boolean> lineDrawn;
    private final Map<Rectangle, Player> boxOwner;
    private int boxCounter;
    private Player currentPlayer;
    private final Player player1;
    private final Player player2;

    public GameState(int gridSize, Player player1, Player player2) {
        this.gridSize = gridSize;
        this.player1 = player1;
        this.player2 = player2;
        this.currentPlayer = player1;
        
        hLines = new Line[gridSize][gridSize-1];
        vLines = new Line[gridSize-1][gridSize];
        boxes = new Rectangle[gridSize][gridSize];
        lineDrawn = new HashMap<>();
        boxOwner = new HashMap<>();
        boxCounter = (gridSize-1) * (gridSize-1);
    }

    public int getGridSize() {
        return gridSize;
    }

    public Line[][] getHLines() {
        return hLines;
    }

    public Line[][] getVLines() {
        return vLines;
    }

    public Rectangle[][] getBoxes() {
        return boxes;
    }

    public Map<Line, Boolean> getLineDrawn() {
        return lineDrawn;
    }

    public Map<Rectangle, Player> getBoxOwner() {
        return boxOwner;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public Player getPlayer1() {
        return player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    public int getBoxCounter() {
        return boxCounter;
    }

    public void setBoxCounter(int boxCounter) {
        this.boxCounter = boxCounter;
    }

    public void switchPlayer() {
        currentPlayer = (currentPlayer == player1) ? player2 : player1;
    }

    public boolean isGameOver() {
        return boxCounter < 1;
    }

    public Player getWinner() {
        if (player1.getScore() > player2.getScore()) {
            return player1;
        } else if (player2.getScore() > player1.getScore()) {
            return player2;
        }
        return null; // Tie
    }
} 