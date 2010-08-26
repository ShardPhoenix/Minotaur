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
	private List<Coord> goodNeighbours;
	private Coord[] neighbours;
	
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
		
		goodNeighbours = new ArrayList<Coord>();
		neighbours = new Coord[4];
	}
	
	public List<Coord> findRoute(Coord start, Coord target,  MazeCell[][] maze)
	{
		List<Coord> open = new ArrayList<Coord>();
		open.add(start);
		//List<Coord> closed = new ArrayList<Coord>();
		Map<Coord, Integer> g = new HashMap<Coord, Integer>();
		g.put(start, 0);
		Map<Coord, Integer> h = new HashMap<Coord, Integer>();
		h.put(start, manhattanDist(start, target));
		Map<Coord, Integer> f = new HashMap<Coord, Integer>();
		f.put(start, manhattanDist(start, target));
		Map<Coord, Coord> cameFrom = new HashMap<Coord, Coord>();
		
		Map<Coord, Boolean> openMap = new HashMap<Coord, Boolean>();
		Map<Coord, Boolean> closed = new HashMap<Coord, Boolean>();
		
		return aStar(maze, target, open, openMap, closed, g, h, f, cameFrom);
		
		
	}
	
	private List<Coord> aStar(MazeCell[][] maze, Coord target, List<Coord> open, Map<Coord, Boolean> openMap,
			Map<Coord, Boolean> closed,
			Map<Coord, Integer> g, Map<Coord, Integer> h, Map<Coord, Integer> f, Map<Coord, Coord> cameFrom)
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
			openMap.put(current, false);
			closed.put(current, true);
			int tenativeG = g.get(current) + 1;
			List<Coord> neighbours = getNeighbours(maze, current, closed, openMap, tenativeG, g);
			
			if (!neighbours.isEmpty())
			{
				open.addAll(neighbours);
				for (Coord c : neighbours)
				{
					openMap.put(c, true);
					cameFrom.put(c, current);
					int newH = manhattanDist(c, target);
					g.put(c, tenativeG);
					h.put(c, newH);
					f.put(c, tenativeG + newH);
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

	private Integer manhattanDist(Coord c, Coord target)
	{
		return Math.abs(c.col - target.col) + Math.abs(c.row - target.row);
	}

	private List<Coord> getNeighbours(MazeCell[][] maze, Coord current, Map<Coord, Boolean> closed, Map<Coord, Boolean> openMap,
			double tentativeG, Map<Coord, Integer> g)
	{
		goodNeighbours.clear();
		int col = current.col;
		int row = current.row;
		
		neighbours[0] = coords[col + 1][row];
		neighbours[1] = coords[col - 1][row];
		neighbours[2] = coords[col][row + 1];
		neighbours[3] = coords[col][row - 1];		
		
		for (Coord c : neighbours)
		{
			if (c.isInsideMaze() && closed.get(c) == null
					&& !maze[c.col][c.row].isWall)
			{
				if (openMap.get(c) == null || !openMap.get(c) || g.get(c) > tentativeG)
				{
					goodNeighbours.add(c);
				}
			}
		}
		return goodNeighbours;
	}

	private Coord getLowestF(Map<Coord, Integer> f, List<Coord> open)
	{
		double lowestF = Integer.MAX_VALUE;
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
