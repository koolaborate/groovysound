package com.koolaborate.mvc.view.hoverbar;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JComponent;
import javax.swing.border.EmptyBorder;

/***********************************************************************************
 * Hoverbar                                                                        *
 ***********************************************************************************
 * A bar that becomes visible if the mouse cursor is positioned over a certain     *
 * component. When the mouse cursor leaves the component again, this bar fades out *
 * and becomes translucent again.                                                  *
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
class Hoverbar extends JComponent{
	private static final long serialVersionUID = 3300560477607711488L;

	float alpha = 0.9f;
	
	int width = 20;
	int height = 20;
	int arc = 20;
	int margin = 2;
	
	Color background = new Color(35, 35, 35);    // Color.GRAY.darker()
	Color foreground = new Color(107, 118, 130); // Color.WHITE; / Color.BLACK
	
	
	/**
	 * Constructor.
	 */
	public Hoverbar()
	{
		setPreferredSize(new Dimension(width + 2*margin, height + 2*margin));
		setBorder(new EmptyBorder(margin, margin, margin, margin));
		setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
	}
	
	@Override
	protected void paintComponent(Graphics g) 
	{
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		// set drawing opacity
		g2.setComposite(AlphaComposite.SrcOver.derive(getAlpha()));

		// draw background
		g2.setColor(background);
		g2.fillRoundRect(margin, margin, width-margin, height-margin, arc, arc);
		
		// draw border
		g2.setColor(foreground);
		g2.drawRoundRect(margin, margin, width-margin-1, height-margin, arc, arc);
	}
	
	/**
	 * Adds a HoverbarElement to the bar (it is added to the right).
	 * 
	 * @param e the element to be added
	 */
	public void addElement(HoverbarElement e)
	{
		add(e);
		addToHoverbarSize(e.getPreferredSize().width);
	}

	
	/**
	 * Adds a hoverbar separator of given with to the hover bar.
	 * 
	 * @param width the width of the separator to be added
	 */
	public void addSeparator(int width)
	{
		add(new HoverbarSeparator(width, this.height - margin));
		addToHoverbarSize(width);
	}
	
	
	/**
	 * Adds the value w as additional space to the hover bar width.
	 * 
	 * @param w the additional with in pixels
	 */
	private void addToHoverbarSize(int w)
	{
		this.width += w;
		setPreferredSize(new Dimension(width + 2*margin, height + 2*margin));
	}
}
