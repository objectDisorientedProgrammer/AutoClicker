/* File        : AutoClicker.java
 * Created     : September 14, 2011 [9/14/2011]
 * By          : Douglas Chidester
 * Description : Click many times in one spot.
 * Version       : v0.99.5b [7/29/2014]
 * Last Update : v0.99.7b [12/24/2015]
 *
 * Updates:
 * [3/4/13] - added actionlisteners to start/stop button, split program into 2 classes, made the program click at (x,y),
 *            display mouse location in a label, align GUI.
 * [5/29/13] - implemented hotkeys for start (F6) and stop (F7).
 * [10/11/13] - get and save mouse location with hotkey F8
 * To Do:
 *   - see github issues: https://github.com/objectDisorientedProgrammer/AutoClicker/issues
 * 
 * 
 * 
 * MIT License
 * 
 * Copyright (c) 2011 Douglas Chidester
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 */

package com.localarea.network.doug;

import java.awt.Robot;
import java.awt.event.InputEvent;

public class AutoClicker
{
    public static final String programName = "AutoClicker";
    public static final String version = "1.1.0";
    public static final String author = "Douglas Chidester";

    private boolean running = false; // control the autoclick() thread
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
                    // move the mouse
                    robot.mouseMove(xcoord, ycoord);
                    
                    while(running)
                    {
                        // perform click operation
                        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);

                        // pause robot for N milliseconds
                        robot.delay(clickDelay);
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
    	boolean status = false;
    	
        if(!running && xcoord >= 0 && ycoord >= 0) // coordinates must not be negative
        {
            running = true;
            autoclick(xcoord, ycoord, clickDelay);
            status = true;
        }
        
        return status;
    }

    /**
     * Stop clicking the mouse.
     */
    public void stopClicking()
    {
        running = false;
    }
    
    /**
     * Query the autoclicker to determine if it is clicking.
     * @return true if clicking, otherwise false.
     */
    public boolean isClicking()
    {
    	return running;
    }
}
