package com.koolaborate.mvc.view.optionscreen

import java.awt.BorderLayout
import java.awt.Color
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.image.BufferedImage
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.reflect.Constructor
import java.util.ArrayList
import java.util.Locale

import javax.imageio.ImageIO
import javax.swing.BoxLayout
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.ParserConfigurationException
import javax.xml.transform.OutputKeys
import javax.xml.transform.Transformer
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

import org.apache.commons.lang3.StringUtils
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.NamedNodeMap
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import org.w3c.dom.Text
import org.xml.sax.SAXException

import com.koolaborate.mvc.view.decorations.Decorator
import com.koolaborate.mvc.view.themes.Theme
import com.koolaborate.mvc.view.themes.ThemeHelper

/***********************************************************************************
 * ThemesPanel *
 *********************************************************************************** 
 * A panel that lists the installed themes and lets the user select the one he
 * or * she wants to set for the application. *
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
public class ThemesPanel extends JPanel{
	private static final long serialVersionUID = -6563398784358082301L

	private static String themesFilePath = System.getProperty("user.dir") + File.separator + "themes.xml"

	private ArrayList<String> themeNames = new ArrayList<String>()
	private ArrayList<BufferedImage> themePreviews = new ArrayList<BufferedImage>()
	private ArrayList<ThemePanelEntry> themeEntries = new ArrayList<ThemePanelEntry>()
	private int activeIndex = -1

	private String selectedThemeClassName
	private String originallySelectedTheme
	private PluginAndThemeBrowser window
	private JPanel themesList

	/**
	 * Constructor.
	 * 
	 * @param w
	 *            the reference to the PluginAndThemeBrowser dialog window
	 * @param decorator
	 *            the decorator for the selection color
	 */
	@SuppressWarnings("unchecked")
	public ThemesPanel(PluginAndThemeBrowser w, Decorator decorator){
		window = w
		setLayout(new BorderLayout())
		setBackground(Color.WHITE)

		File f = new File(themesFilePath)
		if(!f.exists()) return

		// list the available themes
		themeNames.add("DefaultTheme")
		BufferedImage defaultPreview = null
		try {
			defaultPreview = ImageIO.read(getClass().getResource(
					"/images/defaultpreview.png"))
		} catch(IOException e2) {
			e2.printStackTrace()
		}

		themesList = new JPanel()
		themesList.setBackground(Color.WHITE)
		themesList.setLayout(new BoxLayout(themesList, BoxLayout.Y_AXIS))

		// first add the default theme
		String defaultName = "Default Theme"
		String defaultDesc = "The default theme that resembles the Media Player&reg;"

		// if the language on the system is German...
		if(Locale.getDefault().getLanguage().toLowerCase().equals("de")) {
			defaultName = "Standard-Thema"
			defaultDesc = "Standardm‰ﬂig erscheint der VibrantPlayer im Media Player&reg;-Look."
		}
		ThemePanelEntry defaultEntry = new ThemePanelEntry(defaultName,
				defaultDesc, "DefaultTheme", decorator)
		themeEntries.add(defaultEntry)
		themePreviews.add(defaultPreview)

		ThemeHelper.ensureThemeXMLFileIsUpToDate(themesFilePath)

		Document doc
		try {
			doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(
					f)
			Element e = doc.getDocumentElement()
			NodeList childNodes = e.getChildNodes()
			for(int i = 0; i < childNodes.getLength(); i++) {
				Node n = childNodes.item(i)
				String name = n.getNodeName()
				if(name.equals("Theme")) {
					String themeName = n.getTextContent().trim()
					themeNames.add(themeName)
					boolean isActiveTheme = false

					NamedNodeMap attributes = n.getAttributes()
					Node activeAttribute = attributes.getNamedItem("active")
					if(activeAttribute != null) {
						boolean active = false
						try {
							String content = activeAttribute.getTextContent()
							if(!StringUtils.isEmpty(content)) {
								active = Boolean.parseBoolean(content)
								if(active) {
									originallySelectedTheme = themeName
									isActiveTheme = true
								}
							}
						} catch(Exception ex) {
							ex.printStackTrace()
						}
					}
					// create the other installed themes
					if(!themeName.equals("DefaultTheme")) {
						try {
							final Decorator tempDec = null
							String packageName = "ui.themes."
							Class c = Class.forName(packageName + themeName)
							if(c != null) {
								// read the name and description as well as the
								// preview
								// image from the theme instance
								Constructor<Decorator> constructor = c.getConstructor(Decorator.class)
								Theme t = (Theme) constructor.newInstance(tempDec)

								ThemePanelEntry entry = new ThemePanelEntry(
										t.getName(), t.getDescription(),
										themeName, decorator)
								if(isActiveTheme) {
									entry.setSelected(true)
									activeIndex = themeEntries.size() + 1
								}
								themeEntries.add(entry)
								themePreviews.add(t.getPreviewImage())
							}
						} catch(Exception ex) {
							ex.printStackTrace()
						}
					}
				}
			}
		} catch(SAXException e1) {
			e1.printStackTrace()
		} catch(IOException e1) {
			e1.printStackTrace()
		} catch(ParserConfigurationException e1) {
			e1.printStackTrace()
		}

		// create a preview panel for the preview image
		final ThemePreviewPanel previewPanel = new ThemePreviewPanel()

		// a listener for the theme selection
		MouseAdapter l = new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent e){
				ThemePanelEntry src = (ThemePanelEntry) e.getSource()
				int i = 0, index = 0
				for(ThemePanelEntry entry: themeEntries) {
					if(entry != src)
						entry.setSelected(false)
					else {
						index = i
						entry.setSelected(true)
						selectedThemeClassName = entry.getClassName()
						if(!selectedThemeClassName.equals(originallySelectedTheme)) {
							window.setThemeChanged(true)
						} else {
							window.setThemeChanged(false)
						}
					}
					i++
				}
				// update the preview image
				BufferedImage currentImage = themePreviews.get(index)
				previewPanel.updateImage(currentImage)
				themesList.repaint()
			}
		}

		// if no theme other than the default theme is selected, set the default
		// theme as active
		if(activeIndex < 0) themeEntries.get(0).setSelected(true)

		// add all entries to the view
		int index = 0
		for(ThemePanelEntry entry: themeEntries) {
			entry.addMouseListener(l)
			themesList.add(entry)
			if(entry.isSelected())
				previewPanel.updateImage(themePreviews.get(index))
			index++
		}

		this.add(new JScrollPane(themesList), BorderLayout.WEST)
		JPanel spacer = new JPanel()
		spacer.setBackground(Color.WHITE)
		this.add(spacer, BorderLayout.CENTER)
		this.add(previewPanel, BorderLayout.EAST)
	}

	/**
	 * Writes the changes (the now selected theme) to the themes XML file.
	 */
	public void applyChangesToXMLFile(){
		File f = new File(themesFilePath)

		// get an instance of factory
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance()
		Document dom
		try {
			// get an instance of builder
			DocumentBuilder db = dbf.newDocumentBuilder()

			// create an instance of DOM
			dom = db.newDocument()
		} catch(ParserConfigurationException pce) {
			pce.printStackTrace()
			return
		}

		// create the root element
		Element rootEle = dom.createElement("Themes")
		dom.appendChild(rootEle)

		// create the available themes
		// add active=true attribute to selectedThemeClassName
		for(ThemePanelEntry entry: themeEntries) {
			Element theme = dom.createElement("Theme")
			if(entry.isSelected()) theme.setAttribute("active", "true")
			Element className = dom.createElement("Classname")
			Text text = dom.createTextNode(entry.getClassName())
			className.appendChild(text)
			theme.appendChild(className)
			rootEle.appendChild(theme)
		}

		// print to file
		try {
			Transformer tr = TransformerFactory.newInstance().newTransformer()
			tr.setOutputProperty(OutputKeys.INDENT, "yes")
			tr.setOutputProperty(OutputKeys.METHOD, "xml")
			tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount",
					"3")

			// to send the output to a file
			tr.transform(new DOMSource(dom), new StreamResult(
					new FileOutputStream(f)))
		} catch(Exception ex) {
			ex.printStackTrace()
		}
	}
}