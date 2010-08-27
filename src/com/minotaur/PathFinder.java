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
	private final List<Coord> goodNeighbours;
	private final Coord[] neighbours;
	
	private final Node[][] nodes;
	
	public PathFinder()
	{
		coords = new Coord[MAZE_COLS][MAZE_ROWS];
		nodes = new Node[MAZE_COLS][MAZE_ROWS];
		
		for (int col = 0; col < MAZE_COLS; col++)
		{
			for (int row = 0; row < MAZE_ROWS; row++)
			{
				coords[col][row] = new Coord(col, row);
				nodes[col][row] = new Node(coords[col][row], 0, 0, 0, false, false, false);
			}
		}
		
		goodNeighbours = new ArrayList<Coord>();
		neighbours = new Coord[4];
		
		
	}
	
	private class Node
	{
		public Coord coord;
		public int f;
		public int g;
		public int h;
		public boolean isPassable;
		public boolean open;
		public boolean closed;
		
		public Node(Coord coord, int f, int g, int h, boolean isPassable, boolean open, boolean closed)
		{
			super();
			this.coord = coord;
			this.f = f;
			this.g = g;
			this.h = h;
			this.isPassable = isPassable;
			this.open = open;
			this.closed = closed;
		}
	}
	
	public List<Coord> findRoute(Coord start, Coord target,  MazeCell[][] maze)
	{
		List<Coord> open = new ArrayList<Coord>();
		open.add(start);
		Map<Coord, Coord> cameFrom = new HashMap<Coord, Coord>();
		
		//reset nodes
		for (int col = 0; col < MAZE_COLS; col++)
		{
			for (int row = 0; row < MAZE_ROWS; row++)
			{
				Node n = nodes[col][row];
				n.f = 0;
				n.g = 0;
				n.h = 0;
				n.isPassable = !maze[col][row].isWall;
				n.open = false;
				n.closed = false;
			}
		}
		
		return aStar(maze, target, open, cameFrom);
	}
	
	private List<Coord> aStar(MazeCell[][] maze, Coord target, List<Coord> open, Map<Coord, Coord> cameFrom)
	{
		while (true)
		{
			if (open.isEmpty())
			{
				return null;
			}
			Coord current = getLowestF(open);
			if (current.equals(target))
			{
				return reconstructPath(cameFrom, current, new ArrayList<Coord>());
			}
			open.remove(current);
			Node currentNode = nodes[current.col][current.row];
			currentNode.closed = true;
			currentNode.open = false;
			
			int tenativeG = currentNode.g + 1;
			List<Coord> neighbours = getNeighbours(maze, current, tenativeG);
			
			if (!neighbours.isEmpty())
			{
				open.addAll(neighbours);
				for (Coord c : neighbours)
				{
					Node n = nodes[c.col][c.row];
					n.open = true;
					cameFrom.put(c, current);
					int newH = manhattanDist(c, target);
					n.g = tenativeG;
					n.h = newH;
					n.f = tenativeG + newH;
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

	private List<Coord> getNeighbours(MazeCell[][] maze, Coord current, double tentativeG)
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
			Node n = nodes[c.col][c.row];
			if (c.isInsideMaze() && !n.closed && n.isPassable)
			{
				if (!n.open || n.g > tentativeG)
				{
					goodNeighbours.add(c);
				}
			}
		}
		return goodNeighbours;
	}

	private Coord getLowestF(List<Coord> open)
	{
		double lowestF = Integer.MAX_VALUE;
		Coord lowestFCoord = null;
		for (Coord c : open)
		{
			if (nodes[c.col][c.row].f < lowestF)
			{
				lowestFCoord = nodes[c.col][c.row].coord;
				lowestF = nodes[c.col][c.row].f;
			}
		}
		return lowestFCoord;
	}
}
