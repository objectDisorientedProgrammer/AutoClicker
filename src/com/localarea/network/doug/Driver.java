/* File        : Main.java
 * Created     : March 4, 2013 [3/4/2013]
 * By          : Douglas Chidester
 * Description : Run an autoclicker.
 * Last Update : [5/29/13]
 * 
 * 	See AutoClicker.java for documentation.
 */

package com.localarea.network.doug;

public class Driver
{
	public static void main(String args[])
	{
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            	new AutoClicker();
            }
        });
	}
}
