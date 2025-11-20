package com.example.snake;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

public class Apple {
    final int SQUARES_PER_SIDE = Application.SQUARES_PER_SIDE;
    final int SIDE_LENGTH = Application.SIDE_LENGTH;

    Image appleImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("Images/apple.png")));
    ImageView appleView = new ImageView();
    int[] appleLocation = new int[2];

    Snake snake;

    public void setUpApple() {
        appleLocation = new int[]{4, 2};

        appleView.setImage(appleImage);
        appleView.setFitWidth(SIDE_LENGTH);
        appleView.setFitHeight(SIDE_LENGTH);

        appleView.setX(appleLocation[0] * SIDE_LENGTH);
        appleView.setY(appleLocation[1] * SIDE_LENGTH + 200);
    }

    public void moveApple() {
        Random random = new Random();
        int[] newLocation;

        if(snake.snake.size() < SQUARES_PER_SIDE * SQUARES_PER_SIDE * 0.7) { //If the snake covers less than 70% of the board
            //Generate random backgroundSquares until an empty one is found
            do {
                int randomX = random.nextInt(0, SQUARES_PER_SIDE);
                int randomY = random.nextInt(0, SQUARES_PER_SIDE);
                newLocation = new int[]{randomX, randomY};
            } while (snake.snakeCollidesWith(newLocation, true)); //includeTail = true
        }
        else { //If the snake covers more than 70% of the board
            //Make a list of the empty backgroundSquares and choose a random one
            ArrayList<int[]> emptySquares = new ArrayList<>(); //Array of 2 long int arrays(x, y)
            for(int i = 0; i < SQUARES_PER_SIDE; i++) {
                for(int j = 0; j < SQUARES_PER_SIDE; j++) {
                    int[] square = new int[]{i, j};
                    if(! snake.snakeCollidesWith(square, true)) { //includeTail = true
                        emptySquares.add(square);
                    }
                }
            }
            int randomIndex;
            try {
                randomIndex = random.nextInt(0, emptySquares.size());
            } catch (IllegalArgumentException _) { //randomIndex == 0: Snake covers every square (Win), don't move the apple
                return;
            }
            newLocation = emptySquares.get(randomIndex);
        }
        //Move the apple to the new location
        appleLocation = newLocation;
        appleView.setX(newLocation[0] * SIDE_LENGTH);
        appleView.setY(newLocation[1] * SIDE_LENGTH + 200);
    }

    public boolean appleCollidesWith(int[] faceCoords) {
        boolean xMatch = (faceCoords[0] == appleLocation[0]);
        boolean yMatch = (faceCoords[1] == appleLocation[1]);
        return xMatch && yMatch;
    }

    public void checkEatApple(int[] faceCoords) {
        if(appleCollidesWith(faceCoords)) {
            Application.score++;
            Application.controller.updateScoreLabel();
            Application.playSound("eat apple.wav");
            moveApple();
        }
        else { //Remove the tail if no apple was eaten
            snake.removeTail();
        }
    }
}
