/* File        : Main.java
 * Created     : March 4, 2013 [3/4/2013]
 * By          : Douglas Chidester
 * Description : Runs the GUI for the autoclicker.
 * Last Update : [12/24/15]
 * 
 * 	See AutoClicker.java for documentation.
 */

package com.localarea.network.doug;

public class Driver
{
	public static void main(String args[])
	{
		javax.swing.SwingUtilities.invokeLater(new Runnable()
		{
            public void run()
            {
            	new MainFrame();
            }
        });
	}
}
