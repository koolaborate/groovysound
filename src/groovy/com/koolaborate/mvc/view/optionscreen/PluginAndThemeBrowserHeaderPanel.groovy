package com.koolaborate.mvc.view.optionscreen

import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.Insets
import java.awt.LinearGradientPaint
import java.awt.Paint
import javax.swing.JPanel;

class PluginAndThemeBrowserHeaderPanel extends JPanel{
	private static final long serialVersionUID = 2411793553668898755L
	
	@Override
	public void paintComponent(Graphics g)
	{
		Graphics2D g2 = (Graphics2D)g
		Paint oldPaint = g2.getPaint()

		def coords = [0.0f, 0.34f, 0.341f, 1.0f]
		def colors = [new Color(0x516b9b),
				new Color(0x435d8d),
				new Color(0x365080),
				new Color(0x2b4575)]
		LinearGradientPaint p = new LinearGradientPaint(0.0f, 0.0f, 0.0f, 50.0f,
				coords,
				colors)
		
		g2.setPaint(p)
		g2.fillRect(0, 0, getWidth(), getHeight())
		
		g2.setPaint(oldPaint)
		super.paintComponents(g)
		g2.dispose()
	}
	
	@Override
	public Insets getInsets() {
		return new Insets(0, 0, 0, 0)
	}
}
