package com.minotaur;

import java.util.Set;

import android.view.KeyEvent;

public class Input
{
	public int deltaCol;
	public int deltaRow;
	public boolean bombPressed;
	
	public Input(Set<Integer> keysHeld)
	{
		if (keysHeld.contains(KeyEvent.KEYCODE_SPACE))
		{
			bombPressed = true;
		}
		int left = keysHeld.contains(KeyEvent.KEYCODE_DPAD_LEFT) ? -1 : 0;
		int right = keysHeld.contains(KeyEvent.KEYCODE_DPAD_RIGHT) ? 1 : 0;
		int up = keysHeld.contains(KeyEvent.KEYCODE_DPAD_UP) ? -1 : 0;
		int down = keysHeld.contains(KeyEvent.KEYCODE_DPAD_DOWN) ? 1 : 0;
		deltaCol = left + right;
		deltaRow = up + down;
	}
}
