package com.minotaur;

import static com.minotaur.Constants.MAZE_CELL_WIDTH;
import static com.minotaur.Constants.MAZE_COLS;
import static com.minotaur.Constants.MAZE_ROWS;
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

	public Renderer(Context context)
	{
		Resources res = context.getResources();

		testImage = context.getResources().getDrawable(R.drawable.icon);
		backgroundImage = BitmapFactory.decodeResource(res, R.drawable.icon);

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
		
		backgroundPaint = new Paint();
		backgroundPaint.setAntiAlias(true);
		backgroundPaint.setARGB(255, 255, 255, 255);

		rect = new RectF(0, 0, 0, 0);
	}

	public void render(Canvas c, GameState game)
	{
		//c.drawBitmap(backgroundImage, 0, 0, null);
		
		rect.set(0, 0, 500, 500);
		c.drawRect(rect, backgroundPaint);

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
					c.drawRect(rect, mazePaint);
				}
				else if (game.maze[col][row].hasBomb)
				{
					c.drawRect(rect, bombPaint);
				}
				else if (game.maze[col][row].hasTreasure)
				{
					c.drawRect(rect, treasurePaint);
				}
			}
		}
		
		renderMoverSmoothly(c, game.player, playerPaint);
		renderMoverSmoothly(c, game.minotaur, minotaurPaint);
	}
	
	private void renderMoverSmoothly(Canvas c, Mover m, Paint p)
	{
		long timeSinceMoved = System.currentTimeMillis() - m.lastMoved;
		double ratio = timeSinceMoved/m.millisPerMove < 1.0 ? timeSinceMoved/m.millisPerMove : 1.0;
		double xDelta = (m.coord.col - m.lastCoord.col) * ratio;
		double yDelta = (m.coord.row - m.lastCoord.row) * ratio;
		
		int x = colToX(m.lastCoord.col + xDelta);
		int y = rowToY(m.lastCoord.row + yDelta);
		
		rect.set(x, y, x + MAZE_CELL_WIDTH, y + MAZE_CELL_WIDTH);
		c.drawRect(rect, p);
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
