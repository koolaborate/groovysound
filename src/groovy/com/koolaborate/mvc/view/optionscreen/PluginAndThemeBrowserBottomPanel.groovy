package com.koolaborate.mvc.view.optionscreen

import com.koolaborate.mvc.view.common.VariableLineBorder
import com.koolaborate.mvc.view.dialogs.VistaDialog;
import com.koolaborate.util.LocaleMessage;

import java.awt.Color;
import java.awt.event.ActionListener

import javax.swing.JButton
import javax.swing.JPanel;
import javax.swing.UIManager;

class PluginAndThemeBrowserBottomPanel extends JPanel{
	boolean changedTheme
	ThemesPanel themesPanel
	PluginAndThemeBrowser pluginAndThemeBrowser
	
	def initialize(){
		setOpaque(false)
		setBorder(new VariableLineBorder(5, 5, 5, 5, Color.GRAY, 1, true,
				false, false, false))
		
		JButton commitButton = new JButton(UIManager.getString("FileChooser.saveButtonText"))
		commitButton.setToolTipText(LocaleMessage.getInstance().getString("common.save_tooltip"))
		commitButton.setOpaque(false)
		commitButton.addActionListener([
			actionPerformed: {
				pluginAndThemeBrowser.dispose()
				if(changedTheme){
					themesPanel.applyChangesToXMLFile()
					// restart of the application necessary
					VistaDialog.showDialog(LocaleMessage.getInstance().getString("common.restart_title"),
							LocaleMessage.getInstance().getString("common.restart_necessary"),
							LocaleMessage.getInstance().getString("common.restart_text"),
							VistaDialog.INFORMATION_MESSAGE)
				}
			}
		] as ActionListener)
		add(commitButton)
		
		final JButton abortButton = new JButton(LocaleMessage.getInstance().getString("common.abort"))
		abortButton.setToolTipText(LocaleMessage.getInstance().getString("common.abort_tooltip"))
		abortButton.setOpaque(false)
		abortButton.addActionListener([
			actionPerformed: {
				pluginAndThemeBrowser.dispose()
			}
		] as ActionListener)
		add(abortButton)
	}
}
