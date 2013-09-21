package com.koolaborate.mvc.view.optionscreen;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

/***********************************************************************************
 * LinkLabel                                                                       *
 ***********************************************************************************
 * An extended JLabel for handling mouse hover and click actions. When the mouse   *
 * cursor is positioned over the label, the foreground color changes. When being   *
 * clicked, an action is being executed. The behaviour resembles a hyperlink in a  *
 * web browser.                                                                    *
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
public class LinkLabel extends JLabel
{
	private static final long serialVersionUID = -5855578803801940641L;
	/** the action thread which is executed when being clicked */
	private Thread action;
	private Color inactiveColor, activeColor;
	
	public LinkLabel(String text)
	{
		setText(text);
		
		inactiveColor = getForeground();
		activeColor = Color.BLUE;
		
		setHorizontalAlignment(SwingConstants.LEFT);
		
		addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseEntered(MouseEvent arg0)
			{
				if(isEnabled())
				{
					setForeground(activeColor);
					setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
					repaint();
				}
			}
			
			@Override
			public void mouseExited(MouseEvent arg0)
			{
				if(isEnabled())
				{
					setForeground(inactiveColor);
					setCursor(Cursor.getDefaultCursor());
					repaint();
				}
			}
			
			@Override
			public void mouseClicked(MouseEvent arg0)
			{
				if(isEnabled() && action != null)
				{
					Thread go = new Thread(action);
					go.start();
				}
			}
		});
		
		if(!isEnabled()) setForeground(Color.GRAY);
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
}
