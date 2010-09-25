package com.minotaur;

import static com.minotaur.Constants.MAZE_COLS;
import static com.minotaur.Constants.MAZE_ROWS;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

public class PathFinder
{
	private final Node[] neighbours;
	private final Node[][] nodes;
	
	public PathFinder()
	{
		nodes = new Node[MAZE_COLS][MAZE_ROWS];
		
		for (int col = 0; col < MAZE_COLS; col++)
		{
			for (int row = 0; row < MAZE_ROWS; row++)
			{
				nodes[col][row] = new Node(Coord.getCoord(col, row), 0, 0, false, false, false, false, null);
			}
		}
		
		neighbours = new Node[4];
	}
	
	private class Node
	{
		public Coord coord;
		public int f;
		public int g;
		public boolean isPassable;
		public boolean isOpen;
		public boolean isClosed;
		public boolean isGoodNeighbour;
		public Node cameFrom;
		
		public Node(Coord coord, int f, int g, boolean isPassable, boolean open, 
				boolean closed, boolean goodNeighbour, Node cameFrom)
		{
			this.coord = coord;
			this.f = f;
			this.g = g;
			this.isPassable = isPassable;
			this.isOpen = open;
			this.isClosed = closed;
			this.isGoodNeighbour = goodNeighbour;
			this.cameFrom = cameFrom;
		}
	}
	
	public List<Coord> findRoute(Coord start, Coord target,  MazeCell[][] maze)
	{
		Comparator<Node> fComparator = new Comparator<Node>() {
			public int compare(Node a, Node b)
			{
				return a.f - b.f;
			}
		};
		
		PriorityQueue<Node> open = new PriorityQueue<Node>(11, fComparator);
		open.add(nodes[start.col][start.row]);
		
		//reset nodes
		for (int col = 0; col < MAZE_COLS; col++)
		{
			for (int row = 0; row < MAZE_ROWS; row++)
			{
				Node n = nodes[col][row];
				n.f = manhattanDist(n.coord, target);
				n.g = 0;
				n.isPassable = !maze[col][row].isWall;
				n.isOpen = false;
				n.isClosed = false;
				n.cameFrom = null;
			}
		}
		nodes[start.col][start.row].isOpen = true;
		
		
		return aStar(target, open);
	}
	
	private List<Coord> aStar(Coord target, PriorityQueue<Node> open)
	{
		while (!open.isEmpty())
		{	
			Node current = open.poll(); //node with lowest f
			if (current.coord == target)
			{
				return reconstructPath(current, new ArrayList<Node>());
			}
			current.isClosed = true;
			current.isOpen = false;
			int tentativeG = current.g + 1;
			checkNeighbours(current, tentativeG);
			
			for (int i = 0; i < neighbours.length; i++)
			{
				Node n = neighbours[i];
				if (n.isGoodNeighbour)
				{
					n.cameFrom = current;
					int newH = manhattanDist(n.coord, target);
					n.g = tentativeG;
					n.f = tentativeG + newH;
					
					if (!n.isOpen)
					{
						open.offer(n);
						n.isOpen = true;
					}
				}
			}
		}
		return null; //no path found
	}

	private List<Coord> reconstructPath(Node current, List<Node> path)
	{
		LinkedList<Coord> route = new LinkedList<Coord>();
		while (current.cameFrom != null)
		{
			route.addFirst(current.coord);
			current = current.cameFrom;
		}
		return route;
	}

	private int manhattanDist(Coord c, Coord target)
	{
		return Math.abs(c.col - target.col) + Math.abs(c.row - target.row);
	}

	private void checkNeighbours(Node current, int tentativeG)
	{
		int col = current.coord.col;
		int row = current.coord.row;
		
		neighbours[0] = nodes[col + 1][row];
		neighbours[1] = nodes[col - 1][row];
		neighbours[2] = nodes[col][row + 1];
		neighbours[3] = nodes[col][row - 1];		
		
		for (int i = 0; i < neighbours.length; i++)
		{
			Node n = neighbours[i];
			n.isGoodNeighbour = n.isPassable && !n.isClosed && (!n.isOpen || n.g > tentativeG);
		}
	}
}
