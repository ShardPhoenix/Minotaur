package com.minotaur;

import java.util.ArrayList;
import java.util.List;

public class Minotaur extends Mover
{
	public List<Coord> route;
	
	public Minotaur(Coord c)
	{
		coord = c;
		lastCoord = c;
		route = new ArrayList<Coord>();
		route.add(c);
		lastMoved = System.currentTimeMillis();
		millisPerMove = Constants.INITIAL_MINOTAUR_MILLIS_PER_MOVE;
	}

	public void nextLevel(Coord minotaurStart)
	{
		coord = minotaurStart;
		lastCoord = minotaurStart;
		route = new ArrayList<Coord>();
		route.add(minotaurStart);
		lastMoved = System.currentTimeMillis();
		millisPerMove *= Constants.MINOTAUR_SPEED_UP;
	}
	
}
