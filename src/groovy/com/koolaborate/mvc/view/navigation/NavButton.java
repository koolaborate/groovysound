package com.koolaborate.mvc.view.navigation;

import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;

/***********************************************************************************
 * NavButton                                                                       *
 ***********************************************************************************
 * A navigation button for the different views.                                    *
 ***********************************************************************************
 * (c) Impressive Artworx, 2k8                                                     * 
 * @author Manuel Kaess                                                            *
 * @version 1.01                                                                   *
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
public class NavButton extends JComponent
{
	private BufferedImage activeImg;
	private BufferedImage inActiveImg;
	private boolean mouseOver = false;
	private boolean active = false;
	
	/**
	 * Constructor.
	 */
	public NavButton()
	{
        setPreferredSize(new Dimension(40, 36));
        
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
        		active = !active;
        		repaint();
        	}
        });
	}
	
	
	/**
	 * Sets the active image (when being selected).
	 * 
	 * @param activeImg the active image to be set
	 */
	public void setActiveImg(BufferedImage activeImg)
	{
		this.activeImg = activeImg;
	}


	/**
	 * Sets the inactive image (when not being selected).
	 * 
	 * @param inActiveImg the inactive image to be set
	 */
	public void setInActiveImg(BufferedImage inActiveImg)
	{
		this.inActiveImg = inActiveImg;
	}


	/**
	 * Sets the state of this button to active or inactive.
	 * 
	 * @param active whether to set the state to active or inactive
	 */
	public void setActive(boolean active)
	{
		this.active = active;
	}


	public boolean isMouseOver()
	{
		return mouseOver;
	}


	public void setMouseOver(boolean mouseOver)
	{
		this.mouseOver = mouseOver;
	}


	public boolean isActive()
	{
		return active;
	}


	public BufferedImage getActiveImg()
	{
		return activeImg;
	}


	public BufferedImage getInActiveImg()
	{
		return inActiveImg;
	}
}