package com.minotaur;

public class Player extends Mover
{
	public int bombsLeft;
	public long lastBombed;
	public boolean isDead;
	public boolean escaped;
	
	public Player(Coord c)
	{
		coord = c;
		lastCoord = c;
		bombsLeft = Constants.STARTING_BOMBS;
		millisPerMove = Constants.INITIAL_PLAYER_MILLIS_PER_MOVE;
		lastBombed = System.currentTimeMillis();
	}

	public void update(Input input, MazeCell[][] maze, Minotaur minotaur)
	{
		coord = tryMove(input, maze);
		
		bombWalls(input, maze);
		
		if (coord == minotaur.coord)
		{
			isDead = true;
		}
		else if (coord == Coord.EXIT)
		{
			escaped = true;
		}
	}
	
	public void nextLevel()
	{
		Coord c = Coord.PLAYER_START;
		coord = c;
		lastCoord = c;
		isDead = false;
		escaped = false;
	}

	private void bombWalls(Input input, MazeCell[][] maze)
	{
		if (input.bombPressed && System.currentTimeMillis() - lastBombed > Constants.BOMB_DELAY_MILLIS
				&& bombsLeft > 0)
		{
			int col = coord.col;
			int row = coord.row;
			Coord[] neighbours = {Coord.getCoord(col + 1, row), Coord.getCoord(col - 1, row),
								  Coord.getCoord(col, row + 1), Coord.getCoord(col, row - 1),
								  Coord.getCoord(col + 1, row - 1), Coord.getCoord(col - 1, row + 1),
								  Coord.getCoord(col + 1, row + 1), Coord.getCoord(col - 1, row - 1)};
			
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
		Coord newCoord = Coord.getCoord(coord.col + input.deltaCol, coord.row + input.deltaRow);
		
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
