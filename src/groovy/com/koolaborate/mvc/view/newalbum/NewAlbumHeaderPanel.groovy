package com.koolaborate.mvc.view.newalbum

import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import java.awt.event.ActionListener
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon
import javax.swing.JButton
import javax.swing.JFileChooser
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField
import javax.swing.SwingUtilities;

import org.jdesktop.swingx.JXBusyLabel;

import com.koolaborate.mvc.view.mainwindow.MainWindow
import com.koolaborate.util.LocaleMessage;

class NewAlbumHeaderPanel extends JPanel{
	BufferedImage bufferedSearchImage
	JTextField folderPath
	JButton searchButton
	MainWindow mainWindow 
	File albumFolder
	JXBusyLabel busyLabel
	NewAlbumFrame newAlbumFrame
	
	def initialize(){
		setOpaque(false)
		setLayout(new GridBagLayout())

		GridBagConstraints c = new GridBagConstraints()

		// search folder image
		JLabel imgLabel = new JLabel()
		imgLabel.setOpaque(false)
		imgLabel.setIcon(new ImageIcon(bufferedSearchImage))
		c.fill = GridBagConstraints.HORIZONTAL
		c.gridx = 0
		c.gridy = 0
		c.gridheight = 2
		c.insets = new Insets(10, 0, 0, 0) // top padding
		add(imgLabel, c)

		// search folder text
		JLabel folderText = new JLabel(LocaleMessage.getInstance().getString("newalbum.select_path"))
		folderText.setOpaque(false)
		c.gridx = 1
		c.gridy = 0
		c.gridheight = 1
		c.gridwidth = 2
		c.insets = new Insets(26, 0, 0, 0) // top padding
		add(folderText, c)

		// folder path text field
		folderPath = new JTextField()
		folderPath.setEditable(false)
		c.gridx = 1
		c.gridy = 1
		c.gridwidth = 1
		c.weightx = 1.0 // stretch the text field
		c.insets = new Insets(0, 0, 0, 0)
		add(folderPath, c)

		// folder search button
		searchButton = new JButton("...")
		searchButton.setToolTipText(LocaleMessage.getInstance().getString("common.searchfolder"))
		searchButton.addActionListener([
			actionPerformed: {
				final JFileChooser chooser = new JFileChooser(mainWindow.getSettings().getLastFolder())
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY)
				int state = chooser.showOpenDialog(newAlbumFrame)
				if(state == JFileChooser.APPROVE_OPTION) {
					if(chooser.getSelectedFile() != null) {
						mainWindow.getSettings().setLastFolder(chooser.getSelectedFile().getParent())
						
						def albumFolderSelection = [
							run: {
								albumFolder = chooser.getSelectedFile()
								SwingUtilities.invokeLater([
									run: {
										folderPath.setText(albumFolder.getAbsolutePath())
										busyLabel.setVisible(true)
										busyLabel.setBusy(true)
									}
								] as Runnable)
								newAlbumFrame.loadAlbumInfo()
							}
						] as Runnable
						new Thread(albumFolderSelection).start()
					}
				}
			}
		]as ActionListener)
		c.fill = GridBagConstraints.NONE
		c.gridx = 2
		c.gridy = 1
		c.weightx = 0.0 // do not stretch the button
		c.gridwidth = 1
		c.insets = new Insets(0, 4, 0, 10) // right and left padding
		add(searchButton, c)
	}
}
