package com.example.snake;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class Application extends javafx.application.Application {
    static Controller controller;
    static Group root = new Group();

    static final int SQUARES_PER_SIDE = 8;
    static final int SIDE_LENGTH = (int) Math.ceil(1000.0 / SQUARES_PER_SIDE); //Length of background square

    static Rectangle[][] backgroundSquares = new Rectangle[SQUARES_PER_SIDE][SQUARES_PER_SIDE];
    static int score = 0;
    static Timeline timeline;
    static GameState gameState = GameState.beforeGame;
    static float volume = -4f;

    static Snake snake = new Snake();
    static Apple apple = new Apple();

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("view.fxml"));
        root.getChildren().add(loader.load());
        controller = loader.getController();

        // Set up backgroundSquares and put them in a list
        for(int x = 0; x < SQUARES_PER_SIDE; x++)
            for (int y = 0; y < SQUARES_PER_SIDE; y++) {
                backgroundSquares[x][y] = makeSquare(x, y);
                root.getChildren().add(backgroundSquares[x][y]);
            }

        //Create the snake
        snake.setUpSnake();
        root.getChildren().add(snake.faceView);
        snake.apple = apple;

        //Create the apple
        apple.setUpApple();
        root.getChildren().add(apple.appleView);
        apple.snake = snake;

        Scene scene = new Scene(root, SIDE_LENGTH * SQUARES_PER_SIDE, SIDE_LENGTH * SQUARES_PER_SIDE + 200);

        root.getChildren().add(controller.createLabel());
        root.getChildren().add(controller.createEndGameAppleView());
        root.getChildren().add(controller.createEndGameScoreLabel());
        root.getChildren().add(controller.createSpaceToContinueLabel());

        controller.anyKeyToStartMessage();
        controller.alignWidgets();
        root.requestFocus();

        setUpControls(scene);

        stage.setTitle("Snake");
        stage.setScene(scene);
        stage.show();

        mainLoop();
    }

    public static Rectangle makeSquare(int x, int y) { //Returns a background square
        Rectangle square = new Rectangle();

        square.setX(x * SIDE_LENGTH);
        square.setY(y * SIDE_LENGTH + 200);
        square.setWidth(SIDE_LENGTH);
        square.setHeight(SIDE_LENGTH);

        //Color the squares in a grid pattern
        if (x % 2 == y % 2)
            square.setFill(Paint.valueOf("#00e000"));
        else
            square.setFill(Paint.valueOf("#00c000"));

        return square;
    }

    public void restartGame() {
        snake.hideSnake();
        controller.hideLabel();

        //Recreate the snake and apple
        snake.setUpSnake();
        apple.setUpApple();
        snake.faceView.toFront();

        controller.label.toFront();
        controller.anyKeyToStartMessage();
        gameState = GameState.beforeGame;
        score = 0;
        controller.updateScoreLabel();
    }

    public static void onDeath() {
        onWinOrLoss();
        playSound("loss.wav");

        controller.showLossMessage();
    }

    public static void onWin() {
        onWinOrLoss();
        playSound("win.wav");

        apple.appleView.setImage(null);
        controller.showWinMessage();
    }

    private static void onWinOrLoss() {
        timeline.stop();
        gameState = GameState.afterGame;
    }

    public static void playSound(String filePath) {
        try {
            File file = new File("src/main/resources/com/example/snake/Audio/" + filePath);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            FloatControl clipControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            clipControl.setValue(volume);

            clip.start();
        }
        catch (Exception e) {
            throw new RuntimeException("Couldn't play the sound effect:\n" + e);
        }
    }

    public void setUpControls(Scene scene) {
        scene.setOnKeyPressed(event -> {
            KeyCode key = event.getCode();

            //Change volume
            if(key == KeyCode.PERIOD || key == KeyCode.W) {
                if(volume < 4.02)
                    volume += 2;
                return;
            }
            if(key == KeyCode.COMMA || key == KeyCode.S) {
                if(volume > -8)
                    volume -= 2;
                return;
            }

            //Press any key to play
            if(gameState == GameState.beforeGame) {
                controller.hideLabel();
                timeline.play();
                gameState = GameState.playing;
            }

            //Play again after win/loss
            if(key == KeyCode.SPACE) {
                if(gameState == GameState.afterGame) {
                    restartGame();
                }
                return;
            }

            //Turn the snake
            if (key == KeyCode.UP &&
                snake.facing != Direction.down && snake.facing != Direction.up && snake.newFacing != Direction.up) {
                    snake.newFacing = Direction.up;
            }
            else if (key == KeyCode.DOWN &&
                snake.facing != Direction.up && snake.facing != Direction.down && snake.newFacing != Direction.down) {
                    snake.newFacing = Direction.down;
            }
            else if (key == KeyCode.RIGHT &&
                snake.facing != Direction.left && snake.facing != Direction.right && snake.newFacing != Direction.right) {
                    snake.newFacing = Direction.right;
            }
            else if (key == KeyCode.LEFT &&
                snake.facing != Direction.right && snake.facing != Direction.left && snake.newFacing != Direction.left) {
                    snake.newFacing = Direction.left;
            }
            else
                return;

            if (gameState == GameState.playing)
                playSound("turn.wav");
        });
    }

    public void mainLoop() {
        timeline = new Timeline(new KeyFrame(Duration.millis(400), _->{

            snake.hideSnake();
            snake.moveSnake(); //Also checks eating apples and dying
            snake.showSnake();

            //If snake takes up whole board
            if (snake.snake.size() == SQUARES_PER_SIDE * SQUARES_PER_SIDE) onWin();

        }));

        timeline.setCycleCount(-1); //Infinite
        //Start when a button is pressed
    }

    public static void main(String[] args) { launch(); }
}