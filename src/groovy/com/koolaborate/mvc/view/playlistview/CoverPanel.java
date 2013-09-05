package com.koolaborate.mvc.view.playlistview;

import java.awt.AlphaComposite;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import org.apache.commons.lang3.StringUtils;
import org.jdesktop.swingx.graphics.ReflectionRenderer;

import com.koolaborate.mvc.view.bigcover.BigCoverFrame;
import com.koolaborate.mvc.view.hoverbar.Hoverbar;
import com.koolaborate.mvc.view.hoverbar.HoverbarElement;
import com.koolaborate.mvc.view.mainwindow.MainWindow;
import com.koolaborate.mvc.view.searchcover.SearchCoverFrame;
import com.koolaborate.util.GraphicsUtilities;

/***********************************************************************************
 * CoverPanel                                                                      *
 ***********************************************************************************
 * A JPanel that paints a picture of the album cover into a cd case (with a nice   *
 * shiny reflection).                                                              *
 ***********************************************************************************
 * (c) Impressive Artworx, 2k8                                                     * 
 * @author Manuel Kaess                                                            *
 * @version 1.0                                                                    *
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
public class CoverPanel extends JPanel
{
	private String defaultCover = "/images/empty.png";
	private String coverPath = defaultCover;
	private String albumTitle, artistName, albumFolder;
	private BufferedImage cover, cdCase, reflections, stitch;
	private boolean blurReflection = true;
	private boolean shinyReflection = true;
	
	// an animated hover bar that fades in and out on the top right side of the cover
	private Hoverbar hoverbar;
	private MouseAdapter ml;
	private static float MAX_ALPHA = 0.9f;
	private Timer fadeInTimer, fadeOutTimer;
	private long animStartTime;
	private int fadeInDuration  = 2000; // fade in animation will take 2 seconds
	private int fadeOutDuration = 1500; // fade out animation will take 1.5 seconds
	
	private MainWindow mainWindow;
	
	private int coverWidth = 225;
	
	/**
	 * Constructor.
	 * 
	 * @param window reference to the main window
	 */
	public CoverPanel(MainWindow window)
	{
		this.mainWindow = window;
		try
		{
			cover 		= ImageIO.read(getClass().getResource(coverPath));
			cdCase 		= ImageIO.read(getClass().getResource("/images/cd_case.png"));
			reflections = ImageIO.read(getClass().getResource("/images/reflections.png"));
			stitch 		= ImageIO.read(getClass().getResource("/images/stitch.png"));
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		setPreferredSize(new Dimension(coverWidth + 20, 300));
		setLayout(null);
		
		// create the hoverbar which is a holder for elements
		hoverbar = new Hoverbar();
		hoverbar.setAlpha(0.0f); // invisible at first
		
		// the first element is the search
		HoverbarElement view = new HoverbarElement();
		try 
		{
			view.setImage(ImageIO.read(getClass().getResource("/images/loupe_small.png")));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		Thread viewAction = new Thread(new Runnable(){
			public void run() 
			{
				SwingUtilities.invokeLater(new Runnable(){
					public void run()
					{
						new BigCoverFrame(mainWindow);
					}
				});
			}
		});
		view.setActionThread(viewAction);
		hoverbar.addElement(view);
		
		// add a separator
		hoverbar.addSeparator(6);
		
		// the second element is the change icon
		HoverbarElement change = new HoverbarElement();
		try 
		{
			change.setImage(ImageIO.read(getClass().getResource("/images/refresh_icon.png")));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		Thread changeAction = new Thread(new Runnable(){
			public void run() 
			{
				SwingUtilities.invokeLater(new Runnable(){
					public void run()
					{
						new SearchCoverFrame(mainWindow, artistName, albumTitle);
					}
				});
			}
		});
		change.setActionThread(changeAction);
		hoverbar.addElement(change);
		
		Dimension s = hoverbar.getPreferredSize();
		hoverbar.setBounds(getPreferredSize().width - s.width - 14, 14, s.width, s.height);
		
		add(hoverbar);
		
		// add the fadeIn
		fadeInTimer = new Timer(30, new ActionListener(){
			public void actionPerformed(ActionEvent arg0)
			{
				long currentTime = System.nanoTime() / 1000000;
				long totalTime = currentTime - animStartTime;
				if(totalTime > fadeInDuration)
				{
					animStartTime = currentTime;
				}
				float fraction = (float) totalTime / fadeInDuration;
				fraction = Math.min(1.0f, fraction);
				float newAlpha = hoverbar.getAlpha() + fraction * MAX_ALPHA;
				if(newAlpha >= MAX_ALPHA)
				{
					newAlpha = MAX_ALPHA;
					fadeInTimer.stop();
				}
				hoverbar.setAlpha(newAlpha);
				hoverbar.repaint();
			}
		});
		
		// and the fade out
		fadeOutTimer = new Timer(30, new ActionListener(){
			public void actionPerformed(ActionEvent arg0)
			{
				long currentTime = System.nanoTime() / 1000000;
				long totalTime = currentTime - animStartTime;
				if(totalTime > fadeOutDuration)
				{
					animStartTime = currentTime;
				}
				float fraction = (float) totalTime / fadeOutDuration;
				fraction = Math.min(1.0f, fraction);
				float newAlpha = hoverbar.getAlpha() - fraction * MAX_ALPHA;
				if(newAlpha <= 0.0f)
				{
					newAlpha = 0.0f;
					fadeOutTimer.stop();
				}
				hoverbar.setAlpha(newAlpha);
				hoverbar.repaint();
			}
		});
		
		hoverbar.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseEntered(MouseEvent arg0)
			{
				fadeInTimer.stop();
				fadeOutTimer.stop();
				hoverbar.setAlpha(MAX_ALPHA);
				hoverbar.repaint();
			}
		});
		
		addMouseListener(ml = new MouseAdapter(){
			@Override
			public void mouseEntered(MouseEvent e)
			{
				fadeIn();
			}
			@Override
			public void mouseExited(MouseEvent arg0)
			{
				fadeOut();
			}
		});
	}
	
	/**
	 * Fades the hover bar in.
	 */
	private void fadeIn()
	{
		// first stop the fadeOut timer
		if(!fadeOutTimer.isRunning()) fadeOutTimer.stop();
		
		// then start the fadeIn timer (if not already running)
		if(!fadeInTimer.isRunning())
		{
			animStartTime = System.nanoTime() / 1000000;
			fadeInTimer.start();
		}
		else
		{
			fadeInTimer.stop();
		}
	}
	
	/**
	 * Fades the hover bar out.
	 */
	private void fadeOut()
	{
		// first stop the fadeIn timer
		if(!fadeInTimer.isRunning()) fadeInTimer.stop();
		
		// then start the fadeOut timer (if not already running)
		if(!fadeOutTimer.isRunning())
		{
			animStartTime = System.nanoTime() / 1000000;
			fadeOutTimer.start();
		}
		else
		{
			fadeOutTimer.stop();
		}
	}
	
	/**
	 * Sets the cover path of the album cover image.
	 * 
	 * @param path the path to the image to be set
	 */
	public void setCoverPath(String path)
	{
		coverPath = path;
	}
	
	/**
	 * Refreshes the album cover image.
	 */
	public void refreshCover()
	{
		// if the cover file path is empty, use the default empty image
		if(StringUtils.isEmpty(coverPath))
		{
			coverPath = defaultCover;
		}

		// create file object to check whether the file is available
		try
		{
			File coverFile = new File(coverPath);
			if(!coverFile.exists())
			{
				mainWindow.setCurrentFolder(albumFolder);
				// the default image has to be loaded from the ressources (from the JAR archive)
				cover = ImageIO.read(getClass().getResource(defaultCover));
			}
			else
			{
				cover = ImageIO.read(coverFile);
				mainWindow.setCurrentFolder(coverFile.getParent());
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		// repaint thread safe
		SwingUtilities.invokeLater(new Runnable(){
			public void run()
			{
				repaint();
			}
		});
	}
	
	@Override
	protected void paintComponent(Graphics g)
	{
		BufferedImage jewelcase = GraphicsUtilities.createCompatibleImage(262, 233);
		Graphics2D g2d = jewelcase.createGraphics();
		g2d.clearRect(0, 0, getWidth(), getHeight());
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2d.setComposite(AlphaComposite.SrcOver);
		g2d.drawImage(cdCase, 0, 0, 262, 233, null);
		g2d.drawImage(cover, 19, 3, 240, 227, null);
		g2d.drawImage(stitch, 19, 3, 19+6, 259, null);
		if(shinyReflection) g2d.drawImage(reflections, 0, 0, 262, 233, null);
		
		Graphics2D g3 = (Graphics2D) g;
		// now create the reflection
		ReflectionRenderer rend = new ReflectionRenderer();
		rend.setBlurEnabled(blurReflection); // the reflection is more realistic with a blur filter applied to it
		BufferedImage reflection = rend.appendReflection(jewelcase);
		g3.drawImage(reflection, 10, 10, coverWidth, (int)(coverWidth * 1.225), null);
	}

	/**
	 * Whether or not to blur the reflection of the album cover image.
	 *  
	 * @param blurReflection <code>true</code> to get a blurred one, <code>false</code> otherwise
	 */
	public void setBlurReflection(boolean blurReflection)
	{
		this.blurReflection = blurReflection;
	}

	/**
	 * Whether or not to paint a shine light reflection on top of the album cover image.
	 * 
	 * @param shinyReflection <code>true</code> to get a shine light reflection, <code>false</code> otherwise
	 */
	public void setShinyReflection(boolean shinyReflection)
	{
		this.shinyReflection = shinyReflection;
	}
	
	/**
	 * Sets the cover size to the given with.
	 * 
	 * @param width the desired with in pixels
	 */
	public void setCoverSize(int width)
	{
		this.coverWidth = width;
		setPreferredSize(new Dimension(width + 20, width * 2));
		repaint();
	}
	
	/**
	 * @return the reference to the main window
	 */
	public MainWindow getWindow()
	{
		return this.mainWindow;
	}
	
	/**
	 * If the big view is enabled, then there is no hover bar.
	 * 
	 * @param b <code>true</code> fr big cover view, <code>false</code> otherwise
	 */
	public void setBigView(boolean b)
	{
		// the big view cover screen shall not have the hoverbar
		if(b == true)
		{
			this.remove(hoverbar);
			this.removeMouseListener(ml);
		}
	}
	
	/**
	 * Sets the name of the artist.
	 * 
	 * @param artist the name of the artist
	 */
	public void setArtistName(String artist)
	{
		this.artistName = artist;
	}
	
	/**
	 * Sets the album title.
	 * 
	 * @param title the title of the album
	 */
	public void setAlbumTitle(String title)
	{
		this.albumTitle = title;
	}

	/**
	 * Sets the album folder.
	 * 
	 * @param albumFolder the path to the album
	 */
	public void setAlbumFolder(String albumFolder)
	{
		this.albumFolder = albumFolder;
	}
}