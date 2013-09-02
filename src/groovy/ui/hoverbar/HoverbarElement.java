package ui.hoverbar;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;

/***********************************************************************************
 * HoverbarElement                                                                 *
 ***********************************************************************************
 * An element inside the hover bar which is only visible if a mouse cursor hovers  *
 * over a certain element. The hoverbar element is highlighted when the cursor is  *
 * positioned over the element and an action is executed when clicked upon.        *
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
public class HoverbarElement extends JComponent
{
	private Thread action;
	private BufferedImage img;
	private boolean mouseOver = false;
	
	
	/**
	 * Constructor.
	 */
	public HoverbarElement()
	{
		setOpaque(false);
		
		addMouseListener(new MouseAdapter(){
			@Override
			public void mouseEntered(MouseEvent e) 
			{
				mouseOver = true;
				repaint();
			}
			
			@Override
			public void mouseExited(MouseEvent e) 
			{
				mouseOver = false;
				repaint();
			}
			
			@Override
			public void mouseClicked(MouseEvent e) 
			{
				if(action != null)
				{
					Thread go = new Thread(action);
					go.start();
				}
			}
		});
	}
	
	
	/**
	 * Sets the image icon.
	 * 
	 * @param i the image to be set
	 */
	public void setImage(BufferedImage i)
	{
		this.img = i;
		setPreferredSize(new Dimension(i.getWidth(), i.getHeight()));
	}
	
	
	/**
	 * Sets the action to be executed when clicked.
	 * 
	 * @param t the thread which will be executed when the label is clicked onto
	 */
	public void setActionThread(Thread t)
	{
		this.action = t;
	}
	

	@Override
	protected void paintComponent(Graphics g) 
	{
		Graphics2D g2 = (Graphics2D) g;
		
		if(mouseOver)
		{
			//TODO on mouseover: show icon highlighted. For now: draw demi-translucent oval
			g2.setComposite(AlphaComposite.SrcOver.derive(0.5f));
			g2.setColor(Color.WHITE);
			Dimension s = getSize();
			g2.fillOval(1, 1, s.width - 1, s.height - 1);
			g2.setComposite(AlphaComposite.SrcOver.derive(1.0f));
		}
		
		if(img != null) g2.drawImage(img, 0, 0, null);
	}
}