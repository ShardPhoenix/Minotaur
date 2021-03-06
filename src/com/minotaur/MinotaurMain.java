package com.minotaur;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.minotaur.MinotaurView.MinotaurThread;

public class MinotaurMain extends Activity
{
	/** A handle to the thread that's actually running the animation. */
	private MinotaurThread minotaurThread;

	/** A handle to the View in which the game is running. */
	private MinotaurView minotaurView;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		minotaurView = (MinotaurView) findViewById(R.id.minotaur);
		minotaurThread = minotaurView.getThread();

		minotaurView.setTextView((TextView) findViewById(R.id.text));

		if (savedInstanceState == null)
		{
			minotaurThread.setState(GameMode.STATE_RUNNING);
			Log.w(this.getClass().getName(), "SIS is null");
		}
		else
		{
			throw new RuntimeException("Doesn't handling saving yet");
		}
	}

	/**
	 * Invoked when the Activity loses user focus.
	 */
	@Override
	protected void onPause()
	{
		super.onPause();
		minotaurView.getThread().pause(); // pause game when Activity pauses
	}
}