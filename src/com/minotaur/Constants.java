package com.minotaur;

public class Constants
{
	public static final int MAZE_CELL_WIDTH = 16; //pixels
	
	//must be odd numbers
	public static final int MAZE_COLS = 19;
	public static final int MAZE_ROWS = 27;
	
	public static final int MAZE_TOP_MARGIN = 0;
	public static final int MAZE_LEFT_MARGIN = 0;
	
	public static final int BOMB_DELAY_MILLIS = 500;
	public static final int STARTING_BOMBS = 2;
	public static final int BOMBS_GAINED_PER_LEVEL = 0;
	public static final int NUMBER_BOMB_PICKUPS = 2;
	public static final int FREE_BOMB_PER = 5000;
	
	public static final double INITIAL_PLAYER_MILLIS_PER_MOVE = 75.0;
	public static final double INITIAL_MINOTAUR_MILLIS_PER_MOVE = 220.0;
	public static final double MINOTAUR_SPEED_UP = 0.92;
	public static final int MINOTAUR_START_DELAY = 4000;
	
	public static final int NUMBER_TREASURE_PICKUPS = 6;
	public static final int TREASURE_SCORE_CONSTANT = 50;
}
