package com.minotaur;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	
	public void update(Coord playerCoord, MazeCell[][] maze)
	{
		updateRoute(playerCoord, maze);
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
	
	private void updateRoute(Coord target, MazeCell[][] maze)
	{
		List<Coord> open = new ArrayList<Coord>();
		open.add(coord);
		List<Coord> closed = new ArrayList<Coord>();
		Map<Coord, Double> g = new HashMap<Coord, Double>();
		g.put(coord, 0.0);
		Map<Coord, Double> h = new HashMap<Coord, Double>();
		h.put(coord, manhattanDist(coord, target));
		Map<Coord, Double> f = new HashMap<Coord, Double>();
		f.put(coord, manhattanDist(coord, target));
		Map<Coord, Coord> cameFrom = new HashMap<Coord, Coord>();
		
		route = aStar(maze, target, open, closed, g, h, f, cameFrom);
	}
	
	
	private List<Coord> aStar(MazeCell[][] maze, Coord target, List<Coord> open, List<Coord> closed,
			Map<Coord, Double> g, Map<Coord, Double> h, Map<Coord, Double> f, Map<Coord, Coord> cameFrom)
	{
		while (true)
		{
			if (open.isEmpty())
			{
				return null;
			}
			Coord current = getLowestF(f, open);
			if (current.equals(target))
			{
				return reconstructPath(cameFrom, current, new ArrayList<Coord>());
			}
			open.remove(current);
			closed.add(current);
			double tenativeG = g.get(current) + 1;
			List<Coord> neighbours = getNeighbours(maze, current, closed, open, tenativeG, g);
			
			if (!neighbours.isEmpty())
			{
				open.addAll(neighbours);
				for (Coord c : neighbours)
				{
					cameFrom.put(c, current);
					g.put(c, tenativeG);
					h.put(c, manhattanDist(c, target));
					f.put(c, g.get(c) + h.get(c));
				}
			}
		}
		
		//return aStar(maze, target, open, closed, g, h, f, cameFrom);
	}
	

	private List<Coord> reconstructPath(Map<Coord, Coord> cameFrom, Coord current, List<Coord> path)
	{
		if (cameFrom.get(current) != null)
		{
			path.add(current);
			return reconstructPath(cameFrom, cameFrom.get(current), path);
		}
		else
		{
			path.add(current);
			Collections.reverse(path);
			return path;
		}
	}

	private Double manhattanDist(Coord c, Coord target)
	{
		return Math.abs(1.0 * (c.col - target.col)) + Math.abs(1.0 * (c.row - target.row));
	}

	private List<Coord> getNeighbours(MazeCell[][] maze, Coord current, List<Coord> closed, List<Coord> open,
			double tentativeG, Map<Coord, Double> g)
	{
		List<Coord> goodNeighbours = new ArrayList<Coord>();
		int col = current.col;
		int row = current.row;
		Coord[] neighbours = {new Coord(col + 1, row), new Coord(col - 1, row),
				new Coord(col, row + 1), new Coord(col, row - 1),
				new Coord(col + 1, row - 1), new Coord(col - 1, row + 1),
				new Coord(col + 1, row + 1), new Coord(col - 1, row - 1)};
		for (Coord c : neighbours)
		{
			if (c.isInsideMaze() && !closed.contains(c)
					&& !maze[c.col][c.row].isWall)
			{
				if (!open.contains(c) || g.get(c) > tentativeG)
				{
					goodNeighbours.add(c);
				}
			}
		}
		return goodNeighbours;
	}

	private Coord getLowestF(Map<Coord, Double> f, List<Coord> open)
	{
		double lowestF = Double.MAX_VALUE;
		Coord lowestFCoord = null;
		for (Coord c : open)
		{
			if (f.get(c) < lowestF)
			{
				lowestFCoord = c;
			}
		}
		return lowestFCoord;
	}
}
