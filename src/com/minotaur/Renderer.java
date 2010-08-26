package com.minotaur;

import static com.minotaur.Constants.MAZE_CELL_WIDTH;
import static com.minotaur.Constants.MAZE_COLS;
import static com.minotaur.Constants.MAZE_ROWS;

import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

public class Renderer
{
	private Paint greenPaint;
	private Paint blackPaint;
	private Paint bluePaint;
	private Paint backgroundPaint;
	private RectF rect;
	
	private Bitmap bombImage;
	private Bitmap mazeImage;
	private Bitmap treasureImage;
	private Bitmap playerImage;
	private Bitmap minotaurImage;
	private Bitmap dirtImage;

	public Renderer(Context context)
	{
		Resources res = context.getResources();

		bombImage = BitmapFactory.decodeResource(res, R.drawable.bomb);
		mazeImage = BitmapFactory.decodeResource(res, R.drawable.wall);
		treasureImage = BitmapFactory.decodeResource(res, R.drawable.gold);
		playerImage = BitmapFactory.decodeResource(res, R.drawable.hero);
		minotaurImage = BitmapFactory.decodeResource(res, R.drawable.mino);
		dirtImage = BitmapFactory.decodeResource(res, R.drawable.dirt);
		
		blackPaint = new Paint();
		blackPaint.setAntiAlias(true);
		blackPaint.setARGB(255, 0, 0, 0);
		
		greenPaint = new Paint();
		greenPaint.setAntiAlias(true);
		greenPaint.setARGB(255, 0, 255, 0);
		
		bluePaint = new Paint();
		bluePaint.setAntiAlias(true);
		bluePaint.setARGB(255, 0, 0, 255);
		
		backgroundPaint = new Paint();
		backgroundPaint.setAntiAlias(true);
		backgroundPaint.setARGB(255, 255, 255, 255);

		rect = new RectF(0, 0, 0, 0);
	}

	public void render(Canvas c, GameMode mode, GameModel game)
	{
		rect.set(0, 0, 500, 500);
		c.drawRect(rect, backgroundPaint);
		
		switch (mode)
		{
			case STATE_RUNNING:
				renderGameplay(c, game);
				break;
			case STATE_WIN:
				renderVictory(c, game);
				break;
			case STATE_LOSE:
				renderLoss(c, game);
				break;
		}
	}

	private void renderLoss(Canvas c, GameModel game)
	{
		c.drawText("You died! (space to restart)", colToX(MAZE_COLS/2), rowToY(MAZE_ROWS/2), blackPaint);
	}

	private void renderVictory(Canvas c, GameModel game)
	{
		c.drawText("You escaped! (space to continue)", colToX(MAZE_COLS/2), rowToY(MAZE_ROWS/2), blackPaint);
	}

	private void renderGameplay(Canvas c, GameModel game)
	{
		for (int col = 0; col < MAZE_COLS; col++)
		{
			for (int row = 0; row < MAZE_ROWS; row++)
			{
				int x = game.maze[col][row].x;
				int y = game.maze[col][row].y;
				rect.set(x, y, x + MAZE_CELL_WIDTH, y + MAZE_CELL_WIDTH);
				
				if (game.maze[col][row].isWall)
				{
					drawSquareImage(c, x, y, MAZE_CELL_WIDTH, mazeImage);
				}
				else
				{
					drawSquareImage(c, x, y, MAZE_CELL_WIDTH, dirtImage);
				}
				
				if (game.maze[col][row].hasTreasure)
				{
					drawSquareImage(c, x, y, MAZE_CELL_WIDTH, treasureImage);
				}
				if (game.maze[col][row].hasBomb)
				{
					drawSquareImage(c, x, y, MAZE_CELL_WIDTH, bombImage);
				}
				if (game.maze[col][row].isExit)
				{
					c.drawRect(rect, bluePaint);
				}
			}
		}
		
		renderMoverSmoothly(c, game.player, playerImage);
		renderMoverSmoothly(c, game.minotaur, minotaurImage);
		
		drawIndicators(c, game);
		
		//debugRenderPath(c, game.maze, game.minotaur.route);
	}

	private void debugRenderPath(Canvas c, MazeCell[][] maze, List<Coord> route)
	{
		for (int i = 1; i < route.size() - 1; i++)
		{
			int x = colToX(route.get(i).col);
			int y = rowToY(route.get(i).row);
			rect.set(x, y, x + MAZE_CELL_WIDTH, y + MAZE_CELL_WIDTH);
			c.drawRect(rect, greenPaint);
		}
		
	}

	private void drawIndicators(Canvas c, GameModel game)
	{
		for (int i = 0; i < game.player.bombsLeft; i++)
		{
			int x = colToX(Constants.MAZE_COLS);
			int y = rowToY(Constants.MAZE_ROWS - 1 - i);
			drawSquareImage(c, x, y, MAZE_CELL_WIDTH, bombImage);
		}
		
		for (int i = 0; i < game.treasuresGained; i++)
		{
			int x = colToX(Constants.MAZE_COLS);
			int y = rowToY(i);
			drawSquareImage(c, x, y, MAZE_CELL_WIDTH, treasureImage);
		}
	}
	
	private void renderMoverSmoothly(Canvas c, Mover m, Bitmap image)
	{
		long timeSinceMoved = System.currentTimeMillis() - m.lastMoved;
		double ratio = timeSinceMoved/m.millisPerMove < 1.0 ? timeSinceMoved/m.millisPerMove : 1.0;
		double xDelta = (m.coord.col - m.lastCoord.col) * ratio;
		double yDelta = (m.coord.row - m.lastCoord.row) * ratio;
		
		int x = colToX(m.lastCoord.col + xDelta);
		int y = rowToY(m.lastCoord.row + yDelta);
		
		drawSquareImage(c, x, y, MAZE_CELL_WIDTH, image);
	}
	
	private void drawSquareImage(Canvas c, int x, int y, int size, Bitmap image)
	{
		c.drawBitmap(image, x, y, null);
		//image.setBounds(x, y, x + size, y + size);
		//image.draw(c);
	}
	
	private int colToX(int col)
	{
		return Constants.MAZE_CELL_WIDTH * col + Constants.MAZE_LEFT_MARGIN;
	}
	
	private int rowToY(int row)
	{
		return Constants.MAZE_CELL_WIDTH * row + Constants.MAZE_TOP_MARGIN;
	}
	
	private int colToX(double col)
	{
		return (int) Math.round(Constants.MAZE_CELL_WIDTH * col + Constants.MAZE_LEFT_MARGIN);
	}
	
	private int rowToY(double row)
	{
		return (int) Math.round(Constants.MAZE_CELL_WIDTH * row + Constants.MAZE_TOP_MARGIN);
	}
}
