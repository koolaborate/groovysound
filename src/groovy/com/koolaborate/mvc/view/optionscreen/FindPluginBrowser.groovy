package com.koolaborate.mvc.view.optionscreen

import java.awt.BorderLayout
import java.awt.Color
import java.awt.FlowLayout
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.io.InputStream
import java.net.URL
import java.util.ArrayList
import java.util.List
import java.util.Locale

import javax.swing.JButton
import javax.swing.JDialog
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.UIManager
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory

import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList

import com.koolaborate.mvc.view.common.VariableLineBorder
import com.koolaborate.util.LocaleMessage

import plug.engine.PlugEngine
import plug.engine.Pluggable

/***********************************************************************************
 * FindPluginBrowser                                                               *
 ***********************************************************************************
 * A dialog window that shows all available plugins on the Impressive Artworx      *
 * homepage. The user can install one of the plugins by choosing it.               *
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
class FindPluginBrowser extends JDialog
{
	private static final long serialVersionUID = 5480618581055245142L
	private static String XML_FILE_PATH = "http://www.impressive-artworx.de/tools/vibrantplayer/plugins/pluginlist.xml"
	
	/**
	 * Constructor.
	 */
	public FindPluginBrowser()
	{
		setTitle(LocaleMessage.getInstance().getString("plugins.find_plugins"))
		setDefaultCloseOperation(DISPOSE_ON_CLOSE)
		setLayout(new BorderLayout())
		setSize(400, 300)
		setResizable(false)
		setLocationRelativeTo(null) // center on screen
		
		JPanel plugins = new JPanel(new GridBagLayout())
		plugins.setBackground(Color.WHITE)
		
		GridBagConstraints gbc = new GridBagConstraints()
		gbc.anchor = GridBagConstraints.FIRST_LINE_START
		gbc.gridx = 0
		int y = 0
		gbc.gridy = y
		gbc.fill = GridBagConstraints.NONE
		gbc.weighty = 0.0f
		
		// get the language on the current machine (for example 'de' for German)
		String language = Locale.getDefault().getLanguage()
		
		// already installed plugins will be greyed out...
		List<Pluggable> allInstalledPlugins = PlugEngine.getInstance().getAllPluggables()
		List<String> allInstalledPluginNames = new ArrayList<String>()
		for(Pluggable plugin : allInstalledPlugins) allInstalledPluginNames.add(plugin.getName())
		
		// parse external XML file
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance()
		try
		{
			DocumentBuilder db = dbf.newDocumentBuilder()
			URL url = new URL(XML_FILE_PATH)
			InputStream inputStream = url.openStream()
			Document document = db.parse(inputStream)
			inputStream.close()
			
			Element e = document.getDocumentElement()
			NodeList childNodes = e.getChildNodes()
			for(int i = 0; i < childNodes.getLength(); i++)
			{
				Node n = childNodes.item(i)
				String name = n.getNodeName()
				if(name.equals("Plugin"))
				{
					String pluginName = "", icoPath = "", desc = "", path = ""
					NodeList pluginNodes = n.getChildNodes()
					for(int j = 0; j < pluginNodes.getLength(); j++)
					{
						Node node = pluginNodes.item(j)
						String nodeName = node.getNodeName()
						if(nodeName.equals("Name") && pluginName.equals(""))
						{
							pluginName = node.getTextContent() 
						}
						else if(nodeName.equals("Icon") && icoPath.equals(""))
						{
							icoPath = node.getTextContent() 
						}
						else if(nodeName.equals("Description") && desc.equals(""))
						{
							desc = node.getTextContent() 
						}
						else if(nodeName.equals("Description_" + language))
						{
							desc = node.getTextContent()
						} else if(nodeName.equals("Path") && path.equals("")){
							path = node.getTextContent()
						}
					}
					// create a panel for every entry (containing the name and description 
					// in the current Locale as well as an icon image and a button to 
					// install the plugin)
					NewPluginPanel panel = new NewPluginPanel(this, pluginName, desc, icoPath, path)
					if(allInstalledPluginNames.contains(pluginName)){
						// grey out and disable button to install the plugin...
						panel.setAlreadyInstalled(true)
					}
					plugins.add(panel, gbc)
					gbc.gridy = y++
					
					// TODO add a separator here... maybe a border at the lower side of
					// the panel 
				}
			}
		
		} catch(Exception e){
			e.printStackTrace()
		}
		
		gbc.weighty = 1.0f
		
		JPanel dummy = new JPanel()
		dummy.setBackground(Color.WHITE)
		plugins.add(dummy, gbc)
		
		// add the panels to a scrollable list
		add(new JScrollPane(plugins, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, 
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER), BorderLayout.CENTER)
		
		// add a close window button
		JPanel buttonPanel = new JPanel()
		buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT))
		
		JButton closeButt = new JButton(UIManager.getString("InternalFrameTitlePane.closeButtonText"))
		closeButt.addActionListener([
			actionPerformed: {
				dispose()
			}
		] as ActionListener)
		buttonPanel.add(closeButt)
		buttonPanel.setBorder(new VariableLineBorder(5, 5, 5, 5, Color.GRAY, 1, true, false, false, false))
		add(buttonPanel, BorderLayout.SOUTH)
	}
	
	/**
	 * Opens the 'find new plugins' dialog window.
	 */
	public static void showDialog(){
		FindPluginBrowser d = new FindPluginBrowser()
		d.setVisible(true)
	}
}