package com.minotaur;

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

		return initialMaze;
	}

}
