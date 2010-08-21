package com.minotaur;

import static com.minotaur.Constants.MAZE_COLS;
import static com.minotaur.Constants.MAZE_ROWS;

/*
 * A class that holds coordinates relative to the maze. Row is the row (y-coordinate) in the maze, 
 * col is the column (x-coordinate) in the maze
 */
public class Coord
{
	public final int col;
	public final int row;
	
	public Coord(int col, int row)
	{
		this.col = col;
		this.row = row;
	}
	
	//returns true if the coord is not outside the maze and is not part of the outer wall
	public boolean isInsideMaze()
	{
		return col < MAZE_COLS - 1 && col > 0 && row < MAZE_ROWS - 1 && row > 0;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + col;
		result = prime * result + row;
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Coord other = (Coord) obj;
		if (col != other.col)
			return false;
		if (row != other.row)
			return false;
		return true;
	}
	
	
}
