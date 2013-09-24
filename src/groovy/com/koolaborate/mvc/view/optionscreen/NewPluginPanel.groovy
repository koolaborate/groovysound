package com.koolaborate.mvc.view.optionscreen;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import plug.engine.PlugEngine;
import plug.engine.Pluggable;

import com.jhlabs.image.GrayFilter;
import com.koolaborate.mvc.view.dialogs.VistaDialog;
import com.koolaborate.util.LocaleMessage;

/***********************************************************************************
 * NewPluginPanel                                                                  *
 ***********************************************************************************
 * A panel that represents one entry of the available plugins.                     *
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
public class NewPluginPanel extends JPanel
{
	private static final long serialVersionUID = 822729186528974783L;
	JButton installPlugin;
	JLabel pluginName, description, iconLabel;
	String pluginPath;
	
	/**
	 * Constructor.
	 * 
	 * @param parentWindow reference to the parent dialog window
	 * @param name the name of the plugin
	 * @param desc the description of the plugin
	 * @param ico the path to the icon image of the plugin (will be resized if necessary)
	 * @param path the path to the plugin if the user wants it to be installed
	 */
	public NewPluginPanel(final FindPluginBrowser parentWindow, String name, String desc, 
			String ico, String path)
	{
		pluginPath = path;
		setLayout(new BorderLayout());
		setOpaque(false);
		setPreferredSize(new Dimension(380, 120));
		
		pluginName = new JLabel("<HTML><b>" + name + "</b></HTML>");
		pluginName.setBorder(new EmptyBorder(4, 10, 4, 4));
		description = new JLabel("<HTML><i>" + desc + "</i></HTML>");
		description.setBorder(new EmptyBorder(0, 0, 4, 4));
		
		BufferedImage empty = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
		ImageIcon img = new ImageIcon(empty);
		
		// try to load the specified image ressource from the web
		try
		{
			img = new ImageIcon(new URL(ico));
		}
		catch (MalformedURLException e)
		{
			e.printStackTrace();
		}
		
		iconLabel = new JLabel(img);
		iconLabel.setBorder(new EmptyBorder(0, 4, 4, 8));
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setOpaque(false);
		buttonPanel.setLayout(new FlowLayout());
		
		installPlugin = new JButton(LocaleMessage.getInstance().getString("plugins.install"));
		installPlugin.addActionListener([
			actionPerformed: { actionEvent ->
				ActionEvent e = actionEvent
				Pluggable plugin;
				try{
					URL url = new URL(pluginPath);
					URI uri = url.toURI();
					plugin = PlugEngine.getInstance().installOrUpdate(uri);
					PlugEngine.getInstance().startPluggable(plugin);
				} catch (Exception e1){
					// show a message that the installation failed
					VistaDialog.showDialog(LocaleMessage.getInstance().getString("error.1"), 
							LocaleMessage.getInstance().getString("error.28"),
							LocaleMessage.getInstance().getString("error.29"), VistaDialog.ERROR_MESSAGE);
				}
				parentWindow.dispose();
			}
		] as ActionListener);
		buttonPanel.add(installPlugin);
		
		add(iconLabel, BorderLayout.WEST);
		add(pluginName, BorderLayout.NORTH);
		add(description, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);
	}
	
	/**
	 * Sets the plugin as already installed which means that it cannot be installed again.
	 * 
	 * @param installed whether or not the plugin is already installed
	 */
	public void setAlreadyInstalled(boolean installed){
		installPlugin.setEnabled(!installed);
		if(installed){
			// grayscale image
			Icon origIcon = iconLabel.getIcon();
			int width = origIcon.getIconWidth();
			int height = origIcon.getIconHeight();
			
			BufferedImage b = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			Graphics g = b.getGraphics();
			origIcon.paintIcon(null, g, 0, 0);
			
			BufferedImage dest = new BufferedImage(b.getWidth(), b.getHeight(), BufferedImage.TYPE_INT_ARGB);
			GrayFilter gray = new GrayFilter();
			gray.filter(b, dest);
			iconLabel.setIcon(new ImageIcon(dest));
			
			// draw test gray
			pluginName.setForeground(Color.GRAY);
			description.setForeground(Color.GRAY);
		}
	}
}