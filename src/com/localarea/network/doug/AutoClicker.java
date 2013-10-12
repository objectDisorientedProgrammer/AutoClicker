/* File        : AutoClicker.java
 * Created     : September 14, 2011 [9/14/2011]
 * By          : Douglas Chidester
 * Description : Click many times in one spot
 * Version	   : v0.10 [9/14/2011]
 * Last Update : v0.97 [10/11/13]
 * 
 * Updates:
 * [3/4/13] - added actionlisteners to start/stop button, split program into 2 classes, made the program click at (x,y),
 * 			  display mouse location in a label, align GUI
 * 
 * [5/29/13] - implemented hotkeys for start (F6) and stop (F7).
 * [10/11/13] - get and save mouse location with hotkey F8
 * To Do:
 *  
 */

package com.localarea.network.doug;

import java.awt.Color;
import java.awt.Font;
import java.awt.HeadlessException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.ComponentInputMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.plaf.ActionMapUIResource;

@SuppressWarnings("serial")
public class AutoClicker extends JFrame
{
	private int frameWidth = 350;
	private int frameHeight = 200;
	private String programName = "Auto Clicker";
	private String version = " v0.97";

	private JTextField xcoordTF, ycoordTF, clickSpeedTF;
	private JLabel xcoordLbl, ycoordLbl, clickSpeedLbl, clickCountLbl,
			clickStatusLbl;
	private JButton startBtn, stopBtn, clickTestBtn, resetClickCountBtn;
	private int xcoord = 0;
	private int ycoord = 0;
	private JLabel mouseCoords;
	private int mouseX = 0;
	private int mouseY = 0;
	private int getMouseCoordsHotKey = KeyEvent.VK_F8;
	private int mouseUpdateDelay = 50;	// in milliseconds
	private int clickDelay = 500;		// in milliseconds
	private int clickCount = 0;
	private boolean running = false;
	private boolean runThread = false;
	private String needXYmsg = "Please enter an x and y coordinate greater than 0.";
	private String xcoordTFString = "x:";
	private String ycoordTFString = "y:";
	private String stoppedString = "Stopped";
	private String runningString = "Running";
	private String startBtnString = "Start ";
	private String startBtnHotkeyString = "(F6)";
	private int startHotKey = KeyEvent.VK_F6;
	private String stopBtnString = "Stop ";
	private String stopBtnHotkeyString = "(F7)";
	private int stopHotKey = KeyEvent.VK_F7;
	private String clickSpeedString = "Click delay (milliseconds):";
	private Robot robot;
	private Thread clickThread;
	private String clickTestBtnString = "Test";
	private String resetClickString = "Reset";
	

	public AutoClicker()
	{
		super();
		setFrameAttributes();

		createAndShowGUI();
		// get mouse location and display on window
		updateMousePosition();
		// Enable hotkeys
		setupHotkeys();

		setVisible(true);
	}

	private void setFrameAttributes()
	{
		setTitle(programName + version);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(frameWidth, frameHeight);	// set frame size
		setLocationRelativeTo(null);		// display in the center of the screen
	}

	private void createAndShowGUI()
	{
		// create xcoord button
		xcoordTF = new JTextField(4);
		xcoordTF.setBounds(36, 39, 38, 20);
		xcoordTF.setText("" + xcoord);

		// create xcoord label
		xcoordLbl = new JLabel(xcoordTFString);
		xcoordLbl.setBounds(10, 42, 16, 15);

		// create ycoord button
		ycoordTF = new JTextField(4);
		ycoordTF.setBounds(110, 39, 38, 20);
		ycoordTF.setText("" + ycoord);

		// create ycoord label
		ycoordLbl = new JLabel(ycoordTFString);
		ycoordLbl.setBounds(84, 42, 16, 15);

		// create label to show status of app (running or not)
		clickStatusLbl = new JLabel(stoppedString);
		clickStatusLbl.setForeground(Color.red);
		clickStatusLbl.setFont(new Font("Tahoma", Font.BOLD, 15));
		clickStatusLbl.setBounds(28, 75, 89, 20);

		// create start button
		startBtn = new JButton(startBtnString + startBtnHotkeyString);
		startBtn.setBounds(10, 113, 107, 35);
		startBtn.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				startClicking();
			}
		});
		
		// create stop button
		stopBtn = new JButton(stopBtnString + stopBtnHotkeyString);
		stopBtn.setBounds(127, 113, 101, 35);
		stopBtn.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				stopClicking();
			}
		});
		getContentPane().setLayout(null);

		// Label for delay TF
		clickSpeedLbl = new JLabel(clickSpeedString);
		clickSpeedLbl.setBounds(10, 9, 185, 14);
		getContentPane().add(clickSpeedLbl);

		// Textfield for time delay
		clickSpeedTF = new JTextField(4);
		clickSpeedTF.setBounds(195, 6, 102, 20);
		clickSpeedTF.setText("" + clickDelay);
		// clickSpeedTF.addActionListener(clearTextFieldOnFocus);
		getContentPane().add(clickSpeedTF);

		// Click counter label
		clickCountLbl = new JLabel("" + clickCount);
		clickCountLbl.setHorizontalAlignment(SwingConstants.RIGHT);
		clickCountLbl.setBounds(147, 60, 50, 14);

		// Click test button
		clickTestBtn = new JButton(clickTestBtnString);
		clickTestBtn.setBounds(207, 38, 90, 23);
		clickTestBtn.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				clickCount++;
				clickCountLbl.setText("" + clickCount);
			}
		});

		// Reset button for click count
		resetClickCountBtn = new JButton(resetClickString);
		resetClickCountBtn.setBounds(207, 71, 90, 23);
		resetClickCountBtn.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				clickCount = 0;
				clickCountLbl.setText("" + clickCount);
			}
		});

		// Label to display current mouse location.
		mouseCoords = new JLabel("");
		mouseCoords.setBounds(250, 118, 82, 23);

		// add all components
		getContentPane().add(xcoordLbl);
		getContentPane().add(xcoordTF);
		getContentPane().add(ycoordLbl);
		getContentPane().add(ycoordTF);
		getContentPane().add(clickStatusLbl);
		getContentPane().add(startBtn);
		getContentPane().add(stopBtn);
		getContentPane().add(clickCountLbl);
		getContentPane().add(clickTestBtn);
		getContentPane().add(resetClickCountBtn);
		getContentPane().add(mouseCoords);
	}

	public void updateMousePosition()
	{
		Thread updateMouseCoords = new Thread(new Runnable()
		{
			PointerInfo mouseInfo;
			Point mouseXY;

			@Override
			public void run()
			{
				while(true)
				{
					try
					{
						mouseInfo = MouseInfo.getPointerInfo();
						mouseXY = mouseInfo.getLocation();
						Thread.sleep(mouseUpdateDelay);
						mouseCoords.setText((int) mouseXY.getX() + ", "
								+ (int) mouseXY.getY());
					} catch(InterruptedException e)
					{
						e.printStackTrace();
					}
				}
			}
		});
		updateMouseCoords.start();
	}

	private void autoclick()
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
						robot.delay(clickDelay); // in ms
						robot.mousePress(InputEvent.BUTTON1_MASK);
						robot.mouseRelease(InputEvent.BUTTON1_MASK);
					}
				} catch(Exception e2)
				{
					e2.printStackTrace();
				}
			}
		});
		clickThread.start();
	}

	private void setupHotkeys()
	{
		InputMap keyMap = new ComponentInputMap((JComponent) getContentPane());
		
		// Start clicking when hotkey is pressed
		keyMap.put(KeyStroke.getKeyStroke(startHotKey, 0), "action_start");
		ActionMap actionMap = new ActionMapUIResource();
	    actionMap.put("action_start", new AbstractAction() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	            startClicking();
	        }
	    });
	    
	    // Stop clicking when hotkey is pressed
	    keyMap.put(KeyStroke.getKeyStroke(stopHotKey , 0), "action_stop");
	    actionMap.put("action_stop", new AbstractAction() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	            stopClicking();
	        }
	    });
	    
	    // Get current mouse coords and put into textfields
	    keyMap.put(KeyStroke.getKeyStroke(getMouseCoordsHotKey , 0), "action_getMouseCoords");
	    actionMap.put("action_getMouseCoords", new AbstractAction() {
	        @Override
	        public void actionPerformed(ActionEvent e)
	        {
	            try
				{
					PointerInfo pi = MouseInfo.getPointerInfo();
					mouseX = pi.getLocation().x;
					mouseY = pi.getLocation().y;
					xcoordTF.setText(mouseX + "");
					ycoordTF.setText(mouseY + "");
				} catch(HeadlessException e1)
				{
					e1.printStackTrace();
				}
	        }
	    });
	    
	    SwingUtilities.replaceUIActionMap((JComponent) getContentPane(), actionMap);
	    SwingUtilities.replaceUIInputMap((JComponent) getContentPane(), JComponent.WHEN_IN_FOCUSED_WINDOW,
	            keyMap);
	}
	
	private void startClicking()
	{
		xcoord = Integer.parseInt(xcoordTF.getText());
		ycoord = Integer.parseInt(ycoordTF.getText());
		clickDelay = Integer.parseInt(clickSpeedTF.getText());

		// check for valid coordinates
		if(xcoord > 0 && ycoord > 0)
		{
			runThread = true;
			if(runThread)
			{
				running = true;
				// update status
				clickStatusLbl.setForeground(new Color(0, 200, 100));
				clickStatusLbl.setText(runningString);
				autoclick();
			}
		} else
			JOptionPane.showMessageDialog(null, needXYmsg, "Coordinate Error",
					JOptionPane.ERROR_MESSAGE);
	}

	private void stopClicking()
	{
		runThread = false;
		running = false;
		// dont keep changing the text if its already stopped
		if(!clickStatusLbl.getText().equals(stoppedString))
		{
			// update status
			clickStatusLbl.setForeground(Color.red);
			clickStatusLbl.setText(stoppedString);
		}
	}
}
