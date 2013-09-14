package com.koolaborate.mvc.view.optionscreen;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.koolaborate.mvc.view.decorations.Decorator;

/***********************************************************************************
 * ThemePanelEntry                                                                 *
 ***********************************************************************************
 * A panel that represents one entry of a theme in the theme list.                 *
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
public class ThemePanelEntry extends JPanel
{
	private static final long serialVersionUID = -9111468339290769406L;
	private String className;
	private boolean selected = false;
	
	private Color selectionColor1, selectionColor2;
	private float selectionAlpha;
	
	/**
	 * Constructor.
	 * 
	 * @param name the name of the theme
	 * @param description the description of the theme
	 * @param className the class name of the theme
	 * @param className the class name for the theme to be loaded
	 * @param decorator the decorator for highlight colors
	 */
	public ThemePanelEntry(String name, String description, String className, Decorator decorator)
	{
		setOpaque(false);
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setPreferredSize(new Dimension(230, 50));
		
		this.className = className;
		
		selectionColor1 = decorator.getSelectionColor1();
		selectionColor2 = decorator.getSelectionColor2();
		selectionAlpha  = decorator.getSelectionAlpha();
		
		JLabel titleLabel = new JLabel(name);
		titleLabel.setBorder(new EmptyBorder(6, 6, 2, 6));
		add(titleLabel);
		JLabel descritptionLabel = new JLabel("<HTML><i>" + description + "</i></HTML>");
		descritptionLabel.setBorder(new EmptyBorder(2, 12, 6, 6));
		add(descritptionLabel);
	}
	
	@Override
	protected void paintComponent(Graphics g)
	{
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		if(isSelected())
		{
			Composite oldComposite = g2.getComposite();
			g2.setComposite(AlphaComposite.SrcOver.derive(selectionAlpha));
			g2.setColor(selectionColor2);
			g2.fillRoundRect(3, 3, getWidth()-7, getHeight()-3, 6, 6);
			g2.setColor(selectionColor1);
			g2.drawRoundRect(3, 3, getWidth()-7, getHeight()-3, 6, 6);
			g2.setComposite(oldComposite);
			paintBorder(g);
		}
	}
	
	// getters and setters
	
	public boolean isSelected()
	{
		return selected;
	}

	public void setSelected(boolean selected)
	{
		this.selected = selected;
	}

	public String getClassName()
	{
		return className;
	}
}