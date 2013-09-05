package com.koolaborate.plugin.main;

import java.awt.image.BufferedImage;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;

import javax.imageio.ImageIO;

import com.koolaborate.model.Settings;
import com.koolaborate.mvc.view.mainwindow.MainWindow;

import plug.engine.Plugin;

/***********************************************************************************
 * MainPlugin                                                                      *
 ***********************************************************************************
 * The main application is wrapped within a plugin which is being executed on      *
 * startup. By doing so, the application can be updated using the plugin engine by *
 * Christophe Le Besnerais (@see http://swing-fx.blogspot.com).                    *
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
public class MainPlugin extends Plugin 
{
	private MainWindow window;
	
	public String getName() 
	{
		return "VibrantPlayer";
	}
	
	public String getVersion() 
	{
		return "1.2";
	}
	
	public URI getURI() 
	{
		try 
		{
			return new URI("http", "www.impressive-artworx.de", 
					"/tools/vibrantplayer/plugins/mainplugin.xml", null);
		} 
		catch(URISyntaxException e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	public void init(Object... args) 
	{
		// first argument must be the settings object
		if(args.length == 0) 
			throw new IllegalArgumentException("First parameter (settings) must be given.");
	
		System.out.println("Main plugin started...");
		
		Settings s = (Settings)args[0];
		s.setVersion(getVersion());
		window = new MainWindow(s);
	}
	
	public MainWindow getMainwindow()
	{
		return window;
	}
	
	@Override
	public String getDescription()
	{
		// the standard text is English
		String desc = "Make sure to always have the most current version of VibrantPlayer " +
				"installed on your computer. This ensures that you are able to benefit " +
				"from new features, latest additions and fewer bugs.";
		if(Locale.getDefault().getLanguage().toLowerCase().equals("de"))
		{
			desc = "Laden Sie dieses Update herunter, um VibrantPlayer immer auf dem " +
					"neuesten Stand zu halten. Auf diese Weise verpassen Sie keine neuen " +
					"Features und profitieren von zusätzlichen Inhalten und ausgemärzten Fehlern.";
		}
		//TODO additional languages?
		
		return desc;
	}
	
	@Override
	public BufferedImage getIcon()
	{
		try
		{
			return ImageIO.read(getClass().getResource("/images/headphones.png"));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
}