package com.koolaborate.mvc.view.tinyview;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import com.koolaborate.model.Album;
import com.koolaborate.mvc.controller.PlaybackController;
import com.koolaborate.mvc.controller.PlaybackController.STATE;
import com.koolaborate.mvc.view.mainwindow.MainWindow;
import com.koolaborate.mvc.view.mainwindow.RoundedBorder;
import com.koolaborate.mvc.view.playlistview.CoverPanel;
import com.koolaborate.mvc.view.playlistview.Playlist;
import com.koolaborate.mvc.view.playlistview.PlaylistEntry;
import com.koolaborate.mvc.view.playlistview.PlaylistPanel;

/***********************************************************************************
 * TinyView                                                                        *
 ***********************************************************************************
 * A small window that supports basic operations like play song, skip to next /    *
 * previous song and pause. Also, a small version of the cover image and the song  *
 * title and artist are shown.                                                     *
 ***********************************************************************************
 * (c) Impressive Artworx, 2k8                                                     * 
 * @author Manuel Kaess                                                            *
 * @version 1.2                                                                    *
 ***********************************************************************************
 * This file is part of VibrantPlayer.                                             *
 *                                                                                 *
 *  VibrantPlayer is free software: you can redistribute it and/or modify          *
 *  it under the terms of the Lesser GNU General Public License as published by    *
 *  the Free Software Foundation, either version 3 of the License, or              *
 *  (at your option) any later version.                                            *
 *                                                                                 *
 *  VibrantPlayer is distributed in the hope that it will be useful,               *
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of                 *
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Lesser            *
 *  GNU General Public License for more details.                                   *
 *                                                                                 *
 *  You should have received a copy of the Lesser GNU General Public License       *
 *  along with VibrantPlayer. If not, see <http://www.gnu.org/licenses/>.          *
 ***********************************************************************************/
public class TinyView extends JWindow
{
	private static final long serialVersionUID = -7974766401641309809L;
	private JButton closeButton;
	private CoverPanel cover;
	private Playlist playlist;
	private JPanel controlPanel, bg;
	private int translateY = 0;
	private int panelHeight = 40;
	private int animationDuration = 800; // for 0.8 seconds
	private Timer outTimer, inTimer;
	long animStartTime, fadeInStartTime;
	private boolean panelIsVisible = false;
	
	private JLabel songLabel, artistLabel;
	private ImageIcon playIcon, pauseIcon, stopIcon, prevIcon, nextIcon;
	
	private int coverSize = 100;
	
	private MainWindow window;
	private Album album;
	private int currentSongId = -1;
	
	private JButton playButton;
	private PlaybackController playerPanel;
	
	/**
	 * Constructor.
	 * 
	 * @param window reference to the main window
	 * @param a the currently selected album
	 * @param currentSongId the ID of the currently played song, or -1
	 */
	public TinyView(MainWindow window, Album a, int currentSongId)
	{
		this.window = window;
		playerPanel = window.getPlayerPanel();
		this.album = a;
		this.currentSongId = currentSongId;
		
		setSize(330, 140);
		SwingUtilities.invokeLater(new Runnable(){
			public void run() 
			{
				initGUI();
				setScreenPosition();
				setWindowShape();
				setSongInformation();
				setVisible(true);
				setAlwaysOnTop(true);
			}
		});
	}
	
	/**
	 * Initializes the GUI elements.
	 */
	private void initGUI()
	{
		loadPictures();
		
//		bg = window.getDecorator().getTinyViewBgPanel();
		//TODO leads to weired behaviour of the tiny window... leave out at first
		
		bg = new JPanel(){
			private static final long serialVersionUID = -7885355487013240664L;

			@Override
			protected void paintComponent(Graphics g)
			{
				Graphics2D g2d = (Graphics2D) g;
				
				int w = getWidth();
				int h = getHeight();
				float[] fractions = {0.0f, 0.3f, 1.0f};
				Color[] colors = {Color.BLACK, Color.BLACK, Color.GRAY};
				
				// Paint a gradient from top to bottom
				LinearGradientPaint gp = new LinearGradientPaint(new Point(0, 0), new Point(0, h), fractions, colors);
				
				g2d.setPaint(gp);
				g2d.fillRect(0, 0, w, h);
			}
		};
		
		bg.setLayout(null);
		
		// add the cover
		cover = new CoverPanel(window);
		cover.setBigView(true);
		cover.setCoverPath(window.getCurrentFolderPath() + File.separator + "folder.jpg");
		cover.refreshCover();
		cover.setCoverSize(coverSize);
		cover.setBounds(0, 0, coverSize + 20, (int)(coverSize * 1.5));
		bg.add(cover);
		
		// add the close view button
		closeButton = new JButton(new ImageIcon(getClass().getResource("/images/close_alone.png")));
		closeButton.setRolloverIcon(new ImageIcon(getClass().getResource("/images/close_alone_over.png")));
		closeButton.setBorder(null);
		closeButton.setFocusPainted(false);
		closeButton.setContentAreaFilled(false);
		closeButton.setPreferredSize(new Dimension(43, 17));
		closeButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				window.setVisible(true);
				window.setTinyWindowShown(false);
				setVisible(false);
				outTimer.stop();
				inTimer.stop();
				dispose();
			}
		});
		closeButton.setBounds(getWidth() - 43 - 6, 0, 43, 17);
		bg.add(closeButton);
		
		// the playlist is needed for getting the current and next song
		PlaylistPanel play = new PlaylistPanel(window);
		play.setAlbumId(album.getId());
		play.refreshSongList();
		playlist = play.getPlaylist();
		
		songLabel = new JLabel();
		songLabel.setFont(new Font("Calibri", Font.BOLD, 18));
		songLabel.setForeground(Color.WHITE);
		songLabel.setBounds(coverSize + 20 + 4, 24, getWidth() - (coverSize+26), 20);
		bg.add(songLabel);
		
		artistLabel = new JLabel();
		artistLabel.setFont(new Font("Calibri", Font.PLAIN, 12));
		artistLabel.setForeground(Color.WHITE);
		artistLabel.setBounds(coverSize + 20 + 4, 50, getWidth() - (coverSize+26), 14);
		bg.add(artistLabel);
		
		// add the player control buttons
		controlPanel = new JPanel(){
			private static final long serialVersionUID = -6420108314717562265L;

			@Override
			protected void paintComponent(Graphics g)
			{
				Graphics2D g2 = (Graphics2D) g;

				g2.setClip(new RoundRectangle2D.Double(0, -12, getWidth(), getHeight()+10, 10, 10));
				
				g2.clearRect(0, 0, getWidth(), getHeight());
				
				// paint the background
				g2.setColor(Color.BLACK);
				g2.setComposite(AlphaComposite.SrcOver.derive(0.3f));
				g2.fillRect(0, 0, getWidth(), getHeight());

				// draw a line
				g2.setComposite(AlphaComposite.SrcOver.derive(1.0f));
				g2.drawLine(0, 0, getWidth(), 0);
			}
			
			@Override
			protected void paintBorder(Graphics g)
			{
				// the black border
				g.setColor(Color.BLACK);
				g.drawLine(0, 0, 0, getHeight()-9);
				g.drawArc(0, getHeight()-12, 11, 11, 180, 90);
				g.drawLine(8, getHeight()-1, getWidth()-8, getHeight()-1);
				g.drawArc(getWidth()-12, getHeight()-12, 11, 11, 270, 90);
				g.drawLine(getWidth()-1, getHeight()-8, getWidth()-1, 0);
			}
			
			@Override
			public void paint(Graphics g)
			{
				g.translate(0, translateY);
				super.paint(g);
			}
		};
		controlPanel.setOpaque(false);
		controlPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 2, 2));
		
		ActionListener l = new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				String command = e.getActionCommand();
				if(command.equals("prev"))
				{
					if(playerPanel.getCurrentState() == STATE.PLAYING)
					{
						playerPanel.fadeOut();
					}
					playerPanel.playPreviousSong();
					playButton.setIcon(pauseIcon);
					playButton.setActionCommand("pause");
				}
				else if(command.equals("play"))
				{
					playerPanel.playSong();
					playButton.setIcon(pauseIcon);
					playButton.setActionCommand("pause");
				}
				else if(command.equals("pause"))
				{
					playerPanel.pauseSong();
					playButton.setIcon(playIcon);
					playButton.setActionCommand("resume");
				}
				else if(command.equals("stop"))
				{
					if(playerPanel.getCurrentState() == STATE.PLAYING)
					{
						playerPanel.fadeOut();
					}
					playerPanel.stopSong();
					playButton.setIcon(playIcon);
					playButton.setActionCommand("play");
				}
				else if(command.equals("next"))
				{
					if(playerPanel.getCurrentState() == STATE.PLAYING)
					{
						playerPanel.fadeOut();
					}
					playerPanel.playNextSong();
					playButton.setIcon(pauseIcon);
					playButton.setActionCommand("pause");
				}
				else if(command.equals("resume"))
				{
					playerPanel.resumeSong();
					playButton.setIcon(pauseIcon);
					playButton.setActionCommand("pause");
				}
			}
		};
		
		JButton prevButton = new JButton(prevIcon);
		prevButton.setActionCommand("prev");
		prevButton.addActionListener(l);
		playButton = new JButton(playIcon);
		playButton.setActionCommand("play");
		playButton.addActionListener(l);
		JButton stopButton = new JButton(stopIcon);
		stopButton.setActionCommand("stop");
		stopButton.addActionListener(l);
		JButton nextButton = new JButton(nextIcon);
		nextButton.setActionCommand("next");
		nextButton.addActionListener(l);
		
		controlPanel.add(prevButton);
		controlPanel.add(playButton);
		controlPanel.add(stopButton);
		controlPanel.add(nextButton);
		
		controlPanel.setBounds(0, getHeight() - panelHeight, getWidth(), panelHeight);
		translateY = panelHeight; // move the panel to the bottom since it shall be invisible at first
		initPanelFadeTimers();
		bg.add(controlPanel);
		
		// if song is already playing, update play button
		if(window.getPlayerPanel().getCurrentState() == STATE.PLAYING)
		{
			playButton.setIcon(pauseIcon);
			playButton.setActionCommand("pause");
		}
		
		add(bg);
		
		addMouseListener(new MouseAdapter(){		
			@Override
			public void mouseEntered(MouseEvent e)
			{
				fadePanelIn();
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
				if(getComponentAt(e.getPoint()) == null) fadePanelOut();
			}
		});
	}
	
	/**
	 * Loads the button images.
	 */
	private void loadPictures()
	{
		playIcon  = new ImageIcon(getClass().getResource("/images/navplay.png"));
		pauseIcon = new ImageIcon(getClass().getResource("/images/tiny_pause.png"));
		stopIcon  = new ImageIcon(getClass().getResource("/images/tiny_stop.png"));
		prevIcon  = new ImageIcon(getClass().getResource("/images/tiny_prev.png"));
		nextIcon  = new ImageIcon(getClass().getResource("/images/tiny_next.png"));
	}

	/**
	 * Initializes the timers used for the fade effect of the button panel.
	 */
	private void initPanelFadeTimers()
	{
		outTimer = new Timer(30, new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				long currentTime = System.nanoTime() / 1000000;
				long totalTime = currentTime - animStartTime;
				if(totalTime > animationDuration)
				{
					animStartTime = currentTime;
				}
				float fraction = (float)totalTime / animationDuration;
				fraction = Math.min(1.0f, fraction);
				translateY = (int) (panelHeight * fraction);
				controlPanel.repaint();
				if(fraction >= 1.0f)
				{
					outTimer.stop();
					panelIsVisible = false;
				}
			}
		});
		
		inTimer = new Timer(30, new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				long currentTime = System.nanoTime() / 1000000;
				long totalTime = currentTime - fadeInStartTime;
				if(totalTime > animationDuration)
				{
					fadeInStartTime = currentTime;
				}
				float fraction = (float)totalTime / animationDuration;
				fraction = Math.min(1.0f, fraction);
				translateY = -(int)(panelHeight * fraction);
				translateY+= panelHeight;
				controlPanel.repaint();
				if(fraction >= 1.0f)
				{
					inTimer.stop();
					panelIsVisible = true;
				}
			}
		});
	}
	
	/**
	 * Fades the button panel out vertically.
	 */
	private void fadePanelOut()
	{
		if(!panelIsVisible) return;
		
		if(inTimer.isRunning()) inTimer.stop();
		
		if(!outTimer.isRunning())
		{
			animStartTime = System.nanoTime() / 1000000;
			outTimer.start();
		}
	}
	
	/**
	 * Fades the button panel in vertically.
	 */
	private void fadePanelIn()
	{
		if(panelIsVisible) return;
		
		if(outTimer.isRunning()) outTimer.stop();
		
		if(!inTimer.isRunning())
		{
			fadeInStartTime = System.nanoTime() / 1000000;
			inTimer.start();
		}
	}
	
	/**
	 * Sets the position of the window in the bottom right area of the screen.
	 */
	private void setScreenPosition()
	{
		// Get the size of the default screen
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		int x = dim.width - getSize().width - 20;
		int y = dim.height - getSize().height - 40;
		setLocation(new Point(x, y));
	}
	
	/**
	 * Sets the window shape to a rounded rectangle (if the class is available).
	 */
	private void setWindowShape()
	{
		if(window.getWindowShaper().shapeWindow(this, new RoundRectangle2D.Double(0, 0, 
				getWidth(), getHeight(), 10, 10)) && bg != null) 
			bg.setBorder(new RoundedBorder(10));
	}
	
	/**
	 * Sets the artist and song title for the currently played song.
	 */
	private void setSongInformation()
	{
		if(currentSongId != -1)
		{
			for(PlaylistEntry entry : playlist.getEntries())
			{
				if(entry.getSongId() == currentSongId) 
				{
					playlist.setSelectedEntry(entry);
//					window.getCenterPanel().getCoverPanel().get //TODO get title and artist name over cover panel song info in main window
					songLabel.setText(entry.getTitle());
					artistLabel.setText(album.getArtist());
					return;
				}
			}
		}
	}
	
	/**
	 * Updates the song title and artist name for the given song.
	 * 
	 * @param currentSongId the currently played song id
	 */
	public void updateView(int currentSongId)
	{
		this.currentSongId = currentSongId;
		setSongInformation();
	}
}