package com.koolaborate.mvc.view.themes

import java.awt.BasicStroke
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
import java.awt.Rectangle
import java.awt.RenderingHints
import java.awt.Shape
import java.awt.Stroke
import java.awt.MultipleGradientPaint.CycleMethod
import java.awt.geom.AffineTransform
import java.awt.geom.Area
import java.awt.geom.Ellipse2D
import java.awt.geom.GeneralPath
import java.awt.geom.RoundRectangle2D
import java.awt.image.BufferedImage
import java.awt.image.ConvolveOp
import java.awt.image.Kernel
import java.io.IOException
import java.util.Locale

import javax.imageio.ImageIO
import javax.swing.ImageIcon
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JSlider
import javax.swing.border.Border

import com.jhlabs.image.BrushedMetalFilter
import com.koolaborate.mvc.view.decorations.Decorator
import com.koolaborate.mvc.view.navigation.NavButton
import com.koolaborate.mvc.view.playercontrols.NextButton
import com.koolaborate.mvc.view.playercontrols.PlayButton
import com.koolaborate.mvc.view.playercontrols.PreviousButton
import com.koolaborate.mvc.view.playercontrols.StopButton

/***********************************************************************************
 * MetalTheme                                                                      *
 ***********************************************************************************
 * A brushed metal look and feel for the application. The look resembles Apple(R)  *
 * iTunes(R).                                                                      *
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
public class MetalTheme implements Theme
{
	private Decorator d
	
	/**
	 * Constructor.
	 * 
	 * @param d the decorator object
	 */
	public MetalTheme(Decorator d)
	{
		this.d = d
	}
	
	/**
	 * @return the description of this theme
	 */
	public String getDescription()
	{
		String description = "A brushed metal theme like it is used throughout Apple&reg; MacOS&reg;."
		// if the language on the system is German...
		if(Locale.getDefault().getLanguage().toLowerCase().equals("de"))
		{
			description = "Dieses Thema ähnelt dem Look von Anwendungen unter Apple&reg; MacOS&reg;."
		}
		return description
	}

	/**
	 * @return the name of this theme
	 */
	public String getName()
	{
		String name = "Brushed metal"
		// if the language on the system is German...
		if(Locale.getDefault().getLanguage().toLowerCase().equals("de"))
		{
			name = "Gebürstetes Aluminium"
		}
		return name
	}

	/**
	 * @return a preview image of this theme
	 */
	public BufferedImage getPreviewImage()
	{
		BufferedImage img = null
		try
		{
			img = ImageIO.read(getClass().getResource("/images/metal_theme/preview.jpg"))
		}
		catch (IOException e)
		{
			e.printStackTrace()
		}
		return img
	}

	/**
	 * Sets the theme specific panels and images.
	 */
	public void setThemeSettings()
	{
		/*
		 * BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
		 * image = new BrushedMetalFilter(...).filter(image, image);
		 * The filter has five parameters:
		 *  - Color - the color of the metal.
		 *  - Radius - the "radius" of the blur.
		 *  - Amount - the amount of noise to add (range 0-1).
		 *  - Monochrome - true if the noise should be monochrome.
		 *  - Shine - the amount of shine to add (range 0-1).
		 */
		
		// Selection colors ---------------------------------------------------------------------------------------
		d.setSelectionColor1(new Color(159, 219, 249)) 
		d.setSelectionColor2(new Color(106, 152, 230))
		d.setSelectionAlpha(0.4f)
		
		// The header panel ---------------------------------------------------------------------------------------
		d.setLogoForegroundColor(Color.BLACK)
		
		JPanel headerPanel = new JPanel(){
			private static final long serialVersionUID = 6980744576157405413L

			@Override
			protected void paintComponent(Graphics g)
			{
				BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB)
				image = new BrushedMetalFilter().filter(image, image)
				g.drawImage(image, 0, 0, null)

				// don't paint the arcs in full screen mode 
				if(getWidth() < 600)
				{
					g.setColor(Color.BLACK)
					g.drawArc(0, 0, 13, 13, 90, 90) // top left
					g.drawArc(getWidth()-14, 0, 13, 13, 0, 90) // top right
				}
			}
		}
		d.setHeaderPanel(headerPanel)
		
		// The side panels ----------------------------------------------------------------------------------------
		final JPanel sidePanel = new JPanel(){
			private static final long serialVersionUID = -6682651278505821385L

			@Override
			protected void paintComponent(Graphics g)
			{
				BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB)
				image = new BrushedMetalFilter().filter(image, image)
				g.drawImage(image, 0, 0, null)
			}
		}
		sidePanel.setOpaque(true)
		sidePanel.setPreferredSize(new Dimension(10, Integer.MAX_VALUE))
		sidePanel.setBackground(Color.BLUE)
		sidePanel.setBorder(new Border(){
			public Insets getBorderInsets(Component c)
			{
				return new Insets(0, 0, 0, 0)
			}
			public boolean isBorderOpaque()
			{
				return false
			}
			public void paintBorder(Component c, Graphics g, int arg2, int arg3, int arg4, int arg5)
			{
				g.setColor(Color.BLACK)
				g.drawLine(9, 0, 9, sidePanel.getHeight())
			}
		})
		d.setLeftSidePanel(sidePanel)
		
		final JPanel sidePanel2 = new JPanel(){
			private static final long serialVersionUID = 8451146314106538654L

			@Override
			protected void paintComponent(Graphics g)
			{
				BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB)
				image = new BrushedMetalFilter().filter(image, image)
				g.drawImage(image, 0, 0, null)
			}
		}
		sidePanel2.setOpaque(true)
		sidePanel2.setPreferredSize(new Dimension(10, Integer.MAX_VALUE))
		sidePanel2.setBackground(Color.BLUE)
		sidePanel2.setBorder(new Border(){
			public Insets getBorderInsets(Component c)
			{
				return new Insets(0, 0, 0, 0)
			}
			public boolean isBorderOpaque()
			{
				return false
			}
			public void paintBorder(Component c, Graphics g, int arg2, int arg3, int arg4, int arg5)
			{
				g.setColor(Color.BLACK)
				g.drawLine(0, 0, 0, sidePanel2.getHeight())
			}
		})
		d.setRightSidePanel(sidePanel2)
		
		// The navigation bar --------------------------------------------------------------------------------------
		d.setNavbarForegroundColor(Color.BLACK)
		JPanel navigationBackgroundPanel = new JPanel(){
			private static final long serialVersionUID = 1688867772017187616L

			@Override
			protected void paintComponent(Graphics g){
				Graphics2D g2d = (Graphics2D) g
	
				int w = getWidth()
				int h = getHeight()
				
				Paint p = g2d.getPaint()
	
				// the brushed metal image
				BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB)
				image = new BrushedMetalFilter().filter(image, image)
				g2d.drawImage(image, 0, 0, null)
				
				// Paint a gradient from top to bottom
			    float[] dist = [0.0f, 1.0f]
			    Color[] colors = [new Color(232, 232, 232), new Color(144, 144, 144)]
			    LinearGradientPaint gp = new LinearGradientPaint(new Point(0, 0), new Point(0, getHeight()), dist, colors, CycleMethod.NO_CYCLE)
				g2d.setPaint(gp)
				g2d.fillRoundRect(9, 0, w-19, h*2, 5, 5)
				
				g2d.setPaint(p)
				g2d.setColor(Color.BLACK)
				g2d.drawRoundRect(9, 0, w-19, h*2, 5, 5)
				g2d.drawLine(10, getHeight()-1, getWidth()-10, getHeight()-1)
			}
		}
		d.setNavigationBackgroundPanel(navigationBackgroundPanel)
		
		// navigation buttons
		NavButton b1 = createNewNavButton("/images/albums.png", "/images/albums_inactive.png")
		NavButton b2 = createNewNavButton("/images/navplay.png", "/images/navplay_inactive.png")
		NavButton b3 = createNewNavButton("/images/navsettings.png", "/images/navsettings_inactive.png")
		d.setAlbumsButton(b1)
		d.setPlaylistButton(b2)
		d.setSettingsButton(b3)
		
		// The bottom panel ---------------------------------------------------------------------------------------
		JPanel bottomPanel = new JPanel(){
			private static final long serialVersionUID = 1868630368770200438L

			@Override
			protected void paintComponent(Graphics g)
			{
				BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB)
				image = new BrushedMetalFilter().filter(image, image)
				g.drawImage(image, 0, 0, null)
				
				// don't paint the arcs in full screen mode 
				if(getWidth() < 600)
				{
					g.setColor(Color.BLACK)
					g.drawArc(0, getHeight()-14, 13, 13, 180, 90) // bottom left
					g.drawArc(getWidth()-14, getHeight()-14, 13, 13, 270, 90) // bottom right
				}
			}
		}
		d.setBottomPanel(bottomPanel)
		
		// The player controls panel ------------------------------------------------------------------------------
		JPanel p = new JPanel(){
			private static final long serialVersionUID = 5700019947808087945L

			@Override
			protected void paintComponent(Graphics g)
			{
				BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB)
				image = new BrushedMetalFilter().filter(image, image)
				g.drawImage(image, 0, 0, null)
				
				g.setColor(Color.BLACK)
				g.drawLine(10, 0, getWidth()-10, 0)
			}
		}
		p.setLayout(new FlowLayout())
		d.setPlayerControlsPanel(p)
		
		PlayButton playButt = createPlaybackPlayButton()
		d.setPlayButt(playButt)
		NextButton nextButt = createPlaybackNextButton()
		d.setNextButt(nextButt)
		PreviousButton prevButt = createPlaybackPrevButton()
		d.setPrevButt(prevButt)
		StopButton stopButt = createPlaybackStopButton()
		d.setStopButt(stopButt)
		JSlider volume = new JSlider()
		d.setVolumeSlider(volume)
		JLabel softLabel = new JLabel(new ImageIcon(MetalTheme.class.getResource("/images/metal_theme/soft.gif")))
		JLabel loudLabel = new JLabel(new ImageIcon(MetalTheme.class.getResource("/images/metal_theme/loud.gif")))
		d.setPlaybackControlsPanel(createPlaybackControlsPanel(playButt, prevButt, nextButt, stopButt, 
				volume, softLabel, loudLabel))
	}
	
	
	/**
	 * Creates a new navigation button.
	 * 
	 * @param activeImgLoc the location to the active image
	 * @param inactiveImgLoc the location to the inactive image
	 * @return the created navigation button
	 */
	private static NavButton createNewNavButton(String activeImgLoc, String inactiveImgLoc)
	{
		NavButton b = new NavButton(){
			/**
			 * 
			 */
			private static final long serialVersionUID = -1662580153245273197L

			@Override
			protected void paintComponent(Graphics g)
			{
				BufferedImage icon = null
				
				Color borderColor = new Color(102, 102, 102)
				Stroke borderStroke = new BasicStroke(1.8f)
				
				Color borderHighlightColor = new Color(255, 255, 255, 240)
				Stroke borderHighlightStroke = new BasicStroke(1.25f)
				
				Color fillStartColor = new Color(222, 222, 222)
				Color fillEndColor = new Color(159, 159, 159)
				
				Color selectedColor = new Color(133, 133, 133)
				Color selectedHighlightColor = new Color(selectedColor.getRed(),selectedColor.getGreen(),selectedColor.getBlue(),160)
				
				Graphics2D g2d = (Graphics2D) g
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

				int insets = 3

				int w = getWidth() - (insets * 2)
				int h = getHeight() - (insets * 2)

				int x = insets
				int y = insets
				double arc = (double) h * 0.4d

				// paint button body
				Shape vButtonShape = new RoundRectangle2D.Double((double) x, (double) y, (double) w, (double) h, arc, arc)
				Shape vOldClip = g2d.getClip()
				g2d.setClip(vButtonShape)

				int a4 = (int) (arc / 4)

				if(isActive()) g2d.setColor(selectedColor)
				else g2d.setColor(Color.WHITE)

				g2d.fillRect(x, y, w, h)

				Paint vPaint = new GradientPaint(0, y + a4, fillStartColor, 0, y + h, fillEndColor)
				g2d.setPaint(vPaint)
				g2d.translate(0, a4)
				g2d.fillRoundRect(x+1, y + a4, w-1, h - a4 - 1, (int) arc, (int) arc)

				if(isActive())
				{
					g.setColor(selectedHighlightColor)
					g.drawLine(x+1, y + a4, x+1, h - a4 - 1)
					g.drawLine(x+w-1, y + a4, x+w-1, h - a4 - 1)
				}

				g.translate(0, -1 * a4)

				// then draw the icon (if not null)
				if(isMouseOver() || isActive())	icon = getActiveImg()
				else icon = getInActiveImg()
				if(icon != null) 
				{
					int icoWidth = icon.getWidth()
					int icoHeight = icon.getHeight()
					g2d.drawImage(icon, (int)(getWidth()/2 - icoWidth/2), (int)(getHeight()/2 - icoHeight / 2), icoWidth, icoHeight, null)
				}
				g2d.setClip(vOldClip)
				
				// paint button border
				g2d.setClip(x,y+h-(h/2),w,y+h)
				g2d.setColor(borderHighlightColor)
				g2d.setStroke(borderHighlightStroke)
				g2d.translate(0, 2)
				g2d.draw(vButtonShape)

				g2d.setClip(vOldClip)
				g2d.setColor(borderColor)
				g2d.setStroke(borderStroke)
				g2d.translate(0, -2)
				g2d.draw(vButtonShape)
			}
		}
		try
		{
			b.setActiveImg(ImageIO.read(MetalTheme.class.getResource(activeImgLoc)))
			b.setInActiveImg(ImageIO.read(MetalTheme.class.getResource(inactiveImgLoc)))
		}
		catch (IOException e)
		{
			System.out.println("Image not found for navigation button in theme decorator. Requested ressource missing: " + e.getMessage())
		}
		return b
	}
	
	
	/**
	 * Creates the playback control area with the play button, volume slider etc.
	 * 
	 * @param playButt the play button
	 * @param prevButt the previous song button
	 * @param nextButt the next song button
	 * @param stopButt the stop button
	 * @param volumeSlider the volume slider
	 * @param softLabel the image label for soft volume
	 * @param loudLabel the image label for loud volume
	 * @return the newly created panel
	 */
	private static JPanel createPlaybackControlsPanel(PlayButton playButt, PreviousButton prevButt, NextButton nextButt, 
			StopButton stopButt, JSlider volumeSlider, JLabel softLabel, JLabel loudLabel)
	{
		JPanel playbackControlsPanel = new JPanel()
		
		playbackControlsPanel.setOpaque(false)
		playbackControlsPanel.setPreferredSize(new Dimension(350, 50))
		playbackControlsPanel.setLayout(null)
		
		// add the buttons...
		int beginIndex = 25
		int padding = 5
		
		// previous song button
		Dimension buttDim = prevButt.getPreferredSize()
		int prevButtIndex = beginIndex
		prevButt.setBounds(prevButtIndex, (int)(playbackControlsPanel.getPreferredSize().height/2) - buttDim.height/2, 
				buttDim.width, buttDim.height)
		
		// play button
		int playButtIndex = prevButtIndex + buttDim.width + padding
		buttDim = playButt.getPreferredSize()
		playButt.setBounds(playButtIndex, 5, buttDim.width, buttDim.height)
		
		// stop button
		int stopButtIndex = playButtIndex + buttDim.width + padding
		buttDim = stopButt.getPreferredSize()
		stopButt.setBounds(stopButtIndex, (int)(playbackControlsPanel.getPreferredSize().height/2) - buttDim.height/2,
				buttDim.width, buttDim.height)
		
		// next song button
		int nextButtIndex = stopButtIndex + buttDim.width + padding
		buttDim = nextButt.getPreferredSize()
		nextButt.setBounds(nextButtIndex, (int)(playbackControlsPanel.getPreferredSize().height/2) - buttDim.height/2,
				buttDim.width, buttDim.height)
		
		// volume slider
		volumeSlider.setOpaque(false)
		
		// loudness labels
		int gap = 34
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
	
	private static PlayButton createPlaybackPlayButton()
	{
		// play song button
		float[] BLUR = [0.10f, 0.10f, 0.10f, 0.10f, 0.30f, 0.10f, 0.10f, 0.10f, 0.10f]
		final ConvolveOp blurOp = new ConvolveOp(new Kernel(3, 3, BLUR))

		final Color borderColor = new Color(124, 125, 124)
		final Stroke borderStroke = new BasicStroke(1.5f)

		final Color borderHighlightColor = new Color(255,255,255,240)
		final Stroke borderHighlightStroke = new BasicStroke(1.25f)

		final Color unselectedBevelTopStart = new Color(227, 227, 227)
		final Color unselectedBevelTopEnd = Color.WHITE
		final Color unselectedBevelBottomStart = new Color(179, 177, 177)
		final Color unselectedBevelBottomEnd = Color.WHITE

		final Color selectedBevelTopStart = new Color(106, 152, 230)
		final Color selectedBevelTopEnd = new Color(159, 219, 249)
		final Color selectedBevelBottomStart = new Color(53, 120, 209)
		final Color selectedBevelBottomEnd = new Color(142, 215, 248)

		try
		{
			BufferedImage dummy = new BufferedImage(40, 40, BufferedImage.TYPE_INT_ARGB)
			final PlayButton playButt = new PlayButton(activeImg: dummy, inActiveImg: dummy, pauseImg: dummy){
				/**
				 * 
				 */
				private static final long serialVersionUID = 6576719861952482089L

				@Override
				protected void paintComponent(Graphics g)
				{
					Graphics2D g2d = (Graphics2D)g
					g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

					int insets = 2
					int w = getWidth() - (insets * 2)
					int h = getHeight() - (insets * 2)
					int x = insets
					int y = insets

					// paint button body
					float h2 = h/2
					float h4 = h/4

					GeneralPath path = new GeneralPath()
					path.moveTo(0,h)
					path.curveTo(0,h-h4,h4,h2,h2,h2)
					path.lineTo(w-h2,h2)
					path.curveTo(w-h4,h2,w,h4,w,0)
					path.lineTo(w,h)

					// Create a buffered image to paint the rounded rectangle
					BufferedImage vBuffer = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB)
					Graphics2D bg2d = vBuffer.createGraphics()
					bg2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

					Area area = new Area(new Rectangle(0,0,w,h+1))
					area.subtract(new Area(path))

					Color vStartColor = null
					Color vEndColor = null

					if (isPressed() || isActive())
					{
						vStartColor = selectedBevelTopStart
						vEndColor = selectedBevelTopEnd
					}
					else
					{
						vStartColor = unselectedBevelTopStart
						vEndColor = unselectedBevelTopEnd
					}

					Paint p = new GradientPaint(0f, 0f, vStartColor, 0f, h, vEndColor)
					bg2d.setPaint(p)

					bg2d.fill(area)

					if (isPressed() || isActive())
					{
						vStartColor = selectedBevelBottomStart
						vEndColor = selectedBevelBottomEnd
					}
					else
					{
						vStartColor = unselectedBevelBottomStart
						vEndColor = unselectedBevelBottomEnd
					}

					p = new GradientPaint(0.0F, y, vStartColor, 0.0F,y+h, vEndColor)
					bg2d.setPaint(p)

					bg2d.fill(path)

					bg2d.setStroke(borderStroke)
					bg2d.draw(path)

					// Blur the background
					vBuffer = blurOp.filter(vBuffer, null)

					Shape vOldClip = g.getClip()

					Ellipse2D vNewClip = new Ellipse2D.Double(x, y, w, h)
					g.setClip(vNewClip)
					g.drawImage(vBuffer,x,y,null)

					g.setClip(vOldClip)

					// paint the play triangle or pause stripes
					g2d.setColor(new Color(61, 61, 61))
					int w2 = getWidth()/2
					h2 = getHeight()/2
					if(isPressed())
					{
						int rectWidth = 4
						int rectHeight = 16
						int space = 4
						g2d.fillRect(w2 - space/2 - rectWidth/2, (int) (h2 - rectHeight/2), rectWidth, rectHeight)
						g2d.fillRect(w2 + space/2, (int) (h2 - rectHeight/2), rectWidth, rectHeight)
					}
					else
					{
						int trinagleWidth = 16
						int triangleHeight = 16
						GeneralPath trianglePath = new GeneralPath()
						trianglePath.moveTo(w2-trinagleWidth/2+2, h2-triangleHeight/2)
						trianglePath.lineTo(w2+trinagleWidth/2+2, h2)
						trianglePath.lineTo(w2-trinagleWidth/2+2, h2+triangleHeight/2)
						trianglePath.closePath()
						// transformation into a shape
						AffineTransform af = new AffineTransform()
						Shape triangle = trianglePath.createTransformedShape(af)
						g2d.fill(triangle)
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
					int x = 2
					int y = 2
					
					int w = getWidth() - 2 * x
					int h = getHeight() - 2 * y
					
					Graphics2D g2d = (Graphics2D) g
					Stroke vOldStroke = g2d.getStroke()
					Shape vOldClip = g2d.getClip()

					g2d.setStroke(borderHighlightStroke)
					g2d.setClip(0,h/2,w,h)
					g2d.setColor(borderHighlightColor)
					g2d.drawOval(x,y+1,w,h)

					g2d.setClip(0,0,w,h/2)
					g2d.setColor(borderColor)
					g2d.drawOval(x,y-1,w,h)

					g2d.setStroke(borderStroke)
					g2d.setClip(vOldClip)
					g2d.setColor(borderColor)
					g2d.drawOval(x,y,w,h)

					g2d.setStroke(vOldStroke)
				}
			}
			
			Shape base = playButt.getBounds()
			playButt.setBase(base)
			
			Dimension s = playButt.getPreferredSize()
			Shape shape = new Ellipse2D.Float(0, 0, s.width-1, s.height-1)
			playButt.setShape(shape)
			
			return playButt
		}
		catch(Exception e)
		{
			e.printStackTrace()
		}
		return null
	}

	private static NextButton createPlaybackNextButton()
	{
		// next song button
		float[] BLUR = [0.10f, 0.10f, 0.10f, 0.10f, 0.30f, 0.10f, 0.10f, 0.10f, 0.10f]
		final ConvolveOp blurOp = new ConvolveOp(new Kernel(3, 3, BLUR))

		final Color borderColor = new Color(124, 125, 124)
		final Stroke borderStroke = new BasicStroke(1.5f)

		final Color borderHighlightColor = new Color(255,255,255,240)
		final Stroke borderHighlightStroke = new BasicStroke(1.25f)

		final Color unselectedBevelTopStart = new Color(227, 227, 227)
		final Color unselectedBevelTopEnd = Color.WHITE
		final Color unselectedBevelBottomStart = new Color(179, 177, 177)
		final Color unselectedBevelBottomEnd = Color.WHITE

		final Color selectedBevelTopStart = new Color(106, 152, 230)
		final Color selectedBevelTopEnd = new Color(159, 219, 249)
		final Color selectedBevelBottomStart = new Color(53, 120, 209)
		final Color selectedBevelBottomEnd = new Color(142, 215, 248)

		try {
			BufferedImage dummy = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB)
			final NextButton nextButt = new NextButton(activeImg: dummy, inActiveImg: dummy){
				/**
				 * 
				 */
				private static final long serialVersionUID = 6761062214573251877L

				@Override
				protected void paintComponent(Graphics g)
				{
					Graphics2D g2d = (Graphics2D)g
					g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

					int insets = 2
					int w = getWidth() - (insets * 2)
					int h = getHeight() - (insets * 2)
					int x = insets
					int y = insets

					// paint button body
					float h2 = h/2
					float h4 = h/4

					GeneralPath path = new GeneralPath()
					path.moveTo(0,h)
					path.curveTo(0,h-h4,h4,h2,h2,h2)
					path.lineTo(w-h2,h2)
					path.curveTo(w-h4,h2,w,h4,w,0)
					path.lineTo(w,h)

					// Create a buffered image to paint the rounded rectangle
					BufferedImage vBuffer = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB)
					Graphics2D bg2d = vBuffer.createGraphics()
					bg2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

					Area area = new Area(new Rectangle(0,0,w,h+1))
					area.subtract(new Area(path))

					Color vStartColor = null
					Color vEndColor = null

					if(isActive())
					{
						vStartColor = selectedBevelTopStart
						vEndColor = selectedBevelTopEnd
					}
					else
					{
						vStartColor = unselectedBevelTopStart
						vEndColor = unselectedBevelTopEnd
					}

					Paint p = new GradientPaint(0f, 0f, vStartColor, 0f, h, vEndColor)
					bg2d.setPaint(p)

					bg2d.fill(area)

					if(isActive())
					{
						vStartColor = selectedBevelBottomStart
						vEndColor = selectedBevelBottomEnd
					}
					else
					{
						vStartColor = unselectedBevelBottomStart
						vEndColor = unselectedBevelBottomEnd
					}

					p = new GradientPaint(0.0F, y, vStartColor, 0.0F,y+h, vEndColor)
					bg2d.setPaint(p)

					bg2d.fill(path)

					bg2d.setStroke(borderStroke)
					bg2d.draw(path)

					// Blur the background
					vBuffer = blurOp.filter(vBuffer, null)

					Shape vOldClip = g.getClip()

					Ellipse2D vNewClip = new Ellipse2D.Double(x, y, w, h)
					g.setClip(vNewClip)
					g.drawImage(vBuffer,x,y,null)

					g.setClip(vOldClip)

					// paint the next song triangles
					g2d.setColor(new Color(61, 61, 61))
					int w2 = getWidth()/2
					h2 = getHeight()/2
					int trinagleWidth = 9
					int triangleHeight = 10
					
					GeneralPath triangle1Path = new GeneralPath()
					triangle1Path.moveTo(w2-trinagleWidth+1, h2-triangleHeight/2)
					triangle1Path.lineTo(w2+2, h2)
					triangle1Path.lineTo(w2-trinagleWidth+1, h2+triangleHeight/2)
					triangle1Path.closePath()
					// transformation into a shape
					AffineTransform af = new AffineTransform()
					Shape triangle1 = triangle1Path.createTransformedShape(af)
					g2d.fill(triangle1)
					
					GeneralPath triangle2Path = new GeneralPath()
					triangle2Path.moveTo(w2+2, h2-triangleHeight/2)
					triangle2Path.lineTo(w2+trinagleWidth+2, h2)
					triangle2Path.lineTo(w2+2, h2+triangleHeight/2)
					triangle2Path.closePath()
					// transformation into a shape
					Shape triangle2 = triangle2Path.createTransformedShape(af)
					g2d.fill(triangle2)
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
					int x = 2
					int y = 2
					
					int w = getWidth() - 2 * x
					int h = getHeight() - 2 * y
					
					Graphics2D g2d = (Graphics2D) g
					Stroke vOldStroke = g2d.getStroke()
					Shape vOldClip = g2d.getClip()

					g2d.setStroke(borderHighlightStroke)
					g2d.setClip(0,h/2,w,h)
					g2d.setColor(borderHighlightColor)
					g2d.drawOval(x,y+1,w,h)

					g2d.setClip(0,0,w,h/2)
					g2d.setColor(borderColor)
					g2d.drawOval(x,y-1,w,h)

					g2d.setStroke(borderStroke)
					g2d.setClip(vOldClip)
					g2d.setColor(borderColor)
					g2d.drawOval(x,y,w,h)

					g2d.setStroke(vOldStroke)
				}
			}
			
			Shape base = nextButt.getBounds()
			nextButt.setBase(base)
			
			Dimension s = nextButt.getPreferredSize()
			Shape shape = new Ellipse2D.Float(0, 0, s.width-1, s.height-1)
			nextButt.setShape(shape)
			
			return nextButt
		}
		catch(Exception e)
		{
			e.printStackTrace()
		}
		return null
	}

	private static PreviousButton createPlaybackPrevButton()
	{
		// previous song button
		float[] BLUR = [0.10f, 0.10f, 0.10f, 0.10f, 0.30f, 0.10f, 0.10f, 0.10f, 0.10f]
		final ConvolveOp blurOp = new ConvolveOp(new Kernel(3, 3, BLUR))

		final Color borderColor = new Color(124, 125, 124)
		final Stroke borderStroke = new BasicStroke(1.5f)

		final Color borderHighlightColor = new Color(255,255,255,240)
		final Stroke borderHighlightStroke = new BasicStroke(1.25f)

		final Color unselectedBevelTopStart = new Color(227, 227, 227)
		final Color unselectedBevelTopEnd = Color.WHITE
		final Color unselectedBevelBottomStart = new Color(179, 177, 177)
		final Color unselectedBevelBottomEnd = Color.WHITE

		final Color selectedBevelTopStart = new Color(106, 152, 230)
		final Color selectedBevelTopEnd = new Color(159, 219, 249)
		final Color selectedBevelBottomStart = new Color(53, 120, 209)
		final Color selectedBevelBottomEnd = new Color(142, 215, 248)

		try
		{
			BufferedImage dummy = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB)
			final PreviousButton prevButt = new PreviousButton(activeImg: dummy, inActiveImg: dummy){
				/**
				 * 
				 */
				private static final long serialVersionUID = -1551939669987569210L

				@Override
				protected void paintComponent(Graphics g)
				{
					Graphics2D g2d = (Graphics2D)g
					g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

					int insets = 2
					int w = getWidth() - (insets * 2)
					int h = getHeight() - (insets * 2)
					int x = insets
					int y = insets

					// paint button body
					float h2 = h/2
					float h4 = h/4

					GeneralPath path = new GeneralPath()
					path.moveTo(0,h)
					path.curveTo(0,h-h4,h4,h2,h2,h2)
					path.lineTo(w-h2,h2)
					path.curveTo(w-h4,h2,w,h4,w,0)
					path.lineTo(w,h)

					// Create a buffered image to paint the rounded rectangle
					BufferedImage vBuffer = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB)
					Graphics2D bg2d = vBuffer.createGraphics()
					bg2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

					Area area = new Area(new Rectangle(0,0,w,h+1))
					area.subtract(new Area(path))

					Color vStartColor = null
					Color vEndColor = null

					if(isActive())
					{
						vStartColor = selectedBevelTopStart
						vEndColor = selectedBevelTopEnd
					}
					else
					{
						vStartColor = unselectedBevelTopStart
						vEndColor = unselectedBevelTopEnd
					}

					Paint p = new GradientPaint(0f, 0f, vStartColor, 0f, h, vEndColor)
					bg2d.setPaint(p)

					bg2d.fill(area)

					if(isActive())
					{
						vStartColor = selectedBevelBottomStart
						vEndColor = selectedBevelBottomEnd
					}
					else
					{
						vStartColor = unselectedBevelBottomStart
						vEndColor = unselectedBevelBottomEnd
					}

					p = new GradientPaint(0.0F, y, vStartColor, 0.0F,y+h, vEndColor)
					bg2d.setPaint(p)

					bg2d.fill(path)

					bg2d.setStroke(borderStroke)
					bg2d.draw(path)

					// Blur the background
					vBuffer = blurOp.filter(vBuffer, null)

					Shape vOldClip = g.getClip()

					Ellipse2D vNewClip = new Ellipse2D.Double(x, y, w, h)
					g.setClip(vNewClip)
					g.drawImage(vBuffer,x,y,null)

					g.setClip(vOldClip)

					// paint the previous song triangles
					g2d.setColor(new Color(61, 61, 61))
					int w2 = getWidth()/2
					h2 = getHeight()/2
					int trinagleWidth = 9
					int triangleHeight = 10
					
					GeneralPath triangle1Path = new GeneralPath()
					triangle1Path.moveTo(w2-1, h2-triangleHeight/2)
					triangle1Path.lineTo(w2-trinagleWidth-2, h2)
					triangle1Path.lineTo(w2-1, h2+triangleHeight/2)
					triangle1Path.closePath()
					// transformation into a shape
					AffineTransform af = new AffineTransform()
					Shape triangle1 = triangle1Path.createTransformedShape(af)
					g2d.fill(triangle1)
					
					GeneralPath triangle2Path = new GeneralPath()
					triangle2Path.moveTo(w2+trinagleWidth, h2-triangleHeight/2)
					triangle2Path.lineTo(w2-1, h2)
					triangle2Path.lineTo(w2+trinagleWidth, h2+triangleHeight/2)
					triangle2Path.closePath()
					// transformation into a shape
					Shape triangle2 = triangle2Path.createTransformedShape(af)
					g2d.fill(triangle2)
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
					int x = 2
					int y = 2
					
					int w = getWidth() - 2 * x
					int h = getHeight() - 2 * y
					
					Graphics2D g2d = (Graphics2D) g
					Stroke vOldStroke = g2d.getStroke()
					Shape vOldClip = g2d.getClip()

					g2d.setStroke(borderHighlightStroke)
					g2d.setClip(0,h/2,w,h)
					g2d.setColor(borderHighlightColor)
					g2d.drawOval(x,y+1,w,h)

					g2d.setClip(0,0,w,h/2)
					g2d.setColor(borderColor)
					g2d.drawOval(x,y-1,w,h)

					g2d.setStroke(borderStroke)
					g2d.setClip(vOldClip)
					g2d.setColor(borderColor)
					g2d.drawOval(x,y,w,h)

					g2d.setStroke(vOldStroke)
				}
			}
			
			Shape base = prevButt.getBounds()
			prevButt.setBase(base)
			
			Dimension s = prevButt.getPreferredSize()
			Shape shape = new Ellipse2D.Float(0, 0, s.width-1, s.height-1)
			prevButt.setShape(shape)
			
			return prevButt
		}
		catch(Exception e)
		{
			e.printStackTrace()
		}
		return null
	}

	private static StopButton createPlaybackStopButton()
	{
		// stop song button
		float[] BLUR = [0.10f, 0.10f, 0.10f, 0.10f, 0.30f, 0.10f, 0.10f, 0.10f, 0.10f]
		final ConvolveOp blurOp = new ConvolveOp(new Kernel(3, 3, BLUR))

		final Color borderColor = new Color(124, 125, 124)
		final Stroke borderStroke = new BasicStroke(1.5f)

		final Color borderHighlightColor = new Color(255,255,255,240)
		final Stroke borderHighlightStroke = new BasicStroke(1.25f)

		final Color unselectedBevelTopStart = new Color(227, 227, 227)
		final Color unselectedBevelTopEnd = Color.WHITE
		final Color unselectedBevelBottomStart = new Color(179, 177, 177)
		final Color unselectedBevelBottomEnd = Color.WHITE

		final Color selectedBevelTopStart = new Color(106, 152, 230)
		final Color selectedBevelTopEnd = new Color(159, 219, 249)
		final Color selectedBevelBottomStart = new Color(53, 120, 209)
		final Color selectedBevelBottomEnd = new Color(142, 215, 248)

		try
		{
			BufferedImage dummy = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB)
			final StopButton stopButt = new StopButton(activeImg: dummy, inActiveImg: dummy){
				/**
				 * 
				 */
				private static final long serialVersionUID = -2759409234899678963L

				@Override
				protected void paintComponent(Graphics g)
				{
					Graphics2D g2d = (Graphics2D)g
					g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

					int insets = 2
					int w = getWidth() - (insets * 2)
					int h = getHeight() - (insets * 2)
					int x = insets
					int y = insets

					// paint button body
					float h2 = h/2
					float h4 = h/4

					GeneralPath path = new GeneralPath()
					path.moveTo(0,h)
					path.curveTo(0,h-h4,h4,h2,h2,h2)
					path.lineTo(w-h2,h2)
					path.curveTo(w-h4,h2,w,h4,w,0)
					path.lineTo(w,h)

					// Create a buffered image to paint the rounded rectangle
					BufferedImage vBuffer = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB)
					Graphics2D bg2d = vBuffer.createGraphics()
					bg2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

					Area area = new Area(new Rectangle(0,0,w,h+1))
					area.subtract(new Area(path))

					Color vStartColor = null
					Color vEndColor = null

					if(isActive())
					{
						vStartColor = selectedBevelTopStart
						vEndColor = selectedBevelTopEnd
					}
					else
					{
						vStartColor = unselectedBevelTopStart
						vEndColor = unselectedBevelTopEnd
					}

					Paint p = new GradientPaint(0f, 0f, vStartColor, 0f, h, vEndColor)
					bg2d.setPaint(p)

					bg2d.fill(area)

					if(isActive())
					{
						vStartColor = selectedBevelBottomStart
						vEndColor = selectedBevelBottomEnd
					}
					else
					{
						vStartColor = unselectedBevelBottomStart
						vEndColor = unselectedBevelBottomEnd
					}

					p = new GradientPaint(0.0F, y, vStartColor, 0.0F,y+h, vEndColor)
					bg2d.setPaint(p)

					bg2d.fill(path)

					bg2d.setStroke(borderStroke)
					bg2d.draw(path)

					// Blur the background
					vBuffer = blurOp.filter(vBuffer, null)

					Shape vOldClip = g.getClip()

					Ellipse2D vNewClip = new Ellipse2D.Double(x, y, w, h)
					g.setClip(vNewClip)
					g.drawImage(vBuffer,x,y,null)

					g.setClip(vOldClip)

					// paint the pause quad
					g2d.setColor(new Color(61, 61, 61))
					int w2 = getWidth()/2
					h2 = getHeight()/2
					int rectWidth = 10
					int rectHeight = 10
					g2d.fillRect(w2 - rectWidth/2, (int) (h2 - rectHeight/2), rectWidth, rectHeight)
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
					int x = 2
					int y = 2
					
					int w = getWidth() - 2 * x
					int h = getHeight() - 2 * y
					
					Graphics2D g2d = (Graphics2D) g
					Stroke vOldStroke = g2d.getStroke()
					Shape vOldClip = g2d.getClip()

					g2d.setStroke(borderHighlightStroke)
					g2d.setClip(0,h/2,w,h)
					g2d.setColor(borderHighlightColor)
					g2d.drawOval(x,y+1,w,h)

					g2d.setClip(0,0,w,h/2)
					g2d.setColor(borderColor)
					g2d.drawOval(x,y-1,w,h)

					g2d.setStroke(borderStroke)
					g2d.setClip(vOldClip)
					g2d.setColor(borderColor)
					g2d.drawOval(x,y,w,h)

					g2d.setStroke(vOldStroke)
				}
			}
			
			Shape base = stopButt.getBounds()
			stopButt.setBase(base)
			
			Dimension s = stopButt.getPreferredSize()
			Shape shape = new Ellipse2D.Float(0, 0, s.width-1, s.height-1)
			stopButt.setShape(shape)
			
			return stopButt
		}
		catch(Exception e)
		{
			e.printStackTrace()
		}
		return null
	}
}