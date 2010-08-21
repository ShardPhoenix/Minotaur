package com.minotaur;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;

public class MinotaurView extends SurfaceView implements SurfaceHolder.Callback
{
	class MinotaurThread extends Thread
	{
		/** Indicate whether the surface has been created & is ready to draw */
		private boolean run = false;
		/** The state of the game. One of READY, RUNNING, PAUSE, LOSE, or WIN */
		private GameMode mode;

		/** Message handler used by thread to interact with TextView */
		private Handler handler;

		/** Handle to the surface manager object we interact with */
		private SurfaceHolder surfaceHolder;

		private int canvasWidth = 1;
		private int canvasHeight = 1;

		private GameModel gameModel;
		private Renderer renderer;
		private Set<Integer> keysHeld;

		public MinotaurThread(SurfaceHolder theSurfaceHolder, Context theContext, Handler theHandler)
		{
			// get handles to some important objects
			surfaceHolder = theSurfaceHolder;
			handler = theHandler;
			context = theContext;

			gameModel = new GameModel();
			renderer = new Renderer(theContext);
			keysHeld = new HashSet<Integer>();
			
			doStart();
		}

		/**
		 * Starts the game, setting parameters for the current difficulty.
		 */
		public void doStart()
		{
			synchronized (surfaceHolder)
			{
				setState(GameMode.STATE_RUNNING);
			}
		}

		@Override
		public void run()
		{
			while (run)
			{
				Canvas c = null;
				try
				{
					c = surfaceHolder.lockCanvas(null);
					synchronized (surfaceHolder)
					{
						if (mode == GameMode.STATE_RUNNING)
						{
							setState(gameModel.update(keysHeld));
						}

						renderer.render(c, mode, gameModel);
						
						if (mode == GameMode.STATE_LOSE && keysHeld.contains(KeyEvent.KEYCODE_SPACE))
						{
							gameModel = new GameModel();
							mode = GameMode.STATE_RUNNING;
						}
						if (mode == GameMode.STATE_WIN && keysHeld.contains(KeyEvent.KEYCODE_SPACE))
						{
							gameModel.nextLevel();
							mode = GameMode.STATE_RUNNING;
						}
					}
				}
				finally
				{
					// do this in a finally so that if an exception is thrown
					// during the above, we don't leave the Surface in an
					// inconsistent state
					if (c != null)
					{
						surfaceHolder.unlockCanvasAndPost(c);
					}
				}
			}
		}

		/**
		 * Used to signal the thread whether it should be running or not.
		 * Passing true allows the thread to run; passing false will shut it
		 * down if it's already running. Calling start() after this was most
		 * recently called with false will result in an immediate shutdown.
		 * 
		 * @param b
		 *            true to run, false to shut down
		 */
		public void setRunning(boolean b)
		{
			run = b;
		}

		public boolean doKeyDown(int keyCode, KeyEvent msg)
		{
			synchronized (surfaceHolder) 
			{
				keysHeld.add(keyCode);
				return true;
			}
		}

		public boolean doKeyUp(int keyCode, KeyEvent msg)
		{
			synchronized (surfaceHolder) 
			{
				keysHeld.remove(keyCode);
				return true;
			}
		}

		/**
		 * Pauses the physics update & animation.
		 */
		public void pause()
		{
			synchronized (surfaceHolder)
			{
				//if (mode == GameMode.STATE_RUNNING)
				{
					setState(GameMode.STATE_PAUSE);
				}
			}
		}

		/**
		 * Sets the game mode. That is, whether we are running, paused, in the
		 * failure state, in the victory state, etc.
		 * 
		 * @see #setState(int, CharSequence)
		 * @param mode
		 *            one of the STATE_* constants
		 */
		public void setState(GameMode mode)
		{
			synchronized (surfaceHolder)
			{
				this.mode = mode;
			}
		}

		/* Callback invoked when the surface dimensions change. */
		public void setSurfaceSize(int width, int height)
		{
			// synchronized to make sure these all change atomically
			synchronized (surfaceHolder)
			{
				canvasWidth = width;
				canvasHeight = height;

				// don't forget to resize the background image
				// TODO: need to pass the size in instead or something
				// backgroundImage = Bitmap.createScaledBitmap(backgroundImage,
				// width, height, true);
			}
		}

	}

	/** Handle to the application context, used to e.g. fetch Drawables. */
	private Context context;

	/** Pointer to the text view to display "Paused.." etc. */
	private TextView statusText;

	/** The thread that actually draws the animation */
	private MinotaurThread thread;

	public MinotaurView(Context context, AttributeSet attrs)
	{
		super(context, attrs);

		// register our interest in hearing about changes to our surface
		SurfaceHolder holder = getHolder();
		holder.addCallback(this);

		// create thread only; it's started in surfaceCreated()
		thread = new MinotaurThread(holder, context, new Handler() {
			@Override
			public void handleMessage(Message m)
			{
				statusText.setVisibility(m.getData().getInt("viz"));
				statusText.setText(m.getData().getString("text"));
			}
		});

		setFocusable(true); // make sure we get key events
	}

	public MinotaurThread getThread()
	{
		return thread;
	}

	/**
	 * Standard override to get key-press events.
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent msg)
	{
		return thread.doKeyDown(keyCode, msg);
	}

	/**
	 * Standard override for key-up. We actually care about these, so we can
	 * turn off the engine or stop rotating.
	 */
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent msg)
	{
		return thread.doKeyUp(keyCode, msg);
	}

	/**
	 * Standard window-focus override. Notice focus lost so we can pause on
	 * focus lost. e.g. user switches to take a call.
	 */
	@Override
	public void onWindowFocusChanged(boolean hasWindowFocus)
	{
		if (!hasWindowFocus)
			thread.pause();
	}

	/**
	 * Installs a pointer to the text view used for messages.
	 */
	public void setTextView(TextView textView)
	{
		statusText = textView;
	}

	/* Callback invoked when the surface dimensions change. */
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
	{
		thread.setSurfaceSize(width, height);
	}

	/*
	 * Callback invoked when the Surface has been created and is ready to be
	 * used.
	 */
	public void surfaceCreated(SurfaceHolder holder)
	{
		// start the thread here so that we don't busy-wait in run()
		// waiting for the surface to be created
		thread.setRunning(true);
		thread.start();
	}

	/*
	 * Callback invoked when the Surface has been destroyed and must no longer
	 * be touched. WARNING: after this method returns, the Surface/Canvas must
	 * never be touched again!
	 */
	public void surfaceDestroyed(SurfaceHolder holder)
	{
		// we have to tell thread to shut down & wait for it to finish, or else
		// it might touch the Surface after we return and explode
		boolean retry = true;
		thread.setRunning(false);
		while (retry)
		{
			try
			{
				thread.join();
				retry = false;
			}
			catch (InterruptedException e)
			{
			}
		}
	}

}
