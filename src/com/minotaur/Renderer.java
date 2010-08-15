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
	private Paint testPaint;
	private Paint playerPaint;
	private Paint backgroundPaint;
	private RectF testRect;
	private Drawable testImage;

	public Renderer(Context context)
	{
		Resources res = context.getResources();

		testImage = context.getResources().getDrawable(R.drawable.icon);
		backgroundImage = BitmapFactory.decodeResource(res, R.drawable.icon);

		testPaint = new Paint();
		testPaint.setAntiAlias(true);
		testPaint.setARGB(255, 255, 0, 0);
		
		playerPaint = new Paint();
		playerPaint.setAntiAlias(true);
		playerPaint.setARGB(255, 0, 255, 0);
		
		backgroundPaint = new Paint();
		backgroundPaint.setAntiAlias(true);
		backgroundPaint.setARGB(255, 255, 255, 255);

		testRect = new RectF(0, 0, 0, 0);
	}
	
	private int colToX(int col)
	{
		return Constants.MAZE_CELL_WIDTH * col + Constants.MAZE_LEFT_MARGIN;
	}
	
	private int rowToY(int row)
	{
		return Constants.MAZE_CELL_WIDTH * row + Constants.MAZE_TOP_MARGIN;
	}

	public void render(Canvas c, GameState game)
	{
		//c.drawBitmap(backgroundImage, 0, 0, null);
		
		testRect.set(0, 0, 500, 500);
		c.drawRect(testRect, backgroundPaint);

		//testImage.setBounds(30, 30, 30, 30);
		//testImage.draw(c);

		for (int col = 0; col < MAZE_COLS; col++)
		{
			for (int row = 0; row < MAZE_ROWS; row++)
			{
				if (game.maze[col][row].isWall)
				{
					int x = game.maze[col][row].x;
					int y = game.maze[col][row].y;
					testRect.set(x, y, x + MAZE_CELL_WIDTH, y + MAZE_CELL_WIDTH);
					c.drawRect(testRect, testPaint);
				}
			}
		}
		
		int x = colToX(game.player.coord.col);
		int y = rowToY(game.player.coord.row);
		testRect.set(x, y, x + MAZE_CELL_WIDTH, y + MAZE_CELL_WIDTH);
		c.drawRect(testRect, playerPaint);
	}

}
