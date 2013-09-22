package com.koolaborate.mvc.view.dialogs

import java.awt.BorderLayout
import java.awt.Color
import java.awt.FlowLayout
import java.awt.Font
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.LinearGradientPaint
import java.awt.Paint
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.util.Locale

import javax.swing.ImageIcon
import javax.swing.JButton
import javax.swing.JDialog
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.SwingUtilities
import javax.swing.UIManager

import com.koolaborate.model.Settings
import com.koolaborate.mvc.view.common.VariableLineBorder
import com.koolaborate.mvc.view.mainwindow.MainWindow
import com.koolaborate.util.BrowserControl
import com.koolaborate.util.LocaleMessage

/***********************************************************************************
 * AboutDialog                                                                     *
 ***********************************************************************************
 * A dialog window that shows information about the product.                       *
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
public class AboutDialog extends JDialog
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7090138410467143385L
	JLabel imageLabel
	JLabel titleLabel
	JLabel versionLabel
	JLabel textField
	JButton okButt
	
	Font bigFont = new Font("Tahoma", Font.BOLD, 18)
	Font smallFont = new Font("Tahoma", Font.PLAIN, 11)
	
	Settings settings
	
	/**
	 * Constructor.
	 * 
	 * @param s the settings object
	 */
	public AboutDialog(MainWindow window)
	{
		settings = window.settings
		
		this.setTitle(LocaleMessage.getInstance().getString("common.about") + " VibrantPlayer")
		this.setIconImage(new ImageIcon(getClass().getResource("/images/about.png")).getImage())
		
		SwingUtilities.invokeLater([
			run: {
				initGUI()
				okButt.requestFocus()
				okButt.requestFocusInWindow()
			}
		] as Runnable)
		this.setSize(300, 410)
		this.setLocationRelativeTo(null)
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE)
		this.setAlwaysOnTop(true)
		this.setResizable(false)
		this.setVisible(true)
	}
	
	/**
	 * Init the GUI elements.
	 */
	protected void initGUI(){
		this.setLayout(new BorderLayout())
		
		// build a two-stops gradient backgroundpanel
		JPanel background = new JPanel(){
			private static final long serialVersionUID = -7174635128295673280L

			@Override
			protected void paintComponent(Graphics g){
				Graphics2D g2 = (Graphics2D)g
				Paint oldPaint = g2.getPaint()
				LinearGradientPaint p
				
				Color[] colors = [Color.GRAY, Color.LIGHT_GRAY, Color.WHITE, Color.WHITE]
				float[] coords = [0.0f, 0.2f, 0.4f, 1.0f]
				p = new LinearGradientPaint(0.0f, 0.0f, 0.0f, getHeight(), coords, colors)
				g2.setPaint(p)
				g2.fillRect(0, 0, getWidth(), getHeight())
				g2.setPaint(oldPaint)
			}
		}
		background.setSize(300, 380)
		this.add(background, BorderLayout.CENTER)
		background.setLayout(null)
		imageLabel = new JLabel()
		background.add(imageLabel)
		imageLabel.setIcon(new ImageIcon(getClass().getResource("/images/logo.png")))
		imageLabel.setBounds(74, 0, 256, 256)
		titleLabel = new JLabel()
		background.add(titleLabel)
		titleLabel.setText("VibrantPlayer")
		titleLabel.setBounds(12, 20, 130, 20)
		titleLabel.setFont(bigFont)
		versionLabel = new JLabel()
		background.add(versionLabel)
		versionLabel.setText("Version " + settings.getVersion())
		versionLabel.setBounds(12, 40, 64, 10)
		versionLabel.setFont(smallFont)
		textField = new JLabel()
		background.add(textField)
		
		String text = "<HTML>&copy; Impressive Artworx, 2k8<br />Programmed by Manuel Kaess. " +
				"This program is free for personal and non-commercial use. Please pay " +
				"attention to the terms of license. VibrantPlayer is distributed in the hope " +
				"that it will be useful, but WITHOUT ANY WARRANTY.</HTML>"
		
		// get the language code for the current locale, for example "de"
		String testlang = Locale.getDefault().getLanguage() 
		if(testlang.toLowerCase().equals("de")){
			text = "<HTML>&copy; Impressive Artworx, 2k8<br />Programmiert von Manuel Kaess. " +
				"Dieses Programm darf für den nicht kommerziellen Einsatz uneingeschränkt " +
				"benutzt werden. Bitte beachten Sie die Lizensbedingungen. Für einen eventuellen " +
				"Verlust von Daten kann keinerlei Haftung übernommen werden.</HTML>"
		}
		
		textField.setFont(smallFont)
		textField.setText(text)
		textField.setOpaque(false)
		textField.setBounds(12, 230, 270, 100)
		
		// build the button panel
		JPanel buttonPanel = new JPanel()
		JButton webButt = new JButton(LocaleMessage.getInstance().getString("about.webpage"))
		webButt.setToolTipText(LocaleMessage.getInstance().getString("about.webpage_tooltip"))
		webButt.addActionListener([
			actionPerformed: {
				BrowserControl.getInstance().displayURL("http://www.impressive-artworx.de/albumplayer.php")
			}
		] as ActionListener)
		okButt = new JButton()
		okButt.setText(UIManager.getString("InternalFrameTitlePane.closeButtonText"))
		okButt.setToolTipText(UIManager.getString("InternalFrameTitlePane.closeButtonText"))
		okButt.addActionListener([
			actionPerformed: {
				dispose()
			}
		] as ActionListener)
		
		buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT))
		buttonPanel.add(webButt)
		buttonPanel.add(okButt)
		okButt.setSelected(true)
		buttonPanel.setBorder(new VariableLineBorder(5, 5, 5, 5, Color.GRAY, 1, true, false, false, false))
		this.add(buttonPanel, BorderLayout.SOUTH)
	}
}