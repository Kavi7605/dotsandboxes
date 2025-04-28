package com.kavi.dotsandboxes;

//Importing classes from JavaFX package
import javafx.application.Application;
import javafx.concurrent.Worker;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.stage.Screen;
import javafx.stage.Stage;
import java.util.HashMap;
import java.util.Map;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.web.WebView;    
import javafx.scene.web.WebEngine;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.animation.FadeTransition;
import javafx.util.Duration;
import javafx.scene.effect.Glow;
import javafx.scene.effect.BlurType;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.IdToken;
import com.google.auth.oauth2.IdTokenProvider;
import com.google.auth.oauth2.UserCredentials;
import java.net.URL;
import java.net.HttpURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;   
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.awt.Desktop;
import javafx.application.Platform;
import fi.iki.elonen.NanoHTTPD;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonArray;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.RadioButton;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;


public class App extends Application implements LoginCallback{  
    //class variable definitions
    // This class defines the primary game elements for the Dots and Boxes game, 
    // including the stage, scenes, grid settings, players, UI elements, and game state tracking.
    private Stage primaryStage;
    private Scene mainMenuScene;
    private static int GRID_SIZE = 7;
    private static String player1Name = "Player 1";
    private static String player2Name = "Player 2";
    private static final int SPACING = 50;
    private static final int LINE_THICKNESS = 5;
    private Label infoLabel = new Label();
    private Rectangle2D screenSize = Screen.getPrimary().getVisualBounds();
    Line[][] vLines, hLines;
    Rectangle[][] boxes;
    private Player player1 = new Player("Player 1", Color.rgb(255, 85, 85));
    private Player player2 = new Player("Player 2", Color.rgb(85, 170, 255));
    private Player currentPlayer = player1;
    private Map<Line, Boolean> lineDrawn = new HashMap<>();
    private Map<Rectangle, Player> boxOwner = new HashMap<>();
    private Label player1ScoreLabel = new Label();
    private Label player2ScoreLabel = new Label();
    private int boxCounter;
    private FacebookLoginHandler facebookLoginServer;
    private Button profileButton;
    private MenuButton profileMenu;
    private UserProfile currentUserProfile;
    private VBox friendsList;
    private ScrollPane friendsScrollPane;
    private boolean isLoggedIn = false;
    private Button FacebookLogin;
    private Timeline loginTimer;
    private static final int LOGIN_TIMEOUT_SECONDS = 30;
    private static boolean isSinglePlayer = false;
    private static BotPlayer.Difficulty botDifficulty = BotPlayer.Difficulty.MEDIUM;
    private BotPlayer botPlayer;
    private Label botThinkingLabel;
    private ProgressIndicator botThinkingIndicator;
    private PauseTransition botDelay;
    private SoundManager soundManager;
    private Button soundToggleButton;

    // Main method to run the application
    // This method is the entry point for the JavaFX application.
    public static void main(String[] args) {
        launch(args);
    }

    // Start method to initialize the primary stage
    // This method initializes the primary stage and sets the main menu scene as the initial scene.
    @Override
    public void start(Stage primaryStage) {
        soundManager = SoundManager.getInstance();
        // FirebaseInitializer.initialize();
        this.primaryStage = primaryStage;
        mainMenuScene = createMainMenuScene();
        primaryStage.setTitle("Dots and Boxes"); 
        primaryStage.setScene(mainMenuScene);     
        primaryStage.setMaximized(true);
        primaryStage.show();
        primaryStage.setOnCloseRequest(event -> {
            cleanupResources(); // Cleanup resources on close
        });
        primaryStage.getIcons().add(new Image("icon.png"));
    }

    // Create main menu scene
    // This method creates the main menu scene with the title, subtitle, and buttons for starting the game or exiting the application.
    private Scene createMainMenuScene() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #1a237e, #0d47a1);");

        VBox center = new VBox(30);
        center.setAlignment(Pos.CENTER);

        Label titleLabel = new Label("Dots and Boxes");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 48));
        titleLabel.setTextFill(Color.WHITE);
        titleLabel.setEffect(new DropShadow(20, Color.rgb(0, 0, 0, 0.5)));

        FadeTransition fadeIn = new FadeTransition(Duration.millis(1000), titleLabel);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.play();

        Glow glow = new Glow(0.0);
        glow.setLevel(0.0);
        titleLabel.setEffect(glow);

        Timeline glowAnimation = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(glow.levelProperty(), 0.0)),
            new KeyFrame(Duration.seconds(1), new KeyValue(glow.levelProperty(), 1.0)),
            new KeyFrame(Duration.seconds(2), new KeyValue(glow.levelProperty(), 0.0))
        );
        glowAnimation.setCycleCount(Timeline.INDEFINITE);
        glowAnimation.play();

        Label subtitleLabel = new Label("A Classic Strategy Game");
        subtitleLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 20));
        subtitleLabel.setTextFill(Color.rgb(255, 255, 255, 0.8));
        subtitleLabel.setEffect(new DropShadow(10, Color.rgb(0, 0, 0, 0.3)));

        FacebookLogin = new Button("Login with Facebook");
        FacebookLogin.setStyle("-fx-font-size: 16px; -fx-padding: 15px 30px; " +
                            "-fx-background-color: #4267B2; -fx-text-fill: white; " +
                            "-fx-background-radius: 25; -fx-font-weight: bold; " +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 0);");

        ImageView facebookIcon = new ImageView(new Image("https://upload.wikimedia.org/wikipedia/commons/5/51/Facebook_f_logo_%282019%29.svg"));
        facebookIcon.setFitHeight(24);
        facebookIcon.setFitWidth(24);
        facebookIcon.setPreserveRatio(true);
        FacebookLogin.setGraphic(facebookIcon);
        FacebookLogin.setContentDisplay(ContentDisplay.CENTER);
        FacebookLogin.setGraphicTextGap(10);

        FacebookLogin.setOnMouseEntered(e -> {
            FacebookLogin.setStyle("-fx-background-color: #3B5998; -fx-text-fill: white; " +
                                "-fx-background-radius: 25; -fx-font-size: 16px; " +
                                "-fx-padding: 15px 30px; -fx-font-weight: bold; " +
                                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 15, 0, 0, 0);");
        });

        FacebookLogin.setOnMouseExited(e -> {
            FacebookLogin.setStyle("-fx-background-color: #4267B2; -fx-text-fill: white; " +
                                "-fx-background-radius: 25; -fx-font-size: 16px; " +
                                "-fx-padding: 15px 30px; -fx-font-weight: bold; " +
                                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 0);");
        });

        FacebookLogin.setOnAction(event -> {
            soundManager.playSound("button");
            startLoginProcess();
        });

        Button playButton = new Button("Play Game");
        styleButton(playButton, "#4CAF50", "#388E3C");

        Button quitButton = new Button("Exit");
        styleButton(quitButton, "#F44336", "#D32F2F");

        playButton.setOnAction(event -> {
            soundManager.playSound("button");
            primaryStage.setScene(optionScene());
        });
        quitButton.setOnAction(event -> {
            soundManager.playSound("button");
            cleanupResources();
        });

        center.getChildren().addAll(titleLabel, subtitleLabel, playButton, quitButton);

        // Add sound toggle button
        soundToggleButton = new Button("🔊");
        soundToggleButton.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 24px;");
        soundToggleButton.setOnAction(event -> {
            soundManager.toggleMute();
            soundToggleButton.setText(soundManager.isMuted() ? "🔇" : "🔊");
            soundManager.playSound("button");
        });

        HBox topRight = new HBox(20, soundToggleButton, FacebookLogin);
        topRight.setAlignment(Pos.TOP_RIGHT);
        topRight.setPadding(new Insets(20));

        root.setTop(topRight);
        root.setCenter(center);

        return new Scene(root, screenSize.getWidth(), screenSize.getHeight());
    }


    private void startLoginProcess() {
        // Disable the login button
        FacebookLogin.setDisable(true);
        System.out.println("Starting login process");
        // Start the login server
        facebookLoginServer = new FacebookLoginHandler(this);
        facebookLoginServer.handleLogin();
        
        // Create and start the timer
        loginTimer = new Timeline(
            new KeyFrame(Duration.seconds(LOGIN_TIMEOUT_SECONDS), e -> {
                if (!isLoggedIn) {
                    handleLoginTimeout();
                }
            })
        );
        loginTimer.play();
    }

    private void handleLoginTimeout() {
        if (facebookLoginServer != null) {
            facebookLoginServer.stop();
            facebookLoginServer = null;
        }
        FacebookLogin.setDisable(false);
    }

    @Override
    public void onLoginFailed(String errorMessage) {
        System.out.println("Login failed: " + errorMessage);
        Platform.runLater(() -> {
            // StackPane root = (StackPane) primaryStage.getScene().getRoot();
            // root.getChildren().removeAll(dimOverlay, loadingBox);
            if (loginTimer != null) {
                loginTimer.stop();
            }
            handleLoginTimeout();

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Login Failed");
            alert.setHeaderText(null);
            alert.setContentText("Error: " + errorMessage);
            alert.showAndWait();
            facebookLoginServer.stop();
        });
    }

    @Override
    public void onLoginSuccessful() {
        System.out.println("Login successful!");
        Platform.runLater(() -> {
            // StackPane root = (StackPane) primaryStage.getScene().getRoot();
            // root.getChildren().removeAll(dimOverlay, loadingBox);
            if (loginTimer != null) {
                loginTimer.stop();
            }
            currentUserProfile = facebookLoginServer.getUserProfile();
            isLoggedIn = true;
            updateProfileButton();
            
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Login Successful");
            alert.setHeaderText(null);
            alert.setContentText("Welcome, " + currentUserProfile.getName() + "!");
            alert.showAndWait();
            facebookLoginServer.stop();
        });
    }

    private void updateProfileButton() {
        if (isLoggedIn && currentUserProfile != null) {
            // Remove the Facebook login button
            FacebookLogin.setVisible(false);
    
            // Create enhanced profile button
            try {
                // Get profile picture from user profile
                Image profileImage = currentUserProfile.getProfilePicture();
                ImageView profileImageView = new ImageView(profileImage != null ? profileImage : 
                    new Image("https://cdn-icons-png.flaticon.com/512/149/149071.png"));
                profileImageView.setFitHeight(50);
                profileImageView.setFitWidth(50);
                profileImageView.setPreserveRatio(true);
                
                // Create circular clip for profile image
                Circle clip = new Circle(25, 25, 25);
                profileImageView.setClip(clip);
                
                profileButton = new Button();
                profileButton.setGraphic(profileImageView);
                profileButton.setStyle("-fx-background-color: rgba(255, 255, 255, 0.2); " +
                                         "-fx-padding: 10; -fx-background-radius: 30; " +
                                         "-fx-border-color: white; -fx-border-width: 2; " +
                                         "-fx-border-radius: 30;");
                profileButton.setEffect(new DropShadow(10, Color.rgb(0, 0, 0, 0.3)));
    
                // Enhanced tooltip with user info
                Tooltip tooltip = new Tooltip();
                tooltip.setStyle("-fx-font-size: 14px; -fx-background-color: rgba(0, 0, 0, 0.8); " +
                                 "-fx-text-fill: white; -fx-padding: 10px;");
                tooltip.setText("Name: " + currentUserProfile.getName() + "\nEmail: " + currentUserProfile.getEmail());
                profileButton.setTooltip(tooltip);
    
                // Create enhanced context menu
                ContextMenu profileMenu = new ContextMenu();
    
                // Create menu items
                MenuItem friendsItem = new MenuItem("Friends");
                MenuItem leaderboardItem = new MenuItem("Leaderboard");
                MenuItem logoutItem = new MenuItem("Logout");
    
                // Add icons to menu items
                ImageView friendsIcon = new ImageView(new Image("https://cdn-icons-png.flaticon.com/512/1077/1077114.png"));
                ImageView leaderboardIcon = new ImageView(new Image("https://cdn-icons-png.flaticon.com/512/2103/2103633.png"));
                ImageView logoutIcon = new ImageView(new Image("https://cdn-icons-png.flaticon.com/512/1828/1828479.png"));
    
                friendsIcon.setFitHeight(20);
                friendsIcon.setFitWidth(20);
                leaderboardIcon.setFitHeight(20);
                leaderboardIcon.setFitWidth(20);
                logoutIcon.setFitHeight(20);
                logoutIcon.setFitWidth(20);
    
                friendsItem.setGraphic(friendsIcon);
                leaderboardItem.setGraphic(leaderboardIcon);
                logoutItem.setGraphic(logoutIcon);
    
                // Style menu items
                String menuItemStyle = "-fx-font-size: 14px; -fx-padding: 10px 20px; -fx-background-color: #D3D3D3;";
                friendsItem.setStyle(menuItemStyle);
                leaderboardItem.setStyle(menuItemStyle);
                logoutItem.setStyle(menuItemStyle);
    
                // Set actions for menu items
                logoutItem.setOnAction(e -> handleLogout());
                friendsItem.setOnAction(e -> showFriendsList());
                leaderboardItem.setOnAction(e -> showLeaderboard());
    
                // Add items to the context menu
                profileMenu.getItems().addAll(friendsItem, leaderboardItem, new SeparatorMenuItem(), logoutItem);
    
                // Show context menu on button click
                profileButton.setOnAction(e -> profileMenu.show(profileButton, Side.BOTTOM, 0, 0));
    
                // Add profile button to the top right corner of the scene
                BorderPane mainLayout = (BorderPane) mainMenuScene.getRoot();
                mainLayout.setTop (profileButton);
                mainLayout.getChildren().remove(FacebookLogin);
                BorderPane.setAlignment(profileButton, Pos.TOP_RIGHT);
            } catch (Exception e) {
                System.out.println("Error loading profile picture: " + e.getMessage());
                e.printStackTrace();
                // Use default profile picture on error
                Image defaultProfileImage = new Image("https://cdn-icons-png.flaticon.com/512/149/149071.png");
                ImageView defaultProfileImageView = new ImageView(defaultProfileImage);
                defaultProfileImageView.setFitHeight(50);
                defaultProfileImageView.setFitWidth(50);
                defaultProfileImageView.setPreserveRatio(true);
    
                Circle clip = new Circle(25, 25, 25);
                defaultProfileImageView.setClip(clip);
    
                profileButton = new Button();
                profileButton.setGraphic(defaultProfileImageView);
                profileButton.setStyle("-fx-background-color: rgba(255, 255, 255, 0.2); " +
                                         "-fx-padding: 10; -fx-background-radius: 30; " +
                                         "-fx-border-color: white; -fx-border-width: 2; " +
                                         "-fx-border-radius: 30;");
                profileButton.setEffect(new DropShadow(10, Color.rgb(0, 0, 0, 0.3)));
    
                // Add profile button to the top right corner of the scene
                BorderPane mainLayout = (BorderPane) mainMenuScene.getRoot();
                mainLayout.setTop(profileButton);
                mainLayout.getChildren().remove(FacebookLogin);
                BorderPane.setAlignment(profileButton, Pos.TOP_RIGHT);
            }
        }
    }

    private void handleLogout() {
        soundManager.playSound("button");
        isLoggedIn = false;
        currentUserProfile = null;
        facebookLoginServer = null;
        
        // Remove profile button and add back Facebook login button
        BorderPane center = (BorderPane) mainMenuScene.getRoot();
        center.getChildren().remove(profileButton);

        center.getChildren().add(FacebookLogin);
        FacebookLogin.setVisible(true);
        FacebookLogin.setDisable(false);
    }

    private void showFriendsList() {
        // Create enhanced friends list scene
        VBox friendsContainer = new VBox(20);
        friendsContainer.setPadding(new Insets(30));
        friendsContainer.setStyle("-fx-background-color: rgba(255, 255, 255, 0.95); " +
                                "-fx-background-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 20, 0, 0, 0);");
        
        // Enhanced title
        Label title = new Label("Friends List");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        title.setTextFill(Color.DARKSLATEBLUE);
        title.setEffect(new DropShadow(5, Color.rgb(0, 0, 0, 0.2)));
        
        // Create search bar
        TextField searchField = new TextField();
        searchField.setPromptText("Search friends...");
        searchField.setStyle("-fx-font-size: 14px; -fx-padding: 10px; " +
                           "-fx-background-color: white; -fx-background-radius: 10; " +
                           "-fx-border-color: #1a237e; -fx-border-width: 2; " +
                           "-fx-border-radius: 10;");
        searchField.setMaxWidth(300);
        
        friendsList = new VBox(15);
        friendsScrollPane = new ScrollPane(friendsList);
        friendsScrollPane.setFitToWidth(true);
        friendsScrollPane.setMaxHeight(500);
        friendsScrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        
        // Fetch and display friends
        fetchAndDisplayFriends();
        
        friendsContainer.getChildren().addAll(title, searchField, friendsScrollPane);
        
        Scene friendsScene = new Scene(friendsContainer, 500, 600);
        Stage friendsStage = new Stage();
        friendsStage.setTitle("Friends List");
        friendsStage.setScene(friendsScene);
        friendsStage.show();
    }

    private void fetchAndDisplayFriends() {
        if (currentUserProfile != null) {
            try {
                // Clear existing friends list
                friendsList.getChildren().clear();
                
                // Add enhanced loading indicator
                HBox loadingBox = new HBox(10);
                loadingBox.setAlignment(Pos.CENTER);
                loadingBox.setPadding(new Insets(20));
                
                ProgressIndicator loadingIndicator = new ProgressIndicator();
                loadingIndicator.setStyle("-fx-progress-color: #1a237e;");
                
                Label loadingLabel = new Label("Loading friends...");
                loadingLabel.setStyle("-fx-text-fill: #1a237e; -fx-font-size: 16px; -fx-font-weight: bold;");
                
                loadingBox.getChildren().addAll(loadingIndicator, loadingLabel);
                friendsList.getChildren().add(loadingBox);

                // Fetch friends using Facebook Graph API
                String friendsUrl = "https://graph.facebook.com/me/friends?fields=id,name,picture&access_token=" + currentUserProfile.getAccessToken();
                HttpURLConnection connection = (HttpURLConnection) new URL(friendsUrl).openConnection();
                connection.setRequestMethod("GET");
                
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }
                    in.close();

                    // Parse the JSON response
                    JsonObject jsonObject = JsonParser.parseString(response.toString()).getAsJsonObject();
                    JsonArray data = jsonObject.getAsJsonArray("data");
                    
                    // Clear loading indicator
                    friendsList.getChildren().clear();
                    
                    if (data != null && data.size() > 0) {
                        // Add each friend to the list
                        for (int i = 0; i < data.size(); i++) {
                            JsonObject friend = data.get(i).getAsJsonObject();
                            String friendId = friend.get("id").getAsString();
                            String friendName = friend.get("name").getAsString();
                            String pictureUrl = friend.getAsJsonObject("picture").getAsJsonObject("data").get("url").getAsString();
                            
                            // Create enhanced friend item
                            HBox friendItem = new HBox(15);
                            friendItem.setStyle("-fx-background-color: white; -fx-background-radius: 10; " +
                                              "-fx-padding: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 0);");
                            friendItem.setAlignment(Pos.CENTER_LEFT);
                            
                            // Add friend's profile picture with circular clip
                            ImageView friendPicture = new ImageView(new Image(pictureUrl));
                            friendPicture.setFitHeight(50);
                            friendPicture.setFitWidth(50);
                            friendPicture.setPreserveRatio(true);
                            Circle pictureClip = new Circle(25, 25, 25);
                            friendPicture.setClip(pictureClip);
                            
                            // Add friend's name with enhanced styling
                            Label nameLabel = new Label(friendName);
                            nameLabel.setStyle("-fx-text-fill: #1a237e; -fx-font-size: 16px; -fx-font-weight: bold;");
                            
                            // Add enhanced play button
                            Button playButton = new Button("Play");
                            playButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; " +
                                              "-fx-font-weight: bold; -fx-background-radius: 20; " +
                                              "-fx-padding: 8px 20px; -fx-font-size: 14px;");
                            playButton.setEffect(new DropShadow(5, Color.rgb(0, 0, 0, 0.2)));
                            
                            playButton.setOnMouseEntered(e -> {
                                playButton.setStyle("-fx-background-color: #388E3C; -fx-text-fill: white; " +
                                                  "-fx-font-weight: bold; -fx-background-radius: 20; " +
                                                  "-fx-padding: 8px 20px; -fx-font-size: 14px;");
                            });
                            
                            playButton.setOnMouseExited(e -> {
                                playButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; " +
                                                  "-fx-font-weight: bold; -fx-background-radius: 20; " +
                                                  "-fx-padding: 8px 20px; -fx-font-size: 14px;");
                            });
                            
                            playButton.setOnAction(e -> {
                                soundManager.playSound("button");
                                Alert alert = new Alert(AlertType.INFORMATION);
                                alert.setTitle("Game Invitation");
                                alert.setHeaderText(null);
                                alert.setContentText("Game invitation sent to " + friendName);
                                alert.getDialogPane().setStyle("-fx-background-color: #1a237e;");
                                alert.getDialogPane().lookup(".content.label").setStyle("-fx-text-fill: white; -fx-font-size: 16px;");
                                alert.showAndWait();
                            });
                            
                            friendItem.getChildren().addAll(friendPicture, nameLabel, playButton);
                            friendsList.getChildren().add(friendItem);
                        }
                    } else {
                        // Enhanced no friends message
                        VBox noFriendsBox = new VBox(10);
                        noFriendsBox.setAlignment(Pos.CENTER);
                        noFriendsBox.setPadding(new Insets(30));
                        
                        Label noFriendsLabel = new Label("No friends found");
                        noFriendsLabel.setStyle("-fx-text-fill: #1a237e; -fx-font-size: 18px; -fx-font-weight: bold;");
                        
                        Label suggestionLabel = new Label("Try connecting with more friends on Facebook!");
                        suggestionLabel.setStyle("-fx-text-fill: #666; -fx-font-size: 14px;");
                        
                        noFriendsBox.getChildren().addAll(noFriendsLabel, suggestionLabel);
                        friendsList.getChildren().add(noFriendsBox);
                    }
                } else {
                    // Enhanced error message
                    VBox errorBox = new VBox(10);
                    errorBox.setAlignment(Pos.CENTER);
                    errorBox.setPadding(new Insets(30));
                    
                    Label errorLabel = new Label("Error loading friends list");
                    errorLabel.setStyle("-fx-text-fill: #f44336; -fx-font-size: 18px; -fx-font-weight: bold;");
                    
                    Label retryLabel = new Label("Please try again later");
                    retryLabel.setStyle("-fx-text-fill: #666; -fx-font-size: 14px;");
                    
                    errorBox.getChildren().addAll(errorLabel, retryLabel);
                    friendsList.getChildren().add(errorBox);
                }
            } catch (Exception e) {
                e.printStackTrace();
                // Enhanced error message
                VBox errorBox = new VBox(10);
                errorBox.setAlignment(Pos.CENTER);
                errorBox.setPadding(new Insets(30));
                
                Label errorLabel = new Label("Error: " + e.getMessage());
                errorLabel.setStyle("-fx-text-fill: #f44336; -fx-font-size: 18px; -fx-font-weight: bold;");
                
                Label retryLabel = new Label("Please try again later");
                retryLabel.setStyle("-fx-text-fill: #666; -fx-font-size: 14px;");
                
                errorBox.getChildren().addAll(errorLabel, retryLabel);
                friendsList.getChildren().add(errorBox);
            }
        }
    }

    private void showLeaderboard() {
        // Create a new scene for leaderboard
        VBox leaderboardContainer = new VBox(10);
        leaderboardContainer.setPadding(new Insets(20));
        leaderboardContainer.setStyle("-fx-background-color: rgba(255, 255, 255, 0.9); -fx-background-radius: 10;");
        
        Label title = new Label("Leaderboard");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        title.setTextFill(Color.DARKSLATEBLUE);
        
        // Add leaderboard content here
        // This would typically fetch data from a database or backend service
        
        leaderboardContainer.getChildren().add(title);
        
        Scene leaderboardScene = new Scene(leaderboardContainer, 400, 500);
        Stage leaderboardStage = new Stage();
        leaderboardStage.setTitle("Leaderboard");
        leaderboardStage.setScene(leaderboardScene);
        leaderboardStage.show();

        // Add close button with sound
        Button closeButton = new Button("Close");
        styleButton(closeButton, "#F44336", "#D32F2F");
        closeButton.setOnAction(e -> {
            soundManager.playSound("button");
            ((Stage) closeButton.getScene().getWindow()).close();
        });
        
        leaderboardContainer.getChildren().add(closeButton);
    }

    // Create game settings scene
    // This method creates the game settings scene with text fields for entering player names and a combo box for selecting the grid size.
    private Scene optionScene() {
        BorderPane root = new BorderPane();
        VBox vBox = new VBox(30);
        vBox.setAlignment(Pos.CENTER);
        vBox.setStyle("-fx-background-color: linear-gradient(to bottom, #1a237e, #0d47a1);");
        vBox.setPadding(new Insets(40));

        Label titleLabel = new Label("Game Settings");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        titleLabel.setTextFill(Color.WHITE);
        titleLabel.setEffect(new DropShadow(15, Color.rgb(0, 0, 0, 0.5)));

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(20);
        grid.setVgap(20);
        grid.setPadding(new Insets(30));

        Label player1Label = new Label("Player 1:");
        Label player2Label = new Label("Player 2:");
        Label gridSize = new Label("Grid Size:");
        Label gameMode = new Label("Game Mode:");
        Label botDifficultyLabel = new Label("Bot Difficulty:");
        
        styleLabel(player1Label);
        styleLabel(player2Label);
        styleLabel(gridSize);
        styleLabel(gameMode);
        styleLabel(botDifficultyLabel);

        TextField p1Name = new TextField("");
        TextField p2Name = new TextField("");
        styleTextField(p1Name);
        styleTextField(p2Name);
        
        p1Name.setPromptText("Enter Player 1 Name");
        p2Name.setPromptText("Enter Player 2 Name");
        p1Name.textProperty().addListener((observable, oldValue, newValue) -> {
            player1Name = newValue;
        });
        p2Name.textProperty().addListener((observable, oldValue, newValue) -> {
            player2Name = newValue;
        });

        ComboBox<String> gridField = new ComboBox<>();
        gridField.getItems().addAll("4x4","5x5","6x6", "7x7", "8x8");
        gridField.getSelectionModel().select(2);
        GRID_SIZE = 7;
        gridField.setOnAction(event -> {
            GRID_SIZE = gridField.getSelectionModel().getSelectedIndex()+5;
        });
        styleComboBox(gridField);

        ToggleGroup gameModeGroup = new ToggleGroup();
        RadioButton singlePlayer = new RadioButton("Single Player");
        RadioButton multiPlayer = new RadioButton("Multi Player");
        singlePlayer.setToggleGroup(gameModeGroup);
        multiPlayer.setToggleGroup(gameModeGroup);
        multiPlayer.setSelected(true);
        singlePlayer.setTextFill(Color.WHITE);
        multiPlayer.setTextFill(Color.WHITE);
        singlePlayer.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        multiPlayer.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        ComboBox<BotPlayer.Difficulty> difficultyComboBox = new ComboBox<>();
        difficultyComboBox.getItems().addAll(BotPlayer.Difficulty.values());
        difficultyComboBox.setValue(BotPlayer.Difficulty.MEDIUM);
        styleComboBox(difficultyComboBox);

        difficultyComboBox.setOnAction(event -> {
            botDifficulty = difficultyComboBox.getValue();
        });

        // Initialize UI state based on current game mode
        p2Name.setDisable(isSinglePlayer);
        if (isSinglePlayer) {
            p2Name.setText("Bot");
            singlePlayer.setSelected(true);
        } else {
            p2Name.setText("");
            multiPlayer.setSelected(true);
        }
        difficultyComboBox.setDisable(!isSinglePlayer);

        singlePlayer.setOnAction(event -> {
            soundManager.playSound("button");
            isSinglePlayer = true;
            p2Name.setDisable(true);
            p2Name.setText("Bot");
            difficultyComboBox.setDisable(false);
        });

        multiPlayer.setOnAction(event -> {
            soundManager.playSound("button");
            isSinglePlayer = false;
            p2Name.setDisable(false);
            p2Name.setText("");
            difficultyComboBox.setDisable(true);
        });

        Button confirmButton = new Button("Start Game");
        styleButton(confirmButton, "#4CAF50", "#388E3C");
        confirmButton.setDisable(true); // Initially disabled

        // Add validation for player names
        p1Name.textProperty().addListener((observable, oldValue, newValue) -> {
            validatePlayerNames(p1Name, p2Name, confirmButton, isSinglePlayer);
        });

        p2Name.textProperty().addListener((observable, oldValue, newValue) -> {
            validatePlayerNames(p1Name, p2Name, confirmButton, isSinglePlayer);
        });

        // Initial validation
        validatePlayerNames(p1Name, p2Name, confirmButton, isSinglePlayer);

        confirmButton.setOnAction(event -> {
            soundManager.playSound("button");
            if (validatePlayerNames(p1Name, p2Name, confirmButton, isSinglePlayer)) {
                primaryStage.setScene(createGameBoardScene());
            }
        });

        Button backButton = new Button("Back to Menu");
        styleButton(backButton, "#2196F3", "#1976D2");
        backButton.setOnAction(event -> {
            soundManager.playSound("button");
            primaryStage.setScene(mainMenuScene);
        });

        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(backButton, confirmButton);

        HBox gameModeBox = new HBox(20);
        gameModeBox.setAlignment(Pos.CENTER);
        gameModeBox.getChildren().addAll(singlePlayer, multiPlayer);

        grid.add(player1Label, 0, 0);
        grid.add(player2Label, 0, 1);
        grid.add(gridSize, 0, 2);
        grid.add(gameMode, 0, 3);
        grid.add(botDifficultyLabel, 0, 4);
        grid.add(p1Name, 1, 0);
        grid.add(p2Name, 1, 1);
        grid.add(gridField, 1, 2);
        grid.add(gameModeBox, 1, 3);
        grid.add(difficultyComboBox, 1, 4);

        vBox.getChildren().addAll(titleLabel, grid, buttonBox);
        root.setCenter(vBox);

        return new Scene(root, screenSize.getWidth(), screenSize.getHeight());
    }

    // styleLabel method to style labels
    private void styleLabel(Label label) {
        label.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        label.setTextFill(Color.WHITE);
        label.setEffect(new DropShadow(5, Color.rgb(0, 0, 0, 0.3)));
    }

    // styleTextField method to style text fields
    private void styleTextField(TextField textField) {
        textField.setMaxWidth(250);
        textField.setAlignment(Pos.CENTER);
        textField.setStyle("-fx-font-size: 16px; -fx-padding: 12px; -fx-background-color: rgba(255, 255, 255, 0.9); " +
                         "-fx-text-fill: #1a237e; -fx-background-radius: 10; -fx-border-radius: 10;");
        textField.setEffect(new DropShadow(5, Color.rgb(0, 0, 0, 0.2)));
    }

    // styleComboBox method to style combo boxes
    private <T> void styleComboBox(ComboBox<T> comboBox) {
        comboBox.setMaxWidth(250);
        comboBox.setStyle("-fx-font-size: 16px; -fx-padding: 12px; -fx-background-color: rgba(255, 255, 255, 0.9); " +
                         "-fx-text-fill: #1a237e; -fx-background-radius: 10; -fx-border-radius: 10;");
        comboBox.setEffect(new DropShadow(5, Color.rgb(0, 0, 0, 0.2)));
    }

    // styleButton method to style buttons
    private void styleButton(Button button, String color, String hoverColor) {
        button.setStyle("-fx-font-size: 18px; -fx-padding: 15px 30px; -fx-background-color: " + color + "; " +
                       "-fx-text-fill: white; -fx-background-radius: 25; -fx-font-weight: bold;");
        button.setEffect(new DropShadow(10, Color.rgb(0, 0, 0, 0.3)));
        
        button.setOnMouseEntered(e -> {
            button.setStyle("-fx-background-color: " + hoverColor + "; -fx-text-fill: white; " +
                          "-fx-background-radius: 25; -fx-font-size: 18px; -fx-padding: 15px 30px; -fx-font-weight: bold;");
            button.setEffect(new DropShadow(15, Color.rgb(0, 0, 0, 0.4)));
        });
        
        button.setOnMouseExited(e -> {
            button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; " +
                          "-fx-background-radius: 25; -fx-font-size: 18px; -fx-padding: 15px 30px; -fx-font-weight: bold;");
            button.setEffect(new DropShadow(10, Color.rgb(0, 0, 0, 0.3)));
        });

        // Store the original action
        EventHandler<ActionEvent> originalAction = button.getOnAction();
        
        // Set new action that plays sound and then executes original action
        button.setOnAction(event -> {
            soundManager.playSound("button");
            if (originalAction != null) {
                originalAction.handle(event);
            }
        });
    }

    // Create game board scene  
    // This method creates the game board scene with the grid of dots, lines, and boxes, as well as the player scores and turn information.
    private Scene createGameBoardScene() {
        hLines = new Line[GRID_SIZE][GRID_SIZE-1];
        vLines = new Line[GRID_SIZE-1][GRID_SIZE];
        boxes = new Rectangle[GRID_SIZE][GRID_SIZE];

        player1 = new Player(player1Name, Color.rgb(255, 85, 85));
        player2 = new Player(player2Name, Color.rgb(85, 170, 255));
        currentPlayer = player1;
        infoLabel.setText(currentPlayer.getName() + "'s turn");


        player1ScoreLabel = new Label();
        player2ScoreLabel = new Label();
        infoLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        infoLabel.setTextFill(Color.DARKSLATEBLUE);

        boxCounter = (GRID_SIZE-1)*(GRID_SIZE-1);

        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(LINE_THICKNESS+50, 0, 0, LINE_THICKNESS+50));

        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                AnchorPane stack = new AnchorPane();
                if(col < GRID_SIZE-1 && row < GRID_SIZE-1) {
                    Rectangle square = new Rectangle(0, 0, SPACING, SPACING);
                    // Enhanced box colors with gradient
                    square.setFill(((row+col)%2==0) ? 
                        Color.valueOf("#E6E6FA") : 
                        Color.valueOf("#D8BFD8"));
                    square.setStrokeWidth(0);
                    stack.getChildren().add(square);
                    boxes[row][col] = square;
                }

                if(col < GRID_SIZE-1) {
                    Line lineUP = new Line(LINE_THICKNESS, 0, SPACING-LINE_THICKNESS, 0);
                    styleLine(lineUP, true);
                    lineDrawn.put(lineUP, false);
                    hLines[row][col] = lineUP;
                    stack.getChildren().add(lineUP);
                }

                if(row < GRID_SIZE-1) {
                    Line lineLeft = new Line(0, LINE_THICKNESS, 0, SPACING-LINE_THICKNESS);
                    styleLine(lineLeft, false);
                    lineDrawn.put(lineLeft, false);
                    vLines[row][col] = lineLeft;
                    stack.getChildren().add(lineLeft);
                }

                Circle dot = new Circle(0, 0, LINE_THICKNESS*2);
                dot.setFill(Color.DARKSLATEBLUE);
                dot.setEffect(new DropShadow(5, Color.rgb(0, 0, 0, 0.3)));
                stack.getChildren().add(dot);

                gridPane.add(stack, col, row);
            }
        }

        BorderPane borderPane = new BorderPane();
        // Enhanced background with animated gradient
        borderPane.setStyle("-fx-background-color: linear-gradient(to bottom, #1a237e, #0d47a1);");
        
        VBox topBar = new VBox(10);
        topBar.setPadding(new Insets(20));
        topBar.setStyle("-fx-background-color: rgba(0, 0, 0, 0.2); -fx-background-radius: 10;");

        HBox playerInfo = new HBox(40);
        playerInfo.setAlignment(Pos.CENTER);

        VBox player1Box = new VBox(5);
        VBox player2Box = new VBox(5);
        
        Label player1Label = new Label(player1Name);
        Label player2Label = new Label(player2Name);
        styleLabel(player1Label);
        styleLabel(player2Label);
        
        player1ScoreLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        player2ScoreLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        player1ScoreLabel.setTextFill(player1.getColor());
        player2ScoreLabel.setTextFill(player2.getColor());
        
        // Add glow effect to score labels
        player1ScoreLabel.setEffect(new Glow(10));
        player2ScoreLabel.setEffect(new Glow(10));
        
        player1Box.getChildren().addAll(player1Label, player1ScoreLabel);
        player2Box.getChildren().addAll(player2Label, player2ScoreLabel);
        
        infoLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        infoLabel.setTextFill(Color.WHITE);
        infoLabel.setEffect(new DropShadow(5, Color.rgb(0, 0, 0, 0.3)));

        Button backButton = new Button("←");
        styleButton(backButton, "#2196F3", "#1976D2");
        backButton.setOnAction(event -> {
            primaryStage.setScene(mainMenuScene);
            soundManager.playSound("button");
        });


        playerInfo.getChildren().addAll(backButton, player1Box, infoLabel, player2Box);
        topBar.getChildren().add(playerInfo);

        HBox gameBoard = new HBox();
        gameBoard.setAlignment(Pos.CENTER);
        gameBoard.getChildren().add(gridPane);

        borderPane.setTop(topBar);
        borderPane.setCenter(gameBoard);

        updateScores();

        if (isSinglePlayer) {
            botPlayer = new BotPlayer("Bot", Color.rgb(85, 170, 255), botDifficulty);
            player2 = botPlayer;
            
            // Create bot thinking UI elements
            botThinkingLabel = new Label("Bot is thinking...");
            botThinkingLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
            botThinkingLabel.setTextFill(Color.WHITE);
            botThinkingLabel.setVisible(false);
            
            botThinkingIndicator = new ProgressIndicator();
            botThinkingIndicator.setVisible(false);
            botThinkingIndicator.setStyle("-fx-progress-color: white;");
            
            HBox botThinkingBox = new HBox(10, botThinkingIndicator, botThinkingLabel);
            botThinkingBox.setAlignment(Pos.CENTER);
            botThinkingBox.setVisible(false);
            
            topBar.getChildren().add(botThinkingBox);
            
            // Initialize bot delay
            botDelay = new PauseTransition(Duration.seconds(1 + Math.random() * 0.5));
            botDelay.setOnFinished(event -> {
                makeBotMove();
                botThinkingBox.setVisible(false);
            });
        }

        return new Scene(borderPane, screenSize.getWidth(), screenSize.getHeight());
    }

    // styleLine method to style lines
    // This method styles the lines in the game board grid with color, thickness, and effects.
    private void styleLine(Line line, boolean isLineUp) {
        line.setStroke(Color.TRANSPARENT);
        line.setStrokeLineCap(StrokeLineCap.ROUND);
        line.setStrokeWidth(LINE_THICKNESS * 2);

        line.setOnMouseEntered(event -> {
            if (!lineDrawn.get(line) && (!isSinglePlayer || currentPlayer != botPlayer)) {
                line.setStroke(currentPlayer.getColor());
                line.setEffect(new Glow(10));
            }
        });

        line.setOnMouseExited(event -> {
            if (!lineDrawn.get(line) && (!isSinglePlayer || currentPlayer != botPlayer)) {
                line.setStroke(Color.TRANSPARENT);
                line.setEffect(null);
            }
        });

        line.setOnMouseClicked(event -> {
            if (lineDrawn.get(line) || (isSinglePlayer && currentPlayer == botPlayer)) {
                return;
            }

            soundManager.playSound("line");
            line.setStroke(currentPlayer.getColor());
            line.setEffect(new Glow(5));
            lineDrawn.put(line, true);

            int boxCompleted = checkForCompletedBox(line, isLineUp);
            if (boxCompleted > 0) {
                soundManager.playSound("box");
            }
            if (boxCompleted == 0) {
                currentPlayer = (currentPlayer == player1) ? player2 : player1;
                infoLabel.setText(currentPlayer.getName() + "'s turn");
                
                FadeTransition fade = new FadeTransition(Duration.millis(300), infoLabel);
                fade.setFromValue(0.5);
                fade.setToValue(1.0);
                fade.play();

                if (isSinglePlayer && currentPlayer == botPlayer) {
                    botPlayer.setGameState(lineDrawn, hLines, vLines, GRID_SIZE);
                    botThinkingLabel.setVisible(true);
                    botThinkingIndicator.setVisible(true);
                    botDelay.play();
                }
            }
            updateScores();
            boxCounter -= boxCompleted;
            
            if(boxCounter < 1) {
                final String winMsg;
                if (player1.getScore() > player2.getScore()) {
                    winMsg = player1Name + " Wins!!";
                    soundManager.playSound("win");
                } else if (player1.getScore() < player2.getScore()) {
                    winMsg = player2Name + " Wins!!";
                    soundManager.playSound("lose");
                } else {
                    winMsg = "It was a tie.";
                    soundManager.playSound("tie");
                }
                
                // Use Platform.runLater to show alert after animation
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
        });
    }

    // checkForCompletedBox method to check for completed boxes
    // This method checks if a box is completed by checking if all four lines around the box have been drawn.
    private int checkForCompletedBox(Line line, boolean isLineUp) {
        int boxCompleted = 0;
        int row = GridPane.getRowIndex(line.getParent());
        int col = GridPane.getColumnIndex(line.getParent());

        if(isLineUp) {
            if(row > 0) {
                if (lineDrawn.get(vLines[row-1][col]) && lineDrawn.get(hLines[row-1][col]) && lineDrawn.get(vLines[row-1][col+1])) {
                    boxes[row-1][col].setFill(currentPlayer.getColor());
                    boxOwner.put(boxes[row-1][col], currentPlayer);
                    currentPlayer.increaseScore(1);
                    boxCompleted++;
                }
            }

            if(row < GRID_SIZE-1) {
                if (lineDrawn.get(vLines[row][col]) && lineDrawn.get(hLines[row+1][col]) && lineDrawn.get(vLines[row][col+1])) {
                    boxes[row][col].setFill(currentPlayer.getColor());
                    boxOwner.put(boxes[row][col], currentPlayer);
                    currentPlayer.increaseScore(1);
                    boxCompleted++;
                }
            }
        } else {
            if(col > 0) {
                if (lineDrawn.get(hLines[row][col-1]) && lineDrawn.get(vLines[row][col-1]) && lineDrawn.get(hLines[row+1][col-1])) {
                    boxes[row][col-1].setFill(currentPlayer.getColor());
                    boxOwner.put(boxes[row][col-1], currentPlayer);
                    currentPlayer.increaseScore(1);
                    boxCompleted++;
                }
            }

            if(col < GRID_SIZE-1) {
                if (lineDrawn.get(hLines[row][col]) && lineDrawn.get(vLines[row][col+1]) && lineDrawn.get(hLines[row+1][col])) {
                    boxes[row][col].setFill(currentPlayer.getColor());
                    boxOwner.put(boxes[row][col], currentPlayer);
                    currentPlayer.increaseScore(1);
                    boxCompleted++;
                }
            }
        }
        return boxCompleted;
    }

    // updateScores method to update player scores  
    // This method updates the player scores displayed on the game board.
    private void updateScores() {
        player1ScoreLabel.setText(String.valueOf(player1.getScore()));
        player2ScoreLabel.setText(String.valueOf(player2.getScore()));
    }
        
    private void cleanupResources() {
        try {
            if (facebookLoginServer != null) {
                facebookLoginServer.stop();
                facebookLoginServer = null;
            }
        } catch (Exception e) {
            System.err.println("Error during cleanup: " + e.getMessage());
        }
        Platform.exit(); // Exit the JavaFX application
        System.exit(0); // Exit the application        
    }

    private void makeBotMove() {
        if (botPlayer != null && currentPlayer == botPlayer) {
            Line botMove = botPlayer.makeMove();
            if (botMove != null) {
                soundManager.playSound("line");
                botMove.setStroke(botPlayer.getColor());
                botMove.setEffect(new Glow(5));
                lineDrawn.put(botMove, true);

                boolean isLineUp = botMove.getStartY() == botMove.getEndY();
                int boxCompleted = checkForCompletedBox(botMove, isLineUp);
                
                if (boxCompleted > 0) {
                    soundManager.playSound("box");
                }
                
                updateScores();
                boxCounter -= boxCompleted;
                
                if(boxCounter < 1) {
                    final String winMsg;
                    if (player1.getScore() > player2.getScore()) {
                        winMsg = player1Name + " Wins!!";
                        soundManager.playSound("win");
                    } else if (player1.getScore() < player2.getScore()) {
                        winMsg = player2Name + " Wins!!";
                        soundManager.playSound("lose");
                    } else {
                        winMsg = "It was a tie.";
                        soundManager.playSound("tie");
                    }
                    
                    // Use Platform.runLater to show alert after animation
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
                    return;
                }

                if (boxCompleted > 0) {
                    botPlayer.setGameState(lineDrawn, hLines, vLines, GRID_SIZE);
                    botDelay.play();
                } else {
                    currentPlayer = player1;
                    infoLabel.setText(currentPlayer.getName() + "'s turn");
                    
                    FadeTransition fade = new FadeTransition(Duration.millis(300), infoLabel);
                    fade.setFromValue(0.5);
                    fade.setToValue(1.0);
                    fade.play();
                }
            }
        }
    }

    private boolean validatePlayerNames(TextField p1Name, TextField p2Name, Button confirmButton, boolean isSinglePlayer) {
        boolean isValid = !p1Name.getText().trim().isEmpty() && 
                         (isSinglePlayer || !p2Name.getText().trim().isEmpty());
        
        confirmButton.setDisable(!isValid);
        if (!isValid) {
            confirmButton.setStyle("-fx-background-color: #CCCCCC; -fx-text-fill: #666666; " +
                                 "-fx-font-size: 18px; -fx-padding: 15px 30px; -fx-background-radius: 25; " +
                                 "-fx-font-weight: bold;");
        } else {
            confirmButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; " +
                                 "-fx-font-size: 18px; -fx-padding: 15px 30px; -fx-background-radius: 25; " +
                                 "-fx-font-weight: bold;");
        }
        return isValid;
    }
}