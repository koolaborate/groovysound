package com.koolaborate.mvc.view.mainwindow

import java.awt.AWTEvent
import java.awt.BorderLayout
import java.awt.Color;
import java.awt.Cursor
import java.awt.Dimension
import java.awt.EventQueue
import java.awt.Font
import java.awt.Graphics2D
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.MouseInfo
import java.awt.Point
import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.Transferable
import java.awt.datatransfer.UnsupportedFlavorException
import java.awt.dnd.DnDConstants
import java.awt.dnd.DropTarget
import java.awt.dnd.DropTargetDragEvent
import java.awt.dnd.DropTargetDropEvent
import java.awt.dnd.DropTargetEvent
import java.awt.dnd.DropTargetListener
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.KeyEvent
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.event.MouseMotionListener
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.awt.geom.Rectangle2D
import java.awt.geom.RoundRectangle2D
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import java.lang.reflect.Constructor
import java.net.MalformedURLException
import java.util.List
import java.util.Locale

import javax.imageio.ImageIO
import javax.swing.Box
import javax.swing.BoxLayout
import javax.swing.ImageIcon
import javax.swing.JButton
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.SwingUtilities
import javax.swing.border.EmptyBorder

import org.apache.commons.lang3.StringUtils
import org.apache.log4j.Logger

import com.koolaborate.model.Album
import com.koolaborate.model.CurrentSongInfo
import com.koolaborate.config.Settings
import com.koolaborate.mvc.controller.PlaybackController
import com.koolaborate.mvc.view.decorations.Decorator
import com.koolaborate.mvc.view.dialogs.VistaDialog
import com.koolaborate.mvc.view.mainwindow.components.WindowGhostDragGlassPane;
import com.koolaborate.mvc.view.mainwindow.components.WindowRoundedBorder;
import com.koolaborate.mvc.view.mainwindow.components.WindowCenterPanel; 
import com.koolaborate.mvc.view.playlistview.CoverAndInfoPanel
import com.koolaborate.mvc.view.playlistview.PlaylistPanel
import com.koolaborate.mvc.view.themes.Theme
import com.koolaborate.mvc.view.themes.ThemeHelper
import com.koolaborate.mvc.view.tinyview.TinyView
import com.koolaborate.mvc.view.trayicon.TrayIconHandler
import com.koolaborate.service.db.Database 
import com.koolaborate.util.GraphicsUtilities
import com.koolaborate.util.GraphicsUtilities2
import com.koolaborate.util.ImageHelper
import com.koolaborate.util.LocaleMessage
import com.koolaborate.util.WindowShaper

import ui.VistaHelp 

/***********************************************************************************
 * MainWindow * 
 *********************************************************************************** 
 * The main window of the application. *
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
class MainWindow extends JFrame implements DropTargetListener{
	private static final long serialVersionUID = 170657189613362120L

	// images
	static BufferedImage logoImg, logoTextImg, minimizeImg, closeImg
	static BufferedImage helpImg, minimizeOverImg, closeOverImg, helpOverImg, maximizeImg
	static BufferedImage maximizeOverImg, maximize2Img, maximize2OverImg, tinyImg
	static BufferedImage tinyOverImg

	// GUI elements 
	JPanel mainPanel, header
	WindowCenterPanel centerPanel 
	JButton maximize

	// the tiny view
	TinyView tinyWindow
	boolean tinyWindowShown = false

	String currentFolder
	CurrentSongInfo songInfo

	Database database
	Settings settings

	public enum NAVIGATION{
		ALBUMS, PLAYLIST, SETTINGS
	}
	
	NAVIGATION currentNavigation = NAVIGATION.ALBUMS

	/** use the glass pane for the preview thumbnail of a new cover image */
	WindowGhostDragGlassPane ghostDragGlassPane 
	File imgFile
	BufferedImage image
	int maxWidth = 80 // maximum width for the ghost image
	int maxHeight = 80 // maximum height for the ghost image
	boolean enableDnD = false

	/** the help viewer */
	VistaHelp help

	/** the decorator holds all JPanels that are used in the entire application */
	Decorator decorator

	// for the rounded window shape
	WindowShaper windowShaper = null
	boolean roundWindowSupported = false
	boolean enableWindowDrag = true

	// system tray support
	TrayIconHandler tray

	/** the log4j logger */
	static Logger log = Logger.getLogger(MainWindow.class.getName())

	def void initializeGui(Settings s){
		// load the settings
		this.settings = s

		decorator = new Decorator()
		setThemeSettings()

		setTitle("VibrantPlayer v." + s.getVersion())
		setSize(600, 480)
		setPreferredSize(new Dimension(600, 480))
		setMinimumSize(new Dimension(400, 400))

		Point p = s.getMainWindowLocation()
		if(p == null)
			setLocationRelativeTo(null) // center on screen if no value is
											// given
		else setLocation(p)

		def windowCloser = [
			addWindowListener: { e ->
				exit()
			}
		] as WindowAdapter
		
	
		// use windowCloser in the param
		addWindowListener(windowCloser)

		setUndecorated(true) // remove the standard close, resize etc. buttons
		setWindowShape(true)

		// establish database connection
		database = new Database()

		songInfo = new CurrentSongInfo()

		loadImages()
		setIconImage(logoImg)

		// define a drop target for the entire frame
		DropTarget dt = new DropTarget(this, this)
		this.setDropTarget(dt)
		ghostDragGlassPane = new WindowGhostDragGlassPane()
		this.setGhostDragGlassPane(ghostDragGlassPane)

		// add the F1 key listerner to show help
		initHelp()
		addF1KeyListener()

		// add the tray icon
		tray = new TrayIconHandler()
		tray.initializeWindow(this)
		tray.showAlbumImage(null)

		def guiInitializer = [
			run: {
				initGUI()
				setVisible(true)
			}	
		] as Runnable
		
		SwingUtilities.invokeLater(guiInitializer)
	}
	
	/**
	 * Sets all theme specific colors and panels.
	 */
	private void setThemeSettings(){
		// retrieve the active theme name from the XML file. If the file is not
		// present,
		String themeName = ThemeHelper.retrieveActiveThemeName()

		if(StringUtils.isEmpty(themeName) || themeName.equals("DefaultTheme")) {
			return
		}

		try {
			String packageName = "ui.themes."
			Class c = Class.forName(packageName + themeName)
			if(c != null) {
				Constructor<Decorator> constructor = c.getConstructor(Decorator.class)
				Theme t = (Theme) constructor.newInstance(decorator)
				log.debug("Theme '" + t.getName() + "' successfully loaded.")
				t.setThemeSettings()
			}
		} catch(Exception e) {
			log.debug(e.getMessage())
		}
	}

	/**
	 * Loads all images.
	 */
	private void loadImages(){
		try {
			logoImg = ImageIO.read(MainWindow.class.getResource("/images/headphones.png"))
			logoTextImg = ImageIO.read(MainWindow.class.getResource("/images/txtlogo.png"))
			minimizeImg = ImageIO.read(MainWindow.class.getResource("/images/minimize.png"))
			maximizeImg = ImageIO.read(MainWindow.class.getResource("/images/maximize.png"))
			closeImg = ImageIO.read(MainWindow.class.getResource("/images/close.png"))
			minimizeOverImg = ImageIO.read(MainWindow.class.getResource("/images/minimize_over.png"))
			maximizeOverImg = ImageIO.read(MainWindow.class.getResource("/images/maximize_over.png"))
			closeOverImg = ImageIO.read(MainWindow.class.getResource("/images/close_over.png"))
			maximize2OverImg = ImageIO.read(MainWindow.class.getResource("/images/maximize2_over.png"))
			maximize2Img = ImageIO.read(MainWindow.class.getResource("/images/maximize2.png"))
			helpImg = ImageIO.read(MainWindow.class.getResource("/images/help_inactive.png"))
			helpOverImg = ImageIO.read(MainWindow.class.getResource("/images/help.png"))
			tinyImg = ImageIO.read(MainWindow.class.getResource("/images/tiny.png"))
			tinyOverImg = ImageIO.read(MainWindow.class.getResource("/images/tiny_over.png"))
		} catch(IOException ioe) {
			log.debug(ioe.getMessage())
		}
	}

	/**
	 * Initialize the GUI elements.
	 */
	private void initGUI(){
		mainPanel = new JPanel(new BorderLayout(), true)
		mainPanel.setOpaque(false)
		if(roundWindowSupported) mainPanel.setBorder(new WindowRoundedBorder(color: Color.BLACK, radius: 20)) 

		// header panel
		mainPanel.add(createHeaderPanel(), BorderLayout.NORTH)

		// center panel
		centerPanel = new WindowCenterPanel()
		centerPanel.initializeMainWindow(this)
		mainPanel.add(centerPanel, BorderLayout.CENTER)

		// bottom panel
		mainPanel.add(decorator.getBottomPanel(), BorderLayout.SOUTH)

		add(mainPanel)
	}

	/**
	 * @return creates and returns the header JPanel
	 */
	private JPanel createHeaderPanel(){
		header = decorator.getHeaderPanel()
		header.setLayout(new GridBagLayout())
		header.setPreferredSize(new Dimension(600, 65))

		JLabel logo = new JLabel(new ImageIcon(logoImg))
		logo.setBorder(new EmptyBorder(0, 10, 0, 0))

		JLabel txtLogo = new JLabel(new ImageIcon(logoTextImg))
		txtLogo.setBorder(new EmptyBorder(0, 10, 0, 0))

		JLabel version = new JLabel(settings.getVersion())
		version.setForeground(decorator.getLogoForegroundColor())
		version.setFont(new Font("Serif", Font.PLAIN, 8))
		version.setBorder(new EmptyBorder(0, 0, 14, 0))

		JPanel decoration = new JPanel(true)
		decoration.setLayout(new BoxLayout(decoration, BoxLayout.LINE_AXIS))
		decoration.setOpaque(false)

		JButton minimize = new JButton(new ImageIcon(minimizeImg))
		minimize.setRolloverIcon(new ImageIcon(minimizeOverImg))
		minimize.setBorder(null)
		minimize.setFocusPainted(false)
		minimize.setContentAreaFilled(false);
		
		def minimizeActionListener = getMinimizeActionListener()
	
		minimize.addActionListener(minimizeActionListener);
		minimize.setPreferredSize(new Dimension(25, 17))
		decoration.add(minimize)

		// for tiny view
		JButton tinyButton = new JButton(new ImageIcon(tinyImg))
		tinyButton.setRolloverIcon(new ImageIcon(tinyOverImg))
		tinyButton.setBorder(null)
		tinyButton.setFocusPainted(false)
		tinyButton.setContentAreaFilled(false);
		
		def tinyButtonActionListener = [
			actionPerformed: {
				this.setVisible(false)
				this.tinyWindow = new TinyView(getThisInstance(),
						database.getAlbumById(songInfo.albumId),
						songInfo.songId)
				tinyWindowShown = true
			}
		] as ActionListener
		
		tinyButton.addActionListener([
			actionPerformed: {
				setVisible(false)
				tinyWindow = new TinyView(getThisInstance(),
						database.getAlbumById(songInfo.getAlbumId()),
						songInfo.songId)
				tinyWindowShown = true
			}
		] as ActionListener)
		tinyButton.setPreferredSize(new Dimension(25, 17))
		decoration.add(tinyButton)

		maximize = new JButton(new ImageIcon(maximizeImg))
		maximize.setRolloverIcon(new ImageIcon(maximizeOverImg))
		maximize.setBorder(null)
		maximize.setFocusPainted(false)
		maximize.setContentAreaFilled(false)
		maximize.addActionListener([
			actionPerformed: { e ->
				if(getExtendedState() != MAXIMIZED_BOTH)
					maximize()
				else deMaximize()

				// resize navigation panel
				centerPanel.getNavigationPanel().calculateSubNavWidth(getSize())

				// recalculate albums preview
				centerPanel.getAlbumsPanel().refreshAlbums(centerPanel,
						centerPanel.getAlbumsPanel().getShownAlbums())
			}
		] as ActionListener)
		maximize.setPreferredSize(new Dimension(25, 17))
		decoration.add(maximize)

		JButton close = new JButton(new ImageIcon(closeImg))
		close.setRolloverIcon(new ImageIcon(closeOverImg))
		close.setBorder(null)
		close.setFocusPainted(false)
		close.setContentAreaFilled(false)
		close.addActionListener([
			actionPerformed: {
				exit()
			}
		] as ActionListener)
		close.setPreferredSize(new Dimension(43, 17))
		decoration.add(close)

		Box boxAfterClose = Box.createHorizontalBox()
		boxAfterClose.setPreferredSize(new Dimension(15, 1))
		decoration.add(boxAfterClose)

		JPanel helpPanel = new JPanel(true)
		helpPanel.setLayout(new BoxLayout(helpPanel, BoxLayout.LINE_AXIS))
		helpPanel.setOpaque(false)

		JButton help = new JButton(new ImageIcon(helpImg))
		help.setRolloverIcon(new ImageIcon(helpOverImg))
		help.setBorder(null)
		help.setFocusPainted(false)
		help.setContentAreaFilled(false)
		help.addActionListener([
			actionPerformed: {
				showHelp()
			}
		] as ActionListener)
		help.setPreferredSize(new Dimension(16, 16))
		helpPanel.add(help)

		Box boxAfterHelp = Box.createHorizontalBox()
		boxAfterHelp.setPreferredSize(new Dimension(15, 1))
		helpPanel.add(boxAfterHelp)

		GridBagConstraints c = new GridBagConstraints()
		c.weighty = 100
		c.anchor = GridBagConstraints.CENTER
		c.gridx = 0
		c.gridy = 0
		c.gridheight = 2
		header.add(logo, c)

		c.weighty = 100
		c.anchor = GridBagConstraints.WEST
		c.gridx = 1
		c.gridy = 0
		header.add(txtLogo, c)

		c.weighty = 100
		c.anchor = GridBagConstraints.SOUTH
		c.gridx = 2
		c.gridy = 0
		c.gridheight = 2
		header.add(version, c)

		c.anchor = GridBagConstraints.NORTHEAST
		c.gridx = 3
		c.gridy = 0
		c.weightx = 100
		header.add(decoration, c)

		c.anchor = GridBagConstraints.EAST
		c.gridx = 3
		c.gridy = 1
		header.add(helpPanel, c)

		WindowsMouseMotionListenerAdapter innerListener = new WindowsMouseMotionListenerAdapter()
		header.addMouseListener(innerListener)
		header.addMouseMotionListener(innerListener)

		return header
	}

	@Override
	def ActionListener getMinimizeActionListener(){
		ActionListener minimizeActionListener = [
			setExtendedState(ICONIFIED)
		] as ActionListener
		
		return minimizeActionListener
	}
	
	/**
	 * Method to maximize the window. The window shape has to be made
	 * rectangular without rounded corners.
	 */
	private void maximize(){
		setExtendedState(MAXIMIZED_BOTH)
		maximize.setIcon(new ImageIcon(maximize2Img))
		maximize.setRolloverIcon(new ImageIcon(maximize2OverImg))
		enableWindowDrag = false
		setWindowShape(false)
		mainPanel.setBorder(null)
	}

	/**
	 * Sets the window shape using reflection since the AWTUtilities class is
	 * not yet official and only present in JRE 1.6_10 and newer.
	 * 
	 * @param roundCorners
	 *            whether or not to give the window smooth rounded corners
	 */
	private void setWindowShape(boolean roundCorners){
		// make sure the window shaper helper class is initialized
		if(windowShaper == null) windowShaper = new WindowShaper()

		if(roundCorners) {
			roundWindowSupported = windowShaper.shapeWindow(this, new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20))
			if(roundWindowSupported && mainPanel != null)
				mainPanel.setBorder(new WindowRoundedBorder(color: Color.BLACK, radius: 20)) 
		} else {
			windowShaper.shapeWindow(this, new Rectangle2D.Double(0, 0, getWidth(), getHeight()))
		}
	}

	/**
	 * Method to de-maximize the window. The window shape has to be made
	 * rectangular with rounded corners again.
	 */
	private void deMaximize(){
		setExtendedState(NORMAL)
		maximize.setIcon(new ImageIcon(maximizeImg))
		maximize.setRolloverIcon(new ImageIcon(maximizeOverImg))
		enableWindowDrag = true
		setWindowShape(true)
	}

	/**
	 * Method to end the application and save the status before leaving.
	 */
	public void exit(){
		settings.setMainWindowLocation(getLocation())
		settings.save()
		dispose()
		database.shutDownConnection()
		System.exit(0)
	}

	/**
	 * @return the playlist panel where the playlist lies within
	 */
	public PlaylistPanel getPlaylist(){
		return this.centerPanel.playlistPanel
	}

	/**
	 * Updates the cover image of the album.
	 * 
	 * @param songInfo
	 *            the current song info to retrieve the path of the cover image
	 */
	public void updateCover(CurrentSongInfo songInfo){
		// update of the cover image
		centerPanel.getCoverPanel().setCoverPath(songInfo.getCoverPath())
		centerPanel.getCoverPanel().refreshCover()
	}

	/**
	 * Updates the artist information.
	 * 
	 * @param songInfo
	 *            the current song info to retrieve the artist's name
	 */
	public void updateArtist(CurrentSongInfo songInfo){
		// update of the artist information
		centerPanel.getInfoPanel().updateSongInfo(songInfo)
		// centerPanel.getTimeElapsedPanel().setTotalTimeLabelText(songInfo.getDuration());
		centerPanel.getTimeElapsedPanel().setSongFileSize(
				new File(songInfo.songPath).length())
		repaintBackgroundPanel()

		// also show a message at the tray icon
		updateTrayIconAndText(songInfo.getCoverPath(), songInfo.getArtist(),
				songInfo.getSongTitle())
	}

	public void repaintBackgroundPanel(){
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				centerPanel.repaint()
				centerPanel.revalidate()
			}
		})
	}

	public PlaybackController getPlayerPanel(){
		return this.centerPanel.playerControls
	}

	/**
	 * Method is called when an object is being dragged onto the application
	 * window.
	 */
	public void dragEnter(DropTargetDragEvent dtde){
		if(!enableDnD) return

		// create image if it has not already been created
		if(image == null) {
			Transferable t = dtde.getTransferable()
			try {
				Object data = t.getTransferData(DataFlavor.javaFileListFlavor)
				List<File> fileList = (List<File>) data
				BufferedImage image = createImage(fileList)
				if(image != null) {
					Point p = MouseInfo.getPointerInfo().getLocation()
					ghostDragGlassPane.showIt(image, p)
				}
			} catch(UnsupportedFlavorException e) {
				log.debug(e.getMessage())
			} catch(IOException e) {
				log.debug(e.getMessage())
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
		BufferedImage image = null

		imgFile = new File("")
		for(File file: files) {
			// accept jpeg, gif, bmp and png images
			if(file.getName().toLowerCase().endsWith(".png")
					|| file.getName().toLowerCase().endsWith(".jpg")
					|| file.getName().toLowerCase().endsWith(".jpeg")
					|| file.getName().toLowerCase().endsWith(".gif")
					|| file.getName().toLowerCase().endsWith(".bmp")) {
				imgFile = file
				break
			} else {
				continue
			}
		}

		try {
			BufferedImage origImg = ImageIO.read(imgFile)
			if(origImg == null) {
				return null
			}

			int origWidth = origImg.getWidth()
			int origHeight = origImg.getHeight()

			// calculate the dimensions of the thumbnail
			int width
			int height
			float factor

			// landscape format
			if(origWidth > origHeight) {
				width = maxWidth
				factor = (float) width / (float) origWidth
				height = (int) ((float) origHeight * factor)
			}
			// higher than wide
			else {
				height = maxHeight
				factor = (float) height / (float) origHeight
				width = (int) ((float) origWidth * factor)
			}

			image = GraphicsUtilities2.getInstance().createCompatibleTranslucentImage(width, height)
			Graphics2D g2 = image.createGraphics()

			BufferedImage externalImage = null
			try {
				externalImage = GraphicsUtilities.getInstance().loadCompatibleImage(imgFile.toURI().toURL())
			} catch(MalformedURLException ex) {
				ex.printStackTrace()
			} catch(IOException ex) {
				ex.printStackTrace()
			}
			// create the thumbnail image using SwingX GraphicsUtilities class
			externalImage = GraphicsUtilities.getInstance().createThumbnailFast(externalImage, width, height)

			g2.drawImage(externalImage, 0, 0, null)
			g2.dispose()
		} catch(IOException e) {
			e.printStackTrace()
		}
		return image
	}

	/**
	 * Method is called when the dragged object leaves the application's frame.
	 */
	public void dragExit(DropTargetEvent dte){
		ghostDragGlassPane.hideIt()
		image = null
	}

	/**
	 * Method is called when an object is being dragged over the application's
	 * frame.
	 */
	public void dragOver(DropTargetDragEvent dtde){
		if(!enableDnD) return

		Point p = MouseInfo.getPointerInfo().getLocation()
		ghostDragGlassPane.moveIt(p)
	}

	/**
	 * Drop method which is called when the dragged object is being dropped.
	 */
	public void drop(DropTargetDropEvent dtde){
		if(!enableDnD) return

		Point p = dtde.getLocation()

		// accept the drop only on the cover image
		if(centerPanel.getComponentAt(p) instanceof JPanel) {
			JPanel styledBackground = (JPanel) centerPanel.getComponentAt(p)
			if(styledBackground.getName() != null
					&& styledBackground.getName().equals("ACTION")) {
				JPanel center = (JPanel) styledBackground.getComponentAt(p)
				if(center.getName() != null && center.getName().equals("DROP")) {
					// test if it was on the cover
					if(center.getComponentAt(p) instanceof CoverAndInfoPanel) {
						CoverAndInfoPanel cover = (CoverAndInfoPanel) center.getComponentAt(p)
						if(cover.getName().equals("COVER")) {
							dtde.acceptDrop(DnDConstants.ACTION_LINK)
							setNewCover(imgFile.getAbsolutePath())
						}
					}
				}
			}
		} else {
			dtde.rejectDrop()
		}
		ghostDragGlassPane.hideIt()
		image = null
	}

	/** unused */
	public void dropActionChanged(DropTargetDragEvent dtde){}

	/**
	 * Sets a new cover when it is dropped via drag n drop. The given cover
	 * image will be copied into the destination folder as hidden folder.jpg and
	 * afterwards the cover image will be repainted.
	 * 
	 * @param absolutePath
	 *            the path to the new cover image
	 */
	public void setNewCover(String absolutePath){
		try {
			setNewCover(ImageIO.read(new File(absolutePath)))
		} catch(IOException e) {
			e.printStackTrace()
		}
	}

	/**
	 * Sets a new cover image and replaces the existing one if necessary. Also,
	 * a thumbnail view is generated and the old thumbnail (if available) is
	 * deleted.
	 * 
	 * @param newCover
	 *            the new cover image
	 */
	public void setNewCover(BufferedImage newCover){
		String destFolder = songInfo.getAlbumPath()
		try {
			// only copy the image if an album is selected
			if(!StringUtils.isEmpty(destFolder)) {
				String filename = "folder.jpg"
				String destination = destFolder + File.separator + filename
				// check if there is already a cover jpg file
				File oldCover = new File(destination)
				// just for now: rename the old jpg
				if(oldCover.exists())
					oldCover.renameTo(new File(destFolder + File.separator + "folder_old.jpg"))

				ImageIO.write(newCover, "jpg", new File(destination))

				// update the cover image
				songInfo.setAlbumPath(destFolder)
				updateCover(songInfo)
				// repaintBackgroundPanel();

				BufferedImage preview = null
				File bigCover = new File(destination)
				if(bigCover.exists()) {
					ImageHelper helper = new ImageHelper()
					preview = helper.createSmallCover(bigCover)
				}

				Album a = database.getAlbumById(songInfo.getAlbumId())
				a.setPreview(preview)
				database.updateAlbum(a)

				// refresh the albumsview since the thumbnail might have changed
				centerPanel.getAlbumsPanel().refreshSelectedAlbum(a)
			}
		} catch(IOException e) {
			VistaDialog.showDialog(LocaleMessage.getInstance().getString("error.1"),
					LocaleMessage.getInstance().getString("error.26"),
					LocaleMessage.getInstance().getString("error.27") + ":\n"
							+ e.getMessage(), VistaDialog.ERROR_MESSAGE)
		}
	}

	public NAVIGATION getCurrentNavigation(){
		return currentNavigation
	}

	public void setCurrentNavigation(NAVIGATION nav){
		this.currentNavigation = nav
		if(nav == NAVIGATION.PLAYLIST)
			enableDnD = true
		else enableDnD = false
	}

	public Database getDatabase(){
		return database
	}

	private MainWindow getThisInstance(){
		return this
	}

	/**
	 * Registers a global listener to show the help in case the F1 key is
	 * pressed.
	 */
	protected void addF1KeyListener(){
		EventQueue e = Toolkit.getDefaultToolkit().getSystemEventQueue()
		e.push([
			dispatchEvent: { event ->
				if(event instanceof KeyEvent) {
					KeyEvent k = (KeyEvent) event
					// only at the event key released and only if it is the F1
					// key
					if(k.getID() == KeyEvent.KEY_RELEASED && k.getKeyCode() == KeyEvent.VK_F1) {
						showHelp()
					}
				}
				super.dispatchEvent(event)
			}
		] as EventQueue)
	}

	/**
	 * Initializes the help according to the current locale.
	 */
	private void initHelp(){
		String helpdir = System.getProperty("user.dir") + File.separator + "help" + File.separator + "help_"
		String lang = "en" // standard dir is help_en
		String testlang = Locale.getDefault().getLanguage() // get the language
																// code for the
																// current
																// locale, for
																// example "de"
		File testFolder = new File(helpdir + testlang) // check if the help is
														// available for this
														// language ("help_de")
		if(testFolder.exists() && testFolder.isDirectory()) lang = testlang // use
																				// it
																				// if
																				// it
																				// is
																				// available

		help = new VistaHelp(helpdir + lang + File.separator + "index.html")
	}

	/**
	 * Shows the help viewer.
	 */
	public void showHelp(){
		help.showHelp()
	}

	public void handleTinyWindowUpdate(){
		if(!tinyWindowShown) return
		tinyWindow.updateView(songInfo.songId)

	}

	/**
	 * Updates the tray icon in the system tray.
	 * 
	 * @param coverPath
	 *            the absolute path to the cover image (will only be changed if
	 *            it differs from the currently shown image)
	 * @param artist
	 *            the name of the artist
	 * @param title
	 *            the name of the album or song
	 */
	public void updateTrayIconAndText(String coverPath, String artist, String title){
		tray.showAlbumImageFromPath(coverPath)
		tray.setIconToolTip(artist + " - " + title)
	}
}


class WindowsMouseMotionListenerAdapter extends MouseAdapter implements MouseMotionListener{
		private Point startDrag
		JFrame jFrame 
		JPanel header
		MainWindow mainWindow
		
		WindowsMouseMotionListenerAdapter(JFrame jFrame){
			this.jFrame = jFrame
		}
		
		@Override
		public void mouseClicked(MouseEvent e){
			if(e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1
					&& e.getComponent() == header) {
				if(jFrame.getExtendedState() == mainWindow.MAXIMIZED_BOTH)
					mainWindow.deMaximize()
				else mainWindow.maximize()
			}
		}

		@Override
		public void mousePressed(MouseEvent e){
			if(e.getButton() == MouseEvent.BUTTON1)
				startDrag = e.getPoint()
			else startDrag = null
		}

		public void mouseDragged(MouseEvent e){
			if(startDrag != null) {
				if(e.getComponent() == header) {
					jFrame.setLocation(e.getX() + jFrame.getLocation().x - startDrag.x,
							e.getY() + jFrame.getLocation().y - startDrag.y)
				} else {
					int width = jFrame.getWidth() + e.getX() - startDrag.x
					int height = jFrame.getHeight() + e.getY() - startDrag.y
					if(width < jFrame.getMinimumSize().width)
						width = jFrame.getMinimumSize().width
					if(height < jFrame.getMinimumSize().height)
						height = jFrame.getMinimumSize().height
					jFrame.setSize(width, height)
					jFrame.setVisible(true)
				}
			}
		}

		public void mouseMoved(MouseEvent e){
			if(mainWindow.enableWindowDrag && e.getComponent() == header) {
				header.setCursor(new Cursor(Cursor.MOVE_CURSOR))
			}
		}

		@Override
		public void mouseExited(MouseEvent e){
			header.setCursor(new Cursor(Cursor.DEFAULT_CURSOR))
		}
		
	}