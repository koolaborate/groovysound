package com.koolaborate.bo.search;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.apache.commons.lang3.StringUtils;

import com.koolaborate.mvc.view.mainwindow.MainWindow;
import com.koolaborate.service.db.Database;
import com.koolaborate.util.LocaleMessage;

/***********************************************************************************
 * SearchFrame                                                                     *
 ***********************************************************************************
 * A window that offers the possibility to search for a String. The String may be  *
 * part of a song title, an artist name or an album title.                         *
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
public class SearchFrame extends JFrame
{
	private Database db;
	private Color color1 = new Color(237, 242, 249);
	private Color color2 = new Color(255, 255, 255);
	private JTextField search;
	private JButton go;
	private JPanel resultPanel;
	private JScrollPane resultList;
	private MainWindow window;
	
	
	/**
	 * Constructor.
	 */
	public SearchFrame(MainWindow w)
	{
		this.window = w;
		this.db = w.getDatabase();
		
		setTitle(LocaleMessage.getString("search.title"));
		setIconImage(new ImageIcon(getClass().getResource("/images/search.png")).getImage());
		
		SwingUtilities.invokeLater(new Runnable(){
			public void run()
			{
				initGUI();
				setSize(560, 360);
				setLocationRelativeTo(null);
				setDefaultCloseOperation(DISPOSE_ON_CLOSE);
				setVisible(true);
			}
		});
	}

	
	/**
	 * Initializes the GUI elements.
	 */
	private void initGUI()
	{
		// a gradient background panel
		JPanel bg = new JPanel(){
			protected void paintComponent(Graphics g) 
			{
				Graphics2D g2d = (Graphics2D) g;
				
				int w = getWidth();
				int h = getHeight();
				
				// Paint a gradient from top to bottom
				GradientPaint gp = new GradientPaint(
						0, 0, color1,
						0, h, color2);
				
				g2d.setPaint(gp);
				g2d.fillRect(0, 0, w, h);
			};
		};
		
		bg.setLayout(new GridBagLayout());
		
		// a description text
		JLabel searchLabel = new JLabel(LocaleMessage.getString("search.phrase"));
		
		// a search text field
		search = new JTextField();
		search.addKeyListener(new KeyAdapter(){
			@Override
			public void keyReleased(KeyEvent e)
			{
				if(e.getKeyCode() == KeyEvent.VK_ENTER) startSearch();
			}
		});
		
		// a "go" button
		go = new JButton(LocaleMessage.getString("search.go_button"));
		go.setToolTipText(LocaleMessage.getString("search.go_tooltip"));
		go.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				startSearch();
			}
		});
		
		// the result list
		resultPanel = new JPanel();
		resultPanel.setOpaque(false);
		resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));
		resultList = new JScrollPane(resultPanel);
		resultList.setOpaque(false);
		resultList.getViewport().setOpaque(false);
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(4, 4, 4, 4);
		gbc.gridx = 0;
		gbc.gridy = 0;
		bg.add(searchLabel, gbc);
		
		gbc.weightx = 1.0f;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 1;
		gbc.insets = new Insets(4, 0, 4, 4);
		bg.add(search, gbc);
		
		gbc.weightx = 0.0f;
		gbc.fill = GridBagConstraints.NONE;
		gbc.gridx = 2;
		bg.add(go, gbc);
		
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(0, 4, 4, 4);
		gbc.weightx = 1.0f;
		gbc.weighty = 1.0f;
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 3;
		bg.add(resultList, gbc);
		
		add(bg);
	}
	
	
	/**
	 * Starts the search executing a database query.
	 */
	private void startSearch()
	{
		if(StringUtils.isEmpty(search.getText())) return;
		
		resultPanel.removeAll();
		
		SwingUtilities.invokeLater(new Runnable(){
			public void run()
			{
				search.setEnabled(false);
				go.setEnabled(false);
			}
		});
		
		ArrayList<SearchResult> results = db.getSearchResults(search.getText().trim());
		for(SearchResult result : results)
		{
			SearchEntry entry = new SearchEntry(window, result, this);
			resultPanel.add(entry);
		}
		resultPanel.add(Box.createVerticalGlue());
		resultList.revalidate();
		
		SwingUtilities.invokeLater(new Runnable(){
			public void run()
			{
				search.setEnabled(true);
				go.setEnabled(true);
			}
		});
	}
}