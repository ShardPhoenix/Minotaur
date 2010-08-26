package com.minotaur;

import static com.minotaur.Constants.MAZE_COLS;
import static com.minotaur.Constants.MAZE_ROWS;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PathFinder
{
	//holds coords so they don't have to be instantiated inside the pathfinding loop
	private final Coord[][] coords;
	
	public PathFinder()
	{
		coords = new Coord[MAZE_COLS][MAZE_ROWS];
		
		for (int col = 0; col < MAZE_COLS; col++)
		{
			for (int row = 0; row < MAZE_ROWS; row++)
			{
				coords[col][row] = new Coord(col, row);
			}
		}
	}
	
	public List<Coord> findRoute(Coord start, Coord target,  MazeCell[][] maze)
	{
		List<Coord> open = new ArrayList<Coord>();
		open.add(start);
		List<Coord> closed = new ArrayList<Coord>();
		Map<Coord, Double> g = new HashMap<Coord, Double>();
		g.put(start, 0.0);
		Map<Coord, Double> h = new HashMap<Coord, Double>();
		h.put(start, manhattanDist(start, target));
		Map<Coord, Double> f = new HashMap<Coord, Double>();
		f.put(start, manhattanDist(start, target));
		Map<Coord, Coord> cameFrom = new HashMap<Coord, Coord>();
		
		return aStar(maze, target, open, closed, g, h, f, cameFrom);
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
			double tenativeG = g.get(current) + 1.0;
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
		
		//TODO: don't allocate an array here
		Coord[] neighbours = {coords[col + 1][row], coords[col - 1][row],
							  coords[col][row + 1], coords[col][row - 1]};		
		
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
				lowestF = f.get(c);
			}
		}
		return lowestFCoord;
	}
}
