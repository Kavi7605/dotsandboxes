package com.kavi.dotsandboxes;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.paint.Color;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.application.Platform;
import javafx.scene.shape.StrokeLineCap;

public class GameBoard {
    private final GameState gameState;
    private final SoundManager soundManager;
    private final Scene mainMenuScene;
    private final Stage primaryStage;
    private Label infoLabel;
    private Label player1ScoreLabel;
    private Label player2ScoreLabel;
    private Label botThinkingLabel;
    private ProgressIndicator botThinkingIndicator;
    private PauseTransition botDelay;
    private boolean isSinglePlayer;
    private Player botPlayer;

    public GameBoard(GameState gameState, SoundManager soundManager, Scene mainMenuScene, Stage primaryStage) {
        this.gameState = gameState;
        this.soundManager = soundManager;
        this.mainMenuScene = mainMenuScene;
        this.primaryStage = primaryStage;
        this.isSinglePlayer = false;
        this.botPlayer = null;
    }

    public void setSinglePlayer(boolean isSinglePlayer) {
        this.isSinglePlayer = isSinglePlayer;
    }

    public void setBotPlayer(Player botPlayer) {
        this.botPlayer = botPlayer;
    }

    public Scene createGameBoardScene() {
        GridPane gridPane = createGridPane();
        BorderPane borderPane = createBorderPane(gridPane);
        return new Scene(borderPane, Screen.getPrimary().getVisualBounds().getWidth(), 
                        Screen.getPrimary().getVisualBounds().getHeight());
    }

    private GridPane createGridPane() {
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(GameConstants.LINE_THICKNESS + 50, 0, 0, GameConstants.LINE_THICKNESS + 50));

        for (int row = 0; row < gameState.getGridSize(); row++) {
            for (int col = 0; col < gameState.getGridSize(); col++) {
                AnchorPane stack = createCell(row, col);
                gridPane.add(stack, col, row);
            }
        }
        return gridPane;
    }

    private AnchorPane createCell(int row, int col) {
        AnchorPane stack = new AnchorPane();
        
        if (col < gameState.getGridSize() - 1 && row < gameState.getGridSize() - 1) {
            Rectangle square = createBox(row, col);
            stack.getChildren().add(square);
            gameState.getBoxes()[row][col] = square;
        }

        if (col < gameState.getGridSize() - 1) {
            Line lineUp = createHorizontalLine(row, col);
            stack.getChildren().add(lineUp);
            gameState.getHLines()[row][col] = lineUp;
        }

        if (row < gameState.getGridSize() - 1) {
            Line lineLeft = createVerticalLine(row, col);
            stack.getChildren().add(lineLeft);
            gameState.getVLines()[row][col] = lineLeft;
        }

        Circle dot = createDot();
        stack.getChildren().add(dot);

        return stack;
    }

    private Rectangle createBox(int row, int col) {
        Rectangle square = new Rectangle(0, 0, GameConstants.SPACING, GameConstants.SPACING);
        square.setFill(((row + col) % 2 == 0) ? 
            Color.valueOf("#E6E6FA") : 
            Color.valueOf("#D8BFD8"));
        square.setStrokeWidth(0);
        return square;
    }

    private Line createHorizontalLine(int row, int col) {
        Line line = new Line(GameConstants.LINE_THICKNESS, 0, 
                           GameConstants.SPACING - GameConstants.LINE_THICKNESS, 0);
        styleLine(line, true);
        gameState.getLineDrawn().put(line, false);
        return line;
    }

    private Line createVerticalLine(int row, int col) {
        Line line = new Line(0, GameConstants.LINE_THICKNESS, 
                           0, GameConstants.SPACING - GameConstants.LINE_THICKNESS);
        styleLine(line, false);
        gameState.getLineDrawn().put(line, false);
        return line;
    }

    private Circle createDot() {
        Circle dot = new Circle(0, 0, GameConstants.LINE_THICKNESS * 2);
        dot.setFill(GameConstants.DARKSLATE_BLUE);
        dot.setEffect(new DropShadow(5, Color.rgb(0, 0, 0, 0.3)));
        return dot;
    }

    private BorderPane createBorderPane(GridPane gridPane) {
        BorderPane borderPane = new BorderPane();
        borderPane.setStyle(GameConstants.MAIN_BACKGROUND);
        
        VBox topBar = createTopBar();
        HBox gameBoard = createGameBoard(gridPane);

        borderPane.setTop(topBar);
        borderPane.setCenter(gameBoard);

        updateScores();
        return borderPane;
    }

    private VBox createTopBar() {
        VBox topBar = new VBox(10);
        topBar.setPadding(new Insets(20));
        topBar.setStyle("-fx-background-color: rgba(0, 0, 0, 0.2); -fx-background-radius: 10;");

        HBox playerInfo = createPlayerInfo();
        topBar.getChildren().add(playerInfo);

        return topBar;
    }

    private HBox createPlayerInfo() {
        HBox playerInfo = new HBox(40);
        playerInfo.setAlignment(Pos.CENTER);

        VBox player1Box = createPlayerBox(gameState.getPlayer1());
        VBox player2Box = createPlayerBox(gameState.getPlayer2());
        
        infoLabel = new Label(gameState.getCurrentPlayer().getName() + "'s turn");
        styleInfoLabel(infoLabel);

        Button backButton = createBackButton();

        playerInfo.getChildren().addAll(backButton, player1Box, infoLabel, player2Box);
        return playerInfo;
    }

    private VBox createPlayerBox(Player player) {
        VBox playerBox = new VBox(5);
        
        Label nameLabel = new Label(player.getName());
        styleLabel(nameLabel);
        
        Label scoreLabel = new Label(String.valueOf(player.getScore()));
        styleScoreLabel(scoreLabel, player.getColor());
        
        playerBox.getChildren().addAll(nameLabel, scoreLabel);
        return playerBox;
    }

    private Button createBackButton() {
        Button backButton = new Button("←");
        styleButton(backButton, "#2196F3", "#1976D2");
        backButton.setOnAction(event -> {
            primaryStage.setScene(mainMenuScene);
            soundManager.playSound(GameConstants.SOUND_BUTTON);
        });
        return backButton;
    }

    private HBox createGameBoard(GridPane gridPane) {
        HBox gameBoard = new HBox();
        gameBoard.setAlignment(Pos.CENTER);
        gameBoard.getChildren().add(gridPane);
        return gameBoard;
    }

    private void styleLine(Line line, boolean isLineUp) {
        line.setStroke(GameConstants.TRANSPARENT);
        line.setStrokeLineCap(StrokeLineCap.ROUND);
        line.setStrokeWidth(GameConstants.LINE_THICKNESS * 2);

        line.setOnMouseEntered(event -> {
            if (!gameState.getLineDrawn().get(line) && 
                (!isSinglePlayer || gameState.getCurrentPlayer() != botPlayer)) {
                line.setStroke(gameState.getCurrentPlayer().getColor());
                line.setEffect(new Glow(10));
            }
        });

        line.setOnMouseExited(event -> {
            if (!gameState.getLineDrawn().get(line) && 
                (!isSinglePlayer || gameState.getCurrentPlayer() != botPlayer)) {
                line.setStroke(GameConstants.TRANSPARENT);
                line.setEffect(null);
            }
        });

        line.setOnMouseClicked(event -> handleLineClick(line, isLineUp));
    }

    private void handleLineClick(Line line, boolean isLineUp) {
        if (gameState.getLineDrawn().get(line) || 
            (isSinglePlayer && gameState.getCurrentPlayer() == botPlayer)) {
            return;
        }

        soundManager.playSound(GameConstants.SOUND_LINE);
        GameLogic.updateLine(line, gameState.getCurrentPlayer());
        gameState.getLineDrawn().put(line, true);

        int boxCompleted = GameLogic.checkForCompletedBox(line, isLineUp, gameState);
        if (boxCompleted > 0) {
            soundManager.playSound(GameConstants.SOUND_BOX);
        }
        
        if (boxCompleted == 0) {
            gameState.switchPlayer();
            infoLabel.setText(gameState.getCurrentPlayer().getName() + "'s turn");
            
            FadeTransition fade = new FadeTransition(Duration.millis(300), infoLabel);
            fade.setFromValue(0.5);
            fade.setToValue(1.0);
            fade.play();

            if (isSinglePlayer && gameState.getCurrentPlayer() == botPlayer) {
                handleBotMove();
            }
        }

        updateScores();
        gameState.setBoxCounter(gameState.getBoxCounter() - boxCompleted);
        
        if (gameState.isGameOver()) {
            handleGameOver();
        }
    }

    private void handleBotMove() {
        if (botPlayer != null) {
            botPlayer.setGameState(gameState.getLineDrawn(), 
                                 gameState.getLineDrawn(),
                                 gameState.getHLines(), 
                                 gameState.getVLines(), 
                                 gameState.getGridSize());
            
            botThinkingLabel.setVisible(true);
            botThinkingIndicator.setVisible(true);
            botDelay.play();
        }
    }

    private void handleGameOver() {
        Player winner = gameState.getWinner();
        final String winMsg;
        
        if (winner == gameState.getPlayer1()) {
            winMsg = gameState.getPlayer1().getName() + " Wins!!";
            soundManager.playSound(GameConstants.SOUND_WIN);
        } else if (winner == gameState.getPlayer2()) {
            winMsg = gameState.getPlayer2().getName() + " Wins!!";
            soundManager.playSound(GameConstants.SOUND_LOSE);
        } else {
            winMsg = "It was a tie.";
            soundManager.playSound(GameConstants.SOUND_TIE);
        }
        
        Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.INFORMATION, winMsg, new ButtonType("Back to Main Menu"));
            alert.setTitle("Game Over");
            alert.setHeaderText(null);
            alert.setContentText(winMsg);
            alert.getDialogPane().setStyle("-fx-background-color: #1a237e;");
            alert.getDialogPane().lookup(".content.label").setStyle("-fx-text-fill: white; -fx-font-size: 16px;");
            alert.showAndWait();
            primaryStage.setScene(mainMenuScene);
        });
    }

    private void updateScores() {
        player1ScoreLabel.setText(String.valueOf(gameState.getPlayer1().getScore()));
        player2ScoreLabel.setText(String.valueOf(gameState.getPlayer2().getScore()));
    }

    private void styleLabel(Label label) {
        label.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        label.setTextFill(Color.WHITE);
        label.setEffect(new DropShadow(5, Color.rgb(0, 0, 0, 0.3)));
    }

    private void styleScoreLabel(Label label, Color color) {
        label.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        label.setTextFill(color);
        label.setEffect(new Glow(10));
    }

    private void styleInfoLabel(Label label) {
        label.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        label.setTextFill(Color.WHITE);
        label.setEffect(new DropShadow(5, Color.rgb(0, 0, 0, 0.3)));
    }

    private void styleButton(Button button, String color, String hoverColor) {
        button.setStyle(GameConstants.BUTTON_STYLE + "-fx-background-color: " + color + ";");
        button.setEffect(new DropShadow(10, Color.rgb(0, 0, 0, 0.3)));
        
        button.setOnMouseEntered(e -> {
            button.setStyle(GameConstants.BUTTON_STYLE + "-fx-background-color: " + hoverColor + ";");
            button.setEffect(new DropShadow(15, Color.rgb(0, 0, 0, 0.4)));
        });
        
        button.setOnMouseExited(e -> {
            button.setStyle(GameConstants.BUTTON_STYLE + "-fx-background-color: " + color + ";");
            button.setEffect(new DropShadow(10, Color.rgb(0, 0, 0, 0.3)));
        });

        EventHandler<ActionEvent> originalAction = button.getOnAction();
        button.setOnAction(event -> {
            soundManager.playSound(GameConstants.SOUND_BUTTON);
            if (originalAction != null) {
                originalAction.handle(event);
            }
        });
    }
} 