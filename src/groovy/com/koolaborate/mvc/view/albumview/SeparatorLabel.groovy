package com.koolaborate.mvc.view.albumview;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JLabel;

/***********************************************************************************
 * SeparatorLabel                                                                  *
 ***********************************************************************************
 * A label that contains the start letter of the current section and a coloured    *
 * line.                                                                           *
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
class SeparatorLabel extends JLabel
{
	private static final long serialVersionUID = -360654062567845706L;
	String letter;
	Color foreground; // = new Color(0x04446e);
	int spaceToBorder = 10;
	
	/**
	 * Constructor.
	 * 
	 * @param firstChar the character for the separator
	 */
	public SeparatorLabel(String firstChar)
	{
		this.letter = firstChar;
		setPreferredSize(new Dimension(2000, 16));
	}
	
	@Override
	protected void paintComponent(Graphics g)
	{
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		// draw in the foreground color
		g2.setColor(foreground);
		
		// paint a circle around the letter
		g2.drawOval(0, 0, getHeight()-1, getHeight()-1);
		
		// draw the letter inside the circle
		FontMetrics fm = getFontMetrics(g2.getFont());
		int textXpos = (int)(getHeight() - fm.stringWidth(letter)) / 2;
		g2.drawString(letter, textXpos, 13);
		
		// draw a nice seperator line
		g2.drawLine(getHeight(), getHeight() / 2, getWidth() - spaceToBorder, getHeight() / 2);
		
		g2.dispose();
	}
}