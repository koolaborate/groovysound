package ui.common;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.border.EmptyBorder;

/***********************************************************************************
 * VariableLineBorder                                                              *
 ***********************************************************************************
 * A class that offers the functionality to create a border where the lines are    *
 * only painted at the desired sides. Also, the color and thickness of the lines   *
 * can be specified.                                                               *
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
public class VariableLineBorder extends EmptyBorder
{
	private Color c;
	private int thickness;
	private boolean paintTop, paintLeft, paintBottom, paintRight;
	
	/**
	 * Constructor.
	 * 
	 * @param top the top insets
	 * @param left the insets to the left edge
	 * @param bottom the bottom insets
	 * @param right the insets to the right edge
	 * @param c the color for the border
	 * @param thickness the width of the line for the border
	 * @param paintTop whether or not to paint a line at the top
	 * @param paintLeft whether or not to paint a line at the left side
	 * @param paintBottom whether or not to paint a line at the bottom
	 * @param paintRight whether or not to paint a line at the right side
	 */
	public VariableLineBorder(int top, int left, int bottom, int right, Color c, 
			int thickness, boolean paintTop, boolean paintLeft, boolean paintBottom, 
			boolean paintRight)
	{
		super(top, left, bottom, right);
		this.c = c;
		this.thickness = thickness;
		this.paintTop = paintTop;
		this.paintLeft = paintLeft;
		this.paintBottom = paintBottom;
		this.paintRight = paintRight;
	}
	
	@Override
	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height)
	{
		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(this.c);
		g2d.setStroke(new BasicStroke((float)this.thickness));
		if(paintTop)    g2d.drawLine(0, 0, width, 0);
		if(paintLeft)   g2d.drawLine(0, 0, 0, height);
		if(paintBottom) g2d.drawLine(0, height, width, height);
		if(paintRight)  g2d.drawLine(width, 0, width, height);
	}
}