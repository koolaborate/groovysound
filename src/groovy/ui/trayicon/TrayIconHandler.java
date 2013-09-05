package ui.trayicon;

import helper.GraphicsUtilities;
import helper.LocaleMessage;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import ui.mainwindow.MainWindow;

/***********************************************************************************
 * TrayIconHandler *
 *********************************************************************************** 
 * A class to add an icon to the system's tray bar. The icon offers a popup
 * window * with controls for the player. The tool tip text of the icon are the
 * name of the * artist and song that is currently playing. *
 *********************************************************************************** 
 * (c) Impressive Artworx, 2k9 *
 * 
 * @author Manuel Kaess *
 * @version 1.2 *
 *********************************************************************************** 
 *          This file is part of VibrantPlayer. * * VibrantPlayer is free
 *          software: you can redistribute it and/or modify * it under the terms
 *          of the Lesser GNU General Public License as published by * the Free
 *          Software Foundation, either version 3 of the License, or * (at your
 *          option) any later version. * * VibrantPlayer is distributed in the
 *          hope that it will be useful, * but WITHOUT ANY WARRANTY; without
 *          even the implied warranty of * MERCHANTABILITY or FITNESS FOR A
 *          PARTICULAR PURPOSE. See the Lesser * GNU General Public License for
 *          more details. * * You should have received a copy of the Lesser GNU
 *          General Public License * along with VibrantPlayer. If not, see
 *          <http://www.gnu.org/licenses/>. *
 ***********************************************************************************/
public class TrayIconHandler{
	private static Logger log = Logger.getLogger(TrayIconHandler.class.getName());

	// using reflection since this class may not be present to a JRE < 1.6_10...
	private Class<?> trayClass = null;
	private Class<?> trayIcoClass = null;

	// necessary variable that indicate whether or not tray icon functionalities
	// are
	// available on the machine
	private boolean supported = false;

	private BufferedImage stdIco;

	private String currentCoverPath;
	private int icoWidth = -1, icoHeight = -1;

	/**
	 * Constructor.
	 * 
	 * @param window
	 *            reference to the main window
	 */
	public TrayIconHandler(final MainWindow window){
		currentCoverPath = System.getProperty("user.dir") + File.separator
				+ "images" + File.separator + "headphones16.gif";

		try {
			stdIco = ImageIO.read(getClass().getResource(
					"/images/headphones16.gif"));
		} catch(IOException e) {
			log.warn("Standard tray icon not found: " + e.getMessage());
		}

		supported = initReflectionClasses();

		if(supported) {
			try {
				Method getTrayMethod = trayClass.getMethod("getSystemTray");
				Object trayObject = (Object) getTrayMethod.invoke(null);

				TrayIconListener listener = new TrayIconListener(window);

				PopupMenu popup = new PopupMenu();
				MenuItem playItem = new MenuItem(
						LocaleMessage.getString("control.play"));
				playItem.setActionCommand("play");
				MenuItem stopItem = new MenuItem(
						LocaleMessage.getString("control.stop"));
				stopItem.setActionCommand("stop");
				MenuItem nextItem = new MenuItem(
						LocaleMessage.getString("control.next"));
				nextItem.setActionCommand("next");
				MenuItem prevItem = new MenuItem(
						LocaleMessage.getString("control.previous"));
				prevItem.setActionCommand("previous");
				MenuItem exitItem = new MenuItem(
						LocaleMessage.getString("control.exit"));
				exitItem.setActionCommand("exit");

				playItem.addActionListener(listener);
				stopItem.addActionListener(listener);
				nextItem.addActionListener(listener);
				prevItem.addActionListener(listener);
				exitItem.addActionListener(listener);

				popup.add(playItem);
				popup.add(stopItem);
				popup.add(nextItem);
				popup.add(prevItem);
				popup.add(exitItem);

				Constructor<?> c = trayIcoClass.getConstructor(Image.class,
						String.class, PopupMenu.class);
				Object trayIcoObject = c.newInstance(stdIco, "VibrantPlayer",
						popup);

				Method setAutoSizeMethod = trayIcoClass.getMethod(
						"setImageAutoSize", boolean.class);
				setAutoSizeMethod.invoke(trayIcoObject, true);

				Method addMethod = trayClass.getMethod("add",
						trayIcoObject.getClass());
				addMethod.invoke(trayObject, trayIcoObject);
			} catch(Exception e) {
				log.debug(e.getMessage());
				e.printStackTrace();
				supported = false;
			}
		}
	}

	/**
	 * Changes the shown tray icon to the given one. If no image is given, the
	 * standard image is shown.
	 * 
	 * @param img
	 *            the image to be shown, may be <code>null</code>
	 */
	public void showAlbumImageFromPath(String imgPath){
		if(StringUtils.isEmpty(imgPath)) showAlbumImage(null);

		try {
			// only change image if necessary
			if(!currentCoverPath.equals(imgPath)) {
				currentCoverPath = imgPath;

				File imgFile = new File(imgPath);
				if(!imgFile.exists()) return;

				// determine max possible ico size (is only done once)
				if(icoWidth < 0 || icoHeight < 0) {
					Method getTrayMethod = trayClass.getMethod("getSystemTray");
					Object trayObject = (Object) getTrayMethod.invoke(null);

					Method getTrayIconsMethod = trayClass.getMethod("getTrayIcons");
					Object[] trayIcons = (Object[]) getTrayIconsMethod.invoke(trayObject);
					Object trayIco = trayIcons[0];

					if(trayIco != null) {
						Method getSizeMethod = trayIcoClass.getMethod("getSize");
						Dimension dim = (Dimension) getSizeMethod.invoke(trayIco);
						icoWidth = dim.width;
						icoHeight = dim.height;
					}
				}

				BufferedImage ico = ImageIO.read(imgFile);
				BufferedImage icoSmall = GraphicsUtilities.createThumbnailFast(
						ico, 16, 16);
				BufferedImage icoSmallBorder = new BufferedImage(icoWidth,
						icoHeight, BufferedImage.TYPE_INT_ARGB);
				Graphics g = icoSmallBorder.getGraphics();
				g.drawImage(icoSmall, 0, 0, null);
				g.setColor(Color.BLACK);
				g.drawRect(0, 0, icoWidth - 1, icoHeight - 1);
				showAlbumImage(icoSmallBorder);
			}
		} catch(Exception e) {
			log.debug(e.getMessage());
			e.printStackTrace();
			supported = false;
		}
	}

	/**
	 * Changes the shown tray icon to the given one. If no image is given, the
	 * standard image is shown.
	 * 
	 * @param img
	 *            the image to be shown, may be <code>null</code>
	 */
	public void showAlbumImage(Image img){
		if(img == null) img = stdIco;

		try {
			Method getTrayMethod = trayClass.getMethod("getSystemTray");
			Object trayObject = (Object) getTrayMethod.invoke(null);

			Method getTrayIconsMethod = trayClass.getMethod("getTrayIcons");
			Object[] trayIcons = (Object[]) getTrayIconsMethod.invoke(trayObject);
			Object trayIco = trayIcons[0];

			Method setImageMethod = trayIco.getClass().getMethod("setImage",
					Image.class);
			setImageMethod.invoke(trayIco, img);
		} catch(Exception e) {
			log.debug(e.getMessage());
			e.printStackTrace();
			supported = false;
		}
	}

	/**
	 * Sets the tool tip of the tray icon to display the given text.
	 * 
	 * @param text
	 *            the text to be displayed on mouse over
	 */
	public void setIconToolTip(String text){
		try {
			Method getTrayMethod = trayClass.getMethod("getSystemTray");
			Object trayObject = (Object) getTrayMethod.invoke(null);

			Method getTrayIconsMethod = trayClass.getMethod("getTrayIcons");
			Object[] trayIcons = (Object[]) getTrayIconsMethod.invoke(trayObject);
			Object trayIco = trayIcons[0];

			Method m = trayIco.getClass().getMethod("setToolTip", String.class);
			m.invoke(trayIco, text);
		} catch(Exception e) {
			log.debug(e.getMessage());
			e.printStackTrace();
			supported = false;
		}
	}

	// /**
	// * Shows a baloon-type message.
	// *
	// * @param caption the caption for the baloon-text
	// * @param text the text to be displayed
	// */
	// public void showMessage(String caption, String text)
	// {
	// try
	// {
	// Method getTrayMethod = trayClass.getMethod("getSystemTray");
	// Object trayObject = (Object) getTrayMethod.invoke(null);
	//
	// Method getTrayIconsMethod = trayClass.getMethod("getTrayIcons");
	// Object[] trayIcons = (Object[]) getTrayIconsMethod.invoke(trayObject);
	// Object trayIco = trayIcons[0];
	//
	// Method m = trayIco.getClass().getMethod("displayMessage", String.class,
	// String.class, int.class);
	// m.invoke(trayIco, caption, text, TrayIcon.MessageType.INFO);
	// }
	// catch(Exception e)
	// {
	// log.debug(e.getMessage());
	// e.printStackTrace();
	// supported = false;
	// }
	// }

	/**
	 * Initializes the refection classes and returns whether or not tray icons
	 * are supported on the machine or not.
	 * 
	 * @return whether or not tray icon functionality is available on the
	 *         machine or not
	 */
	private boolean initReflectionClasses(){
		try {
			trayClass = Class.forName("java.awt.SystemTray");
			trayIcoClass = Class.forName("java.awt.TrayIcon");
			Method trayIsSupportedMethod = trayClass.getMethod("isSupported");
			boolean s = (Boolean) trayIsSupportedMethod.invoke(null);
			return s;
		} catch(Exception e) {
			log.debug("Reflection unseccessful: " + e.getMessage());
			e.printStackTrace();
		}
		return false;
	}
}

/**
 * A listener class for the mouse events when selecting an action from the tray
 * icon.
 * 
 * @author Manuel Kaess
 */
class TrayIconListener implements ActionListener{
	private MainWindow window;

	/**
	 * Constructor.
	 * 
	 * @param window
	 *            reference to the main window
	 */
	public TrayIconListener(MainWindow window){
		this.window = window;
	}

	@Override
	public void actionPerformed(ActionEvent e){
		String action = e.getActionCommand();

		if(action.equals("exit")) {
			window.exit();
		} else if(action.equals("next")) {
			window.getPlayerPanel().fadeOut();
			window.getPlayerPanel().playNextSong();
		} else if(action.equals("previous")) {
			window.getPlayerPanel().fadeOut();
			window.getPlayerPanel().playPreviousSong();
		} else if(action.equals("play")) {
			window.getPlayerPanel().playSong();
		} else if(action.equals("pause")) {
			window.getPlayerPanel().pauseSong();
		} else if(action.equals("stop")) {
			window.getPlayerPanel().fadeOut();
			window.getPlayerPanel().stopSong();
		}
	}
}