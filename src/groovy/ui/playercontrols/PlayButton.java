package ui.playercontrols;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Shape;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;

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
public class PlayButton extends JComponent
{
	private BufferedImage activeImg;
	private BufferedImage inActiveImg;
	private BufferedImage pauseImg;
	private boolean active = false;
	private boolean pressed = false;
	private Shape shape, base;
	
	/**
	 * Constructor.
	 */
	public PlayButton(BufferedImage activeImg, BufferedImage inActiveImg, BufferedImage pauseImg)
	{
		this.activeImg = activeImg;
		this.inActiveImg = inActiveImg;
		this.pauseImg = pauseImg;
		
        int iw = Math.max(activeImg.getWidth(), activeImg.getHeight());
        Dimension dim = new Dimension(iw, iw);
        setPreferredSize(dim);
        setMaximumSize(dim);
        setMinimumSize(dim);
        setBackground(Color.BLACK);
        setAlignmentY(JComponent.TOP_ALIGNMENT);
        initShape();

        addMouseListener(new MouseAdapter(){
        	@Override
        	public void mouseEntered(MouseEvent e)
        	{
        		setActive(true);
        	}
        	
        	@Override
        	public void mouseExited(MouseEvent e)
        	{
        		setActive(false);
        	}
        	
        	@Override
        	public void mouseClicked(MouseEvent e) 
        	{
        		setPressed(!pressed);
        	}
        });
	}
	
	
    protected void initShape() 
    {
    	if(shape == null)
    	{
    		if(!getBounds().equals(base)) 
    		{
    			Dimension s = getPreferredSize();
    			base = getBounds();
    			shape = new Ellipse2D.Float(0, 0, s.width-1, s.height-1);
    		}
    	}
    }
    
    
    public void setActive(boolean active)
    {
    	this.active = active;
    	repaint();
    }
    
    public void setPressed(boolean pressed)
    {
    	this.pressed = pressed;
    	repaint();
    }


	public BufferedImage getActiveImg()
	{
		return activeImg;
	}


	public void setActiveImg(BufferedImage activeImg)
	{
		this.activeImg = activeImg;
	}


	public Shape getBase()
	{
		return base;
	}


	public void setBase(Shape base)
	{
		this.base = base;
	}


	public BufferedImage getInActiveImg()
	{
		return inActiveImg;
	}


	public void setInActiveImg(BufferedImage inActiveImg)
	{
		this.inActiveImg = inActiveImg;
	}


	public BufferedImage getPauseImg()
	{
		return pauseImg;
	}


	public void setPauseImg(BufferedImage pauseImg)
	{
		this.pauseImg = pauseImg;
	}


	public Shape getShape()
	{
		return shape;
	}


	public void setShape(Shape shape)
	{
		this.shape = shape;
	}


	public boolean isActive()
	{
		return active;
	}


	public boolean isPressed()
	{
		return pressed;
	}
}