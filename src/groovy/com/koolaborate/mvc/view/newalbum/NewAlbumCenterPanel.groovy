package com.koolaborate.mvc.view.newalbum

import java.awt.BorderLayout
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets

import javax.swing.JComboBox
import javax.swing.JLabel
import javax.swing.JPanel;
import javax.swing.JTextField
import javax.swing.border.EmptyBorder

import org.jdesktop.swingx.JXBusyLabel

import com.koolaborate.util.LocaleMessage;

class NewAlbumCenterPanel extends JPanel{
	JXBusyLabel busyLabel
	JPanel albumInfoPanel
	JTextField albumTitle
	JTextField albumArtist
	JTextField albumYear
	JComboBox<String> songList
	
	def initialize(){
		setLayout(new BorderLayout())
		setOpaque(false)

		// init the busy panel
		busyLabel = new JXBusyLabel()
		busyLabel.setBorder(new EmptyBorder(6, 10, 0, 10))
		busyLabel.setOpaque(false)
		busyLabel.setText(LocaleMessage.getInstance().getString("newalbum.searching"))
		busyLabel.setToolTipText(LocaleMessage.getInstance().getString("newalbum.searching_tooltip"))
		busyLabel.setBusy(false)
		busyLabel.setVisible(false)

		// init the album info panel
		albumInfoPanel = new JPanel()
		albumInfoPanel.setOpaque(false)
		albumInfoPanel.setLayout(new GridBagLayout())

		GridBagConstraints cLabel = new GridBagConstraints()
		GridBagConstraints cTextfield = new GridBagConstraints()

		cLabel.fill = GridBagConstraints.NONE
		cLabel.weightx = 0.0 // do not stretch the labels
		cLabel.gridwidth = 1
		cLabel.anchor = GridBagConstraints.LINE_START
		cLabel.insets = new Insets(0, 10, 0, 0)
		cTextfield.fill = GridBagConstraints.HORIZONTAL
		cTextfield.weightx = 1.0 // stretch the textfields
		cTextfield.gridwidth = 1
		cTextfield.insets = new Insets(0, 4, 0, 10)

		// title label
		JLabel titleLabel = new JLabel(LocaleMessage.getInstance().getString("newalbum.album_title"))
		titleLabel.setOpaque(false)
		cLabel.gridx = 0
		cLabel.gridy = 0
		albumInfoPanel.add(titleLabel, cLabel)

		// title text
		albumTitle = new JTextField()
		cTextfield.gridx = 1
		cTextfield.gridy = 0
		albumInfoPanel.add(albumTitle, cTextfield)

		// artist label
		JLabel artistLabel = new JLabel(LocaleMessage.getInstance().getString("newalbum.artist"))
		artistLabel.setOpaque(false)
		cLabel.gridx = 0
		cLabel.gridy = 1
		albumInfoPanel.add(artistLabel, cLabel)

		// artist text
		albumArtist = new JTextField()
		cTextfield.gridx = 1
		cTextfield.gridy = 1
		albumInfoPanel.add(albumArtist, cTextfield)

		// year label
		JLabel yearLabel = new JLabel(LocaleMessage.getInstance().getString("newalbum.year"))
		yearLabel.setOpaque(false)
		cLabel.gridx = 0
		cLabel.gridy = 2
		albumInfoPanel.add(yearLabel, cLabel)

		// year text
		albumYear = new JTextField()
		cTextfield.gridx = 1
		cTextfield.gridy = 2
		albumInfoPanel.add(albumYear, cTextfield)

		// song list label
		JLabel songlistLabel = new JLabel(LocaleMessage.getInstance().getString("newalbum.songlist"))
		songlistLabel.setOpaque(false)
		cLabel.gridx = 0
		cLabel.gridy = 3
		albumInfoPanel.add(songlistLabel, cLabel)

		// song list combo box
		songList = new JComboBox<String>()
		cTextfield.gridx = 1
		cTextfield.gridy = 3
		albumInfoPanel.add(songList, cTextfield)

		add(busyLabel, BorderLayout.NORTH)
	}
}
