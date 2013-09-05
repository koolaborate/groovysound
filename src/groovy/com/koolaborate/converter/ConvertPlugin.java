package com.koolaborate.converter;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Locale;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;

import org.apache.commons.lang3.StringUtils;

import com.koolaborate.mvc.view.mainwindow.MainWindow.NAVIGATION;
import com.koolaborate.mvc.view.navigation.SubNavButton;
import com.koolaborate.plugin.main.MainPlugin;

import plug.engine.PlugEngine;
import plug.engine.Pluggable;
import plug.engine.Plugin;

/***********************************************************************************
 * ConvertPlugin                                                                   *
 ***********************************************************************************
 * A plugin to convert mp3 files to wav files. These wav files can then be burned  *
 * onto a 'normal' audio CD. This way this plugin works as the opposite to the CD  *
 * ripper plugin.                                                                  *
 ***********************************************************************************
 * (c) Impressive Artworx, 2k8                                                     * 
 * @author Manuel Kaess                                                            *
 * @version 1.1                                                                    *
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
public class ConvertPlugin extends Plugin 
{
	public String getName()
	{
		String language = Locale.getDefault().getLanguage();
		String name = "MP3 to WAV converter"; // the standard text is English
		if(language.equalsIgnoreCase("de")) name = "MP3 -> WAV Konverter";

		return name;
	}

	public URI getURI()
	{
		try 
		{
			return new URI("http", "www.impressive-artworx.de", "/tools/vibrantplayer/plugins/mp3towavplugin.xml", null);
		} 
		catch(URISyntaxException e)
		{
			e.printStackTrace();
			return null;
		}
	}

	public String getVersion()
	{
		return "1.01";
	}
	
	@Override
	public String getDescription()
	{
		String language = Locale.getDefault().getLanguage();
		String desc = "This plugin enables you to convert your albums in your music library to WAV files which can then be burned as 'normal' audio CDs."; // the standard text is English
		if(language.equalsIgnoreCase("de")) desc = "Mit diesem Plugin können Sie Alben aus Ihrer Musikbibliothek in WAV-Dateien umwandeln und diese danach als 'normale' Audio-CD brennen.";

		return desc;
	}

	public void init(Object... arg0)
	{
		System.out.println("Starting MP3->WAV converter plugin...");
		
		List<Pluggable> allPluggables = PlugEngine.getInstance().getAllPluggables();
		for(Pluggable plugin : allPluggables)
		{
			if(plugin instanceof MainPlugin)
			{
				final MainPlugin m = (MainPlugin) plugin;
			
				// create a sub navigation button for the playlist view which enables the user to convert the album to WAV
				SubNavButton convertButton = new SubNavButton(){
					@Override
					protected void paintComponent(Graphics g)
					{
						Graphics2D g2d = (Graphics2D) g;
						g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
						
						int w = getWidth();
						int h = getHeight();
						
						// draw a nice border if the mouse cursor if over the button
						if(isMouseOver())
						{
							Color c1 = Color.GRAY.darker();
							Color c2 = Color.GRAY.brighter();
							
							g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
							g2d.setColor(c1);
							g2d.drawRoundRect(0, 0, w-1, h-1, 8, 8);
							g2d.setColor(c2);
							g2d.drawRoundRect(1, 1, w-2, h-2, 8, 8);
							
							// draw gradient in the top
							g2d.setClip(new RoundRectangle2D.Double(1, 1, w-2, h-2, 8, 8));
							
							Paint paint = g2d.getPaint();
							
							GradientPaint painter = new GradientPaint(0.0f, 0.0f,
									Color.GRAY.brighter(),
				                    0.0f, h / 2.0f,
				                    new Color(1.0f, 1.0f, 1.0f, 0.0f));
							g2d.setPaint(painter);
							g2d.fillRect(0, 0, w, 20);

							g2d.setPaint(paint);
						}
						
						// draw the icon (if not null)
						if(getIcon() != null) 
						{
							BufferedImage ico = getIcon();
							int icoH = ico.getHeight();
							int icoW = ico.getWidth();
							// draw the icon centered
							g2d.drawImage(ico, (int)(w - icoW) / 2, (int)(h - icoH) / 2, null);
						}
					}
				};
				convertButton.setText("mp3towav");
				convertButton.setIcon(getIcon());
				
				String language = Locale.getDefault().getLanguage();
				String tooltip = "Convert this album to wav format for being able to burn it as an audio CD afterwards"; // the standard tooltip text is English
				if(language.equalsIgnoreCase("de")) tooltip = "Wandeln Sie dieses Album ins WAV-Format, um es danach als Audio-CD brennen zu können";
				convertButton.setToolTipText(tooltip);
				
				convertButton.addMouseListener(new MouseAdapter(){
					@Override
					public void mouseClicked(MouseEvent e)
					{
						String srcFolder = m.getMainwindow().getCurrentFolderPath();
						if(!StringUtils.isEmpty(srcFolder))
						{
							// open a directory choose dialog for selecting the destination folder
							final JFileChooser chooser = new JFileChooser(m.getMainwindow().getSettings().getLastFolder());
							chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
							int state = chooser.showOpenDialog(null);
							if(state == JFileChooser.APPROVE_OPTION)
							{
								if(chooser.getSelectedFile() != null)
								{
									File dstFolder = chooser.getSelectedFile();
									m.getMainwindow().getSettings().setLastFolder(dstFolder.getParent());
									MP3ToWAVConverter converter = new MP3ToWAVConverter(srcFolder, dstFolder.getAbsolutePath());
									converter.convertFiles();
								}
							}
						}
					}
				});
				
				// HOTFIX: trick, since the main window needs more time to set the center panel up...
				if(m.getMainwindow().getCenterPanel() == null)
				{
					// check every second if the main window is loaded or not
					int timeOut = 20000; // stop trying after 20 seconds
					int waited = 0;
					while(waited < timeOut)
					{
						try
						{
							// wait another second for the system to set up the main window
							Thread.sleep(1000); 
							if(m.getMainwindow().getCenterPanel() != null)
							{
								// window is visible now -> end waiting
								waited = timeOut;
							}
						}
						catch(InterruptedException ie){}
						waited += 1000;
					}
				}
				
				m.getMainwindow().getCenterPanel().getNavigationPanel().addSubNavButton(NAVIGATION.PLAYLIST, convertButton);
				break;
			}
		}
	}
	
	@Override
	public BufferedImage getIcon()
	{
		BufferedImage img = null;
		try
		{
			String dir = PlugEngine.getInstance().getDirectory(this).getPath();
			File imageFile = new File(dir + File.separator + "convertico.png");
			img = ImageIO.read(imageFile);
		}
		catch (MalformedURLException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return img;
	}
}