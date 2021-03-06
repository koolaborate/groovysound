package com.koolaborate.mvc.view.playlistview

import java.awt.AlphaComposite
import java.awt.Dimension
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.RenderingHints
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException

import javax.imageio.ImageIO
import javax.swing.JPanel
import javax.swing.SwingUtilities
import javax.swing.Timer

import org.apache.commons.lang3.StringUtils
import org.jdesktop.swingx.graphics.ReflectionRenderer

import com.koolaborate.mvc.view.bigcover.BigCoverFrame
import com.koolaborate.mvc.view.hoverbar.Hoverbar
import com.koolaborate.mvc.view.hoverbar.HoverbarElement
import com.koolaborate.mvc.view.mainwindow.MainWindow
import com.koolaborate.mvc.view.searchcover.SearchCoverFrame
import com.koolaborate.util.GraphicsUtilities

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
public class CoverPanel extends JPanel{
	private static final long serialVersionUID = 2615441197194463263L
	String defaultCover = "/images/empty.png"
	String coverPath = defaultCover
	String albumTitle, artistName, albumFolder
	BufferedImage cover, cdCase, reflections, stitch
	boolean blurReflection = true
	boolean shinyReflection = true
	
	// an animated hover bar that fades in and out on the top right side of the cover
	Hoverbar hoverbar
	MouseAdapter coverPanelMouseAdapter
	protected static float MAX_ALPHA = 0.9f
	Timer fadeInTimer, fadeOutTimer
	long animStartTime
	int fadeInDuration  = 2000 // fade in animation will take 2 seconds
	int fadeOutDuration = 1500 // fade out animation will take 1.5 seconds
	
	MainWindow mainWindow
	
	int coverWidth = 225
	
	/**
	 * Constructor.
	 * 
	 * @param window reference to the main window
	 */
	public CoverPanel(MainWindow window){
		this.mainWindow = window
		try{
			cover 		= ImageIO.read(getClass().getResource(coverPath))
			cdCase 		= ImageIO.read(getClass().getResource("/images/cd_case.png"))
			reflections = ImageIO.read(getClass().getResource("/images/reflections.png"))
			stitch 		= ImageIO.read(getClass().getResource("/images/stitch.png"))
		} catch(IOException e){
			e.printStackTrace()
		}
		setPreferredSize(new Dimension(coverWidth + 20, 300))
		setLayout(null)
		
		// create the hoverbar which is a holder for elements
		hoverbar = new Hoverbar()
		hoverbar.setAlpha(0.0f) // invisible at first
		
		// the first element is the search
		HoverbarElement view = new HoverbarElement()
		try {
			view.setImage(ImageIO.read(getClass().getResource("/images/loupe_small.png")))
		} catch (IOException e) {
			e.printStackTrace()
		}
		Thread viewAction = new Thread([
			run: {
				SwingUtilities.invokeLater([
					run: {
						new BigCoverFrame(mainWindow)
					}
				] as Runnable)
			}
		] as Runnable)
		view.setActionThread(viewAction)
		hoverbar.addElement(view)
		
		// add a separator
		hoverbar.addSeparator(6)
		
		// the second element is the change icon
		HoverbarElement change = new HoverbarElement()
		try{
			change.setImage(ImageIO.read(getClass().getResource("/images/refresh_icon.png")))
		} catch (IOException e){
			e.printStackTrace()
		}
		
		Thread changeAction = new Thread([
			run: {
				SwingUtilities.invokeLater([
					run: {
						new SearchCoverFrame(mainWindow, artistName, albumTitle)
					}
				] as Runnable)
			}
		] as Runnable)
		change.setActionThread(changeAction)
		hoverbar.addElement(change)
		
		Dimension s = hoverbar.getPreferredSize()
		hoverbar.setBounds(getPreferredSize().width - s.width - 14, 14, s.width, s.height)
		
		add(hoverbar)
		
		// add the fadeIn
		fadeInTimer = new Timer(30, [
			actionPerformed: {
				long currentTime = System.nanoTime() / 1000000
				long totalTime = currentTime - animStartTime
				if(totalTime > fadeInDuration){
					animStartTime = currentTime
				}
				
				float fraction = (float) totalTime / fadeInDuration
				fraction = Math.min(1.0f, fraction)
				float newAlpha = hoverbar.getAlpha() + fraction * MAX_ALPHA
				if(newAlpha >= MAX_ALPHA){
					newAlpha = MAX_ALPHA
					fadeInTimer.stop()
				}
				
				hoverbar.setAlpha(newAlpha)
				hoverbar.repaint()
			}
		] as ActionListener)
		
		// and the fade out
		fadeOutTimer = new Timer(30, [
			actionPerformed: {
				long currentTime = System.nanoTime() / 1000000
				long totalTime = currentTime - animStartTime
				if(totalTime > fadeOutDuration){
					animStartTime = currentTime
				}
				
				float fraction = (float) totalTime / fadeOutDuration
				fraction = Math.min(1.0f, fraction)
				float newAlpha = hoverbar.getAlpha() - fraction * MAX_ALPHA
				if(newAlpha <= 0.0f){
					newAlpha = 0.0f
					fadeOutTimer.stop()
				}
				
				hoverbar.setAlpha(newAlpha)
				hoverbar.repaint()
			}
		] as ActionListener)
		
		hoverbar.addMouseListener([
			mouseEntered: {
				fadeInTimer.stop()
				fadeOutTimer.stop()
				hoverbar.setAlpha(MAX_ALPHA)
				hoverbar.repaint()
			}
		] as MouseAdapter)
		
		addMouseListener(coverPanelMouseAdapter = [
			mouseEntered: {
				fadeIn()
			},
		
			mouseExited: {
				fadeOut()
			}
		] as MouseAdapter)
	}
	
	/**
	 * Fades the hover bar in.
	 */
	private void fadeIn(){
		// first stop the fadeOut timer
		if(!fadeOutTimer.isRunning()) fadeOutTimer.stop()
		
		// then start the fadeIn timer (if not already running)
		if(!fadeInTimer.isRunning()){
			animStartTime = System.nanoTime() / 1000000
			fadeInTimer.start()
		} else {
			fadeInTimer.stop()
		}
	}
	
	/**
	 * Fades the hover bar out.
	 */
	private void fadeOut() {
		// first stop the fadeIn timer
		if(!fadeInTimer.isRunning()) fadeInTimer.stop()
		
		// then start the fadeOut timer (if not already running)
		if(!fadeOutTimer.isRunning()) {
			animStartTime = System.nanoTime() / 1000000
			fadeOutTimer.start()
		} else {
			fadeOutTimer.stop()
		}
	}
	
	/**
	 * Sets the cover path of the album cover image.
	 * 
	 * @param path the path to the image to be set
	 */
	public void setCoverPath(String path)
	{
		coverPath = path
	}
	
	/**
	 * Refreshes the album cover image.
	 */
	public void refreshCover() {
		// if the cover file path is empty, use the default empty image
		if(StringUtils.isEmpty(coverPath)) {
			coverPath = defaultCover
		}

		// create file object to check whether the file is available
		try {
			File coverFile = new File(coverPath)
			if(!coverFile.exists()) {
				mainWindow.currentFolder = albumFolder
				// the default image has to be loaded from the ressources (from the JAR archive)
				cover = ImageIO.read(getClass().getResource(defaultCover))
			} else {
				cover = ImageIO.read(coverFile)
				mainWindow.currentFolder = coverFile.getParent()
			}
		} catch(Exception e) {
			e.printStackTrace()
		}

		// repaint thread safe
		SwingUtilities.invokeLater([
			run: {
				repaint()
			}
		] as Runnable)
	}
	
	@Override
	protected void paintComponent(Graphics g){
		BufferedImage jewelcase = GraphicsUtilities.getInstance().createCompatibleImage(262, 233)
		Graphics2D g2d = jewelcase.createGraphics()
		g2d.clearRect(0, 0, getWidth(), getHeight())
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR)
		g2d.setComposite(AlphaComposite.SrcOver)
		g2d.drawImage(cdCase, 0, 0, 262, 233, null)
		g2d.drawImage(cover, 19, 3, 240, 227, null)
		g2d.drawImage(stitch, 19, 3, 19+6, 259, null)
		if(shinyReflection) g2d.drawImage(reflections, 0, 0, 262, 233, null)
		
		Graphics2D g3 = (Graphics2D) g
		// now create the reflection
		ReflectionRenderer rend = new ReflectionRenderer()
		rend.setBlurEnabled(blurReflection) // the reflection is more realistic with a blur filter applied to it
		BufferedImage reflection = rend.appendReflection(jewelcase)
		g3.drawImage(reflection, 10, 10, coverWidth, (int)(coverWidth * 1.225), null)
	}

	/**
	 * Sets the cover size to the given with.
	 * 
	 * @param width the desired with in pixels
	 */
	public void setCoverSize(int width){
		this.coverWidth = width
		setPreferredSize(new Dimension(width + 20, width * 2))
		repaint()
	}
	
	
	/**
	 * If the big view is enabled, then there is no hover bar.
	 * 
	 * @param b <code>true</code> fr big cover view, <code>false</code> otherwise
	 */
	public void setBigView(boolean b){
		// the big view cover screen shall not have the hoverbar
		if(b == true){
			this.remove(hoverbar)
			this.removeMouseListener(coverPanelMouseAdapter)
		}
	}
	
	
}