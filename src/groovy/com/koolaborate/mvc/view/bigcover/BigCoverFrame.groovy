package com.koolaborate.mvc.view.bigcover

import java.awt.BorderLayout
import java.awt.Color
import java.awt.Dimension
import java.awt.FlowLayout
import java.awt.GradientPaint
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import java.awt.Paint
import java.awt.RenderingHints
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.io.File

import javax.swing.Box
import javax.swing.BoxLayout
import javax.swing.JButton
import javax.swing.JCheckBox
import javax.swing.JColorChooser
import javax.swing.JDialog
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JSlider
import javax.swing.SwingUtilities
import javax.swing.UIManager
import javax.swing.event.ChangeEvent
import javax.swing.event.ChangeListener

import com.koolaborate.mvc.view.common.VariableLineBorder
import com.koolaborate.mvc.view.mainwindow.MainWindow
import com.koolaborate.mvc.view.playlistview.CoverPanel
import com.koolaborate.util.LocaleMessage

/***********************************************************************************
 * BigCoverFrame                                                                   *
 ***********************************************************************************
 * A dialog window that shows a bigger (and scalable) version of the album's cover *
 * image. The reflection can also be unblurred and the shine effect can be         *
 * disabled.                                                                       *
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
public class BigCoverFrame extends JDialog
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5732683280761662053L

	MainWindow mainWindow
	
	JPanel backGround
	CoverPanel coverPanel
	int startSize = 300
	
	/** colors for the background gradient */
	Color color1 = Color.BLACK
	Color color2 = Color.GRAY
	
	/**
	 * Constructor.
	 * 
	 * @param window referece to the main window
	 */
	public BigCoverFrame(MainWindow window)
	{
		this.mainWindow = window
		
		setTitle(LocaleMessage.getInstance().getString("cover.detailview"))
		setSize(640, 480)
		setResizable(false)
		setLocationRelativeTo(null)
		setDefaultCloseOperation(DISPOSE_ON_CLOSE)
		
		SwingUtilities.invokeLater([
			run: {
				initGUI()
				setVisible(true)
			}
		] as Runnable)
	}
	
	/**
	 * Initializes the GUI elements.
	 */
	private void initGUI(){
		backGround = new JPanel(){
			/**
			 * 
			 */
			private static final long serialVersionUID = 4774362950346490954L

			@Override
			protected void paintComponent(Graphics g){
				if(!isOpaque()){
					super.paintComponent(g)
					return
				}
				
				Graphics2D g2d = (Graphics2D) g
				
				int w = getWidth()
				int h = getHeight()
				
				// Paint a gradient from top to bottom
				GradientPaint gp = new GradientPaint(
						0, 0, color1,
						0, h, color2)
				
				g2d.setPaint(gp)
				g2d.fillRect(0, 0, w, h)
			}
		}
		backGround.setLayout(new GridBagLayout())
		GridBagConstraints gbc = new GridBagConstraints()
		gbc.anchor = GridBagConstraints.NORTHWEST
		gbc.gridx = 0
		gbc.gridy = 0
		gbc.weightx = 1.0f
		gbc.weighty = 1.0f
		
		// add the cover
		coverPanel = new CoverPanel(mainWindow)
		coverPanel.setBigView(true)
		coverPanel.setCoverPath(mainWindow.currentFolder + File.separator + "folder.jpg")
		coverPanel.refreshCover()
		coverPanel.setCoverSize(startSize)
		Dimension cSize = new Dimension(460, 400)
		coverPanel.setPreferredSize(cSize)
		coverPanel.setMinimumSize(cSize)
		coverPanel.setMaximumSize(cSize)
		backGround.add(coverPanel, gbc)

		gbc.anchor = GridBagConstraints.NORTHEAST
		gbc.gridx = 1
		gbc.gridy = 0
		gbc.weightx = 0.0f
		gbc.weighty = 0.0f
		gbc.insets = new Insets(10, 10, 10, 10)
		
		// add the controls
		JPanel p = [
			paintComponent: { g ->
				if(!isOpaque()) {
					super.paintComponent(g)
					return
				}
				
				Graphics2D g2d = (Graphics2D) g
				
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
				
				int w = getWidth()
				int h = getHeight()
				
				Color c1 = Color.WHITE
				Color c2 = Color.GRAY
				int arc = 20
				
				Paint p = g2d.getPaint()
				
				// Paint a gradient from top to bottom
				GradientPaint gp = new GradientPaint(
						0, 0, c1,
						0, h, c2)
				
				g2d.setPaint(gp)
				g2d.fillRoundRect(0, 0, w, h, arc, arc)
				g2d.setPaint(p)
				g2d.setColor(c2.darker())
				g2d.drawRoundRect(0, 0, w-1, h-1, arc, arc)
			}
		] as JPanel
	
		Dimension d = new Dimension(200, 230)
		p.setPreferredSize(d)
		p.setMinimumSize(d)
		p.setMaximumSize(d)
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS))
		p.add(Box.createVerticalStrut(8))
		
		JLabel settingsLab = new JLabel("<html><b>" + LocaleMessage.getInstance().getString("cover.view_settings") + "</b></html>")
		p.add(settingsLab)
		p.add(Box.createVerticalStrut(12))
		
		JCheckBox check1 = new JCheckBox(LocaleMessage.getInstance().getString("cover.blurred"))
		check1.setOpaque(false)
		check1.setSelected(true)
		check1.addChangeListener([
			stateChanged: { event ->
				ChangeEvent e = (ChangeEvent) event
				JCheckBox source = (JCheckBox) e.getSource()
				coverPanel.setBlurReflection(source.isSelected())
				backGround.repaint()
			}
		] as ChangeListener)
		
		JCheckBox check2 = new JCheckBox(LocaleMessage.getInstance().getString("cover.light_effect"))
		check2.setSelected(true)
		check2.setOpaque(false)
		check2.addChangeListener([
			stateChanged: { event ->
				ChangeEvent e = event
				JCheckBox source = (JCheckBox) e.getSource()
				coverPanel.setShinyReflection(source.isSelected())
				backGround.repaint()
			}
		] as ChangeListener)
		
		final String coverString = LocaleMessage.getInstance().getString("cover.cover")
		final JLabel size = new JLabel(coverString + ": " + startSize + " px")
		
		JSlider coverSize = new JSlider()
		coverSize.setMinimum(100)
		coverSize.setMaximum(400)
		coverSize.setOpaque(false)
		coverSize.setValue(startSize)
		coverSize.addChangeListener([
			stateChanged: { event ->
				ChangeEvent e = event
				JSlider source = (JSlider) e.getSource()
				coverPanel.setCoverSize(source.getValue())
				size.setText(coverString + ": " + source.getValue() + " px")
				backGround.repaint()
			}
		] as ChangeListener)
		
		final JButton color1Butt = new JButton(LocaleMessage.getInstance().getString("cover.color") + " 1") 
		color1Butt.setBackground(color1)
		color1Butt.addActionListener([
			actionPerformed: {
				color1 = JColorChooser.showDialog(null, LocaleMessage.getInstance().getString("cover.color") + " 1", color1) 
				color1Butt.setBackground(color1)
				backGround.repaint()
			}
		] as ActionListener)
		
		final JButton color2Butt = new JButton(LocaleMessage.getInstance().getString("cover.color") + " 2") 
		color2Butt.setBackground(color2)
		color2Butt.addActionListener([
			actionPerformed: {
				color2 = JColorChooser.showDialog(null, LocaleMessage.getInstance().getString("cover.color") + " 2", color2) 
				color2Butt.setBackground(color2)
				backGround.repaint()
			}
		] as ActionListener)
		
		p.add(check1)
		p.add(check2)
		p.add(Box.createVerticalStrut(12))
		p.add(size)
		p.add(coverSize)
		p.add(Box.createVerticalStrut(12))
		p.add(color1Butt)
		p.add(Box.createVerticalStrut(2))
		p.add(color2Butt)
		
		backGround.add(p, gbc)
		
		setLayout(new BorderLayout())
		add(backGround, BorderLayout.CENTER)
		
		JPanel buttonPanel = new JPanel()
		buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT))
		JButton closeButt = new JButton(UIManager.getString("InternalFrameTitlePane.closeButtonText"))
		closeButt.setToolTipText(LocaleMessage.getInstance().getString("common.close_tooltip"))
		closeButt.addActionListener([
			actionPerformed: {
				dispose()
			}
		] as ActionListener)
		buttonPanel.add(closeButt)
		buttonPanel.setBorder(new VariableLineBorder(5, 5, 5, 5, Color.GRAY, 1, true, false, false, false))
		add(buttonPanel, BorderLayout.SOUTH)
	}
}