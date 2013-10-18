package com.koolaborate.mvc.view.optionscreen

import com.koolaborate.mvc.view.common.VariableLineBorder
import com.koolaborate.util.LocaleMessage;

import java.awt.Color;
import java.awt.FlowLayout
import java.awt.event.ActionListener

import javax.swing.JButton
import javax.swing.JPanel;

import plug.engine.ui.swing.applestyle.UpdateDialog;

class PluginAndThemeBrowserButtonPanel extends JPanel{
	PluginAndThemeBrowser pluginAndThemBrowser
	
	def initialize(){
		setLayout(new FlowLayout(FlowLayout.LEFT))
		setOpaque(false)
		setBorder(new VariableLineBorder(0, 10, 10, 10, Color.GRAY, 0, false, false, false, false))
		
		JButton updateButton = new JButton(LocaleMessage.getInstance().getString("options.updatenow_tooltip"))
		updateButton.setToolTipText(LocaleMessage.getInstance().getString("options.updatenow_tooltip"))
		updateButton.setOpaque(false)
		updateButton.addActionListener([
			actionPerformed: {
				UpdateDialog.showDialog()
			}
		] as ActionListener)
		add(updateButton)
		
		final JButton addButton = new JButton(LocaleMessage.getInstance().getString("options.search_plugins"))
		addButton.setToolTipText(LocaleMessage.getInstance().getString("options.search_plugins"))
		addButton.setOpaque(false)
		addButton.addActionListener([
			actionPerformed: {
				FindPluginBrowser.showDialog()
				pluginAndThemBrowser.dispose()
			}
		] as ActionListener)
		add(addButton)
	}
}
