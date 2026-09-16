package sace;

import java.net.URL;
import java.util.Objects;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;

/**
 * Controls the main chat window and passes user commands to Sace.
 */
public class MainWindow {
    private static final String BOT_AVATAR_PATH = "/images/arli-flawless-avatar.png";

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;
    @FXML
    private Label statusLabel;

    private final Image botImage = loadBotImage();
    private Sace sace;

    /**
     * Keeps the conversation scrolled to the newest message.
     */
    @FXML
    private void initialize() {
        dialogContainer.heightProperty().addListener((observable, oldHeight, newHeight) ->
                scrollPane.setVvalue(1.0));
    }

    /**
     * Supplies the chatbot used to answer commands and displays its greeting.
     *
     * @param sace chatbot instance owned by the application.
     */
    public void setSace(Sace sace) {
        this.sace = sace;
        dialogContainer.getChildren().add(
                DialogBox.getBotDialog(sace.getWelcomeMessage(), botImage));
        userInput.requestFocus();
    }

    /**
     * Adds the user's command and Sace's reply to the conversation.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText().trim();
        if (input.isEmpty() || sace == null) {
            return;
        }

        String response = sace.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input),
                DialogBox.getBotDialog(response, botImage));
        userInput.clear();

        if (sace.isExitRequested()) {
            userInput.setDisable(true);
            sendButton.setDisable(true);
            statusLabel.setText("SESSION COMPLETE");
        }
    }

    /**
     * Loads the bot portrait from the application resources.
     */
    private static Image loadBotImage() {
        URL imageResource = Objects.requireNonNull(
                MainWindow.class.getResource(BOT_AVATAR_PATH), "Bot avatar is missing");
        return new Image(imageResource.toExternalForm());
    }
}
