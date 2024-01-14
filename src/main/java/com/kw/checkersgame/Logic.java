package com.kw.checkersgame;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import java.util.*;

/**
 * This class has all the game logic for moves in checkers.
 * It calculates and draws legal movements for the game.
 */
public class Logic {
    private Board board;
    private List<Position> legalPos;
    private boolean tie, lastDark, gameOver, opponentSet, killAvailable, multikillAvailable, lightWinner, darkWinner;

    /**
     * Logic constructor sets internal information for calculations.
     */
    public Logic()
    {
        board = new Board();
        legalPos = new ArrayList<>();
        lastDark = true;
        gameOver = false;
        opponentSet = false;
        killAvailable = false;
        multikillAvailable = false;
        tie = false;
        lightWinner = false;
        darkWinner = false;
    }

    /**
     * Updates legalPos to reflect all legal positions a specified start position can move to.
     * @param p start position to move from
     */
    public void setLegalMovesFromPos(Position p)
    {
        // check for available kills first
        List<Position> kills = getAvailableKills(!lastDark);

        if (kills.isEmpty() // if there are no kills
                && p.inBounds() // if the piece is in bound
                && !board.getPiece(p).getEmpty() // if it is not empty
                && board.getPiece(p).getDark() != lastDark)  // and the color is diff from last time
        {
            // all the legal pos are the moves we can make
            legalPos = getMoves(p);
        }
        else // else, there are valid kills available
        {
            legalPos.clear();
            killAvailable = true;
            for (Position kill : kills)
            {
                // all the moves from strikes are legal
                legalPos.addAll(getMoves(kill));
            }
            killAvailable = false;
        }
    }

    /**
     * Updates the pieces on the board if a specified end spot to move to is legal.
     * @param p end position to move to
     */
    public void tryMovingTo(Position p)
    {
        if (isALegalPos(p)) // if the position we are moving to is legal
        {
            lastDark = !lastDark; // switch the turn

            // moves the current piece to its end position
            board.setPieceOnBoard(p, board.getPiece(legalPosition(p).getLastInRoute()));
            for (Position pos : legalPosition(p).getRoute())
            {
                board.getPiece(pos).setEmpty(true);
            }

            // checks the extremes of the board for king pieces
            for (int i = 0; i < 8; i++)
            {
                if (!board.getPiece(i, 0).getEmpty() && !board.getPiece(i, 0).getDark())
                {
                    board.getPiece(i, 0).setKing(true);
                }
                if (!board.getPiece(i, 7).getEmpty() && board.getPiece(i, 7).getDark())
                {
                    board.getPiece(i, 7).setKing(true);
                }
            }

        }

        legalPos.clear();

    }

    /**
     * Determines whether a specified position is in the list of legal positions.
     * @param p the position that is being checked for legality
     * @return true if the position is a legal position, false otherwise
     */
    private boolean isALegalPos(Position p)
    {
        for (Position pos : legalPos)
        {
            if (p.getX() == pos.getX())
            {
                if (p.getY() == pos.getY())
                {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns a copy of a legal position.
     * @param p the position being checked for legality and possibly copied
     * @return a copy of the position if it is legal, null otherwise
     */
    private Position legalPosition(Position p)
    {
        if(isALegalPos(p))
        {
            for (Position pos : legalPos)
            {
                if (p.getX() == pos.getX())
                {
                    if (p.getY() == pos.getY())
                    {
                        return new Position(pos);
                    }
                }
            }
        }

        return null;
    }


    /**
     * Returns a list of legal positions a specified piece could move to.
     * @param p a start position to move from
     * @return list of positions that are legal to move to
     */
    public List<Position> getMoves(Position p)
    {
        List<Position> moves = new ArrayList<>();

        // check for kills first
        List<Position> kills;
        if (board.getPiece(p).getKing())
        {
            kills = getKillsKing(p);
        }
        else
        {
            kills = getKills(p);
        }

        // if there are kills, add them to the list
        if (kills != null) {
            moves.addAll(kills);
        }


        int[] offsets = {-1,1};

        if (!killAvailable)
        {
            if (!board.getPiece(p).getEmpty())
            {
                // if king, test offsets in all directions
                if (board.getPiece(p).getKing())
                {
                    for (int offX : offsets)
                    {
                        for (int offY : offsets)
                        {
                            Position move = p.shift(offX, offY);

                            // if the piece we are moving to exists, and if it is currently empty...
                            if (board.getPiece(move) != null && board.getPiece(move).getEmpty())
                            {
                                // ... then add that move to our list
                                moves.add(new Position(move));
                            }
                        }
                    }
                }
                // if not king piece, only test up / down offsets depending on color
                else
                {
                    for (int offX : offsets)
                    {
                        Position move = p.shift(offX, board.getPiece(p).getDark() ? 1 : -1);

                        // if the piece we are moving exists, and if it is currently empty...
                        if (board.getPiece(move) != null && board.getPiece(move).getEmpty())
                        {
                            // ... then add that move to our list
                            moves.add(new Position(move));
                        }
                    }
                }
            }
        }

        // add start position to the routes of all possible moves it gets cleared later
        for (Position pos : moves)
        {
            pos.addToRoute(new Position(p));
        }

        return moves;
    }

    /**
     * Returns a list of kills positions that a specified piece could move to.
     * @param p a start position to move from
     * @return list of kill positions that are legal to move to
     */
    private List<Position> getKills(Position p)
    {
        List<Position> kills = new ArrayList<>();

        int[] offsets = {-2, 2};

        // if the piece we want to move is not empty
        if (!board.getPiece(p).getEmpty())
        {
            for (int offX : offsets)
            {
                Position move = p.shift(offX, board.getPiece(p).getDark() ? 2 : -2);
                if (move.inBounds() && // the place we want to move to is in bounds
                    board.getPiece(move).getEmpty() && // the place we want to move to is empty
                    !board.getPiece(move.average(p.getX(), p.getY())).getEmpty() && // the spot between our start and end has a piece
                    board.getPiece(move.average(p.getX(), p.getY())).getDark() != board.getPiece(p).getDark())  // the piece between is the opposite color
                {
                    if (!board.getPiece(p).getKing())
                    {
                        if (board.getPiece(p).getDark())
                        {
                            for (int multiOffX : offsets)
                            {
                                // check potential moves that branch off our current move
                                Position multiMove = move.shift(multiOffX, 2);

                                if (multiMove.inBounds() && // the place we want to move to is in bounds
                                        board.getPiece(multiMove).getEmpty() && // the place we want to move to is empty
                                        !board.getPiece(multiMove.average(move.getX(), move.getY())).getEmpty() && // the spot between our start and end has a piece
                                        !board.getPiece(multiMove.average(move.getX(), move.getY())).getDark())  // the piece between is light
                                {
                                    multiMove.addToRoute(new Position(move.average(p.getX(), p.getY())));
                                    multiMove.addToRoute(new Position(move.getX(), move.getY()));
                                    multiMove.addToRoute(new Position(multiMove.average(move.getX(), move.getY())));
                                    kills.add(new Position(multiMove));
                                    multikillAvailable = true;
                                }

                            }
                        }

                        if (!board.getPiece(p).getDark())
                        {
                            for (int multiOffX : offsets)
                            {
                                // check potential moves that branch off our current move
                                Position multiMove = move.shift(multiOffX, -2);

                                if (multiMove.inBounds() && // the place we want to move to is in bounds
                                        board.getPiece(multiMove).getEmpty() && // the place we want to move to is empty
                                        !board.getPiece(multiMove.average(move.getX(), move.getY())).getEmpty() && // the spot between our start and end has a piece
                                        board.getPiece(multiMove.average(move.getX(), move.getY())).getDark())  // the piece between is dark
                                {
                                    // changed adding position / moving info to the moved position, not og position
                                    multiMove.addToRoute(new Position(move.average(p.getX(), p.getY())));
                                    multiMove.addToRoute(new Position(move.getX(), move.getY()));
                                    multiMove.addToRoute(new Position(multiMove.average(move.getX(), move.getY())));
                                    kills.add(new Position(multiMove));
                                    multikillAvailable = true;
                                }

                            }
                        }
                    }
                    // changed adding position / moving info to the moved position, not og position
                    if (!multikillAvailable)
                    {
                        move.addToRoute(new Position(move.average(p.getX(), p.getY())));
                        kills.add(new Position(move));
                    }
                }
            }
        }

        multikillAvailable = false;
        return kills;
    }

    /**
     * Returns a list of kills positions that a specified king piece could move to.
     * @param p a start position with a king to move from
     * @return list of kill positions that are legal to move to
     */
    private List<Position> getKillsKing(Position p)
    {

        List<Position> kills = new ArrayList<>();

        int[] offsets = {-2, 2};

        // if the piece we want to move is not empty
        if (!board.getPiece(p).getEmpty())
        {
            for (int offX : offsets)
            {
                for (int offY : offsets)
                {
                    Position move = p.shift(offX, offY);
                    if (move.inBounds() && // the place we want to move to is in bounds
                            board.getPiece(move).getEmpty() && // the place we want to move to is empty
                            !board.getPiece(move.average(p.getX(), p.getY())).getEmpty() && // the spot between our start and end has a piece
                            board.getPiece(move.average(p.getX(), p.getY())).getDark() != board.getPiece(p).getDark())  // the piece between is the opposite color
                    {
                        if (board.getPiece(p).getKing())
                        {
                            if (board.getPiece(p).getDark())
                            {
                                for (int multiOffX : offsets)
                                {
                                    for (int multiOffY : offsets)
                                    {
                                        // check potential moves that branch off our current move
                                        Position multiMove = move.shift(multiOffX, multiOffY);

                                        if (multiMove.inBounds() && // the place we want to move to is in bounds
                                                board.getPiece(multiMove).getEmpty() && // the place we want to move to is empty
                                                !board.getPiece(multiMove.average(move.getX(), move.getY())).getEmpty() && // the spot between our start and end has a piece
                                                !board.getPiece(multiMove.average(move.getX(), move.getY())).getDark())  // the piece between is light
                                        {
                                            multiMove.addToRoute(new Position(move.average(p.getX(), p.getY())));
                                            multiMove.addToRoute(new Position(move.getX(), move.getY()));
                                            multiMove.addToRoute(new Position(multiMove.average(move.getX(), move.getY())));
                                            kills.add(new Position(multiMove));
                                            multikillAvailable = true;
                                        }

                                    }
                                }
                            }

                            if (!board.getPiece(p).getDark())
                            {
                                for (int multiOffX : offsets)
                                {
                                    for (int multiOffY : offsets)
                                    {
                                        // check potential moves that branch off our current move
                                        Position multiMove = move.shift(multiOffX, multiOffY);

                                        if (multiMove.inBounds() && // the place we want to move to is in bounds
                                                board.getPiece(multiMove).getEmpty() && // the place we want to move to is empty
                                                !board.getPiece(multiMove.average(move.getX(), move.getY())).getEmpty() && // the spot between our start and end has a piece
                                                board.getPiece(multiMove.average(move.getX(), move.getY())).getDark())  // the piece between is dark
                                        {
                                            // changed adding position / moving info to the moved position, not og position
                                            multiMove.addToRoute(new Position(move.average(p.getX(), p.getY())));
                                            multiMove.addToRoute(new Position(move.getX(), move.getY()));
                                            multiMove.addToRoute(new Position(multiMove.average(move.getX(), move.getY())));
                                            kills.add(new Position(multiMove));
                                            multikillAvailable = true;
                                        }

                                    }
                                }
                            }
                        }

                        if (!multikillAvailable)
                        {
                            move.addToRoute(new Position(move.average(p.getX(), p.getY())));
                            kills.add(new Position(move));
                        }
                    }
                }
            }
        }

        multikillAvailable = false;
        return kills;
    }

    /**
     * Returns a list of all positions with specific colored pieces that could potentially make a move.
     * @param dark the color of the pieces being checked
     * @return list of all positions that can be moved
     */
    public List<Position> getAvailableMoves(boolean dark)
    {

        List<Position> moves = new ArrayList<>();

        // tests all pieces
        for (int i = 0; i < 8; i++)
        {
            for (int j = 0; j < 8; j++)
            {
                // if a spot is not empty, and its color is the one we want...
                if (!board.getPiece(i,j).getEmpty() && board.getPiece(i,j).getDark() == dark)
                {
                    List<Position> availablePos = getMoves(new Position(i,j));
                    if (!availablePos.isEmpty())
                    {
                        // if there are any routes with any length, that is a move
                        if (availablePos.getFirst().routeLength() >= 1)
                        {
                            moves.add(new Position(i, j));
                        }
                    }
                }
            }
        }
        return moves;
    }

    /**
     * Returns a list of all positions with specific colored pieces that could potentially make a kill move.
     * @param color the color of the pieces being checked for kill moves
     * @return list of all positions that can make a kill
     */
    public List<Position> getAvailableKills(boolean color)
    {
        List<Position> moves = new ArrayList<>();

        // tests all pieces
        for (int i = 0; i < 8; i++)
        {
            for (int j = 0; j < 8; j++)
            {
                // if a spot is not empty, and its color is the one we want
                if (!board.getPiece(i,j).getEmpty() && board.getPiece(i,j).getDark() == color)
                {
                    List<Position> availablePos = getMoves(new Position(i,j));

                    if (!availablePos.isEmpty())
                    {
                        // checks if the route assigned to that position is greater than 2
                        if (availablePos.get(0).routeLength() >= 2)
                        {
                            moves.add(new Position(i, j));
                        }
                    }
                }
            }
        }
        return moves;
    }

    /**
     * Returns whether legal positions have been calculated and added to the legalPos list.
     * @return true if there are legal positions in the legalPos list, false otherwise
     */
    public boolean legalPosAvailable()
    {
        return !legalPos.isEmpty();
    }

    /**
     * Draws the board, tiles, and pieces in appropriate positions.
     * @param gc the GraphicsContext being added to
     */
    public void draw(GraphicsContext gc)
    {
        // line separating message from game
        gc.setStroke(Color.DARKGREEN);
        gc.strokeLine(0, 50, 400, 50);
        gc.strokeLine(0, 450, 400, 450);

        gc.setFill(Color.LIGHTYELLOW);
        gc.fillRect(0, 50, 400, 400);

        // drawing tiles
        gc.setFill(Color.DARKSEAGREEN);
        for (int i = 0; i < 8; i++)
        {
            for (int j = 0; j < 8; j+=2)
            {
                boolean even = i % 2 == 0;
                if (even)
                {
                    gc.fillRect(i * 50, (j+1) * 50 + 50, 50, 50);
                    gc.setStroke(Color.DARKGREEN);
                    gc.strokeRect(i * 50, (j+1) * 50 + 50, 50, 50);
                }
                if (!even)
                {
                    gc.fillRect(i * 50, j * 50 + 50, 50, 50);
                    gc.setStroke(Color.DARKGREEN);
                    gc.strokeRect(i * 50, (j+1) * 50 + 50, 50, 50);
                }
            }
        }

        // changing color for all available routes
        for (Position pos : legalPos)
        {
            gc.setFill(Color.DARKORANGE);
            gc.fillRect(pos.getX() * 50, ((1 + pos.getY()) * 50), 50, 50);
            gc.setFill(Color.SANDYBROWN);
            if (pos.getRoute() != null)
                for (Position step : pos.getRoute())
                {
                    gc.fillRect(step.getX() * 50, (step.getY() + 1) * 50, 50, 50);
                }
        }

        // drawing pieces
        for (int i = 0; i < 8; i++)
        {
            for (int j = 0; j < 8; j++)
            {
                board.getPiece(i,j).draw(gc, i * 50, j * 50 + 50);
            }
        }
    }

    /**
     * Translates the pixel coordinates of the mouse into logical coordinates for the board.
     * Creates a position using these translated coordinates.
     * @param mouseX x coordinate the mouse is at
     * @param mouseY y coordinate the mouse is at
     * @return position made from translated mouse coordinates
     */
    public Position decodeMouse(double mouseX, double mouseY)
    {
        // check the mouse is not clicking the message area
        if (mouseY > 50 && mouseY < 450)
        {

            int decodedX = (int)(mouseX / 50);
            int decodedY = (int)((mouseY - 50 ) / 50);

            return new Position(decodedX, decodedY);
        }
        else return null;
    }

    /**
     * Determines whether the game is over and identifies who won or tied.
     * @return true if the game is over, false otherwise
     */
    public boolean isGameOver()
    {
        boolean tied = (lightPiecesLeft(board) == 1 && darkPiecesLeft(board) == 1);
        if (tied)
        {
            tie = true;
        }

        if (darkPiecesLeft(board) == 0)
        {
            lightWinner = true;
        }

        if (lightPiecesLeft(board) == 0)
        {
            darkWinner = true;
        }

        gameOver = lightWinner || darkWinner || tied;

        return gameOver;
    }

    /**
     * Determines the number of light pieces left on the board.
     * @param b the board being searched for pieces
     * @return the number of light pieces on the board
     */
    private int lightPiecesLeft(Board b)
    {
        int count = 0;
        for (int i = 0; i < 8; i++)
        {
            for (int j = 0; j < 8; j++)
            {
                Piece tempPiece = b.getPiece(i, j);
                // if there's a piece at position, and it's dark, add it to total
                if (!tempPiece.getEmpty() && !tempPiece.getDark())
                {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * Determines the number of dark pieces left on the board.
     * @param b the board being searched for pieces
     * @return the number of dark pieces on the board
     */
    private int darkPiecesLeft(Board b)
    {
        int count = 0;
        for (int i = 0; i < 8; i++)
        {
            for (int j = 0; j < 8; j++)
            {
                Piece tempPiece = b.getPiece(i, j);
                // if there's a piece at position, and it's light, add it to total
                if (!tempPiece.getEmpty() && tempPiece.getDark())
                {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * Determines what message should be displayed depending on the situation.
     * If the game is over, there are different messages for ties and winners.
     * If the game is not over, there is a message that displays whose turn it is.
     * @return the message that should be displayed on screen
     */
    public String message()
    {
        if (isGameOver() && tie)
        {
            return "TIE! Click to reset";
        }
        else if (isGameOver() && lightWinner)
        {
            return "WHITE WINS! Click to reset";
        }
        else if (isGameOver() && darkWinner)
        {
            return "RED WINS! Click to reset";
        }
        else return "Player: ";
    }

    /**
     * Resets all the pieces on the board and internally resets information as well.
     */
    public void resetGame()
    {
        board.resetBoard();

        lastDark = true;
        gameOver = false;
        opponentSet = false;
        tie = false;
        killAvailable = false;
        multikillAvailable = false;
        lightWinner = false;
        darkWinner = false;
    }


    /**
     * Returns whether the opponent has been set.
     * @return true if the opponent has been set, false otherwise
     */
    public boolean getOpponentSet()
    {
        return opponentSet;
    }

    /**
     * Sets opponentSet to true, signifying the opponent has been set.
     */
    public void setOpponent()
    {
        opponentSet = true;
    }

    /**
     * Sets the current positions from legalPos to a new, specified list of positions.
     * @param newLegalPos the new list of positions replacing the current legalPos
     */
    public void setLegalPos(List<Position> newLegalPos)
    {
        legalPos = newLegalPos;
    }

    /**
     * Returns which player's turn it is.
     * @return true if the current turn is dark, false if current turn is light
     */
    public boolean getTurn()
    {
        return !lastDark;
    }

}
