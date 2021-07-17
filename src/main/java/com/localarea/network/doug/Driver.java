/* File        : Main.java
 * Created     : March 4, 2013 [3/4/2013]
 * By          : Douglas Chidester
 * Description : Runs the GUI for the autoclicker.
 * Last Update : [12/24/15]
 *
 * See AutoClicker.java for documentation.
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

public class Driver
{
    public static void main(String args[])
    {
        boolean status = false; // command line arg execution status

        // if no command line args, assume application is running as a jarfile
        if (args.length == 0)
        {
            javax.swing.SwingUtilities.invokeLater(new Runnable()
            {
                public void run()
                {
                    new MainFrame();
                }
            });
        }
        else
            status = processCommand(args);
    }

    public static boolean processCommand(String args[])
    {
        boolean valid = true;

        if(args.length == 1)
        {
            switch(args[0])
            {
                // help args
                case "-h":
                case "--help":
                    System.out.println("Build the project and run `java -jar AutoClicker.jar` for the full featured GUI version.");
                    break;
                // handle version args
                case "-v":
                case "-V":
                case "--version":
                    System.out.println(AutoClicker.programName + " version:" + AutoClicker.version);
                    break;
                default:
                    System.out.println("Invalid option.");
                    valid = false;
                    break;
            }
        }
        else
            valid = false;

        return valid;
    }
}

