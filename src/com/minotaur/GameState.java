package com.minotaur;

import static com.minotaur.Constants.MAZE_COLS;
import static com.minotaur.Constants.MAZE_ROWS;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class GameState
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

	public GameState()
	{
		minotaur = new Minotaur(new Coord(4, 4));
		player = new Player(new Coord(1, 1));
		
		maze = generateMaze();

		startTime = System.currentTimeMillis();

		levelNum = 1;
	}

	public void update(Set<Integer> keysHeld)
	{
		Input input = new Input(keysHeld);
		
		player.update(input, maze, minotaur);
		
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
		return generateMaze(initialMaze, new ArrayList<Coord>(), bottomRight);
	}
	
	//using a loop instead of recursion to avoid stack overflow
	//TODO: figure out if the overflow is caused by a bug in the algo
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
		List<Coord> coords = new ArrayList<Coord>();
		
		coords.add(new Coord(c.col + 2, c.row));
		coords.add(new Coord(c.col - 2, c.row));
		coords.add(new Coord(c.col, c.row - 2));
		coords.add(new Coord(c.col, c.row + 2));
		
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
