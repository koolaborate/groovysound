package ui.editid3tag;

import helper.LocaleMessage;
import helper.StringUtils;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.apache.log4j.Logger;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.id3.AbstractID3v2Tag;
import org.jaudiotagger.tag.id3.ID3v1Tag;
import org.jaudiotagger.tag.id3.ID3v24Tag;
import ui.common.VariableLineBorder;
import ui.mainwindow.MainWindow;

/***********************************************************************************
 * EditId3TagFrame                                                                 *
 ***********************************************************************************
 * A window that enables the used to edit the id3 tags of one or multiple mp3 song *
 * files.                                                                          *
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
public class EditId3TagFrame extends JFrame
{
	private MainWindow window;
	
	// song tag information
	private JTextField titleText, artistText, tracknoText, yearText, albumText, genreText;
	private JCheckBox cTitle, cArtist, cTrackno, cYear, cAlbum, cGenre;
	
	private boolean multipleSongs = false;
	
	private int albumId = -1;
	private String albumPath;
	private List<String> songFileNames;
	
	/** the log4j logger */
	static Logger log = Logger.getLogger(EditId3TagFrame.class.getName());
	
	/**
	 * Constructor.
	 * 
	 * @param window reference to the main window
	 * @param albumId the id of the album the song(s) belong to
	 * @param albumPath the path to the album folder
	 * @param songPath the list of song file paths to be edited (may only contain one 
	 *        element)
	 */
	public EditId3TagFrame(MainWindow window, int albumId, String albumPath, 
			List<String> songFileNames)
	{
		this.window = window;
		this.albumId = albumId;
		this.albumPath = albumPath;
		this.songFileNames = songFileNames;
		
		if(songFileNames.size() > 1) multipleSongs = true;

		setIconImage(new ImageIcon(getClass().getResource("/images/tag.png")).getImage());

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setTitle(LocaleMessage.getString("id3.edit_tags"));
		
		SwingUtilities.invokeLater(new Runnable(){
			public void run()
			{
				initGUI();
				setSize(500, 320);
				setLocationRelativeTo(null);
				setVisible(true);
				loadSongInfos();
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

		GridBagConstraints gbcLabel = new GridBagConstraints();
		GridBagConstraints gbcCheckbox = new GridBagConstraints();
		GridBagConstraints gbcTextField = new GridBagConstraints();
		
		gbcLabel.fill = GridBagConstraints.NONE;
		gbcLabel.weightx = 0.0f; 
		gbcLabel.gridwidth = 1;
		gbcLabel.anchor = GridBagConstraints.NORTHWEST;
		gbcLabel.insets = new Insets(10, 10, 0, 0);

		gbcCheckbox.fill = GridBagConstraints.NONE;
		gbcCheckbox.weightx = 0.0f; 
		gbcCheckbox.gridwidth = 1;
		gbcCheckbox.anchor = GridBagConstraints.NORTHWEST;
		gbcCheckbox.insets = new Insets(10, 10, 0, 0);
		
		gbcTextField.fill = GridBagConstraints.HORIZONTAL;
		gbcTextField.weightx = 1.0f; 
		gbcTextField.gridwidth = 2;
		gbcTextField.anchor = GridBagConstraints.NORTHWEST;
		gbcTextField.insets = new Insets(10, 10, 0, 10);
		
		// title
		JLabel tLabel = new JLabel(LocaleMessage.getString("common.title") + ":");
		gbcLabel.gridx = 0;
		gbcLabel.gridy = 0;
		bgPanel.add(tLabel, gbcLabel);
		
		cTitle = new JCheckBox();
		cTitle.setOpaque(false);
		gbcCheckbox.gridx = 1; 
		gbcCheckbox.gridy = 0;
		bgPanel.add(cTitle, gbcCheckbox);
		cTitle.addChangeListener(new ChangeListener(){
			public void stateChanged(ChangeEvent e)
			{
				JCheckBox s = (JCheckBox) e.getSource();
				setComponentEnabled(titleText, s.isSelected());
			}
		});
		
		titleText = new JTextField();
		gbcTextField.gridx = 2; 
		gbcTextField.gridy = 0;
		bgPanel.add(titleText, gbcTextField);
		
		// artist
		JLabel aLabel = new JLabel(LocaleMessage.getString("common.artist") + ":");
		gbcLabel.gridy = 1;
		bgPanel.add(aLabel, gbcLabel);
		
		cArtist = new JCheckBox();
		cArtist.setOpaque(false);
		gbcCheckbox.gridy = 1;
		bgPanel.add(cArtist, gbcCheckbox);
		cArtist.addChangeListener(new ChangeListener(){
			public void stateChanged(ChangeEvent e)
			{
				JCheckBox s = (JCheckBox) e.getSource();
				setComponentEnabled(artistText, s.isSelected());
			}
		});
		
		artistText = new JTextField();
		gbcTextField.gridy = 1;
		bgPanel.add(artistText, gbcTextField);
		
		// album
		JLabel alLabel = new JLabel(LocaleMessage.getString("common.album") + ":");
		gbcLabel.gridy = 2;
		bgPanel.add(alLabel, gbcLabel);
		
		cAlbum = new JCheckBox();
		cAlbum.setOpaque(false);
		gbcCheckbox.gridy = 2;
		bgPanel.add(cAlbum, gbcCheckbox);
		cAlbum.addChangeListener(new ChangeListener(){
			public void stateChanged(ChangeEvent e)
			{
				JCheckBox s = (JCheckBox) e.getSource();
				setComponentEnabled(albumText, s.isSelected());
			}
		});
		
		albumText = new JTextField();
		gbcTextField.gridy = 2;
		bgPanel.add(albumText, gbcTextField);
		
		// track#
		JLabel trLabel = new JLabel(LocaleMessage.getString("common.trackno") + ":"); 
		gbcLabel.gridy = 3;
		bgPanel.add(trLabel, gbcLabel);
		
		cTrackno = new JCheckBox();
		cTrackno.setOpaque(false);
		gbcCheckbox.gridy = 3;
		bgPanel.add(cTrackno, gbcCheckbox);
		cTrackno.addChangeListener(new ChangeListener(){
			public void stateChanged(ChangeEvent e)
			{
				JCheckBox s = (JCheckBox) e.getSource();
				setComponentEnabled(tracknoText, s.isSelected());
			}
		});
		
		tracknoText = new JTextField();
		gbcTextField.gridy = 3;
		bgPanel.add(tracknoText, gbcTextField);
		
		// year
		JLabel yLabel = new JLabel(LocaleMessage.getString("common.year") + ":");
		gbcLabel.gridy = 4;
		bgPanel.add(yLabel, gbcLabel);
		
		cYear = new JCheckBox();
		cYear.setOpaque(false);
		gbcCheckbox.gridy = 4;
		bgPanel.add(cYear, gbcCheckbox);
		cYear.addChangeListener(new ChangeListener(){
			public void stateChanged(ChangeEvent e)
			{
				JCheckBox s = (JCheckBox) e.getSource();
				setComponentEnabled(yearText, s.isSelected());
			}
		});
		
		yearText = new JTextField();
		gbcTextField.gridy = 4;
		bgPanel.add(yearText, gbcTextField);
		
		// genre
		JLabel gLabel = new JLabel(LocaleMessage.getString("common.genre") + ":"); 
		gbcLabel.gridy = 5;
		bgPanel.add(gLabel, gbcLabel);
		
		cGenre = new JCheckBox();
		cGenre.setOpaque(false);
		gbcCheckbox.gridy = 5;
		bgPanel.add(cGenre, gbcCheckbox);
		cGenre.addChangeListener(new ChangeListener(){
			public void stateChanged(ChangeEvent e)
			{
				JCheckBox s = (JCheckBox) e.getSource();
				setComponentEnabled(genreText, s.isSelected());
			}
		});
		
		genreText = new JTextField();
		gbcTextField.gridy = 5;
		bgPanel.add(genreText, gbcTextField);
		
		// add the panels to the window
		add(bgPanel, BorderLayout.CENTER);
		add(createButtonPanel(), BorderLayout.SOUTH);
	}

	/**
	 * Sets the given component to enabled (and editable) or not.
	 * 
	 * @param c the component to be enabled or disabled
	 * @param b <code>true</code> if it shall be enabled, <code>false</code> otherwise
	 */
	private void setComponentEnabled(JComponent c, boolean b)
	{
		if(c instanceof JTextField)
		{
			JTextField t = (JTextField)c;
			t.setEditable(b);
			t.setEnabled(b);
		}
		else if(c instanceof JCheckBox)
		{
			JCheckBox check = (JCheckBox)c;
			check.setSelected(b);
		}
	}
	
	/**
	 * @return creates and returns a button JPanel
	 */
	private JPanel createButtonPanel()
	{
		JPanel p = new JPanel();
		p.setBorder(new VariableLineBorder(5, 5, 5, 5, Color.GRAY, 1, true, false, false,
				false));
		
		JButton saveButt = new JButton(UIManager.getString("FileChooser.saveButtonText")); 
		saveButt.setToolTipText(LocaleMessage.getString("common.save_tooltip"));
		saveButt.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0)
			{
				saveChanges();
				dispose();
			}
		});
		
		JButton closeButt = new JButton(LocaleMessage.getString("common.abort"));
		closeButt.setToolTipText(LocaleMessage.getString("common.abort_tooltip"));
		closeButt.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0)
			{
				dispose();
			}
		});
		
		p.setLayout(new FlowLayout(FlowLayout.RIGHT));
		p.add(saveButt);
		p.add(closeButt);
		return p;
	}
	
	/**
	 * Loads the values for the different text fields from the ID3 tags.
	 */
	private void loadSongInfos()
	{
		// first disable all text fields
		titleText.setEnabled(false);
		artistText.setEnabled(false);
		tracknoText.setEnabled(false);
		yearText.setEnabled(false);
		albumText.setEnabled(false);
		genreText.setEnabled(false);
		// also set all checkboxes to unchecked (meaning: don't make any changes)
		setComponentEnabled(cTitle, false);
		setComponentEnabled(cArtist, false);
		setComponentEnabled(cTrackno, false);
		setComponentEnabled(cAlbum, false);
		setComponentEnabled(cYear, false);
		setComponentEnabled(cGenre, false);
		
		if(multipleSongs)
		{
			String different = LocaleMessage.getString("common.different");
			String currentArtist = "", currentAlbum = "", currentGenre = "", currentYear = "";
			
			for(String songFileName : songFileNames)
			{
				String songFilePath = albumPath + File.separator + songFileName;
				File file = new File(songFilePath);
				if(file.exists())
				{
					try
					{
						AudioFile f = AudioFileIO.read(file);
						Tag tag = f.getTag();
						
						// if there are not already different artists
						if(!currentArtist.equals(different))
						{
							// get the artist which is set in the song
							String artist = tag.getFirstArtist().trim();
							// if no artist is given yet, take the actual one
							if(StringUtils.isNullOrEmpty(currentArtist)) currentArtist = artist;
							// if the artists differ, set current artist to 'different values'
							else if(!currentArtist.equals(artist))
							{
								currentArtist = different;
							}
						}
						
						if(!currentAlbum.equals(different))
						{
							String album = tag.getFirstAlbum().trim();
							if(StringUtils.isNullOrEmpty(currentAlbum)) currentAlbum = album;
							else if(!currentAlbum.equals(album))
							{
								currentAlbum = different;
							}
						}
						
						if(!currentYear.equals(different))
						{
							String year = tag.getFirstYear().trim();
							if(StringUtils.isNullOrEmpty(currentYear)) currentYear = year;
							else if(!currentYear.equals(year))
							{
								currentYear = different;
							}
						}
						
						if(!currentGenre.equals(different))
						{
							String genre = tag.getFirstGenre().trim();
							if(StringUtils.isNullOrEmpty(currentGenre)) currentGenre = genre;
							else if(!currentGenre.equals(genre))
							{
								currentGenre = different;
							}
						}
					}
					catch(Exception e)
					{
						log.error(e.getMessage());
					}
				}
			}
			
			// the track number and the song's title will always be different when there 
			// are multiple files...
			String trackNo = different;
			tracknoText.setText(trackNo);
			String title = different;
			titleText.setText(title);
			
			artistText.setText(currentArtist);
			albumText.setText(currentAlbum);
			yearText.setText(currentYear);
			genreText.setText(currentGenre);
		}
		// if it is only one song to be edited
		else
		{
			File file = new File(albumPath + File.separator + songFileNames.get(0));
			if(file.exists())
			{
				try
				{
					MP3File f = (MP3File)AudioFileIO.read(file);
					ID3v1Tag v1Tag = f.getID3v1Tag();
					String title = "", artist = "", track = "", year = "", album = "", 
						genre = "";
					
					if(f.hasID3v2Tag())
					{
						AbstractID3v2Tag v2Tag = f.getID3v2Tag();
						title = v2Tag.getFirstTitle().trim();
						artist = v2Tag.getFirstArtist().trim();
						track = v2Tag.getFirstTrack().trim();
						year = v2Tag.getFirstYear().trim();
						album = v2Tag.getFirstAlbum().trim();
						genre = v2Tag.getFirstGenre().trim();
					}

					// get ID3v1 entries in case ID3v2 was not available
					if(StringUtils.isNullOrEmpty(title)) title = getFromID3v1Tag(v1Tag.getTitle());
					if(StringUtils.isNullOrEmpty(artist)) artist = getFromID3v1Tag(v1Tag.getArtist());
//					if(StringUtils.isNullOrEmpty(track)) track = getFromID3v1Tag(v1Tag.getTrack());
					if(StringUtils.isNullOrEmpty(year)) year = getFromID3v1Tag(v1Tag.getYear());
					if(StringUtils.isNullOrEmpty(album)) album = getFromID3v1Tag(v1Tag.getAlbum());
					if(StringUtils.isNullOrEmpty(genre)) genre = getFromID3v1Tag(v1Tag.getGenre());

					// set the read values
					titleText.setText(title);
					artistText.setText(artist);
					tracknoText.setText(track);
					yearText.setText(year);
					albumText.setText(album);
					genreText.setText(genre);
				}
				catch(Exception e)
				{
					log.error(e.getMessage());
				}
			}
		}
	}
	
	/**
	 * Method tries to retrieve information from the ID3v1 tag. If the given list is
	 * empty or <code>null</code> an empty String is returned.
	 * 
	 * @param tagList the list of elements for the given tag, may be empty or <code>null</code>
	 * @return the content of the first element of the list or an empty String
	 */
	@SuppressWarnings("unchecked")
	private String getFromID3v1Tag(List tagList)
	{
		if(tagList == null || tagList.size() < 1) return "";
		return ((String)tagList.get(0)).trim();
	}
	
	/**
	 * Saves the changes to the file(s).
	 */
	private void saveChanges()
	{
		// first write the actual file information
		for(String songFileName : songFileNames)
		{
			String songFilePath = albumPath + File.separator + songFileName;
			File file = new File(songFilePath);
			
			// maybe the song name has been modified to be able to save the name into the database
			// therefore, 's have been changed to `s#
			if(!file.exists() && songFilePath.contains("`"))
			{
				songFilePath = songFilePath.replaceAll("`", "'");
				file = new File(songFilePath);
			}
			
			if(file.exists())
			{
				try
				{
					MP3File f = (MP3File)AudioFileIO.read(file);
					ID3v1Tag v1Tag = f.getID3v1Tag();
					AbstractID3v2Tag v2Tag;

					String oldTitle = "", oldArtist = "", oldTrack = "", oldYear = "", 
						oldAlbum = "", oldGenre = "";
					
					if(f.hasID3v2Tag())
					{
						v2Tag = f.getID3v2Tag();
						oldTitle = v2Tag.getFirstTitle().trim();
						oldArtist = v2Tag.getFirstArtist().trim();
						oldTrack = v2Tag.getFirstTrack().trim();
						oldYear = v2Tag.getFirstYear().trim();
						oldAlbum = v2Tag.getFirstAlbum().trim();
						oldGenre = v2Tag.getFirstGenre().trim();
					}
					else // otherwise: create an ID3v2 tag
					{
						v2Tag = new ID3v24Tag(); 
					}
					
					// get ID3v1 entries in case ID3v2 was not available
					if(StringUtils.isNullOrEmpty(oldTitle)) oldTitle = getFromID3v1Tag(v1Tag.getTitle());
					if(StringUtils.isNullOrEmpty(oldArtist)) oldArtist = getFromID3v1Tag(v1Tag.getArtist());
//					if(StringUtils.isNullOrEmpty(oldTrack)) oldTrack = getFromID3v1Tag(v1Tag.getTrack());
					if(StringUtils.isNullOrEmpty(oldYear)) oldYear = getFromID3v1Tag(v1Tag.getYear());
					if(StringUtils.isNullOrEmpty(oldAlbum)) oldAlbum = getFromID3v1Tag(v1Tag.getAlbum());
					if(StringUtils.isNullOrEmpty(oldGenre)) oldGenre = getFromID3v1Tag(v1Tag.getGenre());
					
					log.debug("Processing file " + file.getAbsolutePath() + "...");
					
					// if the title shall be changed
					if(cTitle.isSelected() && !StringUtils.isNullOrEmpty(titleText.getText().trim()))
					{
						String newTitle = titleText.getText().trim();
						// only make changes if the new title is any different
						if(!newTitle.equals(oldTitle))
						{
							v1Tag.setTitle(newTitle);
							v2Tag.setTitle(newTitle);
							log.debug("Setting new title to " + newTitle);
						}
					}
					
					// if the artist shall be changed
					if(cArtist.isSelected() && !StringUtils.isNullOrEmpty(artistText.getText().trim()))
					{
						String newArtist = artistText.getText().trim();
						// only make changes if the new artist is any different
						if(!newArtist.equals(oldArtist))
						{
							v1Tag.setArtist(newArtist);
							v2Tag.setArtist(newArtist);
							log.debug("Setting new artist to " + newArtist);
						}
					}
					
					// if the track# shall be changed
					if(cTrackno.isSelected() && !StringUtils.isNullOrEmpty(tracknoText.getText().trim()))
					{
						String newTrackno = tracknoText.getText().trim();
						// only make changes if the new track number is any different
						if(!newTrackno.equals(oldTrack))
						{
//							v1Tag.setTrack(newTrackno);
							v2Tag.setTrack(newTrackno);
							log.debug("Setting new track# to " + newTrackno);
						}
					}
					
					// if the year shall be changed
					if(cYear.isSelected() && !StringUtils.isNullOrEmpty(yearText.getText().trim()))
					{
						String newYear = yearText.getText().trim();
						// only make changes if the new year is any different
						if(!newYear.equals(oldYear))
						{
							v1Tag.setYear(newYear);
							v2Tag.setYear(newYear);
							log.debug("Setting new year to " + newYear);
						}
					}
					
					// if the album shall be changed
					if(cAlbum.isSelected() && !StringUtils.isNullOrEmpty(albumText.getText().trim()))
					{
						String newAlbum = albumText.getText().trim();
						// only make changes if the new album is any different
						if(!newAlbum.equals(oldAlbum))
						{
							v1Tag.setAlbum(newAlbum);
							v2Tag.setAlbum(newAlbum);
							log.debug("Setting new album to " + newAlbum);
						}
					}
					
					// if the genre shall be changed
					if(cGenre.isSelected() && !StringUtils.isNullOrEmpty(genreText.getText().trim()))
					{
						String newGenre = genreText.getText().trim();
						// only make changes if the new genre is any different
						if(!newGenre.equals(oldGenre))
						{
							v1Tag.setGenre(newGenre);
							v2Tag.setGenre(newGenre);
							log.debug("Setting new genre to " + newGenre);
						}
					}
					
					// if the file did not yet have an ID3v2 tag
					if(!f.hasID3v2Tag())
					{
						f.setID3v2Tag(v2Tag);
					}
					
					// save the settings in the file
					f.commit();
				}
				catch(Exception e)
				{
					log.error("Unable to set new values for file " + file.getAbsolutePath() + 
							". Error message: " + e.getMessage());
				}
			}
			// if file does not exist
			else
			{
				log.debug(file.getAbsolutePath() + " does not exist! Unable to set new values.");
			}
		}
		
		// then update the database entries 
		window.getDatabase().updateSongs(albumId, albumPath, songFileNames);
		
		// refresh the view since the song titles may have changed...
		window.getPlaylist().refreshSongList(); 
	}
}