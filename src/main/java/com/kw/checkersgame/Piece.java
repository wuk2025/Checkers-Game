package com.kw.checkersgame;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * This class stores information for pieces and draws them.
 */
public class Piece
{
    private boolean empty, dark, king;

    /**
     * Creates a piece with default information.
     */
    public Piece()
    {
        empty = true;
        dark = true;
        king = false;
    }

    /**
     * Makes a copy of a given piece.
     * @param p - the piece to make a copy of
     */
    public Piece(Piece p)
    {
        empty = p.empty;
        dark = p.dark;
        king = p.king;
    }

    /**
     * Sets a non-king light piece.
     */
    public void setLightPiece()
    {
        empty = false;
        king = false;
        dark = false;
    }

    /**
     * Sets a non-king dark piece.
     */
    public void setDarkPiece()
    {
        empty = false;
        king = false;
        dark = true;
    }

    /**
     * Returns whether a piece is empty.
     * @return true if a piece is empty, false otherwise
     */
    public boolean getEmpty()
    {
        return empty;
    }

    /**
     * Returns whether a piece is a king.
     * @return true if a piece is a king, false otherwise
     */
    public boolean getKing()
    {
        return king;
    }

    /**
     * Returns whether a piece is dark.
     * @return true if a piece is dark, false if a piece is light
     */
    public boolean getDark()
    {
        return dark;
    }

    /**
     * Sets whether a piece is empty or not.
     * @param e true if we want piece to be empty, false otherwise
     */
    public void setEmpty(boolean e)
    {
        empty = e;
    }

    /**
     * Sets whether a piece is a king or not.
     * @param k true if we want piece to be a king, false otherwise
     */
    public void setKing(boolean k)
    {
        king = k;
    }

    /**
     * Draws the piece in a given position.
     * @param gc GraphicsContext that the piece will be in
     * @param x logical x-coordinate of the piece on the board
     * @param y logical y-coordinate of the piece on the board
     */
    public void draw(GraphicsContext gc, double x, double y)
    {
        if (empty)
        {
            return;
        }

        // sets fill color and fills oval
        if (dark)
        {
            gc.setFill(Color.LIGHTCORAL);
        }
        if (!dark)
        {
            gc.setFill(Color.WHITE);
        }
        gc.fillOval(x + 0.1 * 50, y + 0.1 * 50, 40, 40);


        // sets stroke color and fills stroke
        if (dark)
        {
            gc.setStroke(Color.DARKRED);
            gc.strokeOval(x + 0.1 * 50, y + 0.1 * 50, 40, 40);
        }
        if (!dark)
        {
            gc.setStroke(Color.DARKGREEN);
            gc.strokeOval(x + 0.1 * 50, y + 0.1 * 50, 40, 40);
        }

        gc.setFill(Color.DARKGREEN);

        // sets fill for kings
        if (dark)
        {
            gc.setFill(Color.DARKRED);
        }
        if (king)
        {
            gc.fillText("K", x + 0.3 * 50, y + 0.7 * 50);
        }
    }
}
