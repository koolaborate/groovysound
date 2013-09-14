package com.koolaborate.mvc.view.songinfo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.apache.log4j.Logger;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.tag.Tag;

import com.koolaborate.mvc.view.common.VariableLineBorder;
import com.koolaborate.mvc.view.dialogs.VistaDialog;
import com.koolaborate.mvc.view.editid3tag.EditId3TagFrame;
import com.koolaborate.mvc.view.mainwindow.MainWindow;
import com.koolaborate.util.LocaleMessage;

/***********************************************************************************
 * SongInfoFrame                                                                   *
 ***********************************************************************************
 * A window which shows information to a given mp3 song file. Despite some general *
 * file information (name, size, created on, ...) some mp3 specific fields are     *
 * also shown (bit rate, title, artist, ...).                                      *
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
public class SongInfoFrame extends JFrame
{
	private static final long serialVersionUID = 3125867978600491626L;

	private static Logger log = Logger.getLogger(SongInfoFrame.class.getName());
	
	private MainWindow window;
	private BufferedImage searchImg;
	
	// song file information
	private File songFile;
	private String filename, filepath;
	private float filesize;
	
	// song header information
	private long bitrate;
	private String channels, encoding, format;
	private int samplerate;
	
	// song tag information
	private int length, mins, secs;
	private String title, artist, trackno, year, album, genre, filedate;
	
	private JLabel fileNameLabel, filePathLabel, fileSizeLabel, fileDateLabel, bitrateLabel, 
		channelsLabel, encodingLabel, formatLabel, samplerateLabel, lengthLabel, titleLabel, 
		artistLabel, tracknoLabel, yearLabel, albumLabel, genreLabel;
	
	/**
	 * Constructor.
	 * 
	 * @param window reference to the main window
	 * @param songFilePath the path to the song file
	 */
	public SongInfoFrame(MainWindow window, String songFilePath)
	{
		this.window = window;
		this.songFile = new File(songFilePath);
		if(songFile.exists())
		{
			filename = songFile.getName();
			filepath = songFile.getAbsolutePath();
			filesize = songFile.length();
			filesize /= 1024l; // kB
			filesize /= 1024l; // MB
			long modifiedTime = songFile.lastModified();
			DateFormat formatter = new SimpleDateFormat();
			filedate = formatter.format(modifiedTime);
		}
		else
		{
			VistaDialog.showDialog(LocaleMessage.getString("error.12"), 
					LocaleMessage.getString("error.13"), "'" + songFilePath + "' " + 
					LocaleMessage.getString("error.14"), VistaDialog.ERROR_MESSAGE);
							
			//TODO confirmation dialog: do you want to update songlist/album
			// if directory is missing: remove album
		}
		
		try
		{
			searchImg = ImageIO.read(getClass().getResource("/images/searchfolder.png"));
			setIconImage(searchImg);
		}
		catch(IOException e)
		{
			log.error(e.getMessage());
			e.printStackTrace();
		}
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setTitle(LocaleMessage.getString("songinfo.title"));
		
		SwingUtilities.invokeLater(new Runnable(){
			public void run()
			{
				initGUI();
				setSize(600, 600);
				setLocationRelativeTo(null);
				setVisible(true);
				loadSongInfo();
				setSongInfoFields();
			}
		});
	}
	
	/**
	 * Initializes the GUI components.
	 */
	private void initGUI()
	{
		setLayout(new BorderLayout());
		
		JPanel bgPanel = new JPanel(){
			private static final long serialVersionUID = 3680372109216548569L;

			@Override
			protected void paintComponent(Graphics g)
			{
				Color color1 = new Color(237, 242, 249);
				Color color2 = new Color(255, 255, 255);
				Graphics2D g2d = (Graphics2D) g;

				int w = getWidth();
				int h = getHeight();
				 
				// Paint a gradient from top to bottom
				GradientPaint gp = new GradientPaint(
				    0, 0, color1,
				    0, h, color2);

				g2d.setPaint(gp);
				g2d.fillRect(0, 0, w, h);
			}
		};
		bgPanel.setLayout(new GridBagLayout());

		GridBagConstraints gbc = new GridBagConstraints();
		
		gbc.fill = GridBagConstraints.NONE;
		gbc.weightx = 0.0f; 
		gbc.gridwidth = 1;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.insets = new Insets(10, 10, 0, 0);

		// image label
		JLabel imgLabel = new JLabel(new ImageIcon(getClass().getResource("/images/searchfolder.png")));
		gbc.gridx = 0;  
		gbc.gridy = 0;  
		gbc.gridheight = 3;
		bgPanel.add(imgLabel, gbc);
		
		// title text
		JLabel titleLabel = new JLabel(LocaleMessage.getString("songinfo.songinfo"));
		titleLabel.setFont(new Font("Calibri", Font.BOLD, 16));
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0f;
		gbc.insets = new Insets(10, 10, 10, 10);
		gbc.gridx = 1;  
		gbc.gridy = 0;  
		gbc.gridheight = 1;
		bgPanel.add(titleLabel, gbc);
		
		// file information box
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(0, 10, 10, 10);
		gbc.gridy = 1;
		bgPanel.add(createFileInfoPanel(), gbc);

		// header information box
		gbc.gridy = 2;  
		bgPanel.add(createHeaderInfoPanel(), gbc);
		
		// id3 tag information box
		gbc.gridy = 3;  
		gbc.insets = new Insets(0, 10, 20, 10);
		bgPanel.add(createID3InfoPanel(), gbc);
		
		// empty label
		JPanel empty = new JPanel();
		empty.setOpaque(false);
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weighty = 0.3f;
		gbc.gridy = 4;
		bgPanel.add(empty, gbc);
		
		// add the panels to the window
		add(bgPanel, BorderLayout.CENTER);
		add(createButtonPanel(), BorderLayout.SOUTH);
	}
	
	/**
	 * @return creates and returns the file information JPanel
	 */
	private JPanel createFileInfoPanel()
	{
		JPanel p = new JPanel(new GridBagLayout()){
			private static final long serialVersionUID = -7042471450383036909L;

			@Override
			protected void paintComponent(Graphics g)
			{
				Graphics2D g2d = (Graphics2D) g;
				
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				
				int w = getWidth();
				int h = getHeight();
				
				Color c1 = Color.WHITE;
				Color c2 = Color.GRAY;
				int arc = 20;
				
				Paint p = g2d.getPaint();
				
				// Paint a gradient from top to bottom
				GradientPaint gp = new GradientPaint(
						0, 0, c1,
						0, h, c2);
				
				g2d.setPaint(gp);
				g2d.fillRoundRect(0, 0, w, h, arc, arc);
				g2d.setPaint(p);
				g2d.setColor(c2.darker());
				g2d.drawRoundRect(0, 0, w-1, h-1, arc, arc);
			}
		};
		
		GridBagConstraints gbc1 = new GridBagConstraints();
		GridBagConstraints gbc2 = new GridBagConstraints();
		
		gbc1.anchor = GridBagConstraints.NORTHWEST;
		gbc1.fill = GridBagConstraints.NONE;
		gbc1.insets = new Insets(10, 10, 10, 10);
		gbc1.gridx = 0;
		gbc1.gridy = 0;
		gbc1.gridwidth = 2;
		
		// box title
		JLabel boxTitle = new JLabel("<HTML><b>" + LocaleMessage.getString("songinfo.fileinfo") + "</b></HTML>");
		p.add(boxTitle, gbc1);
		
		// the file name
		gbc1.insets = new Insets(0, 10, 0, 0);
		gbc1.anchor = GridBagConstraints.LINE_START;
		gbc1.gridwidth = 1;
		gbc1.gridy = 1;
		p.add(new JLabel(LocaleMessage.getString("songinfo.filename") + ":"), gbc1);
		
		gbc2.anchor = GridBagConstraints.LINE_START;
		gbc2.fill = GridBagConstraints.HORIZONTAL;
		gbc2.weightx = 1.0f;
		gbc2.insets = new Insets(0, 10, 0, 10);
		gbc2.gridx = 1;
		gbc2.gridy = 1;
		
		fileNameLabel = new JLabel();
		p.add(fileNameLabel, gbc2);
		
		// the file path
		gbc1.gridy = 2;
		p.add(new JLabel(LocaleMessage.getString("searchcover.path")), gbc1);
		
		gbc2.gridy = 2;
		filePathLabel = new JLabel();
		p.add(filePathLabel, gbc2);
		
		// the file size
		gbc1.gridy = 3;
		p.add(new JLabel(LocaleMessage.getString("songinfo.size") + ":"), gbc1);
		
		gbc2.gridy = 3;
		fileSizeLabel = new JLabel();
		p.add(fileSizeLabel, gbc2);
		
		// the file date
		gbc1.gridy = 4;
		gbc1.insets = new Insets(0, 10, 10, 0);
		p.add(new JLabel(LocaleMessage.getString("songinfo.date") + ":"), gbc1);
		
		gbc2.insets = new Insets(0, 10, 10, 10);
		gbc2.gridy = 4;
		fileDateLabel = new JLabel();
		p.add(fileDateLabel, gbc2);
		
		return p;
	}
	
	/**
	 * @return creates and returns the mp3 header information JPanel
	 */
	private JPanel createHeaderInfoPanel()
	{
		JPanel p = new JPanel(new GridBagLayout()){
			private static final long serialVersionUID = -4878739647942395043L;

			@Override
			protected void paintComponent(Graphics g)
			{
				Graphics2D g2d = (Graphics2D) g;
				
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
						RenderingHints.VALUE_ANTIALIAS_ON);
				
				int w = getWidth();
				int h = getHeight();
				
				Color c1 = Color.WHITE;
				Color c2 = Color.GRAY;
				int arc = 20;
				
				Paint p = g2d.getPaint();
				
				// Paint a gradient from top to bottom
				GradientPaint gp = new GradientPaint(
						0, 0, c1,
						0, h, c2);
				
				g2d.setPaint(gp);
				g2d.fillRoundRect(0, 0, w, h, arc, arc);
				g2d.setPaint(p);
				g2d.setColor(c2.darker());
				g2d.drawRoundRect(0, 0, w-1, h-1, arc, arc);
			}
		};
		
		GridBagConstraints gbc1 = new GridBagConstraints();
		GridBagConstraints gbc2 = new GridBagConstraints();
		
		gbc1.anchor = GridBagConstraints.NORTHWEST;
		gbc1.fill = GridBagConstraints.NONE;
		gbc1.insets = new Insets(10, 10, 10, 10);
		gbc1.gridx = 0;
		gbc1.gridy = 0;
		gbc1.gridwidth = 2;
		
		// box title
		JLabel boxTitle = new JLabel("<HTML><b>" + LocaleMessage.getString("songinfo.headerinfo") + "</b></HTML>");
		p.add(boxTitle, gbc1);
		
		// the bit rate
		gbc1.insets = new Insets(0, 10, 0, 0);
		gbc1.anchor = GridBagConstraints.LINE_START;
		gbc1.gridwidth = 1;
		gbc1.gridy = 1;
		p.add(new JLabel(LocaleMessage.getString("songinfo.bitrate") + ":"), gbc1);
		
		gbc2.anchor = GridBagConstraints.LINE_START;
		gbc2.fill = GridBagConstraints.HORIZONTAL;
		gbc2.weightx = 1.0f;
		gbc2.insets = new Insets(0, 10, 0, 10);
		gbc2.gridx = 1;
		gbc2.gridy = 1;
		
		bitrateLabel = new JLabel();
		p.add(bitrateLabel, gbc2);
		
		// the channels
		gbc1.gridy = 2;
		p.add(new JLabel(LocaleMessage.getString("songinfo.channels") + ":"), gbc1);
		
		gbc2.gridy = 2;
		channelsLabel = new JLabel();
		p.add(channelsLabel, gbc2);
		
		// the encoding
		gbc1.gridy = 3;
		p.add(new JLabel(LocaleMessage.getString("songinfo.encoding") + ":"), gbc1);
		
		gbc2.gridy = 3;
		encodingLabel = new JLabel();
		p.add(encodingLabel, gbc2);
		
		// the format
		gbc1.gridy = 4;
		p.add(new JLabel(LocaleMessage.getString("songinfo.format") + ":"), gbc1);
		
		gbc2.gridy = 4;
		formatLabel = new JLabel();
		p.add(formatLabel, gbc2);
		
		// the sample rate
		gbc1.gridy = 5;
		p.add(new JLabel(LocaleMessage.getString("songinfo.frequency") + ":"), gbc1);
		
		gbc2.gridy = 5;
		samplerateLabel = new JLabel();
		p.add(samplerateLabel, gbc2);
		
		// the song's duration
		gbc1.gridy = 6;
		gbc1.insets = new Insets(0, 10, 10, 0);
		p.add(new JLabel(LocaleMessage.getString("songinfo.duration") + ":"), gbc1);
		
		gbc2.insets = new Insets(0, 10, 10, 10);
		gbc2.gridy = 6;
		lengthLabel = new JLabel();
		p.add(lengthLabel, gbc2);
		
		return p;
	}
	
	/**
	 * @return creates and returns the mp3 id3 information JPanel
	 */
	private JPanel createID3InfoPanel()
	{
		JPanel p = new JPanel(new GridBagLayout()){
			private static final long serialVersionUID = 1131728110538127378L;

			@Override
			protected void paintComponent(Graphics g)
			{
				Graphics2D g2d = (Graphics2D) g;
				
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
						RenderingHints.VALUE_ANTIALIAS_ON);
				
				int w = getWidth();
				int h = getHeight();
				
				Color c1 = Color.WHITE;
				Color c2 = Color.GRAY;
				int arc = 20;
				
				Paint p = g2d.getPaint();
				
				// Paint a gradient from top to bottom
				GradientPaint gp = new GradientPaint(
						0, 0, c1,
						0, h, c2);
				
				g2d.setPaint(gp);
				g2d.fillRoundRect(0, 0, w, h, arc, arc);
				g2d.setPaint(p);
				g2d.setColor(c2.darker());
				g2d.drawRoundRect(0, 0, w-1, h-1, arc, arc);
			}
		};
		
		GridBagConstraints gbc1 = new GridBagConstraints();
		GridBagConstraints gbc2 = new GridBagConstraints();
		
		gbc1.anchor = GridBagConstraints.NORTHWEST;
		gbc1.fill = GridBagConstraints.NONE;
		gbc1.insets = new Insets(10, 10, 10, 10);
		gbc1.gridx = 0;
		gbc1.gridy = 0;
		gbc1.gridwidth = 2;
		
		// box title
		JLabel boxTitle = new JLabel("<HTML><b>" + LocaleMessage.getString("songinfo.id3info") + "</b></HTML>");
		p.add(boxTitle, gbc1);
		
		// the title
		gbc1.insets = new Insets(0, 10, 0, 0);
		gbc1.anchor = GridBagConstraints.LINE_START;
		gbc1.gridwidth = 1;
		gbc1.gridy = 1;
		p.add(new JLabel(LocaleMessage.getString("common.title") + ":"), gbc1);
		
		gbc2.anchor = GridBagConstraints.LINE_START;
		gbc2.fill = GridBagConstraints.HORIZONTAL;
		gbc2.weightx = 1.0f;
		gbc2.insets = new Insets(0, 10, 0, 10);
		gbc2.gridx = 1;
		gbc2.gridy = 1;
		
		titleLabel = new JLabel();
		p.add(titleLabel, gbc2);
		
		// the artist
		gbc1.gridy = 2;
		p.add(new JLabel(LocaleMessage.getString("common.artist") + ":"), gbc1);
		
		gbc2.gridy = 2;
		artistLabel = new JLabel();
		p.add(artistLabel, gbc2);
		
		// the album
		gbc1.gridy = 3;
		p.add(new JLabel(LocaleMessage.getString("common.album") + ":"), gbc1);
		
		gbc2.gridy = 3;
		albumLabel = new JLabel();
		p.add(albumLabel, gbc2);
		
		// the track#
		gbc1.gridy = 4;
		p.add(new JLabel(LocaleMessage.getString("common.trackno") + ":"), gbc1);
		
		gbc2.gridy = 4;
		tracknoLabel = new JLabel();
		p.add(tracknoLabel, gbc2);
		
		// the year
		gbc1.gridy = 5;
		p.add(new JLabel(LocaleMessage.getString("common.year") + ":"), gbc1);
		
		gbc2.gridy = 5;
		yearLabel = new JLabel();
		p.add(yearLabel, gbc2);
		
		// the genre
		gbc1.gridy = 6;
		p.add(new JLabel(LocaleMessage.getString("common.genre") + ":"), gbc1);
		
		gbc2.gridy = 6;
		genreLabel = new JLabel();
		p.add(genreLabel, gbc2);
		
		gbc1.insets = new Insets(10, 10, 10, 0);
		gbc1.gridx = 0;
		gbc1.gridy = 7;
		gbc1.gridwidth = 2;
		JButton editButton = new JButton(LocaleMessage.getString("common.edit"));
		editButton.setToolTipText(LocaleMessage.getString("common.edit_tooltip"));
		editButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				List<String> song = new ArrayList<String>();
				song.add(songFile.getName());
				int albumId = window.getPlaylist().getCurrentlySelectedSongAlbumId();
				String albumPath = window.getCurrentFolderPath();
				new EditId3TagFrame(window, albumId, albumPath, song);
				dispose();
			}
		});
		p.add(editButton, gbc1);
		
		return p;
	}
	
	/**
	 * @return creates and returns the button JPanel
	 */
	private JPanel createButtonPanel()
	{
		JPanel p = new JPanel();
		
		JButton closeButt = new JButton(UIManager.getString("InternalFrameTitlePane.closeButtonText"));
		closeButt.setToolTipText(LocaleMessage.getString("common.close_tooltip"));
		closeButt.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0)
			{
				dispose();
			}
		});
		
		p.setLayout(new FlowLayout(FlowLayout.RIGHT));
		p.add(closeButt);
		p.setBorder(new VariableLineBorder(5, 5, 5, 5, Color.GRAY, 1, true, false, false, 
				false));
		return p;
	}
	
	/**
	 * Loads all information for the song file.
	 */
	private void loadSongInfo()
	{
		AudioFile f;
		try
		{
			f = AudioFileIO.read(this.songFile);
			Tag tag = f.getTag();
			AudioHeader h = f.getAudioHeader();
			
			bitrate = h.getBitRateAsNumber();
			channels = h.getChannels();
			encoding = h.getEncodingType();
			format = h.getFormat();
			samplerate = h.getSampleRateAsNumber();
			length =  h.getTrackLength();
			mins = length / 60;
			secs = length % 60;
			title = tag.getFirstTitle();
			artist = tag.getFirstArtist();
			trackno = tag.getFirstTrack();
			year = tag.getFirstYear();
			album = tag.getFirstAlbum();
			genre = tag.getFirstGenre();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Fills in the information into the text fields.
	 */
	private void setSongInfoFields()
	{
		// file information
		this.fileNameLabel.setText(this.filename);
		this.filePathLabel.setText(this.filepath);
		// cut after two numbers after point
		DecimalFormat f = new DecimalFormat("#.00");
		String sizeString = f.format(this.filesize);
		this.fileSizeLabel.setText(sizeString + " MB");
		this.fileDateLabel.setText(this.filedate);
		
		// header information
		this.bitrateLabel.setText(this.bitrate + " kB/s");
		this.channelsLabel.setText(this.channels);
		this.encodingLabel.setText(this.encoding);
		this.formatLabel.setText(this.format);
		this.samplerateLabel.setText(this.samplerate + " Hz");
		String secString = Integer.toString(secs);
		if(secString.length() > 2) secString = secString.substring(0, 2);
		else if(secString.length() < 2) secString = "0" + secString;
		this.lengthLabel.setText(mins + ":" + secString);
		
		// id3 tag information
		this.titleLabel.setText(this.title);
		this.artistLabel.setText(this.artist);
		this.tracknoLabel.setText(this.trackno);
		this.yearLabel.setText(this.year); 
		this.albumLabel.setText(this.album); 
		this.genreLabel.setText(this.genre);
	}
}