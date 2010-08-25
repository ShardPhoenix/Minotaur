package com.minotaur;

import static org.junit.Assert.assertNotNull;

import android.test.ActivityInstrumentationTestCase2;

import com.minotaur.Constants;
import com.minotaur.GameState;

public class GameStateTest extends ActivityInstrumentationTestCase2<GameState>
{
	public void testGenerateMaze()
	{
		GameState game = new GameState();

		for (int col = 0; col < Constants.MAZE_COLS; col++)
		{
			for (int row = 0; row < Constants.MAZE_ROWS; row++)
			{
				assertNotNull(game.maze[col][row]);
			}
		}

	}
}
