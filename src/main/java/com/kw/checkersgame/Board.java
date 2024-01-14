package com.kw.checkersgame;

/**
 * This class initializes a board, which is an array of pieces.
 * It has methods for obtaining and setting these pieces.
 */
public class Board {

    private Piece[][] pieces;

    /**
     * Initializes a board with proper information for its pieces.
     */
    public Board()
    {
        // initializes the board
        pieces = new Piece[8][8];
        for (int i = 0; i < 8; i++)
        {
            for (int j = 0; j < 8; j++)
            {
                pieces[i][j] = new Piece();
            }
        }

        // set dark pieces
        for (int i = 0; i < 3; i++)
        {
            for (int j = 0; j < 8; j += 2)
            {
                boolean even = (i % 2 == 0) ? true : false;
                if (even)
                {
                    pieces[j+1][i].setDarkPiece();
                }
                if (!even)
                {
                    pieces[j][i].setDarkPiece();
                }
            }
        }

        // set light pieces
        for (int i = 5; i < 8; i++)
        {
            for (int j = 0; j < 8; j += 2)
            {
                boolean even = (i % 2 == 0) ? true : false;
                if (even)
                {
                    pieces[j+1][i].setLightPiece();
                }
                if (!even)
                {
                    pieces[j][i].setLightPiece();
                }
            }
        }
    }

    /**
     * Resets all pieces on the board to their original positions.
     */
    public void resetBoard()
    {
        for (int i = 0; i < 8; i++)
        {
            for (int j = 0; j < 8; j++)
            {
                pieces[i][j] = new Piece();
            }
        }

        // set dark pieces
        for (int i = 0; i < 3; i++)
        {
            for (int j = 0; j < 8; j += 2)
            {
                boolean even = (i % 2 == 0) ? true : false;
                if (even)
                {
                    pieces[j+1][i].setDarkPiece();
                }
                if (!even)
                {
                    pieces[j][i].setDarkPiece();
                }
            }
        }

        // set light pieces
        for (int i = 5; i < 8; i++)
        {
            for (int j = 0; j < 8; j += 2)
            {
                boolean even = (i % 2 == 0) ? true : false;
                if (even)
                {
                    pieces[j+1][i].setLightPiece();
                }
                if (!even)
                {
                    pieces[j][i].setLightPiece();
                }
            }
        }
    }

    /**
     * Sets a piece on a specified position.
     * @param pos the position that the piece is being set on
     * @param piece the piece that is being set
     */
    public void setPieceOnBoard(Position pos, Piece piece)
    {
        pieces[pos.getX()][pos.getY()] = new Piece(piece);
    }

    /**
     * Returns the piece at a certain position.
     * @param p the position that we want a piece from
     * @return piece from that position
     */
    public Piece getPiece(Position p)
    {
        if (p.inBounds())
        {
            return pieces[p.getX()][p.getY()];
        }
        else return null;
    }

    /**
     * Returns the piece at a certain x and y coordinate.
     * @param newX the x coordinate of the piece we want
     * @param newY the y coordinate of the piece we want
     * @return piece from the specified x and y coordinates
     */
    public Piece getPiece(int newX, int newY)
    {
        return getPiece(new Position(newX, newY));
    }
}
