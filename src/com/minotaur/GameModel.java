package com.minotaur;

import static com.minotaur.Constants.MAZE_COLS;
import static com.minotaur.Constants.MAZE_ROWS;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class GameModel
{
	public Minotaur minotaur;
	public Player player;

	public MazeCell[][] maze;

	public int victory;
	public long startTime;
	public int treasuresGained;
	public int totalTreasures;
	public int freeBombsGiven;
	public int score;
	public int levelNum;

	public GameModel()
	{
		maze = generateMaze();
		
		minotaur = new Minotaur(getMinotaurStart());
		player = new Player(new Coord(1, 1));
		

		startTime = System.currentTimeMillis();
		
		levelNum = 1;
	}

	public GameMode update(Set<Integer> keysHeld)
	{
		Input input = new Input(keysHeld);
		
		player.update(input, maze, minotaur);
		
		updatePickups();
		
		if (player.isDead)
		{
			return GameMode.STATE_LOSE;
		}
		if (player.escaped)
		{
			return GameMode.STATE_WIN;
		}
		
		return GameMode.STATE_RUNNING;
	}

	private void updatePickups()
	{
		Coord c = player.coord;
		if (maze[c.col][c.row].hasBomb)
		{
			maze[c.col][c.row].hasBomb = false;
			player.bombsLeft++;
		}
		if (maze[c.col][c.row].hasTreasure)
		{
			maze[c.col][c.row].hasTreasure = false;
			treasuresGained++;
		}
		
	}

	private MazeCell[][] generateMaze()
	{
		MazeCell[][] initialMaze = new MazeCell[Constants.MAZE_COLS][Constants.MAZE_ROWS];

		for (int col = 0; col < Constants.MAZE_COLS; col++)
		{
			for (int row = 0; row < Constants.MAZE_ROWS; row++)
			{
				boolean isWall = (col % 2 == 0) || (row % 2 == 0);
				initialMaze[col][row] = new MazeCell(new Coord(col, row), isWall);
			}
		}

		Coord bottomRight =  new Coord(MAZE_COLS - 2, MAZE_ROWS - 2);
		
		initialMaze = generateMaze(initialMaze, new ArrayList<Coord>(), bottomRight);
		
		initialMaze = addPickups(initialMaze);
		
		initialMaze[bottomRight.col][bottomRight.row].isExit = true;
		
		return initialMaze;
	}
	
	private Coord getMinotaurStart()
	{
		List<MazeCell> spaces = getLowerRightSpaces(maze);
		Collections.shuffle(spaces);
		return spaces.get(0).coord;
	}
	
	private MazeCell[][] addPickups(MazeCell[][] initialMaze)
	{
		List<MazeCell> deadEnds = getDeadEndSpaces(initialMaze);
		
		Collections.shuffle(deadEnds);
		
		for (int i = 0; i < Constants.NUMBER_TREASURE_PICKUPS; i++)
		{
			if (deadEnds.size() > i)
			{
				deadEnds.get(i).hasTreasure = true;
				deadEnds.remove(deadEnds.get(i));
			}
		}
		for (int i = 0; i < Constants.NUMBER_BOMB_PICKUPS; i++)
		{
			if (deadEnds.size() > i)
			{
				deadEnds.get(i).hasBomb = true;
				deadEnds.remove(deadEnds.get(i));
			}
		}
		
		return initialMaze;
	}

	private List<MazeCell> getDeadEndSpaces(MazeCell[][] maze2)
	{
		List<MazeCell> deadEnds = new ArrayList<MazeCell>();
		for (int col = 0; col < Constants.MAZE_COLS; col++)
		{
			for (int row = 0; row < Constants.MAZE_ROWS; row++)
			{
				MazeCell cell = maze2[col][row];
				if (!cell.isWall && has3Walls(cell, maze2))
				{
					deadEnds.add(cell);
				}
			}
		}
		
		return deadEnds;
	}
	
	private List<MazeCell> getLowerRightSpaces(MazeCell[][] maze2)
	{
		List<MazeCell> spaces = new ArrayList<MazeCell>();
		for (int col = Constants.MAZE_COLS/2; col < Constants.MAZE_COLS; col++)
		{
			for (int row = Constants.MAZE_ROWS/2; row < Constants.MAZE_ROWS; row++)
			{
				MazeCell cell = maze2[col][row];
				if (!cell.isWall)
				{
					spaces.add(cell);
				}
			}
		}
		
		return spaces;
	}
	

	private boolean has3Walls(MazeCell cell, MazeCell[][] maze2)
	{
		Coord c = cell.coord;
		int up = maze2[c.col][c.row - 1].isWall ? 1 : 0;
		int down = maze2[c.col][c.row + 1].isWall ? 1 : 0;
		int left = maze2[c.col - 1][c.row].isWall ? 1 : 0;
		int right = maze2[c.col + 1][c.row].isWall ? 1 : 0;
		return up + down + left + right == 3;
	}

	//using a loop instead of recursion to avoid stack overflow
	//TODO: figure out if the overflow is caused by a bug in the algo
	//TODO: seems to produce overly long double-back corridors in this version
	private MazeCell[][] generateMaze(MazeCell[][] currentMaze, List<Coord> stack, Coord currentCoord)
	{
		do
		{
			currentMaze[currentCoord.col][currentCoord.row].visited = true;
			List<Coord> neighbours = getUnvisitedNeighbours(currentCoord, currentMaze);
			
			if (neighbours.isEmpty())
			{
				if (stack.isEmpty())
				{
					return currentMaze;
				}
				else
				{
					currentCoord = stack.remove(stack.size() - 1);
				}
			}
			else
			{
				Random r = new Random();
				Coord neighbour = neighbours.get(r.nextInt(neighbours.size()));
				removeWallBetween(currentMaze, neighbour, currentCoord);
				stack.add(currentCoord);
				currentCoord = neighbour;
			}
		}
		while(!stack.isEmpty());
		
		return currentMaze;
			
	}

	private List<Coord> getUnvisitedNeighbours(Coord c, MazeCell[][] currentMaze)
	{
		Coord[] coords = {new Coord(c.col + 2, c.row), new Coord(c.col - 2, c.row),
						  new Coord(c.col, c.row - 2), new Coord(c.col, c.row + 2)};
		
		List<Coord> goodCoords = new ArrayList<Coord>();
		for (Coord coord : coords)
		{
			if (coord.isInsideMaze() && currentMaze[coord.col][coord.row].visited == false)
			{
				goodCoords.add(coord);
			}
		}
		
		return goodCoords;
	}
	
	private void removeWallBetween(MazeCell[][] currentMaze, Coord neighbour, Coord currentCoord)
	{
		if (neighbour.row == currentCoord.row)
		{
			int wallToRemoveCol = neighbour.col > currentCoord.col ? neighbour.col - 1 : currentCoord.col - 1;
			currentMaze[wallToRemoveCol][currentCoord.row].isWall = false;
		}
		else if (neighbour.col == currentCoord.col)
		{
			int wallToRemoveRow = neighbour.row > currentCoord.row ? neighbour.row - 1 : currentCoord.row - 1;
			currentMaze[currentCoord.col][wallToRemoveRow].isWall = false;
		}
	}
}
