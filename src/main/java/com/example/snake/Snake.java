package com.example.snake;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.Objects;

public class Snake {
    final int SQUARES_PER_SIDE = Application.SQUARES_PER_SIDE;
    final int SIDE_LENGTH = Application.SIDE_LENGTH;
    final int SNAKE_SQUARE_SIDE_LENGTH = (int) (SIDE_LENGTH * 0.7);
    final int OFFSET_FROM_SQUARE = (SIDE_LENGTH - SNAKE_SQUARE_SIDE_LENGTH) / 2;

    ArrayList<int[]> snake = new ArrayList<>();
    Rectangle[][] snakeSquares = new Rectangle[SQUARES_PER_SIDE][SQUARES_PER_SIDE];
    Rectangle[][] xConnections = new Rectangle[SQUARES_PER_SIDE - 1][SQUARES_PER_SIDE    ];
    Rectangle[][] yConnections = new Rectangle[SQUARES_PER_SIDE    ][SQUARES_PER_SIDE - 1];
    ImageView faceView  = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("Images/snake face.png"))));
    Direction facing;

    Apple apple;

    public void setUpSnake() {
        for(int x = 0; x < SQUARES_PER_SIDE; x++)
            for(int y = 0; y < SQUARES_PER_SIDE; y++) {
                //Snake square
                snakeSquares[x][y] = makeSnakeSquare(x, y);
                hideSnakeSquare(x, y);
                Application.root.getChildren().add(snakeSquares[x][y]);

                //X connection
                if(x < SQUARES_PER_SIDE - 1) {
                    xConnections[x][y] = makeConnection(x, y, 'x');
                    hideConnection(x, y, 'x');
                    Application.root.getChildren().add(xConnections[x][y]);
                }
                //Y connection
                if(y < SQUARES_PER_SIDE - 1) {
                    yConnections[x][y] = makeConnection(x, y, 'y');
                    hideConnection(x, y, 'y');
                    Application.root.getChildren().add(yConnections[x][y]);
                }
            }

        snake.clear();
        snake.add(new int[]{1, 2});
        snake.add(new int[]{2, 2});

        faceView.setFitWidth(SNAKE_SQUARE_SIDE_LENGTH);
        faceView.setFitHeight(SNAKE_SQUARE_SIDE_LENGTH);

        //Rotate 90 to face the snake face right
        faceView.setRotate(90);
        facing = Direction.right;

        showSnake();
    }

    public Rectangle makeConnection(int x, int y, char type) {
        Rectangle connection = new Rectangle();
        if(type == 'x') {
            connection.setX(x * SIDE_LENGTH + OFFSET_FROM_SQUARE + SNAKE_SQUARE_SIDE_LENGTH);
            connection.setY(y * SIDE_LENGTH + OFFSET_FROM_SQUARE + 200);
            connection.setWidth(2 * OFFSET_FROM_SQUARE + 1);
            connection.setHeight(SNAKE_SQUARE_SIDE_LENGTH);
        }
        else { //type == 'y'
            connection.setX(x * SIDE_LENGTH + OFFSET_FROM_SQUARE);
            connection.setY(y * SIDE_LENGTH + OFFSET_FROM_SQUARE + SNAKE_SQUARE_SIDE_LENGTH + 200);
            connection.setWidth(SNAKE_SQUARE_SIDE_LENGTH);
            connection.setHeight(2 * OFFSET_FROM_SQUARE + 1);
        }
        return connection;
    }

    public Rectangle makeSnakeSquare(int x, int y) {
        Rectangle square = new Rectangle();
        square.setX(x * SIDE_LENGTH + OFFSET_FROM_SQUARE);
        square.setY(y * SIDE_LENGTH + OFFSET_FROM_SQUARE + 200);
        square.setWidth(SNAKE_SQUARE_SIDE_LENGTH);
        square.setHeight(SNAKE_SQUARE_SIDE_LENGTH);
        return square;
    }

    public void moveSnake() {
        int[] headLocation = snake.getLast();
        int[] newHeadLocation = new int[2];
        switch (facing) {
            case up:
                newHeadLocation[0] = headLocation[0];     //X of the head is the same
                newHeadLocation[1] = headLocation[1] - 1; //Y of the head gets decreased (moves up)
                break;

            case down:
                newHeadLocation[0] = headLocation[0];     //X of the head is the same
                newHeadLocation[1] = headLocation[1] + 1; //Y of the head gets increased (moves down)
                break;

            case right:
                newHeadLocation[0] = headLocation[0] + 1; //X of the head gets increased
                newHeadLocation[1] = headLocation[1];     //Y of the head is the same
                break;

            case left:
                newHeadLocation[0] = headLocation[0] - 1; //X of the head gets decreased
                newHeadLocation[1] = headLocation[1];     //Y of the head is the same
                break;
        }

        if (snakeIsDead(newHeadLocation)) {
            Application.onDeath();
            return;
        }

        snake.add(newHeadLocation);

        apple.checkEatApple(snake.getLast()); //Face coords
    }

    public boolean snakeIsDead(int[] newHeadLocation) {
        return newHeadLocation[0] < 0 || newHeadLocation[1] < 0 ||
               newHeadLocation[0] >= SQUARES_PER_SIDE || newHeadLocation[1] >= SQUARES_PER_SIDE ||
               snakeCollidesWith(newHeadLocation, false); //includeTail = false, the snake(and tail) will move before the head gets there
    }

    public boolean snakeCollidesWith(int[] coords, boolean includeTail) {
        //If includeTail is false, skip it by starting at index 1
        for(int i = (includeTail ? 0 : 1); i < snake.size(); i++) {
            int[] part = snake.get(i);
            if(part[0] == coords[0] && part[1] == coords[1]) return true;
        }
        return false;
    }

    public void hideSnake() {
        //Hide snake squares
        for(int[] coords : snake) {
            hideSnakeSquare(coords[0], coords[1]);
        }
        //Hide connections
        for(int x = 0; x < SQUARES_PER_SIDE; x++)
            for(int y = 0; y < SQUARES_PER_SIDE; y++) {
                hideConnection(x, y, 'x');
                hideConnection(x, y, 'y');
            }
    }

    public void showSnakeSquare(int x, int y) {
        snakeSquares[x][y].setFill(Color.valueOf("2e2ee5"));
    }

    public void hideSnakeSquare(int x, int y) {
        snakeSquares[x][y].setFill(Color.TRANSPARENT);
    }

    public void showSnake() {
        //Show snake squares
        for(int[] coords : snake) {
            showSnakeSquare(coords[0], coords[1]);
        }

        //Show connections
        int previousX = snake.getFirst()[0]; //X of tail location
        int previousY = snake.getFirst()[1]; //Y of tail location

        //Show 1 less connection than the length of the snake (start at 1)
        for(int i = 1; i < snake.size(); i++) {
            //Show the connection between the previous square and the current square

            int currentX = snake.get(i)[0];
            int currentY = snake.get(i)[1];

            /*
            Each square(except on some edges) has 2 connections that have the same coords as it (⮕ & ⬇)
            The offset points to the square ('currentX': ⮕, 'currentY': ⬇)
            The type points to the connection off that square */

            int[] offset = getOffsetToConnection(previousX, previousY, currentX, currentY);//(0,0 •), (0,-1 ⬆), (-1,0 ⬅)  (no -1,-1)
            char type = getDirectionMoved(previousX, currentX); //'x' or 'y'

            showConnection(currentX + offset[0], currentY + offset[1], type);

            previousX = currentX;
            previousY = currentY;
        }

        //Move the snake face to the coords of the last part (the face) of the snake
        faceView.setX(snake.getLast()[0] * SIDE_LENGTH + OFFSET_FROM_SQUARE);
        faceView.setY(snake.getLast()[1] * SIDE_LENGTH + OFFSET_FROM_SQUARE +  200);
        switch (facing) {
            case up -> faceView.setRotate(0);
            case down -> faceView.setRotate(180);
            case right -> faceView.setRotate(90);
            case left -> faceView.setRotate(270);
        }
    }

    private int[] getOffsetToConnection(int x1, int y1, int x2, int y2) {
        int[] offset = new int[]{x1 - x2, y1 - y2};

        if(offset[0] == 1) offset[0] = 0;
        if(offset[1] == 1) offset[1] = 0;

        return offset;
    }

    private char getDirectionMoved(int x1, int x2) {
        if(x1 - x2 == 0) return 'y';
        return 'x';
    }

    public void showConnection(int x, int y, char type) {
        if(type == 'x')
            xConnections[x][y].setFill(Color.valueOf("2e2ee5"));
        else
            yConnections[x][y].setFill(Color.valueOf("2e2ee5"));
    }

    public void hideConnection(int x, int y, char type) {
        try {
            if(type == 'x')
                xConnections[x][y].setFill(Color.TRANSPARENT);
            else
                yConnections[x][y].setFill(Color.TRANSPARENT);
        } catch (Exception _) {}
        // for(SQUARES_PER_SIDE) for(SQUARES_PER_SIDE) hideConnection()
        // causes an exception - ignore it
    }

    public void removeTail() {
        snake.removeFirst();
        snake.trimToSize();
    }
}
