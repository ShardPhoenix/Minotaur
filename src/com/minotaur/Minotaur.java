package com.minotaur;

import java.util.ArrayList;
import java.util.List;

public class Minotaur
{
	public Coord coord;
	public List<Coord> route;
	public long lastMoved;
	public Coord lastCoord;
	public double millisPerMove;
	public Coord startCoord;
	
	public Minotaur(Coord c)
	{
		coord = c;
		startCoord = c;
		lastCoord = c;
		route = new ArrayList<Coord>();
		route.add(c);
		lastMoved = System.currentTimeMillis();
		millisPerMove = Constants.INITIAL_MINOTAUR_MILLIS_PER_MOVE;
	}
	
}
