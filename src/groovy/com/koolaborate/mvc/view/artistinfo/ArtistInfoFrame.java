package com.koolaborate.mvc.view.artistinfo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

import org.jdesktop.swingx.JXBusyLabel;

import com.koolaborate.model.Artist;
import com.koolaborate.mvc.view.dialogs.VistaDialog;
import com.koolaborate.mvc.view.mainwindow.GhostDragGlassPane;
import com.koolaborate.mvc.view.mainwindow.MainWindow;
import com.koolaborate.util.GraphicsUtilities;
import com.koolaborate.util.GraphicsUtilities2;
import com.koolaborate.util.HTMLparser;
import com.koolaborate.util.LocaleMessage;

/***********************************************************************************
 * ArtistInfoFrame                                                                 *
 ***********************************************************************************
 * A window that shows detailed information about an artist. A textual description *
 * and an image is shown. The textual description can be retrieved using a         *
 * Wikipedia(R) web search and can be edited afterwards. The image can be looked   *
 * up using a Google(R) image search or a local file can be given.                 *
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
public class ArtistInfoFrame extends JFrame implements DropTargetListener
{
	private String artistname;
	private JScrollPane scroll;
	private JEditorPane htmlPane;
	private JLabel img;
	private JButton editButton, saveButton, closeButton, changeImage;
	
	private boolean imageChanged = false;
	private boolean changesMade = false;
	
	private Artist artist;
	
	private JWindow window;
	private MainWindow mainWindow;
	private BufferedImage bigImage;
	
	/** use the glass pane for the preview thumbnail of a new cover image */
	private GhostDragGlassPane glassPane;
	private File imgFile;
	private BufferedImage image;
	private int maxWidth = 80;  // maximum width for the ghost image
	private int maxHeight = 80; // maximum height for the ghost image
	
	
	/**
	 * Constructor.
	 * 
	 * @param mainWindow reference to the main window
	 * @param artist the name of the artist or band
	 */
	public ArtistInfoFrame(MainWindow w, final String artistname)
	{
		this.mainWindow = w;
		this.artistname = artistname;
		
		SwingUtilities.invokeLater(new Runnable(){
			public void run()
			{
				initGUI();
			}
		});
	}

	
	/**
	 * Initializes the GUI elements.
	 */
	private void initGUI()
	{
		setTitle(LocaleMessage.getString("common.info_about") + " " + artistname);
		setSizeAccordingToScreen(80);
		setIconImage(new ImageIcon(getClass().getResource("/images/artist.png")).getImage());
		
		// define a drop target for the entire frame
		DropTarget dt = new DropTarget(this, this);
		this.setDropTarget(dt);
		glassPane = new GhostDragGlassPane();
		this.setGlassPane(glassPane);
		
		setLayout(new GridBagLayout());
		
		// the name of the artist
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.weightx = 1.0f;
		gbc.gridwidth = 2;
		gbc.insets = new Insets(10, 10, 0, 10);
		JLabel title = new JLabel(artistname);
		title.setFont(new Font("Calibri", Font.BOLD, 20));
		add(title, gbc);
		
		// the image of the artist
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.weightx = 0.0f;
		gbc.gridwidth = 1;
		gbc.insets = new Insets(0, 10, 4, 10);
		img = new JLabel();
		img.setPreferredSize(new Dimension(340, 320));
		img.setBorder(new LineBorder(Color.BLACK));
		add(img, gbc);
		
		// the change image button (at first invisible)
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.weightx = 0.0f;
		gbc.insets = new Insets(0, 10, 4, 10);
		changeImage = new JButton(LocaleMessage.getString("common.changeimg")); 
		changeImage.setToolTipText(LocaleMessage.getString("common.changeimg_tooltip"));
		changeImage.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				new SearchArtistImgFrame(mainWindow, getThisInstance(), artistname);
			}
		});
		changeImage.setVisible(false);
		add(changeImage, gbc);
		
		// the text to the artist
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.gridheight = 2;
		gbc.weightx = 1.0f;
		gbc.weighty = 1.0f;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.insets = new Insets(0, 0, 0, 0);
		htmlPane = new JEditorPane();
		htmlPane.setContentType("text/html");
		
		HTMLEditorKit kit = new HTMLEditorKit(); 
		StyleSheet ss = getStyleSheet();
		kit.setStyleSheet(ss); 
		htmlPane.setEditorKit(kit);
		htmlPane.setEditable(false);
		
		scroll = new JScrollPane(htmlPane);
		add(scroll, gbc);
		
		// the copyright notice
		gbc.gridx = 1;
		gbc.gridy = 3;
		gbc.gridheight = 1;
		gbc.gridwidth = 2;
		gbc.weightx = 1.0f;
		gbc.weighty = 0.0f;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.insets = new Insets(0, 0, 0, 0);
		
		JLabel copyright = new JLabel("<HTML>&copy; Wikipedia&reg;</HTML>");
		add(copyright, gbc);
		
		// the button panel
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 2, 2));
		
		editButton = new JButton(LocaleMessage.getString("common.edit"));
		editButton.setActionCommand("edit");
		editButton.setToolTipText(LocaleMessage.getString("common.edit_tooltip"));
		editButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				JButton s = (JButton) e.getSource();
				if(s.getActionCommand().equals("edit"))
				{
					setEditModeEnabled(true);
				}
				else
				{
					new Thread(new Runnable(){
						public void run()
						{
							showSearchingWindow();
							// update textual information
							searchWikipediaForInfo();
							window.dispose();
						}
					}).start();
				}
			}
		});
		saveButton = new JButton(UIManager.getString("FileChooser.saveButtonText"));
		saveButton.setToolTipText(LocaleMessage.getString("common.save_tooltip"));
		saveButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0)
			{
				setEditModeEnabled(false);
				saveChanges();
			}
		});
		saveButton.setEnabled(false);
		closeButton = new JButton(UIManager.getString("InternalFrameTitlePane.closeButtonText"));
		closeButton.setToolTipText(LocaleMessage.getString("common.close_tooltip"));
		closeButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0)
			{
				if(changesMade || imageChanged)
				{
					VistaDialog dialog = VistaDialog.showConfirmationDialog(LocaleMessage.getString("common.discard_title"), 
							LocaleMessage.getString("common.discard_label"), LocaleMessage.getString("common.discard_text"));
					if(dialog.yesSelected) dispose();
				}
				else
				{
					dispose();
				}
			}
		});
		
		buttonPanel.add(editButton);
		buttonPanel.add(saveButton);
		buttonPanel.add(closeButton);
		
		gbc.gridx = 0;
		gbc.gridy = 4;
		gbc.gridheight = 1;
		gbc.gridwidth = 2;
		gbc.weightx = 1.0f;
		gbc.weighty = 0.0f;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.insets = new Insets(0, 0, 0, 0);
		
		add(buttonPanel, gbc);
		
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		setVisible(true);
		
		SwingUtilities.invokeLater(new Runnable(){
			public void run()
			{
				searchForInfo();
			}
		});
	}
	
	
	/**
	 * Saves the made changes.
	 */
	private void saveChanges()
	{
		if(changesMade || imageChanged) //TODO maybe one boolean is enough...
		{
			if(artist != null)
			{
				artist.setPic(this.bigImage);
				artist.setDescription(htmlPane.getText());
				mainWindow.getDatabase().updateArtist(artist);
			}
			else
			{
				artist = new Artist();
				artist.setName(artistname);
				artist.setPic(this.bigImage);
				artist.setDescription(htmlPane.getText());
				artist.setName(artistname);
				int id = mainWindow.getDatabase().insertNewArtist(artistname, htmlPane.getText(), this.bigImage);
				artist.setId(id);
			}
			changesMade = false;
			imageChanged = false;
		}
	}
	
	
	/**
	 * Sets the window size as big as the screen size - a border.
	 * 
	 * @param border the border size (width and height)
	 */
	private void setSizeAccordingToScreen(int border)
	{
		// Get the size of the default screen
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		int winWidth = dim.width - border;
		int winHeight = dim.height - border;
		setSize(winWidth, winHeight);
	}


	/**
	 * Uses a Wikipedia(R) web search to determine the textual artist information.
	 */
	private void searchForInfo()
	{
		// first try to look in the database for information about the artist
		boolean infoInDb = false;
		
		artist = mainWindow.getDatabase().getArtistByName(artistname);
		if(artist != null) infoInDb = true;
		
		// if there is no information about the artist in the database, try to look it up in the web
		if(!infoInDb)
		{
			new Thread(new Runnable(){
				public void run()
				{
					showSearchingWindow();
					
					// textual information
					searchWikipediaForInfo();
					
					// artist image
					try
					{
						URL imgUrl = HTMLparser.getArtistImageFromGoogle(artistname);
						if(imgUrl != null)
						{
							BufferedImage bigImage = ImageIO.read(imgUrl);
							if(bigImage != null) 
							{
								setArtistImage(bigImage);
							}
						}
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
					changesMade = true;
					setEditModeEnabled(true);
					window.dispose();
				}
			}).start();
		}
		else
		{
			if(artist.getPic() != null) setArtistImage(artist.getPic());
			htmlPane.setText(artist.getDescription());
		}
		
		htmlPane.setCaretPosition(0);
	}
	
	
	/**
	 * Searches at Wikipedia for artist information.
	 */
	private void searchWikipediaForInfo()
	{
		htmlPane.setText(HTMLparser.getArtistInfoFromWikipedia(artistname));
	}
	
	
	/**
	 * Enables (or disables) the edit mode. If the edit mode is activated, the image can be changed and the
	 * text as well.
	 * Afterwards, the changes need to be saved.
	 * 
	 * @param b <code>true</code> if the edit mode shall be set, <code>false</code> if the viewing mode shall be set
	 */
	private void setEditModeEnabled(boolean b)
	{
		// enable/disable text changes
		htmlPane.setEditable(b);
		// enable/disable change image button
		changeImage.setVisible(b);
		
		if(b)
		{
			editButton.setText(LocaleMessage.getString("common.refresh"));
			editButton.setToolTipText(LocaleMessage.getString("artist.refresh_info"));
			editButton.setActionCommand("refresh");
		}
		else
		{
			editButton.setText(LocaleMessage.getString("common.edit"));
			editButton.setToolTipText(LocaleMessage.getString("artist.change_to_edit"));
			editButton.setActionCommand("edit");
		}
		
		saveButton.setEnabled(b);
	}
	
	
	/**
	 * Displays a window while the information is being searched.
	 */
	private void showSearchingWindow()
	{
		String text = LocaleMessage.getString("common.search_for") + " " + artistname + "...";
		final JXBusyLabel busy = new JXBusyLabel();
		busy.setText(text);
		busy.setBorder(new EmptyBorder(20, 10, 20, 10));
		busy.setBusy(true);
		JPanel busyBack = new JPanel();
		busyBack.setBackground(Color.WHITE);
		busyBack.setBorder(new LineBorder(Color.BLACK));
		busyBack.add(busy);
		window = new JWindow();
		window.add(busyBack);
		window.pack();
		window.setLocationRelativeTo(null);
		window.setAlwaysOnTop(true);
		
		SwingUtilities.invokeLater(new Runnable(){
			public void run()
			{
				window.setVisible(true);
			}
		});
	}
	
	
	/**
	 * Returns the stylesheet with the Wikipedia(R) style CSS definitions.
	 * 
	 * @return the stylesheet for the JEditorPane
	 */
	private StyleSheet getStyleSheet() 
	{
		StyleSheet myStyle = new StyleSheet();
		myStyle.addRule(".firstHeading {margin-bottom: .1em; line-height: 1.2em;padding-bottom: 0;}");
		myStyle.addRule(".mw-headline {font-size:1.2em; font-weight:bold; margin-bottom: .1em; line-height: 1.2em; padding-bottom: 0;}");
		
//		myStyle.addRule(".prettytable float-right{display:block; float:right; margin: 1em 1em 1em 0;background: #f9f9f9;border: 1px #aaa solid;border-collapse: collapse;}");
//		myStyle.addRule(".prettytable th, .prettytable td {border: 5px #aaa solid;padding: 0.2em;}");
//		myStyle.addRule(".prettytable th {background: #f2f2f2;text-align: center;}");
//		myStyle.addRule(".prettytable caption {margin-left: inherit; margin-right: inherit;font-weight: bold;}");
		
		return myStyle;
	}


	/**
	 * Sets the artist image with a white border.
	 * 
	 * @param bigImage the orignial BufferedImage which will be resized
	 */
	public void setArtistImage(BufferedImage bigImage)
	{
		this.bigImage = bigImage;
		BufferedImage image = GraphicsUtilities.createThumbnail(bigImage, 300); 
		BufferedImage imgWithBorder = GraphicsUtilities.createCompatibleImage(image.getWidth() + 40, image.getHeight() + 20);
		Graphics2D g2 = imgWithBorder.createGraphics();
		g2.setColor(Color.WHITE);
		g2.fillRect(0, 0, imgWithBorder.getWidth(), imgWithBorder.getHeight());
		g2.drawImage(image, 20, 10, null);
		g2.dispose();
		img.setIcon(new ImageIcon(imgWithBorder));
	}


	/**
	 * Sets the image changed status to b. If the image has been changed and the user closes the window,
	 * he/she is demanded whether or not he/she wants to discard or save the changes.
	 * 
	 * @param b whether or not to set the image changed status
	 */
	public void setImageChanged(boolean b)
	{
		this.imageChanged = b;
	}
	
	
	/**
	 * @return a reference to the instance of the class
	 */
	private ArtistInfoFrame getThisInstance()
	{
		return this;
	}
	
	
	/**
	 * Method is called when an object is being dragged onto the application window.
	 */
	@SuppressWarnings("unchecked")
	public void dragEnter(DropTargetDragEvent dtde)
	{
		// create image if it has not already been created
		if (image == null)
		{
            Transferable t = dtde.getTransferable();
            try
            {
                Object data = t.getTransferData(DataFlavor.javaFileListFlavor);
                List<File> fileList = (List<File>) data;
                BufferedImage image = createImage(fileList);
                if (image != null)
                {
                    Point p = MouseInfo.getPointerInfo().getLocation();
                    glassPane.showIt(image, p);
                }
            } 
            catch(UnsupportedFlavorException e)
            {
            	e.printStackTrace();
            } 
            catch(IOException e)
            {
            	e.printStackTrace();
            }
        }
	}
	
	
	/**
	 * Creates an image of the dragged file.
	 * 
	 * @param files the files being dragged onto the application
	 * @return an image of the first image file in the list
	 */
	private BufferedImage createImage(List<File> files) 
	{
		BufferedImage image = null;

		imgFile = new File("");
        for(File file : files)
        {
        	// accept jpeg, gif, bmp and png images
            if (file.getName().toLowerCase().endsWith(".png") ||
            	file.getName().toLowerCase().endsWith(".jpg") ||
            	file.getName().toLowerCase().endsWith(".jpeg")||
            	file.getName().toLowerCase().endsWith(".gif") ||
                file.getName().toLowerCase().endsWith(".bmp"))
            {
            	imgFile = file;
            	break;
            }
            else
            {
            	continue;
            }
        }
        
		try
		{
			BufferedImage origImg = ImageIO.read(imgFile);
			if(origImg == null)
			{
				return null;
			}
			
			int origWidth = origImg.getWidth();
			int origHeight = origImg.getHeight();
			
			// calculate the dimensions of the thumbnail
			int width;
			int height;
			float factor;
			
			// landscape format
			if(origWidth > origHeight)
			{
				width = maxWidth;
				factor = (float)width / (float)origWidth;
				height = (int)((float)origHeight * factor);
			}
			// higher than wide
			else
			{
				height = maxHeight;
				factor = (float)height / (float)origHeight;
				width = (int)((float)origWidth * factor);
			}
			
			image = GraphicsUtilities2.createCompatibleTranslucentImage(width, height);
			Graphics2D g2 = image.createGraphics();
			
			BufferedImage externalImage = null;
			try
			{
				externalImage = GraphicsUtilities.loadCompatibleImage(imgFile.toURI().toURL());
			} 
			catch (MalformedURLException ex)
			{
				ex.printStackTrace();
			} 
			catch (IOException ex)
			{
				ex.printStackTrace();
			}
			// create the thumbnail image using SwingX GraphicsUtilities class
			externalImage = GraphicsUtilities.createThumbnailFast(externalImage, width, height);
			
			g2.drawImage(externalImage, 0, 0, null);
			g2.dispose();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
        return image;
    }

	
	/**
	 * Method is called when the dragged object leaves the application's frame.
	 */
	public void dragExit(DropTargetEvent dte)
	{
		glassPane.hideIt();
		image = null;
	}

	
	/**
	 * Method is called when an object is being dragged over the application's frame.
	 */
	public void dragOver(DropTargetDragEvent dtde)
	{
		Point p = MouseInfo.getPointerInfo().getLocation();
		glassPane.moveIt(p);
	}

	
	/**
	 * Drop method which is called when the dragged object is being dropped.
	 */
	public void drop(DropTargetDropEvent dtde)
	{
		Point p = dtde.getLocation();
		
		// accept the drop only on the cover image
		if(getContentPane().getComponentAt(p) instanceof JLabel && getContentPane().getComponentAt(p) == this.img)
		{
			dtde.acceptDrop(DnDConstants.ACTION_LINK);
			try
			{
				setArtistImage(ImageIO.read(imgFile));
				imageChanged = true;
				saveButton.setEnabled(true);
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			dtde.rejectDrop();
		}
		glassPane.hideIt();
		image = null;
	}

	/** unused */
	public void dropActionChanged(DropTargetDragEvent dtde){}
}