package com.koolaborate.mvc.view.optionscreen;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;

/***********************************************************************************
 * ToggleButton                                                                    *
 ***********************************************************************************
 * A toggle button with an active and an inactive state as it is seen throughout   *
 * preferences dialogs in Mozilla(R) applications.                                 *
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
public class ToggleButton extends JComponent
{
	private Color textcolor;
	private String text;
	private BufferedImage icon;
	private boolean selected;
	private boolean mouseOver;
	
	/**
	 * Constructor.
	 * 
	 * @param text the text of the toggle button
	 * @param icon the icon image of the button
	 */
	public ToggleButton(String text, BufferedImage icon)
	{
		this.text = text;
		this.icon = icon;
		textcolor = Color.WHITE;
		
		addMouseListener(new MouseListener(){
			public void mouseEntered(MouseEvent e)
			{
				mouseOver = true;
				repaint();
			}
			public void mouseExited(MouseEvent e)
			{
				mouseOver = false;
				repaint();
			}
			public void mousePressed(MouseEvent e){}
			public void mouseReleased(MouseEvent e){}
			public void mouseClicked(MouseEvent e){}
		});
	}
	
	@Override
	public void paintComponent(Graphics g)
	{
		Graphics2D g2 = (Graphics2D)g;
		Composite oldComposite = g2.getComposite();
		Paint oldPaint = g2.getPaint();
		
		// paint the background
		if(selected || mouseOver)
		{
			// paint a linear gradient background
			Color c1 = new Color(0xb8cde6);
			Color c2 = new Color(0xe6eef9);
			GradientPaint gp = new GradientPaint(0.0f, 0.0f, c1, 0.0f, getHeight(), c2);
//			g2.setColor(background);
			g2.setPaint(gp);
			g2.fillRect(0, 0, getWidth(), getHeight());
			g2.setPaint(oldPaint);
			textcolor = Color.BLACK;
		}
		else 
		{
			Color transparent = new Color(0x000000, true);
			g2.setComposite(AlphaComposite.SrcOver);
			g2.setColor(transparent);
			g2.fillRect(0, 0, getWidth(), getHeight());
			g2.setComposite(oldComposite);
			textcolor = Color.WHITE;
		}
		
		// paint the icon
		int icoXpos = (int)(getWidth() - icon.getWidth()) / 2;
		int icoYpos = 1;
		g2.drawImage(icon, icoXpos, icoYpos, null);
		
		// paint the text
		g2.setColor(textcolor);
		FontMetrics fm = getFontMetrics(g.getFont());
		int textXpos = (int)(getWidth() - fm.stringWidth(text)) / 2;
		int textYpos = getHeight()-5;
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2.drawString(text, textXpos, textYpos);
		
		g2.dispose();
	}
	
	
	public void setSelected(boolean selected)
	{
		this.selected = selected;
		repaint();
	}
}