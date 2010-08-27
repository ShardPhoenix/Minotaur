package com.minotaur;

import static com.minotaur.Constants.MAZE_COLS;
import static com.minotaur.Constants.MAZE_ROWS;

import java.util.ArrayList;
import java.util.List;

public class PathFinder
{
	//holds coords so they don't have to be instantiated inside the pathfinding loop
	private final List<Node> goodNeighbours;
	private final Node[] neighbours;
	
	private final Node[][] nodes;
	
	public PathFinder()
	{
		nodes = new Node[MAZE_COLS][MAZE_ROWS];
		
		for (int col = 0; col < MAZE_COLS; col++)
		{
			for (int row = 0; row < MAZE_ROWS; row++)
			{
				nodes[col][row] = new Node(new Coord(col, row), 0, 0, false, false, false, null);
			}
		}
		
		goodNeighbours = new ArrayList<Node>();
		neighbours = new Node[4];
	}
	
	private class Node
	{
		public Coord coord;
		public int f;
		public int g;
		public boolean isPassable;
		public boolean open;
		public boolean closed;
		public Node cameFrom;
		
		public Node(Coord coord, int f, int g, boolean isPassable, boolean open, boolean closed, Node cameFrom)
		{
			super();
			this.coord = coord;
			this.f = f;
			this.g = g;
			this.isPassable = isPassable;
			this.open = open;
			this.closed = closed;
			this.cameFrom = cameFrom;
		}
	}
	
	public List<Coord> findRoute(Coord start, Coord target,  MazeCell[][] maze)
	{
		List<Node> open = new ArrayList<Node>();
		open.add(nodes[start.col][start.row]);
		
		//reset nodes
		for (int col = 0; col < MAZE_COLS; col++)
		{
			for (int row = 0; row < MAZE_ROWS; row++)
			{
				Node n = nodes[col][row];
				n.f = 0;
				n.g = 0;
				n.isPassable = !maze[col][row].isWall;
				n.open = false;
				n.closed = false;
				n.cameFrom = null;
			}
		}
		
		return aStar(target, open);
	}
	
	private List<Coord> aStar(Coord target, List<Node> open)
	{
		while (true)
		{
			if (open.isEmpty())
			{
				return null;
			}
			Node current = getLowestF(open);
			if (current.coord.equals(target))
			{
				return reconstructPath(current, new ArrayList<Node>());
			}
			open.remove(current);
			current.closed = true;
			current.open = false;
			int tenativeG = current.g + 1;
			List<Node> neighbours = getNeighbours(current, tenativeG);
			
			if (!neighbours.isEmpty())
			{
				for (Node n : neighbours)
				{
					open.add(n);
					n.open = true;
					n.cameFrom = current;
					int newH = manhattanDist(n.coord, target);
					n.g = tenativeG;
					n.f = tenativeG + newH;
				}
			}
		}
	}

	private List<Coord> reconstructPath(Node current, List<Node> path)
	{
		while (current.cameFrom != null)
		{
			path.add(current);
			current = current.cameFrom;
		}
		path.add(current);
		List<Coord> route = new ArrayList<Coord>();
		for (int i = path.size() - 1; i >= 0; i--) //build the list of coords in reverse
		{
			route.add(path.get(i).coord);
		}
		return route;
	}

	private Integer manhattanDist(Coord c, Coord target)
	{
		return Math.abs(c.col - target.col) + Math.abs(c.row - target.row);
	}

	private List<Node> getNeighbours(Node current, double tentativeG)
	{
		goodNeighbours.clear();
		int col = current.coord.col;
		int row = current.coord.row;
		
		neighbours[0] = nodes[col + 1][row];
		neighbours[1] = nodes[col - 1][row];
		neighbours[2] = nodes[col][row + 1];
		neighbours[3] = nodes[col][row - 1];		
		
		for (int i = 0; i < neighbours.length; i++)
		{
			Node n = neighbours[i];
			if (!n.closed && n.isPassable && n.coord.isInsideMaze())
			{
				if (!n.open || n.g > tentativeG)
				{
					goodNeighbours.add(n);
				}
			}
		}
		return goodNeighbours;
	}

	private Node getLowestF(List<Node> open)
	{
		double lowestF = Integer.MAX_VALUE;
		Node lowestFNode = null;
		for (Node n : open)
		{
			if (n.f < lowestF)
			{
				lowestFNode = n;
				lowestF = n.f;
			}
		}
		return lowestFNode;
	}
}
