package com.koolaborate.mvc.view.playercontrols

import java.awt.Color
import java.awt.Dimension
import java.awt.Shape
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.geom.Ellipse2D
import java.awt.image.BufferedImage
import javax.swing.JComponent

/***********************************************************************************
 * PlayButton                                                                      *
 ***********************************************************************************
 * A rounded button to start the playback of the current song.                     *
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
class PlayButton extends JComponent{
	private static final long serialVersionUID = 1458008503153571145L
	BufferedImage activeImg
	BufferedImage inActiveImg
	BufferedImage pauseImg
	boolean active = false
	boolean pressed = false
	Shape shape, base
	
	/**
	 * Constructor.
	 */
	public PlayButton() {
        int iw = Math.max(activeImg.getWidth(), activeImg.getHeight())
        Dimension dim = new Dimension(iw, iw)
        setPreferredSize(dim)
        setMaximumSize(dim)
        setMinimumSize(dim)
        setBackground(Color.BLACK)
        setAlignmentY(JComponent.TOP_ALIGNMENT)
        initShape()

        addMouseListener([
        	mouseEntered: {
        		setActive(true)
        	},
        	
        	mouseExited: {
        		setActive(false)
        	},
        	
        	mouseClicked: {
        		setPressed(!pressed)
        	}
        ] as MouseAdapter)
	}
	
	
    protected void initShape(){
    	if(shape == null){
    		if(!getBounds().equals(base)){
    			Dimension s = getPreferredSize()
    			base = getBounds()
    			shape = new Ellipse2D.Float(0, 0, s.width-1, s.height-1)
    		}
    	}
    }
    
    
    public void setActive(boolean active){
    	this.active = active
    	repaint()
    }
    
    public void setPressed(boolean pressed)
    {
    	this.pressed = pressed
    	repaint()
    }


}