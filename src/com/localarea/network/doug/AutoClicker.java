/* File        : AutoClicker.java
 * Created     : September 14, 2011 [9/14/2011]
 * By          : Douglas Chidester
 * Description : Click many times in one spot.
 * Version	   : v0.99.5b [7/29/2014]
 * Last Update : v0.99.7b [12/24/2015]
 * 
 * Updates:
 * [3/4/13] - added actionlisteners to start/stop button, split program into 2 classes, made the program click at (x,y),
 * 			  display mouse location in a label, align GUI.
 * [5/29/13] - implemented hotkeys for start (F6) and stop (F7).
 * [10/11/13] - get and save mouse location with hotkey F8
 * To Do:
 *   - see github issues
 */

package com.localarea.network.doug;

import java.awt.Robot;
import java.awt.event.InputEvent;

import javax.swing.JOptionPane;

@SuppressWarnings("serial")
public class AutoClicker
{
	private boolean running = false;     // control the autoclick() thread
	private boolean validCoords = false; // x & y coords are greater than zero
	private String needXYmsg = "Please enter an x and y coordinate greater than 0.";
	private Robot robot;
	private Thread clickThread;
	
	public AutoClicker()
	{
		super();
	}

	/**
	 * Move the mouse to the user specified location and left click.
	 * @param xcoord - mouse X coordinate
	 * @param ycoord - mouse Y coordinate
	 * @param clickDelay - delay in milliseconds between clicks
	 */
	private void autoclick(final int xcoord, final int ycoord, final int clickDelay)
	{
		clickThread = new Thread(new Runnable()
		{
			public void run()
			{
				try
				{
					robot = new Robot();
					// move the mouse and click
					robot.mouseMove(xcoord, ycoord);
					while(running)
					{
						robot.mousePress(InputEvent.BUTTON1_MASK);
						robot.mouseRelease(InputEvent.BUTTON1_MASK);
						robot.delay(clickDelay); // in ms
					}
				} catch(Exception e2)
				{
					e2.printStackTrace();
				}
			}
		});
		clickThread.start();
	}

	/**
	 * Start clicking the mouse at the specified point with the specified interval.
	 * @param xcoord - mouse X coordinate
	 * @param ycoord - mouse Y coordinate
	 * @param clickDelay - delay in milliseconds between clicks
	 * @return True if clicks have begun, false if unable to start clicks.
	 */
	public boolean startClicking(int xcoord, int ycoord, int clickDelay)
	{
		if(xcoord > 0 && ycoord > 0) // check for valid coordinates
		{
			validCoords = true;
			if(validCoords)
			{
				running = true;
				autoclick(xcoord, ycoord, clickDelay);
				return true;
			}
		}
		else
			JOptionPane.showMessageDialog(null, needXYmsg, "Coordinate Error",
					JOptionPane.ERROR_MESSAGE);
		return false;
	}

	/**
	 * Stop clicking the mouse.
	 */
	public void stopClicking()
	{
		validCoords = false;
		running = false;
	}
}
