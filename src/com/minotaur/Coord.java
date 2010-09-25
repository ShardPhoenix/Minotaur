package com.minotaur;

import static com.minotaur.Constants.MAZE_COLS;
import static com.minotaur.Constants.MAZE_ROWS;

/*
 * A class that holds coordinates relative to the maze. 
 * row is the row (y-coordinate) in the maze, 
 * col is the column (x-coordinate) in the maze
 */
public class Coord
{
	public final int col;
	public final int row;
	
	public static final Coord PLAYER_START;
	public static final Coord EXIT;
	
	//cache the coordinates as they are immutable
	private static final Coord[][] coords;
	static
	{
		coords = new Coord[MAZE_COLS][MAZE_ROWS];
		for (int col = 0; col < MAZE_COLS; col++)
		{
			for (int row = 0; row < MAZE_ROWS ; row++)
			{
				coords[col][row] = new Coord(col, row);
			}
		}
		PLAYER_START = getCoord(1, 1);
		EXIT = getCoord(MAZE_COLS - 2, MAZE_ROWS - 2);
	}
	
	private Coord(int col, int row)
	{
		this.col = col;
		this.row = row;
	}
	
	//returns true if the coord is not outside the maze and is not part of the outer wall
	public boolean isInsideMaze()
	{
		return col < MAZE_COLS - 1 && col > 0 && row < MAZE_ROWS - 1 && row > 0;
	}
	
	public static Coord getCoord(int col, int row)
	{
		if (col < 0 || col > MAZE_COLS - 1 || row < 0 || row > MAZE_ROWS - 1)
		{
			return null;
		}
		
		return coords[col][row];
	}
}
