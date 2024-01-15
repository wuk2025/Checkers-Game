package com.kw.checkersgame;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * This is the main class for an American checkers game.
 * This class bridges the gap between game logic and GUI.
 * Created and tested using IntelliJ IDEA.
 * @author k.wu
 */
public class Launcher extends Application {

    /**
     * The method that creates the visuals based on inputs.
     * @param primaryStage the main stage being shown
     */
    public void start(Stage primaryStage)
    {
        primaryStage.setTitle("Checkers Game");

        Group root = new Group();
        Scene primaryScene = new Scene(root);
        primaryStage.setScene(primaryScene);

        Canvas canvas = new Canvas(400, 500);
        root.getChildren().add(canvas);

        // Creates and draws board
        GraphicsContext gc = canvas.getGraphicsContext2D();
        Logic board = new Logic();
        AIPlayerLogic aiPlayer = new AIPlayerLogic();
        board.draw(gc);
        drawMessage(gc, "Choose one of the players below!", 15, 35, 22);

        // Sets up and adds AI Player, Human Player, and Information buttons to root.
        Font font = Font.font("Verdana");

        Button buttonAI = new Button("AI Player");
        buttonAI.setFont(font);
        buttonAI.setLayoutX(160);
        buttonAI.setLayoutY(460);
        buttonAI.setPrefSize(140, 30);
        buttonAI.setStyle("-fx-border-color: darkgreen; -fx-text-fill: darkgreen; -fx-background-color: lemonchiffon;");

        Button buttonHuman = new Button("Human Player");
        buttonHuman.setFont(font);
        buttonHuman.setLayoutX(10);
        buttonHuman.setLayoutY(460);
        buttonHuman.setPrefSize(140, 30);
        buttonHuman.setStyle("-fx-border-color: darkgreen; -fx-text-fill: darkgreen; -fx-background-color: lemonchiffon;");

        Button buttonInfo = new Button("(!) Info");
        buttonInfo.setFont(font);
        buttonInfo.setLayoutX(313);
        buttonInfo.setLayoutY(460);
        buttonInfo.setPrefSize(70, 30);
        buttonInfo.setStyle("-fx-border-color: darkgreen; -fx-text-fill: darkgreen; -fx-background-color: lemonchiffon;");

        Alert alert = new Alert(Alert.AlertType.NONE);

        root.getChildren().add(buttonAI);
        root.getChildren().add(buttonHuman);
        root.getChildren().add(buttonInfo);

        // When AI Player button is pressed, set AI player information.
        buttonAI.setOnAction(
                new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent actionEvent)
                    {
                        gc.clearRect(0,0,400,50);
                        board.setOpponent();
                        aiPlayer.setActive();
                        buttonAI.setStyle("-fx-border-color: darkgreen; -fx-text-fill: darkgreen; -fx-background-color: lightgreen;");
                        buttonHuman.setStyle("-fx-border-color: darkgreen; -fx-text-fill: darkgreen; -fx-background-color: lemonchiffon;");
                        drawMessage(gc, "Player chosen. Click piece to play.", 15, 35, 22);

                    }
                }
        );

        // When human player button is pressed, set human player information.
        buttonHuman.setOnAction(
                new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent actionEvent)
                    {
                        gc.clearRect(0,0,400,50);
                        board.setOpponent();
                        aiPlayer.setInactive();
                        buttonHuman.setStyle("-fx-border-color: darkgreen; -fx-text-fill: darkgreen; -fx-background-color: lightgreen;");
                        buttonAI.setStyle("-fx-border-color: darkgreen; -fx-text-fill: darkgreen; -fx-background-color: lemonchiffon;");
                        drawMessage(gc, "Player chosen. Click piece to play.", 15, 35, 22);

                    }
                }
        );

        // When information button is pressed, have information pop up
        buttonInfo.setOnAction(
                new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent actionEvent) {
                        alert.setAlertType(Alert.AlertType.INFORMATION);
                        alert.setTitle("BASIC CHECKERS RULES");
                        alert.setContentText("You MUST jump if you can. If there are 2 jumps in a row, you must do both. " +
                                "In game, jumps are calc in advance by program.");
                        alert.show();
                    }
                }
        );

        // When the screen is clicked...
        primaryScene.setOnMouseClicked(
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent)
                    {

                        // ... if there is no opponent, nothing happens
                        if (!board.getOpponentSet())
                        {
                            return;
                        }

                        // ... if the game is over, reset information and buttons
                        if (board.isGameOver())
                        {
                            System.out.println("resetting the board");
                            board.resetGame();
                            buttonAI.setStyle("-fx-border-color: darkgreen; -fx-text-fill: darkgreen; -fx-background-color: lemonchiffon;");
                            buttonHuman.setStyle("-fx-border-color: darkgreen; -fx-text-fill: darkgreen; -fx-background-color: lemonchiffon;");
                        }

                        // ... if the AI is active and it's the AI's turn, make AI move
                        if (board.getTurn() && aiPlayer.isActive())
                        {
                            System.out.println("ai move starts!");
                            aiPlayer.makeMove(board);
                            System.out.println("ai move stops!");
                        }
                        // ... if there are legal positions highlighted, try making a move to a position based on mouse click
                        else if (board.legalPosAvailable())
                        {
                            System.out.println("trying move starts!");
                            Position pos = board.decodeMouse(mouseEvent.getX(), mouseEvent.getY());
                            if (pos != null)
                            {
                                board.tryMovingTo(pos);
                            }
                            System.out.println("trying move stops!");
                        }
                        // ... if there are no legal positions yet, look for legal positions based on mouse click
                        else {
                            System.out.println("getting moves starts!");
                            Position pos = board.decodeMouse(mouseEvent.getX(), mouseEvent.getY());
                            if (pos != null)
                            {
                                board.setLegalMovesFromPos(pos);
                            }
                            System.out.println("getting moves stops!");
                        }

                        // draw some things
                        gc.clearRect(0,0,gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
                        board.draw(gc);
                        if (board.getOpponentSet()) // if there is an opponent
                        {
                            boolean turn = board.getTurn();
                            drawTurnCircle(gc, 120, 10, turn);
                            if (board.isGameOver())
                            {
                                // clear message board
                                gc.clearRect(0,0,400,50);

                                // pop up joke
                                alert.setAlertType(Alert.AlertType.WARNING);
                                alert.setTitle("built in joke!");
                                alert.setContentText("I'm pretty disappointed. Chuck Norris could have won that game in one move.");
                                alert.show();
                            }

                            drawMessage(gc, board.message(), 15, 35, 27);
                        }
                        else // if there's no opponent, display choice message
                        {
                            drawMessage(gc, "Choose one of the players below!", 15, 35, 22);
                        }
                    }
                }
        );

        primaryStage.show();
    }

    /**
     * Draws a custom message based on the arguments taken in.
     * @param gc the GraphicsContext the message is being displayed in
     * @param message the content of the message
     * @param x the x coordinate of the message
     * @param y the y coordinate of the message
     * @param size the size of the message's font
     */
    public void drawMessage(GraphicsContext gc, String message, double x, double y, double size)
    {
        gc.setFont(new Font("Verdana", size));
        gc.setFill(Color.DARKGREEN);
        gc.fillText(message, x, y);
    }

    /**
     * Draws a circle on the message board that indicates which player's turn it is.
     * @param gc the GraphicsContext the circle is being displayed in
     * @param x the x coordinate of the circle
     * @param y the y coordinate of the circle
     * @param dark whether the circle will be dark or light
     */
    public void drawTurnCircle(GraphicsContext gc, double x, double y, boolean dark)
    {
        if (dark)
        {
            gc.setFill(Color.LIGHTCORAL);
        }
        else
        {
            gc.setFill(Color.WHITE);
        }

        gc.fillOval(x, y, 30, 30);

        if (dark)
        {
            gc.setStroke(Color.DARKRED);
            gc.strokeOval(x, y, 30, 30);
        }
        else
        {
            gc.setStroke(Color.DARKGREEN);
            gc.strokeOval(x, y, 30, 30);
        }
    }


    /**
     * The main method that runs the class.
     * @param args command line arguments
     */
    public static void main(String[] args)
    {
        launch(args);
    }
}
