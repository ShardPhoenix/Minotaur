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
		
		bombWalls(input, maze);
	}

	private void bombWalls(Input input, MazeCell[][] maze)
	{
		if (input.bombPressed && System.currentTimeMillis() - lastBombed > Constants.BOMB_DELAY_MILLIS
				&& bombsLeft > 0)
		{
			int col = coord.col;
			int row = coord.row;
			Coord[] neighbours = {new Coord(col + 1, row), new Coord(col - 1, row),
									new Coord(col, row + 1), new Coord(col, row - 1),
									new Coord(col + 1, row - 1), new Coord(col - 1, row + 1),
									new Coord(col + 1, row + 1), new Coord(col - 1, row - 1)};
			
			for (Coord coord : neighbours)
			{
				if (coord.isInsideMaze())
				{
					maze[coord.col][coord.row].isWall = false;
				}
			}
			
			bombsLeft--;
			lastBombed = System.currentTimeMillis();
		}
		
	}

	private Coord tryMove(Input input, MazeCell[][] maze)
	{
		Coord newCoord = new Coord(coord.col + input.deltaCol, coord.row + input.deltaRow);
		
		if (newCoord.isInsideMaze() && !maze[newCoord.col][newCoord.row].isWall
				&& System.currentTimeMillis() - lastMoved > millisPerMove)
		{
			lastCoord = coord;
			lastMoved = System.currentTimeMillis();
			return newCoord;
		}
		
		return coord;
	}
}
