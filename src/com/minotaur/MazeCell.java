package com.minotaur;

public class MazeCell
{
	public Coord coord;
	public int x; //pixel coords
	public int y;
	
	public boolean isWall;
	public boolean visited;
	public boolean touched;
	public boolean hasTreasure;
	public boolean isMinotaurStart;
	public boolean hasBomb;
	public boolean isExit;
	
	public MazeCell(Coord c, boolean isWall)
	{
		coord = c;
		x = Constants.MAZE_CELL_WIDTH * c.col + Constants.MAZE_LEFT_MARGIN;
		y = Constants.MAZE_CELL_WIDTH * c.row + Constants.MAZE_TOP_MARGIN;
		this.isWall = isWall;
	}
}
