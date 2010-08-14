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
    /** The drawable to use as the background of the animation canvas */
    private Bitmap backgroundImage;
	private Paint testPaint;
	private RectF testRect;	
	private Drawable testImage;
	
	public Renderer(Context context)
	{
		Resources res = context.getResources();
		// cache handles to our key sprites & other drawables
		testImage = context.getResources().getDrawable(R.drawable.icon);
		backgroundImage = BitmapFactory.decodeResource(res, R.drawable.icon);

		testPaint = new Paint();
		testPaint.setAntiAlias(true);
		testPaint.setARGB(255, 255, 0, 0);

		testRect = new RectF(0, 0, 0, 0);
	}
	
	public void render(Canvas c, GameState game)
	{
        // Draw the background image. Operations on the Canvas accumulate
        // so this is like clearing the screen.
		
        c.drawBitmap(backgroundImage, 0, 0, null);
        
        testImage.setBounds(30, 30, 30, 30);
        testImage.draw(c);
		
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
	}
	
	
}
