package com.example.snake;

import javafx.geometry.Pos;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

import java.util.Objects;

public class Controller {
    static final int WIDTH  = Application.SQUARES_PER_SIDE * Application.SIDE_LENGTH;
    static final int HEIGHT = WIDTH + 200;

    Image appleImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("Images/apple.png")));

    public AnchorPane pane;
    public Label titleLabel;
    public Label scoreLabel;

    public Label label = new Label();
    public ImageView endGameApple = new ImageView();
    public Label endGameScore = new Label();
    public Label spaceToContinue = new Label();

    public Label createLabel() {
        label.setAlignment(Pos.CENTER);
        label.setContentDisplay(ContentDisplay.CENTER);
        label.setPrefWidth(WIDTH);
        label.setPrefHeight(HEIGHT);
        label.setTextFill(Color.WHITE);
        label.setFont(Font.font("courier new", FontWeight.BOLD, 70));
        return label;
    }

    public ImageView createEndGameAppleView() {
        endGameApple.setFitWidth(163);
        endGameApple.setX(((double) WIDTH / 2) - endGameApple.getFitWidth());
        endGameApple.setY(332);
        endGameApple.setPickOnBounds(true);
        endGameApple.setPreserveRatio(true);
        return endGameApple;
    }

    public Label createEndGameScoreLabel() {
        endGameScore.setPrefWidth(170);
        endGameScore.setPrefHeight(68);
        endGameScore.setLayoutY(387);
        endGameScore.setLayoutX((double) WIDTH / 2 + 10);
        endGameScore.setTextFill(Color.WHITE);
        endGameScore.setFont(Font.font("times new roman", FontWeight.BOLD, 90));
        return endGameScore;
    }

    public Label createSpaceToContinueLabel() {
        spaceToContinue.setFont(Font.font("courier new", FontWeight.BOLD, 60));
        spaceToContinue.setTextFill(Color.WHITE);
        spaceToContinue.setAlignment(Pos.CENTER);
        spaceToContinue.setTextAlignment(TextAlignment.CENTER);
        spaceToContinue.setLayoutY(726);
        spaceToContinue.setPrefWidth(WIDTH);
        return spaceToContinue;
    }

    public void alignWidgets() {
        pane.setPrefWidth(WIDTH);
        pane.setPrefHeight(HEIGHT);

        titleLabel.setPrefWidth(WIDTH);
    }

    public void updateScoreLabel() { scoreLabel.setText(Application.score + ""); }

    public void hideLabel() {
        label.setText("");
        label.setStyle("");
        label.setFont(Font.font("courier new", FontWeight.BOLD, 70));
        spaceToContinue.setText("");
        endGameApple.setImage(null);
        endGameScore.setText("");
    }

    public void anyKeyToStartMessage() {
        label.setText("Press any key to start");

        darkBackground();
    }

    public void showLossMessage() {
        label.setText("You lost!");
        onWinOrLoss();
    }

    public void showWinMessage() {
        label.setText("You won!");
        onWinOrLoss();
    }

    private void onWinOrLoss() {
        spaceToContinue.setText("Press space to play again");
        spaceToContinue.toFront();
        darkBackground();

        label.setFont(Font.font("courier new", FontWeight.BOLD, 120));

        endGameApple.setImage(appleImage);
        endGameScore.setText(Application.score + "");
        endGameApple.toFront();
        endGameScore.toFront();
    }

    private void darkBackground() { label.setStyle("-fx-background-color: #000000b0"); }
}