package com.kavi.dotsandboxes;

import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

import java.util.Map;

import javafx.scene.effect.Glow;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

public class GameLogic {
    private GameLogic() {} // Private constructor for utility class

    public static int checkForCompletedBox(Line line, boolean isLineUp, GameState gameState) {
        int boxCompleted = 0;
        int row = GridPane.getRowIndex(line.getParent());
        int col = GridPane.getColumnIndex(line.getParent());
        Map<Line, Boolean> lineDrawn = gameState.getLineDrawn();
        Line[][] vLines = gameState.getVLines();
        Line[][] hLines = gameState.getHLines();
        Rectangle[][] boxes = gameState.getBoxes();
        Map<Rectangle, Player> boxOwner = gameState.getBoxOwner();
        Player currentPlayer = gameState.getCurrentPlayer();

        if(isLineUp) {
            if(row > 0) {
                if (lineDrawn.get(vLines[row-1][col]) && 
                    lineDrawn.get(hLines[row-1][col]) && 
                    lineDrawn.get(vLines[row-1][col+1])) {
                    boxes[row-1][col].setFill(currentPlayer.getColor());
                    boxOwner.put(boxes[row-1][col], currentPlayer);
                    currentPlayer.increaseScore(1);
                    boxCompleted++;
                }
            }

            if(row < gameState.getGridSize()-1) {
                if (lineDrawn.get(vLines[row][col]) && 
                    lineDrawn.get(hLines[row+1][col]) && 
                    lineDrawn.get(vLines[row][col+1])) {
                    boxes[row][col].setFill(currentPlayer.getColor());
                    boxOwner.put(boxes[row][col], currentPlayer);
                    currentPlayer.increaseScore(1);
                    boxCompleted++;
                }
            }
        } else {
            if(col > 0) {
                if (lineDrawn.get(hLines[row][col-1]) && 
                    lineDrawn.get(vLines[row][col-1]) && 
                    lineDrawn.get(hLines[row+1][col-1])) {
                    boxes[row][col-1].setFill(currentPlayer.getColor());
                    boxOwner.put(boxes[row][col-1], currentPlayer);
                    currentPlayer.increaseScore(1);
                    boxCompleted++;
                }
            }

            if(col < gameState.getGridSize()-1) {
                if (lineDrawn.get(hLines[row][col]) && 
                    lineDrawn.get(vLines[row][col+1]) && 
                    lineDrawn.get(hLines[row+1][col])) {
                    boxes[row][col].setFill(currentPlayer.getColor());
                    boxOwner.put(boxes[row][col], currentPlayer);
                    currentPlayer.increaseScore(1);
                    boxCompleted++;
                }
            }
        }
        return boxCompleted;
    }

    public static void updateLine(Line line, Player currentPlayer) {
        line.setStroke(currentPlayer.getColor());
        line.setEffect(new Glow(5));
    }

    public static void resetLine(Line line) {
        line.setStroke(Color.TRANSPARENT);
        line.setEffect(null);
    }
} 