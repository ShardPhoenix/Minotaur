package com.minotaur;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Minotaur extends Mover
{
	public List<Coord> route;
	private PathFinder pathFinder;
	
	public Minotaur(Coord c)
	{
		coord = c;
		lastCoord = c;
		route = new ArrayList<Coord>();
		route.add(c);
		lastMoved = System.currentTimeMillis();
		millisPerMove = Constants.INITIAL_MINOTAUR_MILLIS_PER_MOVE;
		pathFinder = new PathFinder();
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
	
	public void update(Coord playerCoord, MazeCell[][] maze)
	{
		if (System.currentTimeMillis() - lastMoved > 250)
		{
			route = pathFinder.findRoute(coord, playerCoord, maze);
		}
		move();
	}
	
	private void move()
	{
		if (System.currentTimeMillis() - lastMoved > millisPerMove
				&& route != null && route.size() > 1)
		{
			lastCoord = coord;
			coord = route.get(1);
			lastMoved = System.currentTimeMillis();
		}
	}
}
