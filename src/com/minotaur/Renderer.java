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
import android.graphics.drawable.Drawable;

public class Renderer
{
	private Bitmap backgroundImage;
	private Paint mazePaint;
	private Paint playerPaint;
	private Paint backgroundPaint;
	private RectF rect;
	private Drawable testImage;
	private Paint bombPaint;
	private Paint treasurePaint;
	private Paint minotaurPaint;
	private Paint exitPaint;
	
	private Drawable bombImage;
	private Drawable mazeImage;
	private Drawable treasureImage;
	private Drawable playerImage;
	private Drawable minotaurImage;
	private Drawable dirtImage;

	public Renderer(Context context)
	{
		Resources res = context.getResources();

		testImage = context.getResources().getDrawable(R.drawable.icon);
		backgroundImage = BitmapFactory.decodeResource(res, R.drawable.icon);
		
		bombImage = context.getResources().getDrawable(R.drawable.bomb);
		mazeImage = context.getResources().getDrawable(R.drawable.wall);
		treasureImage = context.getResources().getDrawable(R.drawable.gold);
		playerImage = context.getResources().getDrawable(R.drawable.hero);
		minotaurImage = context.getResources().getDrawable(R.drawable.mino);
		dirtImage = context.getResources().getDrawable(R.drawable.dirt);

		mazePaint = new Paint();
		mazePaint.setAntiAlias(true);
		mazePaint.setARGB(255, 0, 0, 0);
		
		bombPaint = new Paint();
		bombPaint.setAntiAlias(true);
		bombPaint.setARGB(255, 255, 0, 0);
		
		treasurePaint = new Paint();
		treasurePaint.setAntiAlias(true);
		treasurePaint.setARGB(255, 255, 255, 0);
		
		playerPaint = new Paint();
		playerPaint.setAntiAlias(true);
		playerPaint.setARGB(255, 0, 255, 0);
		
		minotaurPaint = new Paint();
		minotaurPaint.setAntiAlias(true);
		minotaurPaint.setARGB(255, 102, 51, 0);
		
		exitPaint = new Paint();
		exitPaint.setAntiAlias(true);
		exitPaint.setARGB(255, 0, 0, 255);
		
		backgroundPaint = new Paint();
		backgroundPaint.setAntiAlias(true);
		backgroundPaint.setARGB(255, 255, 255, 255);

		rect = new RectF(0, 0, 0, 0);
	}

	public void render(Canvas c, GameMode mode, GameModel game)
	{
		//c.drawBitmap(backgroundImage, 0, 0, null);
		
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
		c.drawText("You died! (space to restart)", colToX(MAZE_COLS/2), rowToY(MAZE_ROWS/2), mazePaint);
	}

	private void renderVictory(Canvas c, GameModel game)
	{
		c.drawText("You escaped! (space to continue)", colToX(MAZE_COLS/2), rowToY(MAZE_ROWS/2), mazePaint);
	}

	private void renderGameplay(Canvas c, GameModel game)
	{
		//testImage.setBounds(30, 30, 30, 30);
		//testImage.draw(c);

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
					c.drawRect(rect, exitPaint);
				}
			}
		}
		
		renderMoverSmoothly(c, game.player, playerImage);
		renderMoverSmoothly(c, game.minotaur, minotaurImage);
		
		drawIndicators(c, game);
		
		debugRenderPath(c, game.maze, game.minotaur.route);
	}

	private void debugRenderPath(Canvas c, MazeCell[][] maze, List<Coord> route)
	{
		for (Coord coord : route)
		{
			int x = colToX(coord.col);
			int y = rowToY(coord.row);
			rect.set(x, y, x + MAZE_CELL_WIDTH, y + MAZE_CELL_WIDTH);
			c.drawRect(rect, playerPaint);
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
	
	private void renderMoverSmoothly(Canvas c, Mover m, Drawable image)
	{
		long timeSinceMoved = System.currentTimeMillis() - m.lastMoved;
		double ratio = timeSinceMoved/m.millisPerMove < 1.0 ? timeSinceMoved/m.millisPerMove : 1.0;
		double xDelta = (m.coord.col - m.lastCoord.col) * ratio;
		double yDelta = (m.coord.row - m.lastCoord.row) * ratio;
		
		int x = colToX(m.lastCoord.col + xDelta);
		int y = rowToY(m.lastCoord.row + yDelta);
		
		drawSquareImage(c, x, y, MAZE_CELL_WIDTH, image);
	}
	
	private void drawSquareImage(Canvas c, int x, int y, int size, Drawable image)
	{
		image.setBounds(x, y, x + size, y + size);
		image.draw(c);
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
