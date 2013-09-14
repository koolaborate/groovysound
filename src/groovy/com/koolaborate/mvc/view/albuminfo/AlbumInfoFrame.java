package com.koolaborate.mvc.view.albuminfo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.MouseInfo;
import java.awt.Point;
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
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.apache.commons.lang3.StringUtils;
import org.jdesktop.swingx.graphics.ReflectionRenderer;

import com.koolaborate.model.Album;
import com.koolaborate.mvc.view.common.VariableLineBorder;
import com.koolaborate.mvc.view.dialogs.VistaDialog;
import com.koolaborate.mvc.view.mainwindow.GhostDragGlassPane;
import com.koolaborate.mvc.view.mainwindow.MainWindow;
import com.koolaborate.mvc.view.mainwindow.MainWindow.NAVIGATION;
import com.koolaborate.util.GraphicsUtilities;
import com.koolaborate.util.GraphicsUtilities2;
import com.koolaborate.util.ImageHelper;
import com.koolaborate.util.LocaleMessage;

/***********************************************************************************
 * AlbumInfoFrame *
 *********************************************************************************** 
 * A window that shows information about the album like a small cover image, the
 * * title, the year and the artist. *
 *********************************************************************************** 
 * (c) Impressive Artworx, 2k8 *
 * 
 * @author Manuel Kaess *
 * @version 1.2 *
 *********************************************************************************** 
 *          This file is part of VibrantPlayer. * * VibrantPlayer is free
 *          software: you can redistribute it and/or modify * it under the terms
 *          of the Lesser GNU General Public License as published by * the Free
 *          Software Foundation, either version 3 of the License, or * (at your
 *          option) any later version. * * VibrantPlayer is distributed in the
 *          hope that it will be useful, * but WITHOUT ANY WARRANTY; without
 *          even the implied warranty of * MERCHANTABILITY or FITNESS FOR A
 *          PARTICULAR PURPOSE. See the Lesser * GNU General Public License for
 *          more details. * * You should have received a copy of the Lesser GNU
 *          General Public License * along with VibrantPlayer. If not, see
 *          <http://www.gnu.org/licenses/>. *
 ***********************************************************************************/
public class AlbumInfoFrame extends JFrame implements DropTargetListener{
	private static final long serialVersionUID = -3655016129532421572L;
	private JPanel showPanel, editPanel;
	private JLabel img;
	private JButton editButton, saveButton, closeButton, changeImage;
	private JTextField title, artist, year;

	private boolean imageChanged = false;
	private boolean changesMade = false;

	private Album album;

	private MainWindow mainWindow;
	private BufferedImage coverPreviewImage, coverImageBig;

	/** use the glass pane for the preview thumbnail of a new cover image */
	private GhostDragGlassPane glassPane;
	private File imgFile;
	private BufferedImage image;
	private int maxWidth = 80; // maximum width for the ghost image
	private int maxHeight = 80; // maximum height for the ghost image

	/**
	 * Constructor.
	 * 
	 * @param window
	 *            reference to the main window
	 * @param artist
	 *            the name of the artist or band
	 */
	public AlbumInfoFrame(MainWindow window, int albumId){
		this.mainWindow = window;
		this.album = mainWindow.getDatabase().getAlbumById(albumId);

		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				initGUI();
			}
		});
	}

	/**
	 * Initializes the GUI elements.
	 */
	private void initGUI(){
		setTitle(LocaleMessage.getString("album.info") + " " + album.getTitle());
		setSize(500, 220);
		setMinimumSize(new Dimension(400, 190));
		setLocationRelativeTo(null);

		setIconImage(new ImageIcon(getClass().getResource("/images/about.png")).getImage());

		// define a drop target for the entire frame
		DropTarget dt = new DropTarget(this, this);
		this.setDropTarget(dt);
		glassPane = new GhostDragGlassPane();
		this.setGlassPane(glassPane);

		setLayout(new BorderLayout());

		// initialize the edit and show panels
		initEditPanel();
		initShowPanel();

		// the button panel
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 2, 2));

		editButton = new JButton(LocaleMessage.getString("common.edit"));
		editButton.setToolTipText(LocaleMessage.getString("common.edit_tooltip"));
		editButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				setEditModeEnabled(true);
			}
		});
		saveButton = new JButton(
				UIManager.getString("FileChooser.saveButtonText"));
		saveButton.setToolTipText(LocaleMessage.getString("common.save_tooltip"));
		saveButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0){
				setEditModeEnabled(false);
				saveChanges();
			}
		});
		saveButton.setEnabled(false);
		closeButton = new JButton(
				UIManager.getString("InternalFrameTitlePane.closeButtonText"));
		closeButton.setToolTipText(LocaleMessage.getString("common.close_tooltip"));
		closeButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0){
				if(changesMade || imageChanged) {
					VistaDialog dialog = VistaDialog.showConfirmationDialog(
							LocaleMessage.getString("common.discard_title"),
							LocaleMessage.getString("common.discard_label"),
							LocaleMessage.getString("common.discard_text"));
					if(dialog.yesSelected) dispose();
				} else {
					dispose();
				}
			}
		});

		buttonPanel.add(editButton);
		buttonPanel.add(saveButton);
		buttonPanel.add(closeButton);
		buttonPanel.setBorder(new VariableLineBorder(5, 5, 5, 5, Color.GRAY, 1,
				true, false, false, false));

		add(showPanel, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);

		setEditModeEnabled(false);

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		setVisible(true);
	}

	/**
	 * Initializes the editPanel.
	 */
	private void initEditPanel(){
		editPanel = new JPanel();
		editPanel.setOpaque(true);
		editPanel.setBackground(Color.WHITE);
		editPanel.setLayout(new GridBagLayout());

		// the image of the album
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.weightx = 0.0f;
		gbc.gridwidth = 1;
		gbc.gridheight = 3;
		gbc.insets = new Insets(10, 10, 0, 10);
		// create the reflection
		ReflectionRenderer rend = new ReflectionRenderer();
		rend.setBlurEnabled(true); // more realistic reflection with a blur
									// filter
		coverPreviewImage = album.getPreview();
		// if the album does not have a cover image, use the standard empty
		// album image
		if(coverPreviewImage == null) {
			try {
				coverPreviewImage = ImageIO.read(getClass().getResource(
						"/images/emptycover.jpg"));
			} catch(IOException e1) {
				e1.printStackTrace();
			}
		}
		BufferedImage reflection = rend.appendReflection(coverPreviewImage);
		img = new JLabel(new ImageIcon(reflection));
		editPanel.add(img, gbc);

		// the change image button (at first invisible)
		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.gridheight = 1;
		gbc.weightx = 0.0f;
		gbc.insets = new Insets(0, 10, 4, 10);
		changeImage = new JButton(LocaleMessage.getString("common.changeimg"));
		changeImage.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				new SearchNewCoverFrame(getThisInstance(), album,
						artist.getText().trim(), title.getText().trim());
			}
		});
		changeImage.setToolTipText(LocaleMessage.getString("common.changeimg_tooltip"));
		changeImage.setVisible(false);
		editPanel.add(changeImage, gbc);

		// the name of the album
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.weightx = 0.0f;
		gbc.insets = new Insets(10, 0, 0, 10);
		JLabel titleLabel = new JLabel(LocaleMessage.getString("common.title")
				+ ":");
		editPanel.add(titleLabel, gbc);

		gbc.gridx = 2;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0f;
		gbc.insets = new Insets(10, 0, 0, 10);
		title = new JTextField(album.getTitle());
		editPanel.add(title, gbc);

		// the text to the artist
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.weightx = 0.0f;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(10, 0, 0, 10);
		JLabel artistLabel = new JLabel(
				LocaleMessage.getString("common.artist") + ":");
		editPanel.add(artistLabel, gbc);

		gbc.gridx = 2;
		gbc.gridy = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0f;
		gbc.insets = new Insets(10, 0, 0, 10);
		artist = new JTextField(album.getArtist());
		editPanel.add(artist, gbc);

		// the release year
		gbc.gridx = 1;
		gbc.gridy = 2;
		gbc.weightx = 0.0f;
		gbc.weighty = 1.0f;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(10, 0, 0, 10);
		JLabel yearLabel = new JLabel(LocaleMessage.getString("common.year")
				+ ":");
		editPanel.add(yearLabel, gbc);

		gbc.gridx = 2;
		gbc.gridy = 2;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0f;
		gbc.weighty = 1.0f;
		gbc.insets = new Insets(10, 0, 0, 10);
		year = new JTextField(Integer.toString(album.getYear()));
		editPanel.add(year, gbc);
	}

	/**
	 * Initializes the showPanel.
	 */
	private void initShowPanel(){
		showPanel = new JPanel();
		showPanel.setOpaque(true);
		showPanel.setBackground(Color.WHITE);
		showPanel.setLayout(new GridBagLayout());

		// the image of the album
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.weightx = 0.0f;
		gbc.gridwidth = 1;
		gbc.gridheight = 3;
		gbc.insets = new Insets(10, 10, 0, 10);
		// create the reflection
		ReflectionRenderer rend = new ReflectionRenderer();
		rend.setBlurEnabled(true); // more realistic reflection with a blur
									// filter
		coverPreviewImage = album.getPreview();
		// if the album does not have a cover image, use the standard empty
		// album image
		if(coverPreviewImage == null) {
			try {
				coverPreviewImage = ImageIO.read(getClass().getResource(
						"/images/emptycover.jpg"));
			} catch(IOException e1) {
				e1.printStackTrace();
			}
		}
		BufferedImage reflection = rend.appendReflection(coverPreviewImage);
		img = new JLabel(new ImageIcon(reflection));
		showPanel.add(img, gbc);

		// the change image button (at first invisible)
		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.gridheight = 1;
		gbc.weightx = 0.0f;
		gbc.insets = new Insets(0, 10, 4, 10);
		changeImage = new JButton(LocaleMessage.getString("common.changeimg"));
		changeImage.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				new SearchNewCoverFrame(getThisInstance(), album,
						artist.getText().trim(), title.getText().trim());
			}
		});
		changeImage.setToolTipText(LocaleMessage.getString("common.changeimg_tooltip"));
		changeImage.setVisible(false);
		showPanel.add(changeImage, gbc);

		// the name of the album
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.weightx = 0.0f;
		gbc.insets = new Insets(10, 0, 0, 10);
		JLabel titleLabel = new JLabel(LocaleMessage.getString("common.title")
				+ ":");
		showPanel.add(titleLabel, gbc);

		gbc.gridx = 2;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0f;
		gbc.insets = new Insets(10, 0, 0, 10);
		JLabel titleTextLabel = new JLabel(album.getTitle());
		showPanel.add(titleTextLabel, gbc);

		// the text to the artist
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.weightx = 0.0f;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(10, 0, 0, 10);
		JLabel artistLabel = new JLabel(
				LocaleMessage.getString("common.artist") + ":");
		showPanel.add(artistLabel, gbc);

		gbc.gridx = 2;
		gbc.gridy = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0f;
		gbc.insets = new Insets(10, 0, 0, 10);
		JLabel artistTextLabel = new JLabel(album.getArtist());
		showPanel.add(artistTextLabel, gbc);

		// the release year
		gbc.gridx = 1;
		gbc.gridy = 2;
		gbc.weightx = 0.0f;
		gbc.weighty = 1.0f;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(10, 0, 0, 10);
		JLabel yearLabel = new JLabel(LocaleMessage.getString("common.year")
				+ ":");
		showPanel.add(yearLabel, gbc);

		gbc.gridx = 2;
		gbc.gridy = 2;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0f;
		gbc.weighty = 1.0f;
		gbc.insets = new Insets(10, 0, 0, 10);
		JLabel yearTextLabel = new JLabel(Integer.toString(album.getYear()));

		showPanel.add(yearTextLabel, gbc);
	}

	/**
	 * Saves the made changes.
	 */
	private void saveChanges(){
		if(changesMade || imageChanged) {
			// only if entries are valid...
			if(checkEntries()) {
				album.setArtist(artist.getText().trim());
				album.setTitle(title.getText().trim());
				album.setYear(Integer.parseInt(year.getText().trim()));
				if(imageChanged) {
					// set the little preview image
					album.setPreview(coverPreviewImage);

					// then create the big image in the destination folder
					String filename = "folder.jpg";
					String destination = album.getFolderPath() + File.separator
							+ filename;
					// check if there is already a cover jpg file
					File oldCover = new File(destination);
					// just for now: rename the old jpg
					if(oldCover.exists())
						oldCover.renameTo(new File(album.getFolderPath()
								+ File.separator + "folder_old.jpg"));
					try {
						ImageIO.write(coverImageBig, "jpg", new File(
								destination));
					} catch(IOException e) {
						e.printStackTrace();
					}
				}
				mainWindow.getDatabase().updateAlbum(album);

				// refresh the albums view
				mainWindow.getCenterPanel().getAlbumsPanel().refreshSelectedAlbum(
						album);
				if(mainWindow.getCurrentNavigation() == NAVIGATION.ALBUMS) {
					mainWindow.getCenterPanel().refreshAlbumsView(
							mainWindow.getCenterPanel().getAlbumsPanel().getSortMode());
				}

				// refresh the playlist view if it is the selected album
				if(mainWindow.getCurrentFolderPath() != null
						&& mainWindow.getCurrentFolderPath().equals(
								album.getFolderPath())) {
					mainWindow.getCenterPanel().updateCoverInCase(
							mainWindow.getSongInfo(), true);
				}

				changesMade = false;
				imageChanged = false;
			}
		}
	}

	/**
	 * Checks wether all entries are valid. If there is an invalid entry, a
	 * message box is shown.
	 * 
	 * @return <code>true</code> if everything is fine, <code>false</code>
	 *         otherwise
	 */
	private boolean checkEntries(){
		boolean ret = true;

		String albumTitle = title.getText().trim();
		albumTitle = albumTitle.replaceAll("'", "`");
		title.setText(albumTitle);

		String albumArtist = artist.getText().trim();
		albumArtist = albumArtist.replaceAll("'", "`");
		artist.setText(albumArtist);

		// check parsable year...
		try {
			String yearText = year.getText().trim();
			// an empty year is handled as year 0
			if(StringUtils.isEmpty(yearText)) yearText = "0";
			int yearInt = Integer.parseInt(yearText);
			year.setText(Integer.toString(yearInt));
			ret = true;
		} catch(NumberFormatException e) {
			VistaDialog.showDialog(LocaleMessage.getString("error.6"),
					LocaleMessage.getString("error.7"),
					LocaleMessage.getString("error.8"),
					VistaDialog.ERROR_MESSAGE);
			ret = false;
		}

		return ret;
	}

	/**
	 * Enables (or disables) the edit mode. If the edit mode is activated, the
	 * image can be changed and the text as well. Afterwards, the changes need
	 * to be saved.
	 * 
	 * @param b
	 *            <code>true</code> if the edit mode shall be set,
	 *            <code>false</code> if the viewing mode shall be set
	 */
	private void setEditModeEnabled(final boolean b){
		SwingUtilities.invokeLater(new Runnable(){
			@Override
			public void run(){
				if(b) {
					remove(showPanel);
					add(editPanel, BorderLayout.CENTER);
				} else {
					remove(editPanel);
					add(showPanel, BorderLayout.CENTER);
				}
				// enable/disable change image button
				changeImage.setVisible(b);
				saveButton.setEnabled(b);

				if(b) changesMade = true;

				getContentPane().validate();
				getContentPane().repaint();
			}
		});
	}

	/**
	 * Sets the album image with a reflection.
	 * 
	 * @param image
	 *            the orignial BufferedImage
	 */
	public void setAlbumImage(BufferedImage image){
		this.coverImageBig = image;
		ImageHelper helper = new ImageHelper();
		BufferedImage smallImg = helper.createSmallCover(image);
		this.coverPreviewImage = smallImg;
		ReflectionRenderer rend = new ReflectionRenderer();
		rend.setBlurEnabled(true); // the reflection is more realistic with a
									// blur filter applied to it
		BufferedImage reflection = rend.appendReflection(coverPreviewImage);
		img.setIcon(new ImageIcon(reflection));

		getContentPane().validate();
		getContentPane().repaint();
	}

	/**
	 * Sets the satus of the image to changed if <code>true</code>.
	 * 
	 * @param b
	 *            whether or not to set the image status to changed
	 */
	public void setImageChanged(boolean b){
		this.imageChanged = b;
	}

	/**
	 * @return a reference to the instance of this class
	 */
	private AlbumInfoFrame getThisInstance(){
		return this;
	}

	/**
	 * Method is called when an object is being dragged onto the application
	 * window.
	 */
	@SuppressWarnings("unchecked")
	public void dragEnter(DropTargetDragEvent dtde){
		// create image if it has not already been created
		if(image == null) {
			Transferable t = dtde.getTransferable();
			try {
				Object data = t.getTransferData(DataFlavor.javaFileListFlavor);
				List<File> fileList = (List<File>) data;
				BufferedImage image = createImage(fileList);
				if(image != null) {
					Point p = MouseInfo.getPointerInfo().getLocation();
					glassPane.showIt(image, p);
				}
			} catch(UnsupportedFlavorException e) {
				e.printStackTrace();
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Creates an image of the dragged file.
	 * 
	 * @param files
	 *            the files being dragged onto the application
	 * @return an image of the first image file in the list
	 */
	private BufferedImage createImage(List<File> files){
		BufferedImage image = null;

		imgFile = new File("");
		for(File file: files) {
			// accept jpeg, gif, bmp and png images
			if(file.getName().toLowerCase().endsWith(".png")
					|| file.getName().toLowerCase().endsWith(".jpg")
					|| file.getName().toLowerCase().endsWith(".jpeg")
					|| file.getName().toLowerCase().endsWith(".gif")
					|| file.getName().toLowerCase().endsWith(".bmp")) {
				imgFile = file;
				break;
			} else {
				continue;
			}
		}

		try {
			BufferedImage origImg = ImageIO.read(imgFile);
			if(origImg == null) {
				return null;
			}

			int origWidth = origImg.getWidth();
			int origHeight = origImg.getHeight();

			// calculate the dimensions of the thumbnail
			int width;
			int height;
			float factor;

			// landscape format
			if(origWidth > origHeight) {
				width = maxWidth;
				factor = (float) width / (float) origWidth;
				height = (int) ((float) origHeight * factor);
			}
			// higher than wide
			else {
				height = maxHeight;
				factor = (float) height / (float) origHeight;
				width = (int) ((float) origWidth * factor);
			}

			image = GraphicsUtilities2.createCompatibleTranslucentImage(width,
					height);
			Graphics2D g2 = image.createGraphics();

			BufferedImage externalImage = null;
			try {
				externalImage = GraphicsUtilities.getInstance().loadCompatibleImage(imgFile.toURI().toURL());
			} catch(MalformedURLException ex) {
				ex.printStackTrace();
			} catch(IOException ex) {
				ex.printStackTrace();
			}
			// create the thumbnail image using SwingX GraphicsUtilities class
			externalImage = GraphicsUtilities.getInstance().createThumbnailFast(externalImage, width, height);

			g2.drawImage(externalImage, 0, 0, null);
			g2.dispose();
		} catch(IOException e) {
			e.printStackTrace();
		}
		return image;
	}

	/**
	 * Method is called when the dragged object leaves the application's frame.
	 */
	public void dragExit(DropTargetEvent dte){
		glassPane.hideIt();
		image = null;
	}

	/**
	 * Method is called when an object is being dragged over the application's
	 * frame.
	 */
	public void dragOver(DropTargetDragEvent dtde){
		Point p = MouseInfo.getPointerInfo().getLocation();
		glassPane.moveIt(p);
	}

	/**
	 * Drop method which is called when the dragged object is being dropped.
	 */
	public void drop(DropTargetDropEvent dtde){
		Point point = dtde.getLocation();

		// accept the drop only on the cover image
		if(getContentPane().getComponentAt(point) instanceof JPanel) {
			if((showPanel.getComponentAt(point) instanceof JLabel && showPanel.getComponentAt(point) == this.img)
					|| (editPanel.getComponentAt(point) instanceof JLabel && editPanel.getComponentAt(point) == this.img)) {
				dtde.acceptDrop(DnDConstants.ACTION_LINK);
				try {
					setAlbumImage(ImageIO.read(imgFile));
					imageChanged = true;
					saveButton.setEnabled(true);
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
		} else {
			dtde.rejectDrop();
		}
		glassPane.hideIt();
		image = null;
	}

	/** unused */
	public void dropActionChanged(DropTargetDragEvent dtde){}

	/**
	 * @return the reference to the main window
	 */
	public MainWindow getMainWindow(){
		return this.mainWindow;
	}
}