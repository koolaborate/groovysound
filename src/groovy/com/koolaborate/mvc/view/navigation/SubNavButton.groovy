package com.koolaborate.mvc.view.navigation;

import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;
import javax.swing.border.EmptyBorder;

/***********************************************************************************
 * SubNavButton                                                                    *
 ***********************************************************************************
 * Sub navigation buttons show only information that is available due to the view  *
 * that is currently selected.                                                     *
 ***********************************************************************************
 * (c) Impressive Artworx, 2k8                                                     * 
 * @author Manuel Kaess                                                            *
 * @version 1.04                                                                   *
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
public class SubNavButton extends JComponent
{
	private static final long serialVersionUID = 2476824458682313176L;
	private BufferedImage ico; 
	private String text;
	private boolean mouseOver = false;
	
	// the mouse listener that performs an action when the button is clicked
	private MouseAdapter listener;
	
	/**
	 * Constructor.
	 */
	public SubNavButton()
	{
        setPreferredSize(new Dimension(36, 36));
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
        });
        setBorder(new EmptyBorder(0, 0, 0, 2)); // 2 px border at the right
	}
	
	
	/**
	 * Sets the icon for this button.
	 * 
	 * @param icon the icon to be set
	 */
	public void setIcon(BufferedImage icon)
	{
		this.ico = icon;
	}

	
	/**
	 * @return the text of the sub navigation button
	 */
	public String getText()
	{
		return text;
	}

	/**
	 * Sets the text of this button.
	 * 
	 * @param text the text to be set
	 */
	public void setText(String text)
	{
		this.text = text;
		setToolTipText(text);
	}


	public BufferedImage getIcon()
	{
		return ico;
	}


	public boolean isMouseOver()
	{
		return mouseOver;
	}


	public void setMouseOver(boolean mouseOver)
	{
		this.mouseOver = mouseOver;
	}


	/**
	 * Sets the mouse listener for this button. When the button is pressed, the action
	 * which is associated with the MouseAdapter is executed. Use this method and do
	 * not use addMouseListener(...) since the old listener has to be removed every time
	 * the button is reconsturcted.
	 * 
	 * @param mouseAdapter the mouse adapter which holds the action for the button if it
	 * is pressed 
	 */
	public void setMouseListener(MouseAdapter mouseAdapter) 
	{
		// remove the old listener first
		this.removeMouseListener(listener);
		this.listener = mouseAdapter;
		// add the new listener
		this.addMouseListener(listener);
	}
}