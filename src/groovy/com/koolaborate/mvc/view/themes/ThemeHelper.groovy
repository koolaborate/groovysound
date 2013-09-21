package com.koolaborate.mvc.view.themes;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

/***********************************************************************************
 * ThemeHelper *
 *********************************************************************************** 
 * A helper class to read and write the currently selected theme to a xml file.
 * *
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
public class ThemeHelper{
	private final static String THEME_XML_FILE = "themes.xml";

	/**
	 * @return the class name of the currently active theme
	 */
	public static String retrieveActiveThemeName(){
		File f = new File(System.getProperty("user.dir") + File.separator
				+ THEME_XML_FILE);
		if(!f.exists()) {
			createNewThemesFile(null);
			return null;
		}

		String themeName = null;

		Document doc;
		try {
			doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(
					f);
			Element e = doc.getDocumentElement();
			NodeList childNodes = e.getChildNodes();
			for(int i = 0; i < childNodes.getLength(); i++) {
				Node n = childNodes.item(i);
				String name = n.getNodeName();
				if(name.equals("Theme")) {
					NamedNodeMap attributes = n.getAttributes();
					Node activeAttribute = attributes.getNamedItem("active");
					if(activeAttribute != null) {
						boolean active = false;
						try {
							String content = activeAttribute.getTextContent();
							if(!StringUtils.isEmpty(content)) {
								active = Boolean.parseBoolean(content);
								if(active) {
									themeName = n.getTextContent().trim();
									return themeName;
								}
							}
						} catch(Exception ex) {
							ex.printStackTrace();
						}
					}
				}
			}
		} catch(SAXException e1) {
			e1.printStackTrace();
		} catch(IOException e1) {
			e1.printStackTrace();
		} catch(ParserConfigurationException e1) {
			e1.printStackTrace();
		}
		return themeName;
	}

	/**
	 * This method is called if no themes.xml file exists (which is the case
	 * after the first start).
	 * 
	 * @param activeTheme
	 *            if the file exists already, a new one is created. However, if
	 *            a name is given, this one will be set as active. Can be
	 *            <code>null</code>
	 */
	private static void createNewThemesFile(String activeTheme){
		File f = new File(System.getProperty("user.dir") + File.separator
				+ THEME_XML_FILE);

		// delete if it already exists
		if(f.exists()) f.delete();

		// get an instance of factory
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		Document dom;
		try {
			// get an instance of builder
			DocumentBuilder db = dbf.newDocumentBuilder();

			// create an instance of DOM
			dom = db.newDocument();
		} catch(ParserConfigurationException pce) {
			pce.printStackTrace();
			return;
		}

		// create the root element
		Element rootEle = dom.createElement("Themes");
		dom.appendChild(rootEle);

		// create the available themes
		String themeName = "DefaultTheme";
		Element theme1 = dom.createElement("Theme");
		if(activeTheme != null && activeTheme.equals(themeName))
			theme1.setAttribute("active", "true");
		Element className = dom.createElement("Classname");
		Text text = dom.createTextNode(themeName);
		className.appendChild(text);
		theme1.appendChild(className);
		rootEle.appendChild(theme1);

		// brushed metal theme
		themeName = "MetalTheme";
		Element theme2 = dom.createElement("Theme");
		if(activeTheme != null && activeTheme.equals(themeName))
			theme2.setAttribute("active", "true");
		Element className2 = dom.createElement("Classname");
		Text text2 = dom.createTextNode(themeName);
		className2.appendChild(text2);
		theme2.appendChild(className2);
		rootEle.appendChild(theme2);

		// live theme
		themeName = "LiveTheme";
		Element theme3 = dom.createElement("Theme");
		if(activeTheme != null && activeTheme.equals(themeName))
			theme3.setAttribute("active", "true");
		Element className3 = dom.createElement("Classname");
		Text text3 = dom.createTextNode(themeName);
		className3.appendChild(text3);
		theme3.appendChild(className3);
		rootEle.appendChild(theme3);

		// TODO append new themes here...

		// print to file
		try {
			Transformer tr = TransformerFactory.newInstance().newTransformer();
			tr.setOutputProperty(OutputKeys.INDENT, "yes");
			tr.setOutputProperty(OutputKeys.METHOD, "xml");
			tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount",
					"3");

			// to send the output to a file
			tr.transform(new DOMSource(dom), new StreamResult(
					new FileOutputStream(f)));
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Method ensures that all themes are in the XML file. If not, the XML file
	 * is written anew.
	 * 
	 * @param themesFilePath
	 *            the path to the XML file
	 */
	public static void ensureThemeXMLFileIsUpToDate(String themesFilePath){
		// TODO quick and dirty...write it every time anew
		String activeOne = retrieveActiveThemeName();
		createNewThemesFile(activeOne);
	}
}