package com.minotaur;

import static com.minotaur.Constants.MAZE_COLS;
import static com.minotaur.Constants.MAZE_ROWS;

public class Player
{
	public Coord coord;
	public long lastMoved;
	public Coord lastCoord;
	public double millisPerMove;
	public int bombsLeft;
	public long lastBombed;
	
	public Player(Coord c)
	{
		coord = c;
		lastCoord = c;
		bombsLeft = Constants.STARTING_BOMBS;
		millisPerMove = Constants.INITIAL_PLAYER_MILLIS_PER_MOVE;
	}

	public void update(Input input, MazeCell[][] maze, Minotaur minotaur)
	{
		coord = tryMove(input, maze);
		
	}

	private Coord tryMove(Input input, MazeCell[][] maze)
	{
		Coord newCoord = new Coord(coord.col + input.deltaCol, coord.row + input.deltaRow);
		
		if (newCoord.col < MAZE_COLS && newCoord.col > 0 && newCoord.row < MAZE_ROWS && newCoord.row > 0
				&& !maze[newCoord.col][newCoord.row].isWall
				&& System.currentTimeMillis() - lastMoved > millisPerMove)
		{
			lastMoved = System.currentTimeMillis();
			return newCoord;
		}
		
		return coord;
	}
}
