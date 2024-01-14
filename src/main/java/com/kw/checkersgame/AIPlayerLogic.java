package com.kw.checkersgame;

import javafx.scene.paint.Color;

import java.util.List;

/**
 * The AIPLayerLogic class has all the logic for an AI move in checkers.
 * It calculates and makes random moves.
 */
public class AIPlayerLogic extends Logic {
    boolean active;

    /**
     * Constructs an AIPlayerLogic, which starts inactive.
     */
    public AIPlayerLogic()
    {
        active = false;
    }

    /**
     * Calculates and makes a random, legal move available for a dark piece when it is dark's turn.
     * @param board the current logic object being manipulated, which contains important information such as positions
     */
    public void makeMove(Logic board)
    {

        // if the board has moves available for dark pieces
        if (!(board.getAvailableMoves(true).isEmpty()))
        {

            // get all available moves and kills
            List<Position> movesAI = board.getAvailableMoves(true);
            List<Position> killsAI = board.getAvailableKills(true);

            // if there are no kills
            if (killsAI.isEmpty())
            {
                // choose a random move from available moves
                int randomMove = (int)(Math.random() * movesAI.size());
                Position from = movesAI.get(randomMove);
                List<Position> to = board.getMoves(from);
                board.setLegalPos(to);

                // try that move
                board.tryMovingTo(to.get(0));

            }
            else // if there are kills
            {
                // choose a random move from available kills
                int randomMove = (int)(Math.random() * killsAI.size());
                Position from = killsAI.get(randomMove);
                List<Position> to = board.getMoves(from);
                board.setLegalPos(to);

                // try that move
                board.tryMovingTo(to.get(0));
            }

        }
    }

    /**
     * Returns whether the AI player is active.
     * @return true if the AI is active, false otherwise
     */
    public boolean isActive()
    {
        return active;
    }

    /**
     * Sets the AI player to be active.
     */
    public void setActive()
    {
        active = true;
    }

    /**
     * Sets the AI player to be inactive.
     */
    public void setInactive()
    {
        active = false;
    }
}
