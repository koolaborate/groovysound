package com.koolaborate.mvc.view.optionscreen

import com.koolaborate.mvc.view.common.VariableLineBorder
import com.koolaborate.mvc.view.mainwindow.MainWindow
import java.awt.BorderLayout
import java.awt.CardLayout
import java.awt.Color;
import java.awt.Dimension

import javax.swing.JPanel;
import javax.swing.JScrollPane
import javax.swing.JTable

class PluginAndThemeBrowserMainPanel extends JPanel{
	JPanel panel
	ThemesPanel themesPanel
	MainWindow mainWindow 
	JTable table
	static String step1
	static String step2
	PluginAndThemeBrowser pluginAndThemeBrowser
	
	def initialize(){
		setLayout(new CardLayout())
		setBackground(Color.WHITE)
		setBorder(new VariableLineBorder(5, 5, 5, 5, Color.GRAY, 1, true, false,
				false, false))
		
		JScrollPane scrollPane = new JScrollPane(table)
		scrollPane.getViewport().setOpaque(false)
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER)
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS)
		scrollPane.setPreferredSize(new Dimension(500, 300))
		
		panel = new JPanel(new BorderLayout(), true)
		panel.setBackground(Color.WHITE)
		panel.add(scrollPane, BorderLayout.CENTER)
		panel.add(pluginAndThemeBrowser.getButtonPanel(), BorderLayout.SOUTH)
		
		themesPanel = new ThemesPanel(this, mainWindow.getDecorator())
		add(panel, step1)
		add(themesPanel, step2)
	}
	
	
}





