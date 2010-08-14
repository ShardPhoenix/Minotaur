package com.minotaur;

/*
 * A class that holds coordinates relative to the maze. Row is the row (y-coordinate) in the maze, 
 * col is the column (x-coordinate) in the maze
 */
public class Coord
{
	public int col;
	public int row;
	
	public Coord(int col, int row)
	{
		this.col = col;
		this.row = row;
	}
}
