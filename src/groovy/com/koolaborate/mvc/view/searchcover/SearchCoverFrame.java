package com.koolaborate.mvc.view.searchcover;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileFilter;

import org.jdesktop.swingx.JXBusyLabel;

import com.koolaborate.mvc.view.common.VariableLineBorder;
import com.koolaborate.mvc.view.mainwindow.MainWindow;
import com.koolaborate.util.GraphicsUtilities;
import com.koolaborate.util.HtmlParser;
import com.koolaborate.util.LocaleMessage;

/***********************************************************************************
 * SearchCoverFrame                                                                *
 ***********************************************************************************
 * A window that lets the user search for an album cover image using a web search  *
 * or selecting a local image file.                                                *
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
public class SearchCoverFrame extends JDialog
{
	//TODO this class is pretty much identical to the one of find new cover using album information screen...
	//combine them to one class!
	
	private static final long serialVersionUID = 4308469264735406302L;
	private JButton cancelButt, searchButt;
	private JPanel centerPanel, searchPanel1, searchPanel2, searchPanel3;
	private JXBusyLabel busyLabel1, busyLabel2, busyLabel3;
	private MainWindow window;
	private BufferedImage searchImg, i1, i2, i3;
	
	private MouseAdapter ml;
	
	private String artist, album;
	private JTextField pathText;
	
	/**
	 * Constructor.
	 * 
	 * @param window reference to the main window
	 * @param artist the name of the artist
	 * @param album the title of the album
	 */
	public SearchCoverFrame(MainWindow window, String artist, String album)
	{
		this.window = window;
		this.artist = artist;
		this.album = album;
	
		setSize(524, 390);
		setResizable(false);
		setModal(false);
		
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
		try
		{
			searchImg = ImageIO.read(getClass().getResource("/images/searchcover.png"));
			setIconImage(searchImg);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setTitle(LocaleMessage.getInstance().getString("searchcover.title"));
		
		setLayout(new BorderLayout());
		
		JPanel bgPanel = new JPanel(){
			private static final long serialVersionUID = 3255388876280461681L;

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
		bgPanel.setLayout(new BorderLayout());
		
		// init the mouse listener for selection events
		ml = new MouseAdapter(){
			@Override
			public void mouseEntered(MouseEvent me)
			{
				if(me.getSource() instanceof JLabel)
				{
					JLabel s = (JLabel) me.getSource();
					s.setBorder(new LineBorder(Color.RED));
					setCursor(new Cursor(Cursor.HAND_CURSOR));
				}
			}
			@Override
			public void mouseExited(MouseEvent me)
			{
				if(me.getSource() instanceof JLabel)
				{
					JLabel s = (JLabel) me.getSource();
					s.setBorder(new LineBorder(Color.BLACK));
					setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				}
			}
			@Override
			public void mouseClicked(MouseEvent me)
			{
				JLabel s = (JLabel) me.getSource();
				String name = s.getName();
				if(name.equals("i1"))
				{
					saveAlbumCover(i1);
				}
				else if(name.equals("i2"))
				{
					saveAlbumCover(i2);
				}
				else if(name.equals("i3"))
				{
					saveAlbumCover(i3);
				}
			}
		};
		
		// header panel
		bgPanel.add(createHeaderPanel(), BorderLayout.NORTH);
		
		// main panel
		centerPanel = createCenterPanel();
		bgPanel.add(centerPanel, BorderLayout.CENTER);

		add(bgPanel, BorderLayout.CENTER);
		
		// button panel
		add(createButtonPanel(), BorderLayout.SOUTH);
		
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	/**
	 * @return creates and returns the header JPanel
	 */
	private JPanel createHeaderPanel()
	{
		JPanel p = new JPanel();
		p.setOpaque(false);
		p.setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		
		// search cover image
		JLabel imgLabel = new JLabel();
		imgLabel.setOpaque(false);
		imgLabel.setIcon(new ImageIcon(searchImg));
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.LINE_START;
		c.weightx = 0.0f;
		c.gridx = 0;  
		c.gridy = 0;  
		c.gridheight = 2; 
		c.insets = new Insets(10, 20, 0, 0);  // top padding
		p.add(imgLabel, c);
		
		// search cover text
		JLabel coverText = new JLabel(LocaleMessage.getInstance().getString("searchcover.description"));
		coverText.setOpaque(false);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1.0f;
		c.gridx = 1; 
		c.gridy = 0; 
		c.gridheight = 1;
		c.gridwidth = 2;
		c.insets = new Insets(26, 10, 0, 0);  // top padding
		p.add(coverText, c);
		
		// cover search button
		searchButt = new JButton(LocaleMessage.getInstance().getString("searchcover.beginsearch"));
		searchButt.setToolTipText(LocaleMessage.getInstance().getString("searchcover.beginsearch_tooltip"));
		searchButt.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0)
			{
				new Thread(new Runnable(){
					public void run()
					{
						SwingUtilities.invokeLater(new Runnable(){
							public void run()
							{
								busyLabel1.setVisible(true);
								busyLabel1.setBusy(true);
								busyLabel2.setVisible(true);
								busyLabel2.setBusy(true);
								busyLabel3.setVisible(true);
								busyLabel3.setBusy(true);
							}
						});
						try
						{
							searchCovers();
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
					}
				}).start();
			}
		});

		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.LINE_START;
		c.weightx = 0.0f;
		c.gridx = 1;  
		c.gridy = 1;  
		c.gridheight = 1; 
		c.insets = new Insets(10, 10, 10, 0);  // top padding
		p.add(searchButt, c);
		
		return p;
	}
	
	/**
	 * @return creates and returns the center JPanel
	 */
	private JPanel createCenterPanel()
	{
		JPanel center = new JPanel();
		center.setLayout(new BorderLayout());
		center.setOpaque(false);
		
		JPanel p = new JPanel();
		p.setLayout(new FlowLayout());
		p.setOpaque(false);
		
		Dimension dim = new Dimension(152, 152); 
		
		// layout manager
		GridBagLayout gbl      = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor             = GridBagConstraints.CENTER;
		
		searchPanel1 = new JPanel(gbl);
		searchPanel1.setPreferredSize(dim);
		searchPanel1.setOpaque(false);
		busyLabel1 = new JXBusyLabel();
		busyLabel1.setVisible(false);
		gbl.setConstraints(busyLabel1, gbc);
		searchPanel1.add(busyLabel1);
		p.add(searchPanel1);
		
		searchPanel2 = new JPanel(gbl);
		searchPanel2.setOpaque(false);
		searchPanel2.setPreferredSize(dim);
		busyLabel2 = new JXBusyLabel();
		busyLabel2.setVisible(false);
		searchPanel2.add(busyLabel2, gbc);
		p.add(searchPanel2);
		
		searchPanel3 = new JPanel(gbl);
		searchPanel3.setOpaque(false);
		searchPanel3.setPreferredSize(dim);
		busyLabel3 = new JXBusyLabel();
		busyLabel3.setBorder(new EmptyBorder(0, 30, 0, 30));
		busyLabel3.setVisible(false);
		searchPanel3.add(busyLabel3, gbc);
		p.add(searchPanel3);
		
		center.add(p, BorderLayout.CENTER);
		
		JPanel ownPanel = new JPanel(new GridBagLayout());
		ownPanel.setOpaque(false);
		
		JLabel pathLabel = new JLabel(LocaleMessage.getInstance().getString("searchcover.ownpath"));
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 3;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0f;
		gbc.insets = new Insets(10, 10, 0, 0);
		ownPanel.add(pathLabel, gbc);
		
		JLabel path = new JLabel(LocaleMessage.getInstance().getString("searchcover.path"));
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.fill = GridBagConstraints.NONE;
		gbc.weightx = 0.0f;
		gbc.insets = new Insets(6, 10, 10, 4);
		ownPanel.add(path, gbc);
		
		pathText = new JTextField();
		pathText.setEditable(false);
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0f;
		gbc.insets = new Insets(6, 0, 10, 0);
		ownPanel.add(pathText, gbc);
		
		JButton searchButt = new JButton("...");
		searchButt.setToolTipText(LocaleMessage.getInstance().getString("common.searchfile"));
		searchButt.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0)
			{
				JFileChooser fc = new JFileChooser(new File(window.getSettings().getLastFolder())); 
			    fc.setFileFilter(new FileFilter() 
			    { 
			      @Override public boolean accept(File f) 
			      { 
			    	  String fileName = f.getName().toLowerCase();
			    	  
			    	  // accept only images
			    	  return f.isDirectory() || 
			    	  fileName.endsWith(".jpg") ||
			    	  fileName.endsWith(".jpeg") ||
			    	  fileName.endsWith(".gif") ||
			    	  fileName.endsWith(".png") ||
			    	  fileName.endsWith(".bmp"); 
			      } 
			      @Override public String getDescription() 
			      { 
			    	  return LocaleMessage.getInstance().getString("common.imagefiles"); 
			      } 
			    });
			    
			    int state = fc.showOpenDialog(getThisInstance()); 
			    if(state == JFileChooser.APPROVE_OPTION && fc.getSelectedFile() != null) 
			    { 
			    	File file = fc.getSelectedFile(); 
			    	window.getSettings().setLastFolder(file.getParent());
			    	pathText.setText(file.getAbsolutePath());
			    	
			    	String filename = "folder.jpg";
					String destination = window.getCurrentFolderPath() + File.separator + filename;
					// check if there is already a cover jpg file
					File oldCover = new File(destination);
					// just for now: rename the old jpg
					if(oldCover.exists()) oldCover.renameTo(new File(window.getCurrentFolderPath() + File.separator + "folder_old.jpg"));
					
			    	BufferedImage img = null;
					try
					{
						img = ImageIO.read(file);
						ImageIO.write(img, "jpg", new File(destination));
						saveAlbumCover(img);
						dispose();
					}
					catch (IOException e1)
					{
						e1.printStackTrace();
					}
			    } 
			}
		});
		
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.gridx = 2;
		gbc.gridy = 1;
		gbc.fill = GridBagConstraints.NONE;
		gbc.weightx = 0.0f;
		gbc.insets = new Insets(6, 4, 10, 10);
		ownPanel.add(searchButt, gbc);
		
		center.add(ownPanel, BorderLayout.SOUTH);
		
		return center;
	}
	
	/**
	 * @return creates and returns the button JPanel
	 */
	private JPanel createButtonPanel()
	{
		JPanel p = new JPanel();
		cancelButt = new JButton(LocaleMessage.getInstance().getString("common.abort"));
		cancelButt.setToolTipText(LocaleMessage.getInstance().getString("common.abort_tooltip"));
		cancelButt.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0)
			{
				dispose();
			}
		});
		
		p.setLayout(new FlowLayout(FlowLayout.RIGHT));
		p.add(cancelButt);
		p.setBorder(new VariableLineBorder(5, 5, 5, 5, Color.GRAY, 1, true, false, false, 
				false));
		return p;
	}
	
	/**
	 * Uses the Amazon(R) web service and a Google(R) image search to determine cover images.
	 */
	private void searchCovers()
	{
		final String tooltip = LocaleMessage.getInstance().getString("searchcover.choosecover"); 
		final LineBorder b = new LineBorder(Color.BLACK);
		
		Thread t1 = new Thread(new Runnable(){
			public void run()
			{
				// first, look for amazon cover image
				try
				{
					i1 = com.koolaborate.util.AmazonWebServiceUtils.getInstance().getAlbumArtImg(artist, album);
				}
				catch(Exception e)
				{
					// no image found at amazon
					e.printStackTrace();
					i1 = null;
				}
				busyLabel1.setBusy(false);
				if(i1 != null)
				{
					BufferedImage i1Small = GraphicsUtilities.getInstance().createThumbnail(i1, 150, 150);
					final JLabel amazonCover = new JLabel(new ImageIcon(i1Small));
					amazonCover.setName("i1");
					amazonCover.setBorder(b);
					amazonCover.setToolTipText(tooltip + " (" + i1.getWidth() + "x" + i1.getHeight() + ")");
					amazonCover.addMouseListener(ml);
					SwingUtilities.invokeLater(new Runnable(){
						public void run()
						{
							searchPanel1.removeAll();
							searchPanel1.add(amazonCover);
							searchPanel1.revalidate();
						}
					});
				}
			}
		});
		t1.start();
		
		Thread t2 = new Thread(new Runnable(){
			public void run()
			{
				// then look for the google image search
				URL[] urls = HtmlParser.getInstance().getCoverImagePathsFromGoogle(artist, album);
				busyLabel2.setBusy(false);
				busyLabel3.setBusy(false);
				if(urls[0] != null)
				{
					try
					{
						i2 = ImageIO.read(urls[0]);
					}
					catch (IOException e)
					{
						e.printStackTrace();
						i2 = null;
					}
					if(i2 != null)
					{
						BufferedImage i2Small = GraphicsUtilities.getInstance().createThumbnail(i2, 150, 150);
						final JLabel googleCover = new JLabel(new ImageIcon(i2Small));
						googleCover.setName("i2");
						googleCover.setBorder(b);
						googleCover.setToolTipText(tooltip + " (" + i2.getWidth() + "x" + i2.getHeight() + ")");
						googleCover.addMouseListener(ml);
						SwingUtilities.invokeLater(new Runnable(){
							public void run()
							{
								searchPanel2.removeAll();
								searchPanel2.add(googleCover);
								searchPanel2.revalidate();
							}
						});
					}
				}
				if(urls[1] != null)
				{
					try
					{
						i3 = ImageIO.read(urls[1]);
					}
					catch (IOException e)
					{
						e.printStackTrace();
						i3 = null;
					}
					if(i3 != null)
					{
						BufferedImage i3Small = GraphicsUtilities.getInstance().createThumbnail(i3, 150, 150);
						final JLabel googleCover2 = new JLabel(new ImageIcon(i3Small));
						googleCover2.setName("i3");
						googleCover2.setBorder(b);
						googleCover2.setToolTipText(tooltip + " (" + i3.getWidth() + "x" + i3.getHeight() + ")");
						googleCover2.addMouseListener(ml);
						SwingUtilities.invokeLater(new Runnable(){
							public void run()
							{
								searchPanel3.removeAll();
								searchPanel3.add(googleCover2);
								searchPanel3.revalidate();
							}
						});
					}
				}
			}
		});
		t2.start();
	}
	
	/**
	 * Saves the selected album and replaces the existing one.
	 * 
	 * @param img the selected image
	 */
	private void saveAlbumCover(BufferedImage img)
	{
		window.setNewCover(img);
		window.getCenterPanel().updateCover(window.getSongInfo());
		dispose();
	}
	
	/**
	 * @return a reference to the instance of this class
	 */
	private SearchCoverFrame getThisInstance()
	{
		return this;
	}
}