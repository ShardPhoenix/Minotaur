package com.minotaur;

import java.util.ArrayList;
import java.util.List;

public class Minotaur extends Mover
{
	public List<Coord> route;
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
