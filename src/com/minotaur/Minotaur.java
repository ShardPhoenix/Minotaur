package com.minotaur;

import java.util.ArrayList;
import java.util.List;

public class Minotaur extends Mover
{
	public volatile List<Coord> route;
	private PathFinder pathFinder;
	private int threadCount;
	
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
		if (System.currentTimeMillis() - lastMoved > millisPerMove)
		{
			move();
			route = pathFinder.findRoute(coord, playerCoord, maze);
			/*
			if (threadCount == 0)
			{
				threadCount++;
				Thread pathThread = new PathThread(playerCoord, maze);
				pathThread.start();
			}
			*/
		}
	}
	
	private void move()
	{
		if (route != null && route.size() > 1)
		{
			lastCoord = coord;
			coord = route.get(1);
			lastMoved = System.currentTimeMillis();
		}
	}
	
	class PathThread extends Thread
	{
		private Coord playerCoord;
		private MazeCell[][] maze;
		
		public PathThread(Coord playerCoord, MazeCell[][] maze)
		{
			this.playerCoord = playerCoord;
			this.maze = maze;
		}
		
		@Override
		public void run()
		{
			route = pathFinder.findRoute(coord, playerCoord, maze);
			threadCount--;
		}
	}
}
