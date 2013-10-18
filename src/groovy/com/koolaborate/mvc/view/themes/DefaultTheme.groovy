package com.koolaborate.mvc.view.themes

import java.awt.Color
import java.awt.Component
import java.awt.Dimension
import java.awt.FlowLayout
import java.awt.GradientPaint
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.Insets
import java.awt.LinearGradientPaint
import java.awt.Paint
import java.awt.Point
import java.awt.RenderingHints
import java.awt.Shape
import java.awt.MultipleGradientPaint.CycleMethod
import java.awt.geom.AffineTransform
import java.awt.geom.Ellipse2D
import java.awt.geom.GeneralPath
import java.awt.geom.RoundRectangle2D
import java.awt.image.BufferedImage
import java.io.IOException

import javax.imageio.ImageIO
import javax.swing.ImageIcon
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JSlider
import javax.swing.border.Border

import com.koolaborate.mvc.view.navigation.NavButton
import com.koolaborate.mvc.view.navigation.Separator
import com.koolaborate.mvc.view.navigation.SubNavButton
import com.koolaborate.mvc.view.playercontrols.NextButton
import com.koolaborate.mvc.view.playercontrols.PlayButton
import com.koolaborate.mvc.view.playercontrols.PreviousButton
import com.koolaborate.mvc.view.playercontrols.StopButton

/***********************************************************************************
 * DefaultTheme                                                                    *
 ***********************************************************************************
 * This class implements the default theme of the application. Other themes can be *
 * developed using this class as an example. Basically, every JPanel that is being *
 * used throughout the application is defined here with an overwritten             *
 * paintComponent(...) method.                                                     *
 * The default look resembles the Media Player(R).                                 *
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
public class DefaultTheme {
	/**
	 * Creates a side panel which forms a kind of border to the content of the views.
	 * 
	 * @param leftSide whether it is the left side panel or not
	 * @return the newly created side panel
	 */
	public static JPanel createSidePanel(final boolean leftSide){
		final JPanel sidePanel = new JPanel()
		sidePanel.setOpaque(true)
		sidePanel.setPreferredSize(new Dimension(10, Integer.MAX_VALUE))
		sidePanel.setBackground(new Color(99, 108, 135))
		sidePanel.setBorder([
			getBorderInsets: {
				return new Insets(0, 0, 0, 0)
			},
		
			isBorderOpaque: {
				return false
			},
		
//			public void paintBorder(Component c, Graphics g, int arg2, int arg3, int arg4, int arg5)
			paintBorder: { c, graphics ->
				Graphics g = (Graphics) graphics
				g.setColor(Color.BLACK)
				if(leftSide) g.drawLine(9, 0, 9, sidePanel.getHeight())
				else g.drawLine(0, 0, 0, sidePanel.getHeight())
			}
		] as Border)
		return sidePanel
	}
	
	/**
	 * Creates the main background panel that is used in the different views except the TinyView.
	 * 
	 * @return the newly created main panel
	 */
	public static JPanel createMainBackgroundPanel(){
		final Color color1 = new Color(237, 242, 249)//getBackground();
		final Color color2 = new Color(255, 255, 255)//color1.darker();
		
		JPanel mainBackgroundPanel = [
			paintComponent: { g ->
				Graphics2D g2d = (Graphics2D) g

				int w = getWidth()
				int h = getHeight()
				 
				// Paint a gradient from top to bottom
				GradientPaint gp = new GradientPaint(
				    0, 0, color1,
				    0, h, color2)

				g2d.setPaint(gp)
				g2d.fillRect(0, 0, w, h)
			}
		] as JPanel
		
		mainBackgroundPanel.setOpaque(true)
		mainBackgroundPanel.setName("ACTION") // define action region for drag'n'drop
		return mainBackgroundPanel
	}
	
	/**
	 * Creates the background panel for the navigation bar.
	 * 
	 * @return the newly created panel
	 */
	public static JPanel createNavigationBackgroundPanel(){
		final Color black = new Color(13, 15, 19)
		final Color grey = new Color(97, 107, 133)
		
		final JPanel navigationBackgroundPanel = [
			paintComponent: { graphics ->
				Graphics g = (Graphics) graphics
				Graphics2D g2d = (Graphics2D) g
	
				int w = getWidth()
				int h = getHeight()
				
				Paint p = g2d.getPaint()
				
				// Paint a gradient from top to bottom
			    float[] dist = [0.0f, 0.2f, 1.0f]
			    Color[] colors = [black, black, grey]
			    LinearGradientPaint gp = new LinearGradientPaint(new Point(0, 0), new Point(0, getHeight()), dist, colors, CycleMethod.NO_CYCLE)
	
				g2d.setPaint(gp)
				g2d.fillRect(0, 0, w, h)
				
				// Paint a gradient from top to bottom
			    float[] dist2 = [0.0f, 0.5f, 0.51f, 1.0f]
			    Color[] colors2 = [new Color(122, 130, 149), new Color(63, 71, 86), new Color(17, 17, 17), new Color(41, 49, 77)]
			    LinearGradientPaint gp2 = new LinearGradientPaint(new Point(0, 0), new Point(0, getHeight()), dist2, colors2, CycleMethod.NO_CYCLE)
	
				g2d.setPaint(gp2)
				g2d.fillRoundRect(9, 0, w-19, h*2, 5, 5)
				
				g2d.setPaint(p)
				
				g2d.setColor(new Color(122, 130, 149))
				g2d.drawRoundRect(10, 2, w-21, h*2, 5, 5)
				
				g2d.setColor(Color.BLACK)
				g2d.drawRoundRect(9, 0, w-19, h*2, 5, 5)
				
				g2d.drawLine(10, getHeight()-1, getWidth()-10, getHeight()-1)
			}
		] as JPanel
	
		return navigationBackgroundPanel
	}
	
	/**
	 * Creates a new navigation button.
	 * 
	 * @param activeImgLoc the location to the active image
	 * @param inactiveImgLoc the location to the inactive image
	 * @return the created navigation button
	 */
	public static NavButton createNewNavButton(String activeImgLoc, String inactiveImgLoc){
		NavButton b = [
			paintComponent: { graphics ->
				Graphics g = (Graphics) graphics
				Graphics2D g2d = (Graphics2D) g
				
				BufferedImage icon = null
				Color[] colors = new Color[4]
				float[] dist = [0.0f, 0.5f, 0.51f, 1.0f]
				
				Paint p = g2d.getPaint()
				
				// first, draw the background according to mouseover or active status
				if(isMouseOver())
				{
					icon = getActiveImg()
					colors[0] = new Color(131, 176, 255)
					colors[1] = new Color(111, 145, 220)
					colors[2] = new Color(62, 104, 208)
					colors[3] = new Color(90, 190, 228)
				}
				else if(isActive())
				{
					icon = getActiveImg()
					colors[0] = new Color(91, 125, 212)
					colors[1] = new Color(75, 92, 164)
					colors[2] = new Color(13, 36, 130)
					colors[3] = new Color(90, 190, 228)
				}
				else
				{
					icon = getInActiveImg()
					colors[0] = new Color(145, 152, 169)
					colors[1] = new Color(80, 90, 108)
					colors[2] = new Color(23, 27, 39)
					colors[3] = new Color(60, 96, 157)
				}
				
				// now draw the background gradient
				LinearGradientPaint gp = new LinearGradientPaint(new Point(0, 0), new Point(0, getHeight()), dist, colors, 
			    		CycleMethod.NO_CYCLE)
				g2d.setPaint(gp)
				g2d.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 8, 8)
				g2d.setPaint(p)
				
				// then draw the icon (if not null)
				if(icon != null) 
				{
					int icoWidth = icon.getWidth()
					int icoHeight = icon.getHeight()
					g2d.drawImage(icon, (int)(getWidth()/2 - icoWidth/2), (int)(getHeight()/2 - icoHeight / 2), icoWidth, icoHeight, null)
				}
			},
			
			paintBorder: { graphics ->
				Graphics g = (Graphics) graphcis
		        Graphics2D g2 = (Graphics2D) g
		        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
		        g2.setColor(Color.BLACK)
		        g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 8, 8)
		    }
		] as NavButton
		
		try {
			b.setActiveImg(ImageIO.read(DefaultTheme.class.getResource(activeImgLoc)))
			b.setInActiveImg(ImageIO.read(DefaultTheme.class.getResource(inactiveImgLoc)))
		} catch (IOException e) {
			System.out.println("Image not found for navigation button in theme decorator. Requested ressource missing: " + e.getMessage())
		}
		return b
	}
	
	/**
	 * Creates a new sub-navigation button.
	 * 
	 * @param imgLoc the location to the button's image
	 * @return the created sub-navigation button
	 */
	public static SubNavButton createNewSubNavButton(String imgLoc){
		SubNavButton b = [
			paintComponent: { graphics ->
				Graphics g = (Graphics) graphics
				Graphics2D g2d = (Graphics2D) g
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
				
				int w = getWidth()
				int h = getHeight()
				
				// draw a nice border if the mouse cursor if over the button
				if(isMouseOver()){
					Color c1 = Color.GRAY.darker()
					Color c2 = Color.GRAY.brighter()
					
					g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
					g2d.setColor(c1)
					g2d.drawRoundRect(0, 0, w-1, h-1, 8, 8)
					g2d.setColor(c2)
					g2d.drawRoundRect(1, 1, w-2, h-2, 8, 8)
					
					// draw gradient in the top
					g2d.setClip(new RoundRectangle2D.Double(1, 1, w-2, h-2, 8, 8))
					
					Paint paint = g2d.getPaint()
					
					GradientPaint painter = new GradientPaint(0.0f, 0.0f,
							Color.GRAY.brighter(),
		                    0.0f, h / 2.0f,
		                    new Color(1.0f, 1.0f, 1.0f, 0.0f))
					g2d.setPaint(painter)
					g2d.fillRect(0, 0, w, 20)

					g2d.setPaint(paint)
				}
				
				// draw the icon (if not null)
				if(getIcon() != null) {
					BufferedImage ico = getIcon()
					int icoH = ico.getHeight()
					int icoW = ico.getWidth()
					// draw the icon centered
					g2d.drawImage(ico, (int)(w - icoW) / 2, (int)(h - icoH) / 2, null)
				}
			}
		] as SubNavButton
	
		try {
			b.setIcon(ImageIO.read(DefaultTheme.class.getResource(imgLoc)))
		} catch (IOException e) {
			System.out.println("Image not found for sub-navigation button in theme decorator. Requested ressource missing: " + e.getMessage())
		}
		return b
	}
	
	/**
	 * Creates a new separator for the navigation bar.
	 * 
	 * @return the newly created separator
	 */
	public static Separator createNavigationSeparator(){
		final Separator subNavSeparator = new Separator()
		subNavSeparator.setPreferredSize(new Dimension(3, 36))
		subNavSeparator.setSize(new Dimension(3, 36))
		subNavSeparator.setMinimumSize(new Dimension(3, 36))
		subNavSeparator.setMaximumSize(new Dimension(3, 36))
		JLabel separatorContent = [
			paintComponent: { graphics ->
				Graphics g = (Graphics) graphics
				g.setColor(Color.BLACK)
//				g.drawLine(0, 0, 0, subNavSeparator.getHeight());
				g.drawLine(0, 0, 0, 36)
				g.setColor(Color.GRAY.darker())
//				g.drawLine(1, 1, 1, subNavSeparator.getHeight()-1);
				g.drawLine(0, 0, 0, 35)
			}
		] as JLabel
	
		subNavSeparator.setOpaque(false)
		separatorContent.setOpaque(false)
		subNavSeparator.setContent(separatorContent)
		return subNavSeparator
	}

	public static JLabel createSubNavSeparatorLabel(){
		JLabel separatorContent = [
			paintComponent: { graphics ->
				Graphics g = (Graphics) graphics
				g.setColor(Color.BLACK)
//				g.drawLine(0, 0, 0, subNavSeparator.getHeight());
				g.drawLine(0, 0, 0, 36)
				g.setColor(Color.GRAY.darker())
//				g.drawLine(1, 1, 1, subNavSeparator.getHeight()-1);
				g.drawLine(0, 0, 0, 35)
			}
		] as JLabel
	
		separatorContent.setPreferredSize(new Dimension(3, 36))
		separatorContent.setSize(new Dimension(3, 36))
		separatorContent.setMinimumSize(new Dimension(3, 36))
		separatorContent.setMaximumSize(new Dimension(3, 36))
		separatorContent.setOpaque(false)
		return separatorContent
	}
	
	public static JPanel createHeaderPanel()
	{
		final Color c1 = new Color(18, 18, 18)
		final Color c2 = new Color(66, 76, 116)
		
		JPanel header = [
			paintComponent: { graphics ->
				Graphics g = graphics
				Graphics2D g2d = (Graphics2D) g
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
				
				GradientPaint gp = new GradientPaint(0, 0, c2, 0, 65, c1)
				g2d.setPaint(gp)
				g2d.fillRect(0, 0, getWidth(), getHeight() + 8)
			}
		] as JPanel
		header.setOpaque(false)
		return header
	}

	/**
	 * Creates the little panel at the very bottom of the window.
	 * 
	 * @return the newly created panel
	 */
	public static JPanel createBottomPanel(){
		final Color c1 = new Color(18, 18, 18)
		final Color c2 = new Color(66, 76, 116)
		
		JPanel bottomPanel = [
			paintComponent: { graphics ->
				Graphics g = graphics
				Graphics2D g2d = (Graphics2D) g

				int w = getWidth()
				int h = getHeight()
				 
				// Paint a gradient from top to bottom
			    GradientPaint gp = new GradientPaint(0, 0, c1, 0, h, c2)
				g2d.setPaint(gp)
				g2d.fillRect(0, 0, w, h)
			}
		] as JPanel
		return bottomPanel
	}

	public static JPanel createTinyViewBgPanel()
	{
		final Color c1 = Color.BLACK
		final Color c2 = Color.GRAY
		
		JPanel bg = [
			paintComponent: { graphics ->
				Graphics g = graphics
				Graphics2D g2d = (Graphics2D) g
				
				int w = getWidth()
				int h = getHeight()
				
				float[] fractions = [0.0f, 0.3f, 1.0f]
				Color[] colors = [c1, c1, c2]
				
				// Paint a gradient from top to bottom
				LinearGradientPaint gp = new LinearGradientPaint(new Point(0, 0), new Point(0, h), fractions, colors)
				
				g2d.setPaint(gp)
				g2d.fillRect(0, 0, w, h)
			}
		] as JPanel
	
		return bg
	}

	public static JPanel createPlaybackControlsPanel(PlayButton playButt, PreviousButton prevButt, NextButton nextButt, StopButton stopButt, JSlider volumeSlider, JLabel softLabel, JLabel loudLabel){
		JPanel playbackControlsPanel = [
			paintComponent: { graphics ->
				Graphics g = graphics
				Graphics2D g2d = (Graphics2D) g
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

				// first the upper half
				g2d.setColor(Color.BLACK)
				g2d.setClip(0, 0, getWidth(), (int)(getHeight()/2)+1)
				g2d.drawRoundRect(0, 10, getWidth()-10, getHeight()-20, 30, 40)
				// then the lower half
				g2d.setColor(new Color(65, 65, 65))
				g2d.setClip(0, (int)(getHeight()/2)+1, getWidth(), getHeight())
				g2d.drawRoundRect(0, 10, getWidth()-10, getHeight()-20, 30, 40)
				
				g2d.setClip(0, 0, getWidth(), getHeight())
			}
		] as JPanel
		
		playbackControlsPanel.setOpaque(false)
		playbackControlsPanel.setPreferredSize(new Dimension(350, 50))
		playbackControlsPanel.setLayout(null)
		
		// add the buttons...
		int beginIndex = 25
		
		// previous song button
		Dimension buttDim = prevButt.getPreferredSize()
		int prevButtIndex = beginIndex
		prevButt.setBounds(prevButtIndex, 15, buttDim.width, buttDim.height)
		
		// play button
		buttDim = playButt.getPreferredSize()
		int playButtIndex = prevButtIndex + 26
		playButt.setBounds(playButtIndex, 5, buttDim.width, buttDim.height)
		
		// stop button
		buttDim = stopButt.getPreferredSize()
		int stopButtIndex = playButtIndex + 39
		stopButt.setBounds(stopButtIndex, 15, buttDim.width, buttDim.height)
		
		// next song button
		buttDim = nextButt.getPreferredSize()
		int nextButtIndex = stopButtIndex + 24
		nextButt.setBounds(nextButtIndex, 15, buttDim.width, buttDim.height)
		
		// volume slider
		volumeSlider.setOpaque(false)
		
		// loudness labels
		int gap = 40
		int softnessStart = nextButtIndex + 24 + gap
		softLabel.setBounds(softnessStart, 19, 13, 13)
		
		int sliderStart = softnessStart + 17
		volumeSlider.setBounds(sliderStart, 17, 90, 16)
		
		int loudnessStart = sliderStart + 96
		loudLabel.setBounds(loudnessStart, 19, 13, 13)
		
		playbackControlsPanel.add(prevButt)
		playbackControlsPanel.add(playButt)
		playbackControlsPanel.add(stopButt)
		playbackControlsPanel.add(nextButt)
		playbackControlsPanel.add(softLabel)
		playbackControlsPanel.add(volumeSlider)
		playbackControlsPanel.add(loudLabel)
		
		return playbackControlsPanel
	}

	public static PlayButton createPlaybackPlayButton(){
		// play song button
		try{
			def activePlayButton = ImageIO.read(DefaultTheme.class.getResource("/images/playbutton_active.png"))
			def inactivePlayButton = ImageIO.read(DefaultTheme.class.getResource("/images/playbutton_inactive.png"))
			def pauseButton = ImageIO.read(DefaultTheme.class.getResource("/images/pausebutton.png"))
			// check intellisense
			final PlayButton playButton = new PlayButton(activeImg: activePlayButton, inActiveImg: inactivePlayButton, pauseImg: pauseButton){
				private static final long serialVersionUID = 7523674211790241350L

				@Override
				protected void paintComponent(Graphics g){
					initShape()
					Graphics2D g2d = (Graphics2D) g
					g2d.setClip(getShape())
					if(isPressed()){
						g2d.drawImage(getPauseImg(), 0, 0, getActiveImg().getWidth(), getActiveImg().getHeight(), null)
					} else if(isActive()){
						g2d.drawImage(getActiveImg(), 0, 0, getActiveImg().getWidth(), getActiveImg().getHeight(), null)
					} else {
						g2d.drawImage(getInActiveImg(), 0, 0, getActiveImg().getWidth(), getActiveImg().getHeight(), null)
					}
				}
				
				@Override
			    public boolean contains(int x, int y) 
				{
			        initShape()
			        return getShape().contains(x, y)
			    }
				
				@Override
				protected void paintBorder(Graphics g)
				{
					initShape()
					Graphics2D g2 = (Graphics2D)g
					g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
					g2.setColor(getBackground())
					//g2.setStroke(new BasicStroke(1.0f));
					g2.draw(getShape())
//				        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
				}
			}
			
			
			Shape base = playButton.getBounds()
			playButton.setBase(base)
			
			Dimension s = playButton.getPreferredSize()
			Shape shape = new Ellipse2D.Float(0, 0, s.width-1, s.height-1)
			playButton.setShape(shape)
			
			return playButton
		} catch(Exception e) {
			e.printStackTrace()
		}
		
		return null
	}

	public static NextButton createPlaybackNextButton()
	{
		// next song button
		try
		{
			final NextButton nextButt = new NextButton(activeImg: ImageIO.read(DefaultTheme.class.getResource("/images/nextbutton_active.png")),
					inActiveImg: ImageIO.read(DefaultTheme.class.getResource("/images/nextbutton_inactive.png"))){
						private static final long serialVersionUID = 2471068278887035929L

				@Override
				protected void paintComponent(Graphics g)
				{
					initShape()
					Graphics2D g2d = (Graphics2D) g
					g2d.setClip(getShape())
					if(isActive())
					{
						g2d.drawImage(getActiveImg(), 0, 0, null)
					}
					else
					{
						g2d.drawImage(getInActiveImg(), 0, 0, null)
					}
				}
				
				@Override
			    public boolean contains(int x, int y) 
				{
			        initShape()
			        return getShape().contains(x, y)
			    }
				
				@Override
			    protected void paintBorder(Graphics g)
			    {
			        initShape()
			        Graphics2D g2 = (Graphics2D)g
			        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
			        g2.setColor(getBackground())
			        g2.draw(getShape())
			    }
			}
			
			
			Shape base = nextButt.getBounds()
			nextButt.setBase(base)
			
			// coordinates
			int centerY = 11
			int startLeftX = 1
			int endRightX = 21
			
			GeneralPath myShape = new GeneralPath(GeneralPath.WIND_EVEN_ODD)
			// upper side
			myShape.moveTo(startLeftX, 0)
			myShape.lineTo(endRightX, 0)
			// right arc
			float quadPointX = 30
			float quadPointY = centerY
			myShape.quadTo(quadPointX, quadPointY, endRightX, endRightX) 
			// lower side
			myShape.lineTo(startLeftX, endRightX)
			// left arc
			quadPointX = 6
			quadPointY = centerY
			myShape.quadTo(quadPointX, quadPointY, startLeftX, 0) 
			
			myShape.closePath()
			
			// transformation into a shape
			AffineTransform af = new AffineTransform()
			Shape shape = myShape.createTransformedShape(af)
			nextButt.setShape(shape)
			
			return nextButt
		}
		catch(Exception e)
		{
			e.printStackTrace()
		}
		
		return null
	}

	public static PreviousButton createPlaybackPrevButton()
	{
		// previous song button
		try
		{
			final PreviousButton prevButt = new PreviousButton(activeImg: ImageIO.read(DefaultTheme.class.getResource("/images/prevbutton_active.png")),
					inActiveImg: ImageIO.read(DefaultTheme.class.getResource("/images/prevbutton_inactive.png"))){
						private static final long serialVersionUID = -6491422973648419664L

				@Override
				protected void paintComponent(Graphics g)
				{
					initShape()
					Graphics2D g2d = (Graphics2D) g
					g2d.setClip(getShape())
					if(isActive())
					{
						g2d.drawImage(getActiveImg(), 0, 0, null)
					}
					else
					{
						g2d.drawImage(getInActiveImg(), 0, 0, null)
					}
				}
				
				@Override
			    public boolean contains(int x, int y) 
				{
			        initShape()
			        return getShape().contains(x, y)
			    }
				
				@Override
			    protected void paintBorder(Graphics g)
			    {
			        initShape()
			        Graphics2D g2 = (Graphics2D)g
			        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
			        g2.setColor(getBackground())
			        g2.draw(getShape())
			    }
			}
			
			
			Shape base = prevButt.getBounds()
			prevButt.setBase(base)
			
			// coordinates
			int rightEndX = 26
			int centerY = 11
			int startLeftX = 5
			int endLowerY = 21
			
			GeneralPath myShape = new GeneralPath(GeneralPath.WIND_EVEN_ODD)
			// upper side
			myShape.moveTo(startLeftX, 0)
			myShape.lineTo(rightEndX, 0)
			// right arc
			float quadPointX = 20
			float quadPointY = centerY
			myShape.quadTo(quadPointX, quadPointY, rightEndX, endLowerY) 
			// lower side
			myShape.lineTo(startLeftX, endLowerY)
			// left arc
			quadPointX = -4
			quadPointY = centerY
			myShape.quadTo(quadPointX, quadPointY, startLeftX, 0) 
			
			myShape.closePath()
			
			// transformation into a shape
			AffineTransform af = new AffineTransform()
			Shape shape = myShape.createTransformedShape(af)
			prevButt.setShape(shape)
			
			return prevButt
		}
		catch(Exception e)
		{
			e.printStackTrace()
		}
		
		return null
	}

	public static StopButton createPlaybackStopButton(){
		// stop song button
		try{
			def activeStopButton = ImageIO.read(DefaultTheme.class.getResource("/images/stopbutton_active.png"))
			def inactiveStopButton = ImageIO.read(DefaultTheme.class.getResource("/images/stopbutton_inactive.png"))
			final StopButton stopButton = new DefaultThemeStopButton(activeStopButton, inactiveStopButton)
			
			Shape base = stopButton.getBounds()
			stopButton.setBase(base)
			
			// coordinates
			int centerY = 11
			int startLeftX = 1
			int endRightX = 21
			
			GeneralPath myShape = new GeneralPath(GeneralPath.WIND_EVEN_ODD)
			// upper side
			myShape.moveTo(startLeftX, 0)
			myShape.lineTo(endRightX, 0)
			// right arc
			float quadPointX = 30
			float quadPointY = centerY
			myShape.quadTo(quadPointX, quadPointY, endRightX, endRightX) 
			// lower side
			myShape.lineTo(startLeftX, endRightX)
			// left arc
			quadPointX = 6
			quadPointY = centerY
			myShape.quadTo(quadPointX, quadPointY, startLeftX, 0) 
			
			myShape.closePath()
			
			// transformation into a shape
			AffineTransform af = new AffineTransform()
			Shape shape = myShape.createTransformedShape(af)
			stopButton.setShape(shape)
			
			return stopButton
		} catch(Exception e) {
			e.printStackTrace()
		}
		
		return null
	}

	public static JSlider createVolumeSlider(){
		return new JSlider()
	}

	public static JLabel createLoudVolumeLabel(){
		JLabel loudLabel = new JLabel(new ImageIcon(DefaultTheme.class.getResource("/images/loud.png")))
		return loudLabel
	}

	public static JLabel createSoftVolumeLabel(){
		JLabel softLabel = new JLabel(new ImageIcon(DefaultTheme.class.getResource("/images/soft.png")))
		return softLabel
	}

	public static JPanel createPlayerControlsPanel(){
		JPanel p = [
			paintComponent: { graphics ->
				Graphics g = graphics
				if(!isOpaque()){
			        super.paintComponent(g)
			        return
			    }

				Graphics2D g2d = (Graphics2D) g

				int w = getWidth()
				int h = getHeight()
				 
				// Paint a gradient from top to bottom
//				Point2D start = new Point2D.Float(0, 0);
//			    Point2D end = new Point2D.Float(50, 50);
			    float[] dist = [0.0f, 0.499f, 0.5f, 1.0f]
			    Color[] colors = [new Color(99, 108, 135), new Color(56, 62, 75), new Color(21, 21, 22), new Color(18, 18, 18)]
			    LinearGradientPaint gp = new LinearGradientPaint(new Point(0, 0), new Point(0, getHeight()), dist, colors, CycleMethod.NO_CYCLE)
			    
			    Paint p = g2d.getPaint()
				g2d.setPaint(gp)
				g2d.fillRect(0, 0, w, h)
				
				g2d.setPaint(p)
				g2d.setColor(Color.BLACK)
				g2d.drawLine(10, 0, getWidth()-10, 0)
				g2d.setColor(new Color(99, 108, 135))
				g2d.fillRect(9, 1, getWidth()-20, 2)
			}
		] as JPanel
	
		p.setLayout(new FlowLayout())
		return p
	}
}


class DefaultThemeStopButton extends StopButton{
	private static final long serialVersionUID = -6107597720059198702L

	DefaultThemeStopButton(BufferedImage activeStopButton, BufferedImage inactiveStopButton){
		super(activeStopButton, inactiveStopButton)
	}
	
	@Override
	protected void paintComponent(Graphics g){
		initShape()
		Graphics2D g2d = (Graphics2D) g
		g2d.setClip(getShape())
		if(isActive()){
			g2d.drawImage(getActiveImg(), 0, 0, null)
		} else {
			g2d.drawImage(getInActiveImg(), 0, 0, null)
		}
	}

	@Override
	public boolean contains(int x, int y){
		initShape()
		return getShape().contains(x, y)
	}

	@Override
	protected void paintBorder(Graphics g){
		initShape()
		Graphics2D g2 = (Graphics2D)g
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
		g2.setColor(getBackground())
		g2.draw(getShape())
	}
}





