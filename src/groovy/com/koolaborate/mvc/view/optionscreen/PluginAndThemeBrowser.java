package com.koolaborate.mvc.view.optionscreen;

import javax.imageio.ImageIO;
import javax.swing.JDialog;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LinearGradientPaint;
import java.awt.Paint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.EventObject;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;

import com.koolaborate.mvc.view.common.VariableLineBorder;
import com.koolaborate.mvc.view.dialogs.VistaDialog;
import com.koolaborate.mvc.view.mainwindow.MainWindow;
import com.koolaborate.util.LocaleMessage;

import plug.engine.PlugEngine;
import plug.engine.Pluggable;
import plug.engine.ui.swing.PluggableWrapper;
import plug.engine.ui.swing.firefoxstyle.UpdateDialog;

/***********************************************************************************
 * PluginAndThemeBrowser                                                           *
 ***********************************************************************************
 * A dialog window that shows all installed plugins and themes.                    *
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
public class PluginAndThemeBrowser extends JDialog
{
	private static final long serialVersionUID = 3020990619723531782L;
	private final JTable table;
	private final DefaultTableModel model;
	
	private JPanel header, mainPanel;
	private JPanel panel;
	private ToggleButton b1, b2;
	
	private boolean changedTheme = false;
	
	static String step1 = "plugins";
	static String step2 = "themes";
	
	private MainWindow window;
	private ThemesPanel themesPanel;
	
	/**
	 * Constructor.
	 * 
	 * @param window reference to the main window
	 */
	public PluginAndThemeBrowser(MainWindow window)
	{
		super();
		this.window = window;
		setTitle(LocaleMessage.getString("options.plugins"));
		setModal(true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
		// create the card layout
		
		// header panel
		header = buildHeaderPanel();
		ToggleButton button = (ToggleButton)header.getComponent(0);
		button.setSelected(true);
		add(header, BorderLayout.NORTH);
		
		// create the plugin list
		model = new DefaultTableModel(){
			private static final long serialVersionUID = -383831680154515950L;

			@Override
			public java.lang.Class<?> getColumnClass(int columnIndex){
				return PluggableWrapper.class;
			};		
		};
		fillModel();
		table = new JTable(model){
			private static final long serialVersionUID = -3484204370997614489L;
			@Override
			public boolean editCellAt(int row, int column, EventObject e){
				this.selectRow(row);
				return super.editCellAt(row, column, e);
			}	
			public void selectRow(int row){
				selectionModel.setSelectionInterval(row, row);
			}
		};
		table.setTableHeader(null);
		table.setShowGrid(false);
		table.setIntercellSpacing(new Dimension(0, 0));
		
		table.setDefaultRenderer(PluggableWrapper.class, new PluginCellRenderer());
		table.setDefaultEditor(PluggableWrapper.class, new PluginCellRenderer());
		
		// main panel
		mainPanel = buildMainPanel();
		add(mainPanel, BorderLayout.CENTER);
		
		// button panel
		add(getBottomPanel(), BorderLayout.SOUTH);
		
		pack();
		setLocationRelativeTo(null);
		setResizable(false);
	}
	
	/**
	 * @return the button panel for the plugin tab
	 */
	private JPanel getButtonPanel()
	{
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		buttonPanel.setOpaque(false);
		buttonPanel.setBorder(new VariableLineBorder(0, 10, 10, 10, Color.GRAY, 0, false, 
				false, false, false));
		
		JButton updateButton = new JButton(LocaleMessage.getString("options.updatenow_tooltip"));
		updateButton.setToolTipText(LocaleMessage.getString("options.updatenow_tooltip"));
		updateButton.setOpaque(false);
		updateButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				UpdateDialog.showDialog();
			}			
		});
		buttonPanel.add(updateButton);
		
		final JButton addButton = new JButton(LocaleMessage.getString("options.search_plugins"));
		addButton.setToolTipText(LocaleMessage.getString("options.search_plugins"));
		addButton.setOpaque(false);
		addButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				FindPluginBrowser.showDialog();
				dispose();
			}		
		});
		buttonPanel.add(addButton);
		
		return buttonPanel;
	}
	
	/**
	 * @return the bottom panel with the save and abort buttons
	 */
	private JPanel getBottomPanel()
	{
		JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		bottomPanel.setOpaque(false);
		bottomPanel.setBorder(new VariableLineBorder(5, 5, 5, 5, Color.GRAY, 1, true,
				false, false, false));
		
		JButton commitButton = new JButton(UIManager.getString("FileChooser.saveButtonText")); 
		commitButton.setToolTipText(LocaleMessage.getString("common.save_tooltip"));
		commitButton.setOpaque(false);
		commitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
				if(changedTheme) 
				{
					themesPanel.applyChangesToXMLFile();
					// restart of the application necessary
					VistaDialog.showDialog(LocaleMessage.getString("common.restart_title"), 
							LocaleMessage.getString("common.restart_necessary"), 
							LocaleMessage.getString("common.restart_text"), 
							VistaDialog.INFORMATION_MESSAGE);
				}
			}			
		});
		bottomPanel.add(commitButton);
		
		final JButton abortButton = new JButton(LocaleMessage.getString("common.abort"));
		abortButton.setToolTipText(LocaleMessage.getString("common.abort_tooltip"));
		abortButton.setOpaque(false);
		abortButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}		
		});
		bottomPanel.add(abortButton);
		
		return bottomPanel;
	}

	/**
	 * open a PluginDialog
	 */
	public static void showDialog(MainWindow w) 
	{
		PluginAndThemeBrowser d = new PluginAndThemeBrowser(w);
		d.setVisible(true);
	}
	
	private void fillModel() 
	{
		model.setRowCount(0);
		model.setColumnCount(0);
		List<Pluggable> data = PlugEngine.getInstance().getAllVisiblePluggables();
		PluggableWrapper[] wrappers = new PluggableWrapper[data.size()];
		for (int i = 0; i < wrappers.length; i++) 
		{
			PluggableWrapper wrapper = new PluggableWrapper(data.get(i));
			wrappers[i] = wrapper;
		}
		model.addColumn("", wrappers); 
	}
	
	/**
	 * @inheritDoc
	 */
	@Override
	public void repaint() 
	{
		this.fillModel();
		table.setModel(new DefaultTableModel());
		table.setModel(model);
		super.repaint();
	}
	
	/**
	 * Method sets the clicked toggle button as selected and the others to be deselected.
	 * 
	 * @param b the selected <code>MyToggleButton</code>
	 */
	public void setSelectedToggleButton(ToggleButton b)
	{
		// deselect all other buttons
		b1.setSelected(b == b1);
		b2.setSelected(b == b2);
		
		CardLayout cp = (CardLayout)mainPanel.getLayout();
		if(b == b1) cp.show(mainPanel, step1);
		else if(b == b2) cp.show(mainPanel, step2);
	}
	
	/**
	 * Builds the main panel.
	 * 
	 * @return the created <code>JPanel</code>
	 */
	private JPanel buildMainPanel()
	{
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new CardLayout());
		mainPanel.setBackground(Color.WHITE);
		mainPanel.setBorder(new VariableLineBorder(5, 5, 5, 5, Color.GRAY, 1, true, false, 
				false, false));
		
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.getViewport().setOpaque(false);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setPreferredSize(new Dimension(500, 300));
		
		panel = new JPanel(new BorderLayout(), true);
		panel.setBackground(Color.WHITE);
		panel.add(scrollPane, BorderLayout.CENTER);
		panel.add(getButtonPanel(), BorderLayout.SOUTH);
		
		themesPanel = new ThemesPanel(this, window.getDecorator());
		mainPanel.add(panel, step1);
		mainPanel.add(themesPanel, step2);
		return mainPanel;
	}
	
	/**
	 * Builds the header panel with the toggle buttons.
	 * 
	 * @return the created <code>JPanel</code>
	 */
	private JPanel buildHeaderPanel()
	{
		JPanel panel = new JPanel(){
			private static final long serialVersionUID = 2411793553668898755L;
			@Override
			public void paintComponent(Graphics g)
			{
				Graphics2D g2 = (Graphics2D)g;
				Paint oldPaint = g2.getPaint();

				LinearGradientPaint p = new LinearGradientPaint(0.0f, 0.0f, 0.0f, 50.0f,
						new float[]{0.0f, 0.34f, 0.341f, 1.0f},
						new Color[]{new Color(0x516b9b),
						new Color(0x435d8d),
						new Color(0x365080),
						new Color(0x2b4575)});
		        
		        g2.setPaint(p);
		        g2.fillRect(0, 0, getWidth(), getHeight());
		        
		        g2.setPaint(oldPaint);
		        super.paintComponents(g);
		        g2.dispose();
			}
			@Override
			public Insets getInsets() 
			{
			    return new Insets(0, 0, 0, 0);
			}
		};
		panel.setPreferredSize(new Dimension(200, 50));
		panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		FlowLayout myLayout = new FlowLayout(FlowLayout.LEFT);
		myLayout.setVgap(0);
		myLayout.setHgap(0);
		panel.setLayout(myLayout);
		
		// built the toggle buttons
		BufferedImage i1 = null, i2 = null;
		try
		{
			i1 = ImageIO.read(getClass().getResource("/images/plugins.png"));
			i2 = ImageIO.read(getClass().getResource("/images/colorize.png"));
		}
		catch (IOException e1)
		{
			e1.printStackTrace();
		}
		
		Dimension buttonSize = new Dimension(80, 50);
		b1 = new ToggleButton(LocaleMessage.getString("options.plugin"), i1);
		b1.setPreferredSize(buttonSize);
		b1.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent e)
			{
				ToggleButton src = (ToggleButton) e.getSource();
				setSelectedToggleButton(src);
			}
		});
		panel.add(b1);
		
		b2 = new ToggleButton(LocaleMessage.getString("options.themes"), i2);
		b2.setPreferredSize(buttonSize);
		b2.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent e)
			{
				ToggleButton src = (ToggleButton) e.getSource();
				setSelectedToggleButton(src);
			}
		});
		panel.add(b2);
		
		return panel;
	}
	
	/**
	 * Sets the value of whether the current theme has been changed or not.
	 * 
	 * @param b <code>true</code> if the active theme has been changed, <code>false</code> otherwise
	 */
	public void setThemeChanged(boolean b)
	{
		this.changedTheme = b;
	}
}