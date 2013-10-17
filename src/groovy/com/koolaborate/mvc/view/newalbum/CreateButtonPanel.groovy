package com.koolaborate.mvc.view.newalbum

import java.awt.FlowLayout
import java.awt.event.ActionListener

import javax.swing.JButton
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.koolaborate.mvc.view.mainwindow.MainWindow;
import com.koolaborate.util.LocaleMessage;

class CreateButtonPanel extends JPanel{
	JPanel centerPanel
	JButton okButton
	JButton cancelButton
	MainWindow mainWindow
	NewAlbumFrame newAlbumFrame
	
	def initialize(){
		okButton = new JButton(LocaleMessage.getInstance().getString("newalbum.okbutton"))
		okButton.setToolTipText(LocaleMessage.getInstance().getString("newalbum.okbutton_tooltip"))
		okButton.addActionListener([
			actionPerformed: {
				if(newAlbumFrame.saveAlbumAndSongsIntoDB()) {
					newAlbumFrame.dispose()
					mainWindow.getCenterPanel().refreshAlbumsView(
							mainWindow.getCenterPanel().getAlbumsPanel().getSortMode())
					SwingUtilities.invokeLater([
						run: {
							centerPanel.revalidate()
						}
					] as Runnable)
				}
			}
		] as ActionListener)
		okButton.setEnabled(false)

		cancelButton = new JButton(LocaleMessage.getInstance().getString("common.abort"))
		cancelButton.setToolTipText(LocaleMessage.getInstance().getString("common.abort_tooltip"))
		cancelButton.addActionListener([
			actionPerformed: {
				newAlbumFrame.dispose()
			}
		] as ActionListener)

		setLayout(new FlowLayout(FlowLayout.RIGHT))
		add(okButton)
		add(cancelButton)
	}
}
