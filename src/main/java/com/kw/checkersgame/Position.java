package com.kw.checkersgame;

import java.util.ArrayList;
import java.util.List;

/**
 * This class stores x and y coordinates, known as positions.
 * A position may have a route associated with it, which is a list of positions that lead to that position.
 * This class has methods for obtaining, calculating, and manipulating positions.
 */
public class Position {
    private int x, y;
    private List<Position> route;

    /**
     * Constructs a position based on given x and y coordinates.
     * @param x x coordinates for new position
     * @param y y coordiantes for new position
     */
    public Position(int x, int y)
    {
        // sorry, had to use the 'this' keyword :( not many ways to shorten x or y
        this.x = x;
        this.y = y;

    }

    /**
     * Creates a copy of another position
     * @param pos the position to be copied
     */
    public Position(Position pos)
    {
        x = pos.x;
        y = pos.y;
        if (pos.route != null)
        {
            route = new ArrayList<>(pos.route);
        }
    }

    /**
     * Calculates a new position based on shifts being added to x and y.
     * @param addX the shift being added to the x coordinate
     * @param addY the shift being added to the y coordinate
     * @return the new, shifted position
     */
    public Position shift(int addX, int addY)
    {
        return new Position(x + addX, y + addY);
    }

    /**
     * Calculates the position between two positions by finding the average.
     * @param avgX the x coordinate of the position we want to average our current position with
     * @param avgY the y coordinate of the position we want to average our current position with
     * @return the average between the current and given positions, a.k.a the piece in between the two positions
     */
    public Position average(int avgX, int avgY)
    {
        return new Position ((x + avgX) / 2, (y + avgY) / 2);
    }


    /**
     * Adds a position to the route.
     * @param step the position to be added to the route
     */
    public void addToRoute(Position step)
    {
        if (route == null)
        {
            route = new ArrayList<>();
        }
        route.add(step);
    }

    /**
     * Returns the last position saved in a route, which is the position that is about to move.
     * @return - piece that is about to move
     */
    public Position getLastInRoute()
    {
        return route.get(route.size() - 1);
    }

    /**
     * Returns the length of the route assigned to a position.
     * @return length of the route assigned to a pos
     */
    public int routeLength()
    {
        if (route == null)
        {
            return 0;
        }
        else return route.size();
    }

    /**
     * Returns the route of a position.
     * @return the route of a pos
     */
    public List<Position> getRoute()
    {
        if (route == null)
        {
            return new ArrayList<>();
        }
        return route;
    }

    /**
     * Returns the x coordinate of a position.
     * @return x coordinate of pos
     */
    public int getX()
    {
        return x;
    }

    /**
     * Returns the y coordinate of a position.
     * @return y coordinate of pos
     */
    public int getY()
    {
        return y;
    }

    /**
     * Returns whether a position is in bounds (does not exceed 8*8 board)
     * @return true if a position is in bounds, false otherwise
     */
    public boolean inBounds()
    {
        return x >= 0 && y >= 0 && x < 8 && y < 8;
    }
}
