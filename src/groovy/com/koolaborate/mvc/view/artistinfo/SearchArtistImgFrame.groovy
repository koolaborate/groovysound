package com.koolaborate.mvc.view.artistinfo

import java.awt.BorderLayout
import java.awt.Color
import java.awt.Cursor
import java.awt.Dimension
import java.awt.FlowLayout
import java.awt.GradientPaint
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import java.net.URL

import javax.imageio.ImageIO
import javax.swing.ImageIcon
import javax.swing.JButton
import javax.swing.JDialog
import javax.swing.JFileChooser
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextField
import javax.swing.SwingUtilities
import javax.swing.UIManager
import javax.swing.border.EmptyBorder
import javax.swing.border.LineBorder
import javax.swing.filechooser.FileFilter

import org.jdesktop.swingx.JXBusyLabel

import com.koolaborate.mvc.view.dialogs.VistaDialog
import com.koolaborate.mvc.view.mainwindow.MainWindow
import com.koolaborate.util.GraphicsUtilities
import com.koolaborate.util.HtmlParser
import com.koolaborate.util.LocaleMessage

/***********************************************************************************
 * SearchArtistImgFrame                                                            *
 ***********************************************************************************
 * A dialog window which searches for an artist image parsing the results of a     *
 * Google(R) image search. Also, the user is able to select a local image file.    *
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
public class SearchArtistImgFrame extends JDialog
{
	private static final long serialVersionUID = -123343458456008535L
	JButton cancelButt, searchButt
	JPanel centerPanel, searchPanel1, searchPanel2, searchPanel3
	JXBusyLabel busyLabel1, busyLabel2, busyLabel3
	JTextField pathText
	ArtistInfoFrame window
	BufferedImage searchImg, i1, i2, i3
	
	MouseAdapter ml
	String artist
	MainWindow mainWindow
	
	
	/**
	 * Constructor.
	 */
	public SearchArtistImgFrame(MainWindow mainWindow, ArtistInfoFrame window, String artist){
		this.mainWindow = mainWindow
		this.window = window
		this.artist = artist
	
		setSize(524, 390)
		setResizable(false)
		setModal(false)
		
	    SwingUtilities.invokeLater([
			run: {
				initGUI()
			}
	    ] as Runnable)
	}
	
	
	/**
	 * Initializes the GUI elements.
	 */
	private void initGUI() {
		try {
			searchImg = ImageIO.read(getClass().getResource("/images/searchcover.png"))
			setIconImage(searchImg)
		} catch (IOException e) {
			e.printStackTrace()
		}
		setDefaultCloseOperation(DISPOSE_ON_CLOSE)
		setTitle(LocaleMessage.getInstance().getString("searchartist.title"))
		
		setLayout(new BorderLayout())
		
		JPanel bgPanel = new JPanel(){
			private static final long serialVersionUID = -3783703142181933007L

			@Override
			protected void paintComponent(Graphics g) {
				Color color1 = new Color(237, 242, 249)
				Color color2 = new Color(255, 255, 255)
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
		bgPanel.setLayout(new BorderLayout())
		
		// init the mouse listener for selection events
		ml = [
			mouseEntered: { me ->
				if(me.getSource() instanceof JLabel)
				{
					JLabel s = (JLabel) me.getSource()
					s.setBorder(new LineBorder(Color.RED))
					setCursor(new Cursor(Cursor.HAND_CURSOR))
				}
			},
		
			mouseExited: { me ->
				if(me.getSource() instanceof JLabel)
				{
					JLabel s = (JLabel) me.getSource()
					s.setBorder(new LineBorder(Color.BLACK))
					setCursor(new Cursor(Cursor.DEFAULT_CURSOR))
				}
			},
		
			mouseClicked: { me ->
				JLabel s = (JLabel) me.getSource()
				String name = s.getName()
				if(name.equals("i1"))
				{
					saveArtistImage(i1)
				}
				else if(name.equals("i2"))
				{
					saveArtistImage(i2)
				}
				else if(name.equals("i3"))
				{
					saveArtistImage(i3)
				}
			}
		] as MouseAdapter
		
		// header panel
		bgPanel.add(createHeaderPanel(), BorderLayout.NORTH)
		
		// main panel
		centerPanel = createCenterPanel()
		bgPanel.add(centerPanel, BorderLayout.CENTER)

		add(bgPanel, BorderLayout.CENTER)
		
		// button panel
		add(createButtonPanel(), BorderLayout.SOUTH)
		
		setLocationRelativeTo(null)
		setVisible(true)
	}
	
	
	/**
	 * Creates the header panel.
	 * 
	 * @return the header JPanel
	 */
	private JPanel createHeaderPanel()
	{
		JPanel p = new JPanel()
		p.setOpaque(false)
		p.setLayout(new GridBagLayout())
		
		GridBagConstraints c = new GridBagConstraints()
		
		// search cover image
		JLabel imgLabel = new JLabel()
		imgLabel.setOpaque(false)
		imgLabel.setIcon(new ImageIcon(searchImg))
		c.fill = GridBagConstraints.NONE
		c.anchor = GridBagConstraints.LINE_START
		c.weightx = 0.0f
		c.gridx = 0  
		c.gridy = 0  
		c.gridheight = 2 
		c.insets = new Insets(10, 20, 0, 0)  // top padding
		p.add(imgLabel, c)
		
		// search cover text
		JLabel coverText = new JLabel(LocaleMessage.getInstance().getString("searchartist.description"))
		coverText.setOpaque(false)
		c.fill = GridBagConstraints.HORIZONTAL
		c.weightx = 1.0f
		c.gridx = 1 
		c.gridy = 0 
		c.gridheight = 1
		c.gridwidth = 2
		c.insets = new Insets(26, 10, 0, 0)  // top padding
		p.add(coverText, c)
		
		// cover search button
		searchButt = new JButton(LocaleMessage.getInstance().getString("searchcover.beginsearch"))
		searchButt.addActionListener([
			actionPerformed: {
				new Thread([
					run: {
						SwingUtilities.invokeLater([
							run: {
								busyLabel1.setVisible(true)
								busyLabel1.setBusy(true)
								busyLabel2.setVisible(true)
								busyLabel2.setBusy(true)
								busyLabel3.setVisible(true)
								busyLabel3.setBusy(true)
							}
						] as Runnable)
						try {
							searchCovers()
						} catch (Exception e) {
							e.printStackTrace()
						}
					}
				] as Runnable
				).start()
			}
		] as ActionListener)

		c.fill = GridBagConstraints.NONE
		c.anchor = GridBagConstraints.LINE_START
		c.weightx = 0.0f
		c.gridx = 1  
		c.gridy = 1  
		c.gridheight = 1 
		c.insets = new Insets(10, 10, 10, 0)  // top padding
		p.add(searchButt, c)
		
		return p
	}
	
	
	/**
	 * Creates the main panel.
	 * 
	 * @return the center JPanel
	 */
	private JPanel createCenterPanel()
	{
		JPanel center = new JPanel()
		center.setLayout(new BorderLayout())
		center.setOpaque(false)
		
		JPanel p = new JPanel()
		p.setLayout(new FlowLayout())
		p.setOpaque(false)
		
		Dimension dim = new Dimension(152, 152) 
		
		// layout manager
		GridBagLayout gbl      = new GridBagLayout()
		GridBagConstraints gbc = new GridBagConstraints()
		gbc.anchor             = GridBagConstraints.CENTER
		
		searchPanel1 = new JPanel(gbl)
		searchPanel1.setPreferredSize(dim)
		searchPanel1.setOpaque(false)
		busyLabel1 = new JXBusyLabel()
		busyLabel1.setVisible(false)
		gbl.setConstraints(busyLabel1, gbc)
		searchPanel1.add(busyLabel1)
		p.add(searchPanel1)
		
		searchPanel2 = new JPanel(gbl)
		searchPanel2.setOpaque(false)
		searchPanel2.setPreferredSize(dim)
		busyLabel2 = new JXBusyLabel()
		busyLabel2.setVisible(false)
		searchPanel2.add(busyLabel2, gbc)
		p.add(searchPanel2)
		
		searchPanel3 = new JPanel(gbl)
		searchPanel3.setOpaque(false)
		searchPanel3.setPreferredSize(dim)
		busyLabel3 = new JXBusyLabel()
		busyLabel3.setBorder(new EmptyBorder(0, 30, 0, 30))
		busyLabel3.setVisible(false)
		searchPanel3.add(busyLabel3, gbc)
		p.add(searchPanel3)
		
		center.add(p, BorderLayout.CENTER)
		
		JPanel ownPanel = new JPanel(new GridBagLayout())
		ownPanel.setOpaque(false)
		
		JLabel pathLabel = new JLabel(LocaleMessage.getInstance().getString("searchartist.ownpath"))
		gbc.anchor = GridBagConstraints.LINE_START
		gbc.gridx = 0
		gbc.gridy = 0
		gbc.gridwidth = 3
		gbc.fill = GridBagConstraints.HORIZONTAL
		gbc.weightx = 1.0f
		gbc.insets = new Insets(10, 10, 0, 0)
		ownPanel.add(pathLabel, gbc)
		
		JLabel path = new JLabel(LocaleMessage.getInstance().getString("searchcover.path"))
		gbc.anchor = GridBagConstraints.LINE_START
		gbc.gridx = 0
		gbc.gridy = 1
		gbc.gridwidth = 1
		gbc.fill = GridBagConstraints.NONE
		gbc.weightx = 0.0f
		gbc.insets = new Insets(6, 10, 10, 4)
		ownPanel.add(path, gbc)
		
		pathText = new JTextField()
		pathText.setEditable(false)
		gbc.anchor = GridBagConstraints.LINE_START
		gbc.gridx = 1
		gbc.gridy = 1
		gbc.fill = GridBagConstraints.HORIZONTAL
		gbc.weightx = 1.0f
		gbc.insets = new Insets(6, 0, 10, 0)
		ownPanel.add(pathText, gbc)
		
		JButton searchButt = new JButton("...")
		gbc.anchor = GridBagConstraints.LINE_START
		gbc.gridx = 2
		gbc.gridy = 1
		gbc.fill = GridBagConstraints.NONE
		gbc.weightx = 0.0f
		gbc.insets = new Insets(6, 4, 10, 10)
		
		searchButt.addActionListener([
			actionPerformed: {
				JFileChooser fc = new JFileChooser(new File(mainWindow.s.getLastFolder())) 
			    fc.setFileFilter([
			      accept: { file ->
					  File f = (File) file 
			    	  String fileName = f.getName().toLowerCase()
			    	  
			    	  // accept only images
			    	  return f.isDirectory() || 
			    	  fileName.endsWith(".jpg") ||
			    	  fileName.endsWith(".jpeg") ||
			    	  fileName.endsWith(".gif") ||
			    	  fileName.endsWith(".png") ||
			    	  fileName.endsWith(".bmp") 
			      },
			  
			      getDescription: { 
			    	  return "Grafikdateien" 
			      } 
			    ] as FileFilter)
			    
			    int state = fc.showOpenDialog(getThisInstance()) 
			    if(state == JFileChooser.APPROVE_OPTION && fc.getSelectedFile() != null){ 
			    	File file = fc.getSelectedFile() 
			    	mainWindow.s.setLastFolder(file.getParent())
			    	pathText.setText(file.getAbsolutePath())
			    	
			    	BufferedImage ownImg = null
			    	try {
			    		ownImg = ImageIO.read(file)
			    		saveArtistImage(ownImg)
			    	} catch(Exception e) {
			    		VistaDialog.showDialog(LocaleMessage.getInstance().getString("error.1"), LocaleMessage.getInstance().getString("error.2"), LocaleMessage.getInstance().getString("searchartist.fileerror"), VistaDialog.ERROR_MESSAGE)
			    	}
			    	dispose()
			    } 
			}
		] as ActionListener)
		
		ownPanel.add(searchButt, gbc)
		center.add(ownPanel, BorderLayout.SOUTH)
		return center
	}
	
	
	/**
	 * Creates the button panel.
	 * 
	 * @return the button JPanel
	 */
	private JPanel createButtonPanel()
	{
		JPanel p = new JPanel()
		cancelButt = new JButton(UIManager.getString("OptionPane.cancelButtonText"))
		cancelButt.addActionListener([
			actionPerformed: {
				dispose()
			}
		] as ActionListener)
		
		p.setLayout(new FlowLayout(FlowLayout.RIGHT))
		p.add(cancelButt)
		return p
	}
	
	
	/**
	 * Returns the instance of this class.
	 * 
	 * @return the <code>this</code> instance
	 */
	private SearchArtistImgFrame getThisInstance() {
		return this
	}
	
	/**
	 * Begins the Google image search for artist images.
	 */
	private void searchCovers() {
		final String tooltip = LocaleMessage.getInstance().getString("common.selectimg")
		final LineBorder b = new LineBorder(Color.BLACK)
		
		new Thread([
			run: {
				// then look for the google image search
				URL[] urls = HtmlParser.getInstance().getArtistImagePathsFromGoogle(artist)
				busyLabel1.setBusy(false)
				busyLabel2.setBusy(false)
				busyLabel3.setBusy(false)
				
				if(urls[0] != null) {
					try {
						i1 = ImageIO.read(urls[0])
					} catch (IOException e) {
						e.printStackTrace()
						i1 = null
					}
					
					if(i1 != null){
						BufferedImage i2Small = GraphicsUtilities.getInstance().createThumbnail(i1, 150, 150)
						final JLabel googleArtist1 = new JLabel(new ImageIcon(i2Small))
						googleArtist1.setName("i1")
						googleArtist1.setBorder(b)
						googleArtist1.setToolTipText(tooltip)
						googleArtist1.addMouseListener(ml)
						SwingUtilities.invokeLater([
							run: {
								searchPanel1.removeAll()
								searchPanel1.add(googleArtist1)
								searchPanel1.revalidate()
							}
						] as Runnable)
					}
				}
				
				if(urls[1] != null) {
					try {
						i2 = ImageIO.read(urls[1])
					} catch (IOException e) {
						e.printStackTrace()
						i2 = null
					}
					
					if(i2 != null) {
						BufferedImage i2Small = GraphicsUtilities.getInstance().createThumbnail(i2, 150, 150)
						final JLabel googleArtist2 = new JLabel(new ImageIcon(i2Small))
						googleArtist2.setName("i2")
						googleArtist2.setBorder(b)
						googleArtist2.setToolTipText(tooltip)
						googleArtist2.addMouseListener(ml)
						SwingUtilities.invokeLater([
							run: {
								searchPanel2.removeAll()
								searchPanel2.add(googleArtist2)
								searchPanel2.revalidate()
							}
						] as Runnable)
					}
				}
				
				if(urls[2] != null){
					try{
						i3 = ImageIO.read(urls[2])
					} catch (IOException e){
						e.printStackTrace()
						i3 = null
					}
					
					if(i3 != null){
						BufferedImage i3Small = GraphicsUtilities.getInstance().createThumbnail(i3, 150, 150)
						final JLabel googleArtist3 = new JLabel(new ImageIcon(i3Small))
						googleArtist3.setName("i3")
						googleArtist3.setBorder(b)
						googleArtist3.setToolTipText(tooltip)
						googleArtist3.addMouseListener(ml)
						SwingUtilities.invokeLater([
							run: {
								searchPanel3.removeAll()
								searchPanel3.add(googleArtist3)
								searchPanel3.revalidate()
							}
						] as Runnable)
					}
				}
			}
		] as Runnable).start()
	}
	
	
	/**
	 * Saves the selected album and replaces the existing one.
	 * 
	 * @param img the selected image
	 */
	private void saveArtistImage(BufferedImage img)
	{
		window.setArtistImage(img)
		window.setImageChanged(true)
		dispose()
	}
}