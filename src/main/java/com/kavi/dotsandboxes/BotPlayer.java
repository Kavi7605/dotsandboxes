package com.kavi.dotsandboxes;

import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.Map;
import java.util.HashMap;

public class BotPlayer extends Player {
    public enum Difficulty {
        EASY,
        MEDIUM,
        HARD
    }

    private Difficulty difficulty;
    private Random random;
    private Map<Line, Boolean> lineDrawn;
    private Line[][] hLines;
    private Line[][] vLines;
    private int gridSize;

    public BotPlayer(String name, Color color, Difficulty difficulty) {
        super(name, color);
        this.difficulty = difficulty;
        this.random = new Random();
    }

    public void setGameState(Map<Line, Boolean> lineDrawn, Line[][] hLines, Line[][] vLines, int gridSize) {
        this.lineDrawn = lineDrawn;
        this.hLines = hLines;
        this.vLines = vLines;
        this.gridSize = gridSize;
    }

    public Line makeMove() {
        List<Line> availableMoves = getAvailableMoves();
        if (availableMoves.isEmpty()) {
            return null;
        }

        switch (difficulty) {
            case EASY:
                return makeEasyMove(availableMoves);
            case MEDIUM:
                return makeMediumMove(availableMoves);
            case HARD:
                return makeHardMove(availableMoves);
            default:
                return makeEasyMove(availableMoves);
        }
    }

    private List<Line> getAvailableMoves() {
        List<Line> availableMoves = new ArrayList<>();
        
        // Check horizontal lines
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize - 1; col++) {
                Line line = hLines[row][col];
                if (!lineDrawn.get(line)) {
                    availableMoves.add(line);
                }
            }
        }
        
        // Check vertical lines
        for (int row = 0; row < gridSize - 1; row++) {
            for (int col = 0; col < gridSize; col++) {
                Line line = vLines[row][col];
                if (!lineDrawn.get(line)) {
                    availableMoves.add(line);
                }
            }
        }
        
        return availableMoves;
    }

    private Line makeEasyMove(List<Line> availableMoves) {
        return availableMoves.get(random.nextInt(availableMoves.size()));
    }

    private Line makeMediumMove(List<Line> availableMoves) {
        // First, look for moves that would complete a box
        for (Line line : availableMoves) {
            if (wouldCompleteBox(line)) {
                return line;
            }
        }
        
        // If no box-completing moves, make a random move
        return makeEasyMove(availableMoves);
    }

    private Line makeHardMove(List<Line> availableMoves) {
        // First, look for moves that would complete a box
        for (Line line : availableMoves) {
            if (wouldCompleteBox(line)) {
                return line;
            }
        }
        
        // If no box-completing moves, avoid moves that could give opponent an easy box
        List<Line> safeMoves = new ArrayList<>();
        for (Line line : availableMoves) {
            if (!wouldGiveOpponentBox(line)) {
                safeMoves.add(line);
            }
        }
        
        // If there are safe moves, choose randomly from them
        if (!safeMoves.isEmpty()) {
            return safeMoves.get(random.nextInt(safeMoves.size()));
        }
        
        // If no safe moves, make a random move
        return makeEasyMove(availableMoves);
    }

    private boolean wouldCompleteBox(Line line) {
        // Check if drawing this line would complete a box
        int row = GridPane.getRowIndex(line.getParent());
        int col = GridPane.getColumnIndex(line.getParent());
        boolean isHorizontal = line.getStartY() == line.getEndY();

        if (isHorizontal) {
            // Check box above
            if (row > 0) {
                if (lineDrawn.get(vLines[row-1][col]) && 
                    lineDrawn.get(hLines[row-1][col]) && 
                    lineDrawn.get(vLines[row-1][col+1])) {
                    return true;
                }
            }
            // Check box below
            if (row < gridSize-1) {
                if (lineDrawn.get(vLines[row][col]) && 
                    lineDrawn.get(hLines[row+1][col]) && 
                    lineDrawn.get(vLines[row][col+1])) {
                    return true;
                }
            }
        } else {
            // Check box to the left
            if (col > 0) {
                if (lineDrawn.get(hLines[row][col-1]) && 
                    lineDrawn.get(vLines[row][col-1]) && 
                    lineDrawn.get(hLines[row+1][col-1])) {
                    return true;
                }
            }
            // Check box to the right
            if (col < gridSize-1) {
                if (lineDrawn.get(hLines[row][col]) && 
                    lineDrawn.get(vLines[row][col+1]) && 
                    lineDrawn.get(hLines[row+1][col])) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean wouldGiveOpponentBox(Line line) {
        // Check if drawing this line would create a situation where opponent could complete a box
        int row = GridPane.getRowIndex(line.getParent());
        int col = GridPane.getColumnIndex(line.getParent());
        boolean isHorizontal = line.getStartY() == line.getEndY();

        if (isHorizontal) {
            // Check if it would create a box with 3 sides
            if (row > 0) {
                int count = 0;
                if (lineDrawn.get(vLines[row-1][col])) count++;
                if (lineDrawn.get(hLines[row-1][col])) count++;
                if (lineDrawn.get(vLines[row-1][col+1])) count++;
                if (count == 2) return true;
            }
            if (row < gridSize-1) {
                int count = 0;
                if (lineDrawn.get(vLines[row][col])) count++;
                if (lineDrawn.get(hLines[row+1][col])) count++;
                if (lineDrawn.get(vLines[row][col+1])) count++;
                if (count == 2) return true;
            }
        } else {
            // Check if it would create a box with 3 sides
            if (col > 0) {
                int count = 0;
                if (lineDrawn.get(hLines[row][col-1])) count++;
                if (lineDrawn.get(vLines[row][col-1])) count++;
                if (lineDrawn.get(hLines[row+1][col-1])) count++;
                if (count == 2) return true;
            }
            if (col < gridSize-1) {
                int count = 0;
                if (lineDrawn.get(hLines[row][col])) count++;
                if (lineDrawn.get(vLines[row][col+1])) count++;
                if (lineDrawn.get(hLines[row+1][col])) count++;
                if (count == 2) return true;
            }
        }
        return false;
    }
} 