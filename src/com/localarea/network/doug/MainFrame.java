/* File        : MainFrame.java
 * Created     : Dec 27, 2014 [12/27/2014]
 * By          : Douglas Chidester
 * Description : Main GUI JFrame.
 * Last Update : 
 * 
 * 	See AutoClicker.java for documentation.
 */

package com.localarea.network.doug;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.ComponentInputMap;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.plaf.ActionMapUIResource;

public class MainFrame extends JFrame
{
	private AutoClicker clicker;
	
	private int frameWidth = 240;
	private int frameHeight = 270;
	private String programName = "Auto Clicker";
	private String version = " v0.99.8-b";
	private String author = "Douglas Chidester";

	private JTextField xcoordTF, ycoordTF, clickSpeedTF;
	private JLabel xcoordLbl, ycoordLbl, clickSpeedLbl, clickCountLbl, clickStatusLbl;
	private JButton startBtn, stopBtn, clickTestBtn, resetClickCountBtn;
	private JLabel mouseCoords;
	private int xcoord = 0;
	private int ycoord = 0;
	
	private String getMouseCoordsHotkeyString = "(F8)";
	private int getMouseCoordsHotKey = KeyEvent.VK_F8;
	private int mouseUpdateDelay = 50;	// in milliseconds
	private int clickDelay = 1000;		// in milliseconds
	private int clickCount = 0;
	private String xcoordTFString = "x:";
	private String ycoordTFString = "y:";
	private String stoppedString = "Stopped";
	private String runningString = "Running";
	private Color buttonColor;
	private String startBtnString = "Start ";
	private String startBtnHotkeyString = "(F6)";
	private int startHotKey = KeyEvent.VK_F6;
	private String stopBtnString = "Stop ";
	private String stopBtnHotkeyString = "(F7)";
	private int stopHotKey = KeyEvent.VK_F7;
	private String clickSpeedString = "Click delay:";
	private String clickTestBtnString = "Test";
	private String resetClickString = "Reset";
	
	private JPanel mainPanel;
	private String imagePath = "/images/";	// path in jar file for images
	private String hotkeyMessage = "Hotkeys:\n" + startBtnHotkeyString + " to start.\n" + stopBtnHotkeyString + " to stop.\n"
			 + getMouseCoordsHotkeyString + " to get the current mouse position.\n\n";
	private String clickDelayMessage = "Click Delay:\nDelay time is in milliseconds (ms).";
	
	private boolean getMouse = false; // control the updateMousePosition() thread
	
	public MainFrame()
	{
		super();
		clicker = new AutoClicker();
		
		setFrameAttributes();
		initializePanel();
		
		buttonColor = new Color(225, 225, 225);	// RGB
		createGUIelements();
		createAndAddMenuBar();
		addAllComponentsToFrame();
		// get mouse location and display on window
		getMouse = true;
		updateMousePosition();
		// Enable hotkeys
		setupHotkeys();
		
		this.setVisible(true);
	}
	
	/**
	 * Get the version number string. Major.Minor.Subminor[-Beta]
	 * @return version string a.b.c-M
	 */
	public String getVersionNumber()
	{
		return this.version;
	}

	private void initializePanel()
	{
		mainPanel = new JPanel((new GridLayout(0, 2, 5, 5)));	// rows, cols, horiz gap, vert gap
		mainPanel.setBackground(new Color(200, 200, 200));		// RGB
	}

	private void createAndAddMenuBar()
	{
		JMenuBar menuBar = new JMenuBar();
		this.setJMenuBar(menuBar);
		
		JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F);
		menuBar.add(fileMenu);
		
		JMenuItem quitMenuItem = new JMenuItem("Quit", new ImageIcon(this.getClass().getResource(imagePath+"exit.png")));
		quitMenuItem.setMnemonic(KeyEvent.VK_Q);
		quitMenuItem.addActionListener(new ActionListener()
		{
		    public void actionPerformed(ActionEvent ae)
		    {
		    	getMouse = false;
		    	clicker.stopClicking();
		        dispose(); // close program if user clicks: File -> Quit
		    }
		});
		fileMenu.add(quitMenuItem);
		
		JMenu helpMenu = new JMenu("Help");
		helpMenu.setMnemonic(KeyEvent.VK_H);
		menuBar.add(helpMenu);
		
		JMenuItem updateMenuItem = new JMenuItem("Updates", new ImageIcon(this.getClass().getResource(imagePath+"update.png")));
		updateMenuItem.setMnemonic(KeyEvent.VK_U);
		updateMenuItem.addActionListener(new ActionListener()
		{
			private Dimension frameDimentions = new Dimension(240, 150);

			@Override
			public void actionPerformed(ActionEvent e)
			{
				// create a window with a button to launch the github website
				JButton update = new JButton("Get Update");
				update.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent e)
					{
						try
						{
							java.awt.Desktop.getDesktop().browse(
									java.net.URI.create("https://github.com/objectDisorientedProgrammer/AutoClicker/releases"));
						} catch(IOException e1)
						{
							JOptionPane.showMessageDialog(null, "Could not connect.", "Connection Error",
									JOptionPane.ERROR_MESSAGE);
						}
					}
				});
				// show version number and upgrade instructions
				JTextArea updateInfo = new JTextArea(
						"Download the latest jar file from the releases page if you have an older version.\n\nCurrent Version: " + version);
				updateInfo.setEditable(false);
				updateInfo.setLineWrap(true);
				updateInfo.setWrapStyleWord(true);
				updateInfo.setMinimumSize(frameDimentions);
				
				JFrame f = new JFrame("Check for updates");
				f.setLocationRelativeTo(null);
				f.setSize(frameDimentions);
				f.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
				f.setLayout(new BorderLayout(10, 10));
				
				f.getContentPane().add(updateInfo, BorderLayout.PAGE_START);
				f.getContentPane().add(update, BorderLayout.PAGE_END);
				
				f.setVisible(true);
			}
		});
		helpMenu.add(updateMenuItem);
		
		JMenuItem helpMenuItem = new JMenuItem("Hints", new ImageIcon(this.getClass().getResource(imagePath+"help.png")));
		helpMenuItem.setMnemonic(KeyEvent.VK_H);
		helpMenuItem.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				// show basic use instructions if user clicks: Help -> Getting Started
                JOptionPane.showMessageDialog(null, hotkeyMessage+clickDelayMessage, "Tips & Tricks",
						JOptionPane.PLAIN_MESSAGE, new ImageIcon(this.getClass().getResource(imagePath+"help64.png")));
			}
		});
		helpMenu.add(helpMenuItem);
		
		JMenuItem aboutMenuItem = new JMenuItem("About", new ImageIcon(this.getClass().getResource(imagePath+"about.png")));
		aboutMenuItem.setMnemonic(KeyEvent.VK_A);
		aboutMenuItem.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				// show author and version if user clicks: Help -> About
				JOptionPane.showMessageDialog(null, "Created by " + author + "\nVersion " + version, "About",
						JOptionPane.INFORMATION_MESSAGE, new ImageIcon(this.getClass().getResource(imagePath+"person.png")));
			}
		});
		helpMenu.add(aboutMenuItem);
	}
	
	private void setFrameAttributes()
	{
		this.setTitle(programName);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(frameWidth, frameHeight);	// set frame size
		this.setLocationRelativeTo(null);		// display in the center of the screen
	}

	private void createGUIelements()
	{
		// create xcoord button
		xcoordTF = new JTextField(4);
		xcoordTF.setText("" + xcoord);

		// create xcoord label
		xcoordLbl = new JLabel(xcoordTFString);
		xcoordLbl.setHorizontalAlignment(JLabel.RIGHT);

		// create ycoord button
		ycoordTF = new JTextField(4);
		ycoordTF.setText("" + ycoord);

		// create ycoord label
		ycoordLbl = new JLabel(ycoordTFString);
		ycoordLbl.setHorizontalAlignment(JLabel.RIGHT);

		// create label to show status of app (running or not)
		clickStatusLbl = new JLabel(stoppedString);
		clickStatusLbl.setForeground(Color.red);
		clickStatusLbl.setFont(new Font("Tahoma", Font.BOLD, 15));
		clickStatusLbl.setHorizontalAlignment(JLabel.CENTER);

		// create start button
		startBtn = new JButton(startBtnString);
		startBtn.setBackground(buttonColor);
		startBtn.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				
				if(clicker.startClicking(Integer.parseInt(xcoordTF.getText()),
										Integer.parseInt(ycoordTF.getText()),
										Integer.parseInt(clickSpeedTF.getText())))
				{
					// update status
					clickStatusLbl.setForeground(new Color(0, 200, 100));
					clickStatusLbl.setText(runningString);
				}
			}
		});
		
		// create stop button
		stopBtn = new JButton(stopBtnString);
		stopBtn.setBackground(buttonColor);
		stopBtn.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				clicker.stopClicking();
				// dont keep changing the text if its already stopped
				if(!clickStatusLbl.getText().equals(stoppedString))
				{
					// update status
					clickStatusLbl.setForeground(Color.red);
					clickStatusLbl.setText(stoppedString);
				}
			}
		});

		// Label for delay TF
		clickSpeedLbl = new JLabel(clickSpeedString);
		clickSpeedLbl.setHorizontalAlignment(JLabel.RIGHT);

		// Textfield for time delay
		clickSpeedTF = new JTextField(4);
		clickSpeedTF.setText("" + clickDelay);
		// clickSpeedTF.addActionListener(clearTextFieldOnFocus);

		// Click counter label
		clickCountLbl = new JLabel("" + clickCount);
		clickCountLbl.setHorizontalAlignment(SwingConstants.RIGHT);

		// Click test button
		clickTestBtn = new JButton(clickTestBtnString);
		clickTestBtn.setBackground(buttonColor);
		clickTestBtn.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				clickCountLbl.setText("" + ++clickCount);
			}
		});

		// Reset button for click count
		resetClickCountBtn = new JButton(resetClickString);
		resetClickCountBtn.setBackground(buttonColor);
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
	}

	private void addAllComponentsToFrame()
	{
		mainPanel.add(clickSpeedLbl);
		mainPanel.add(clickSpeedTF);
		mainPanel.add(xcoordLbl);
		mainPanel.add(xcoordTF);
		mainPanel.add(ycoordLbl);
		mainPanel.add(ycoordTF);
		mainPanel.add(clickStatusLbl);
		mainPanel.add(clickTestBtn);
		mainPanel.add(startBtn);
		mainPanel.add(clickCountLbl);
		mainPanel.add(stopBtn);
		mainPanel.add(resetClickCountBtn);
		mainPanel.add(mouseCoords);
		
		this.add(mainPanel);
	}

	/**
	 * Constantly get the mouse X and Y coordinates and update the textfield that displays them.
	 */
	public void updateMousePosition()
	{
		Thread updateMouseCoords = new Thread(new Runnable()
		{
			Point mouseXY;

			@Override
			public void run()
			{
				while(getMouse)
				{
					try
					{
						mouseXY = MouseInfo.getPointerInfo().getLocation();
						Thread.sleep(mouseUpdateDelay);
						mouseCoords.setText((int) mouseXY.getX() + ", "
								+ (int) mouseXY.getY());
					} catch(InterruptedException e)
					{
						mouseCoords.setText(e.getMessage());
					}
				}
			}
		});
		updateMouseCoords.start();
	}
	
	private void setupHotkeys()
	{
		InputMap keyMap = new ComponentInputMap((JComponent) mainPanel);
		
		// Start clicking when hotkey is pressed
		keyMap.put(KeyStroke.getKeyStroke(startHotKey, 0), "action_start");
		ActionMap actionMap = new ActionMapUIResource();
	    actionMap.put("action_start", new AbstractAction() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	        	if(clicker.startClicking(Integer.parseInt(xcoordTF.getText()),
						Integer.parseInt(ycoordTF.getText()),
						Integer.parseInt(clickSpeedTF.getText())))
				{
					// update status
					clickStatusLbl.setForeground(new Color(0, 200, 100));
					clickStatusLbl.setText(runningString);
				}
	        }
	    });
	    
	    // Stop clicking when hotkey is pressed
	    keyMap.put(KeyStroke.getKeyStroke(stopHotKey , 0), "action_stop");
	    actionMap.put("action_stop", new AbstractAction() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	            clicker.stopClicking();
	            // dont keep changing the text if its already stopped
	    		if(!clickStatusLbl.getText().equals(stoppedString))
	    		{
	    			// update status
	    			clickStatusLbl.setForeground(Color.red);
	    			clickStatusLbl.setText(stoppedString);
	    		}
	        }
	    });
	    
	    // Get current mouse coords and put into textfields when F8 is pressed
	    keyMap.put(KeyStroke.getKeyStroke(getMouseCoordsHotKey, 0), "action_getMouseCoords");
	    actionMap.put("action_getMouseCoords", new AbstractAction() {
	        @Override
	        public void actionPerformed(ActionEvent e)
	        {
	            try
				{
					Point pi = MouseInfo.getPointerInfo().getLocation();
					xcoordTF.setText(pi.x + "");
					ycoordTF.setText(pi.y + "");
				} catch(HeadlessException e1)
				{
					e1.printStackTrace();
				}
	        }
	    });
	    
	    SwingUtilities.replaceUIActionMap((JComponent) mainPanel, actionMap);
	    SwingUtilities.replaceUIInputMap((JComponent) mainPanel, JComponent.WHEN_IN_FOCUSED_WINDOW, keyMap);
	}
}
