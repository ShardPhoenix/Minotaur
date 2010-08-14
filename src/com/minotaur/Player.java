package com.minotaur;

public class Player
{
	public Coord coord;
	public long lastMoved;
	public Coord lastCoord;
	public double millisPerMove;
	public int bombs;
	public long lastBombed;
	
	public Player(Coord c)
	{
		coord = c;
		lastCoord = c;
		bombs = Constants.STARTING_BOMBS;
		millisPerMove = Constants.INITIAL_PLAYER_MILLIS_PER_MOVE;
	}
}
