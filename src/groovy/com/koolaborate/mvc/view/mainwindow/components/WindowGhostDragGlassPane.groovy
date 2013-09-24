package com.koolaborate.mvc.view.mainwindow.components;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;

/***********************************************************************************
 * GhostDragGlassPane                                                              *
 ***********************************************************************************
 * A glass pane for showing thumbnails of pictures being dragged onto the window.  *
 ***********************************************************************************
 * (c) Impressive Artworx, 2k8                                                     * 
 * @author Romain Guy, Manuel Kaess                                                *
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
class WindowGhostDragGlassPane extends JComponent
{
	private static final long serialVersionUID = 5509743453860849064L;
	private BufferedImage image;
    private Point location;
    private BufferedImage shadow;
    
    public void moveIt(Point location) 
    {
        Point oldLocation = this.location;
        SwingUtilities.convertPointFromScreen(location, this);
        this.location = location;
        
        Rectangle newClip = new Rectangle(location.x - image.getWidth() / 2, 
        		location.y - image.getHeight() / 2, image.getWidth(), image.getHeight());
        newClip.add(new Rectangle(oldLocation.x - image.getWidth() / 2, 
        		oldLocation.y - image.getHeight() / 2, image.getWidth(), image.getHeight()));
        newClip.add(new Rectangle(oldLocation.x - image.getWidth() / 2, 
        		oldLocation.y - image.getHeight() / 2, shadow.getWidth(), shadow.getHeight()));
        newClip.add(new Rectangle(location.x - image.getWidth() / 2, 
        		location.y - image.getHeight() / 2, shadow.getWidth(), shadow.getHeight()));

        repaint(newClip);
    }
    
    public void hideIt() 
    {
        setVisible(false);
    }
    
    public void showIt(BufferedImage image, Point location) {
        this.image = image;
        this.shadow = new WindowShadowRenderer(5, 0.3f, Color.BLACK).createShadow(image);

        SwingUtilities.convertPointFromScreen(location, this);
        this.location = location;
        
        setVisible(true);
    }
    
    @Override
    protected void paintComponent(Graphics g)
    {
    	Graphics2D g2 = (Graphics2D) g;
    	// paint with a transparency effect
    	g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER).derive(0.85f));
    	
        if(image != null && location != null) 
        {
            int x = location.x - image.getWidth() / 2;
            int y = location.y - image.getHeight() / 2;
            
            g2.drawImage(shadow, x, y, null);
            g2.drawImage(image, x, y, null);
            
            // now draw a border
            g2.setColor(Color.BLACK);
            g2.drawRect(x, y, image.getWidth(), image.getHeight());
        }
    }
    
    @Override
	public boolean contains(int x, int y)
    {
    	return false;
    }
}