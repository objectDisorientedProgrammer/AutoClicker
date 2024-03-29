/* File        : MainFrame.java
 * Created     : Dec 27, 2014 [12/27/2014]
 * By          : Douglas Chidester
 * Description : Main GUI JFrame.
 *
 * See AutoClicker.java for documentation.
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

import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.BoxLayout;
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
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.plaf.ActionMapUIResource;

@SuppressWarnings("serial")
public class MainFrame extends JFrame
{
    private AutoClicker clicker;

    private int frameWidth = 240;
    private int frameHeight = 270;
    private String programName = AutoClicker.programName;
    private String version = AutoClicker.version;
    private String author = AutoClicker.author;

    private JTextField xcoordTF, ycoordTF, clickSpeedTF;
    private JLabel xcoordLbl, ycoordLbl, clickSpeedLbl, clickCountLbl, clickStatusLbl;
    private JButton startBtn, stopBtn, clickTestBtn, resetClickCountBtn;
    private JLabel mouseCoords;
    private int xcoord = 0;
    private int ycoord = 0;
    private static final int MAX_DELAY_MS = 60000; // milliseconds
    private static final int MIN_X_COORD = 0;
    private static final int MIN_Y_COORD = 0;
    private String invalidCoordsMsg = "Please enter an x and y coordinate greater than (" + MIN_X_COORD + ", " + MIN_Y_COORD + ").";

    private String getMouseCoordsHotkeyString = "F8";
    private int getMouseCoordsHotKey = KeyEvent.VK_F8;
    private int mouseUpdateDelay = 50; // in milliseconds
    private int clickDelay = 1000;     // in milliseconds
    private int clickCount = 0;
    private String xcoordTFString = "x:";
    private String ycoordTFString = "y:";
    private String stoppedString = "Stopped";
    private String runningString = "Running";
    private Color buttonColor;
    private String startBtnString = "Start ";
    private String startBtnHotkeyString = "F6";
    private int startHotKey = KeyEvent.VK_F6;
    private String stopBtnString = "Stop ";
    private String stopBtnHotkeyString = "F7";
    private int stopHotKey = KeyEvent.VK_F7;
    private String clickSpeedString = "Click delay:";
    private String clickTestBtnString = "Test";
    private String resetClickString = "Reset";

    private JPanel mainPanel;
    private String imagePath = "/images/"; // path in jar file for images
    private String hotkeyMessage = "Hotkeys:\n" + startBtnHotkeyString + " to start.\n" + stopBtnHotkeyString + " to stop.\n"
             + getMouseCoordsHotkeyString + " to get the current mouse position.\n\n";
    private String clickDelayMessage = "Click Delay:\nDelay time is in milliseconds (ms).";

    private boolean getMouse = false; // control the updateMousePosition() thread

    private String license = "MIT License\n\nCopyright (c) 2011 Douglas Chidester\n\n" +
                            "Permission is hereby granted, free of charge, to any person obtaining a copy\n" +
                            "of this software and associated documentation files (the \"Software\"), to deal\n" +
                            "in the Software without restriction, including without limitation the rights\n" +
                            "to use, copy, modify, merge, publish, distribute, sublicense, and/or sell\n" +
                            "copies of the Software, and to permit persons to whom the Software is\n" +
                            "furnished to do so, subject to the following conditions:\n\n" +
                            "The above copyright notice and this permission notice shall be included in all\n" +
                            "copies or substantial portions of the Software.\n\n" +
                            "THE SOFTWARE IS PROVIDED \"AS IS\", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR\n" +
                            "IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,\n" +
                            "FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE\n" +
                            "AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER\n" +
                            "LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,\n" +
                            "OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE\n" +
                            "SOFTWARE.";

    public MainFrame()
    {
        super();
        clicker = new AutoClicker();

        setFrameAttributes();
        initializePanel();

        buttonColor = new Color(102, 204, 255); // light blue
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
     * Get the version number string. Major.Minor.Subminor
     * @return version string x.y.z
     */
    public String getVersionNumber()
    {
        return this.version;
    }

    private void initializePanel()
    {
        mainPanel = new JPanel((new GridLayout(0, 2, 5, 5))); // rows, cols, horiz gap, vert gap
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

        JMenuItem updateMenuItem = new JMenuItem("Check for updates",
                new ImageIcon(this.getClass().getResource(imagePath+"update.png")));
        updateMenuItem.setMnemonic(KeyEvent.VK_C);
        updateMenuItem.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                try
                {
                    // Set up a REST GET query to the github API
                    final String urlCommon = "objectDisorientedProgrammer/AutoClicker/";
                    final String urlBase = "https://api.github.com/repos/" + urlCommon;
                    URL tags = new URL(urlBase + "tags");
                    HttpURLConnection conn = (HttpURLConnection) tags.openConnection();
                    conn.setRequestMethod("GET");

                    if(conn.getResponseCode() == HttpURLConnection.HTTP_OK)
                    {
                        // Collect the response
                        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        StringBuffer response = new StringBuffer();
                        String line = null;
                        while((line = in.readLine()) != null)
                        {
                            response.append(line);
                        }
                        in.close();

                        // Parse the json blob to find the most recent release version
                        /* Currently need to skip over the first three entries because
                           I changed tag naming convention from vX.Y.Z to X.Y.Z...
                        */
                        int ver = 0;
                        String sub = "";
                        // skip 3 "vX.Y.Z" tags
                        // skip first "vX.Y.Z" in the response string
                        ver = response.toString().indexOf("name");
                        sub = response.substring(ver+1);
                        // skip the remaining "vX.Y.Z" in the substring
                        for (int i = 0; i < 2; i++)
                        {
                            ver = sub.indexOf("name");
                            sub = sub.substring(ver+1);
						}
                        sub = sub.substring(ver+7);
                        ver = sub.indexOf('"');
                        String latest = sub.substring(0, ver);
                        latest = latest.trim();
                        final String urlVersion = latest;

                        // parse each version string to compare X.Y.Z in order to determine "up to date-ness"
                        String[] currentVersion = AutoClicker.version.trim().split("\\.");
                        String[] latestVersion = latest.split("\\.");

                        // if the queried latest version is larger than the current application version, prompt the user to update
                        if(Integer.parseInt(latestVersion[0]) > Integer.parseInt(currentVersion[0])
                                || Integer.parseInt(latestVersion[1]) > Integer.parseInt(currentVersion[1])
                                || Integer.parseInt(latestVersion[2]) > Integer.parseInt(currentVersion[2]))
                        {
                            // Create a fancy panel to show current and new versions along with a
                            // button to take the user to the download page
                            JPanel update = new JPanel();
                            update.setLayout(new BoxLayout(update, BoxLayout.Y_AXIS));
                            JLabel curver = new JLabel("Current version: " + AutoClicker.version);
                            curver.setAlignmentX(Component.CENTER_ALIGNMENT);
                            update.add(curver);

                            JLabel newver = new JLabel("New version: " + latest);
                            newver.setAlignmentX(Component.CENTER_ALIGNMENT);
                            update.add(newver);

                            update.add(new JLabel(" ")); // poor man's padding

                            // TODO replace image?
                            JButton download = new JButton(
                                    new ImageIcon(this.getClass().getResource(imagePath+"update.png")));
                            download.setAlignmentX(Component.CENTER_ALIGNMENT);
                            download.addActionListener(new ActionListener()
                            {
                                @Override
                                public void actionPerformed(ActionEvent e)
                                {
                                    try {
                                        final String dl = "https://www.github.com/" + urlCommon +
                                                "releases/download/" + urlVersion + "/"
                                                + AutoClicker.programName + ".jar";
                                        Desktop.getDesktop().browse(new URI(dl));
                                    } catch (Exception e1) {
                                        JOptionPane.showMessageDialog(getMainWindow(), e1.getMessage(), "URL ERROR",
                                                JOptionPane.ERROR_MESSAGE, null);
                                    }
                                }
                            });
                            update.add(download);

                            update.add(new JLabel(" ")); // poor man's padding

                            // Display the update message window
                            Object[] options = { "Close" };
                            JOptionPane.showOptionDialog(getMainWindow(), update, "Update Available", JOptionPane.DEFAULT_OPTION,
                                    JOptionPane.WARNING_MESSAGE, null, options, options[0]);
                        }
                        else
                        {
                            // Program is up to date - inform the user
                            Object[] opt = { "Great" };
                            JOptionPane.showOptionDialog(getMainWindow(), "Version: "+ AutoClicker.version, "Up to date",
                                    JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, opt, opt[0]);
                        }
                    }
                    else
                    {
                        System.out.println("REST GET error...");
                    }
                } catch(IOException e1)
                {
                    e1.printStackTrace();
                }
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
                JOptionPane.showMessageDialog(getMainWindow(), hotkeyMessage+clickDelayMessage, "Tips & Tricks",
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
                JOptionPane.showMessageDialog(getMainWindow(), "Created by " + author + "\nVersion " + version + "\n\n\n\n" + license, "About",
                        JOptionPane.INFORMATION_MESSAGE, new ImageIcon(this.getClass().getResource(imagePath+"person.png")));
            }
        });
        helpMenu.add(aboutMenuItem);
    }

    private void setFrameAttributes()
    {
        this.setTitle(programName);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(frameWidth, frameHeight); // set frame size
        this.setLocationRelativeTo(null); // display in the center of the screen
    }

    private void createGUIelements()
    {
        // create xcoord button
        xcoordTF = new JTextField(4);
        xcoordTF.setToolTipText("Shortcut key " + getMouseCoordsHotkeyString);
        xcoordTF.setText("" + xcoord);

        // create xcoord label
        xcoordLbl = new JLabel(xcoordTFString);
        xcoordLbl.setHorizontalAlignment(JLabel.RIGHT);

        // create ycoord button
        ycoordTF = new JTextField(4);
        ycoordTF.setToolTipText("Shortcut key " + getMouseCoordsHotkeyString);
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
        startBtn.setToolTipText("Shortcut key "+startBtnHotkeyString);
        startBtn.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent ae) {
                startClickLogic();
            }
        });

        // create stop button
        stopBtn = new JButton(stopBtnString);
        stopBtn.setBackground(buttonColor);
        stopBtn.setToolTipText("Shortcut key "+stopBtnHotkeyString);
        stopBtn.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e) {
            	stopClickLogic();
            }
        });

        // Label for delay TF
        clickSpeedLbl = new JLabel(clickSpeedString);
        clickSpeedLbl.setToolTipText("milliseconds");
        clickSpeedLbl.setHorizontalAlignment(JLabel.RIGHT);

        // Textfield for time delay
        clickSpeedTF = new JTextField(4);
        clickSpeedTF.setText("" + clickDelay);
        clickSpeedTF.setToolTipText("0 to " + MAX_DELAY_MS + "ms");

        // Click counter label
        clickCountLbl = new JLabel(Integer.toString(clickCount));
        clickCountLbl.setHorizontalAlignment(SwingConstants.RIGHT);

        // Click test button
        clickTestBtn = new JButton(clickTestBtnString);
        clickTestBtn.setBackground(buttonColor);
        clickTestBtn.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
            	++clickCount;
                clickCountLbl.setText(Integer.toString(clickCount));
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
                clickCountLbl.setText(Integer.toString(clickCount));
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
                        mouseCoords.setText(xcoordTFString + Integer.toString((int) mouseXY.getX()) +
                                            ", " + ycoordTFString + Integer.toString((int) mouseXY.getY()));
                    } catch(InterruptedException e)
                    {
                        mouseCoords.setText(e.getMessage());
                    }
                }
            }
        });
        updateMouseCoords.start();
    }

    /**
     * Function to ensure common behavior between button and hotkey.
     * Could be done as a "Handler" class to be more object oriented...
     */
    private void startClickLogic()
    {
        int x = Integer.parseInt(xcoordTF.getText());
        int y = Integer.parseInt(ycoordTF.getText());
        int clickDelay = Integer.parseInt(clickSpeedTF.getText());
        boolean clickStatus = false;

    	// if not clicking
    	if(!clicker.isClicking())
        {
            // Check click delay is within valid range
            if(clickDelay > MAX_DELAY_MS)
            {
                JOptionPane.showMessageDialog(getMainWindow(), "Delay out of range. Must be 0 to " + MAX_DELAY_MS + " milliseconds.", "Click Delay Error",
                            JOptionPane.ERROR_MESSAGE);
            }
            // if valid coords
            else if(x > MIN_X_COORD && y > MIN_Y_COORD)
            {
                clickStatus = clicker.startClicking(x, y, clickDelay);
            }
            else
            {
                JOptionPane.showMessageDialog(getMainWindow(), invalidCoordsMsg, "Coordinate Error",
                        JOptionPane.ERROR_MESSAGE);
    		}

    		if(clickStatus)
    		{
    			// update status
                clickStatusLbl.setForeground(new Color(0, 200, 100));
                clickStatusLbl.setText(runningString);

                // disable start button
                startBtn.setEnabled(false);
                // enable stop button
                stopBtn.setEnabled(true);
    		}
        }
    }

    /**
     * Function to ensure common behavior between button and hotkey.
     * Could be done as a "Handler" class to be more object oriented...
     */
    private void stopClickLogic()
    {
    	// if clicking
        if(clicker.isClicking())
        {
        	// stop clicking
        	clicker.stopClicking();

        	// update status
            clickStatusLbl.setForeground(Color.red);
            clickStatusLbl.setText(stoppedString);

            // enable start button
            startBtn.setEnabled(true);
            // disable stop button
            stopBtn.setEnabled(false);
        }
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
                startClickLogic();
            }
        });

        // Stop clicking when hotkey is pressed
        keyMap.put(KeyStroke.getKeyStroke(stopHotKey , 0), "action_stop");
        actionMap.put("action_stop", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	stopClickLogic();
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
    
    public JFrame getMainWindow()
    {
        return this;
    }

}
