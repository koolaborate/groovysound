package com.koolaborate.util

import java.net.MalformedURLException
import java.net.URL
import java.util.ArrayList
import java.util.Locale
import java.util.MissingResourceException
import java.util.ResourceBundle

import org.apache.commons.lang3.StringUtils
import org.apache.log4j.Logger
import org.htmlparser.Node
import org.htmlparser.NodeFilter
import org.htmlparser.Parser
import org.htmlparser.filters.HasAttributeFilter
import org.htmlparser.filters.NodeClassFilter
import org.htmlparser.nodes.RemarkNode
import org.htmlparser.nodes.TagNode
import org.htmlparser.tags.*
import org.htmlparser.util.NodeList
import org.htmlparser.util.ParserException

/***********************************************************************************
 * HTMLparser                                                                      *
 ***********************************************************************************
 * A parser that collects data from html web pages. It is used to retrieve images  *
 * for artists and texts describing their live and career.                         *
 ***********************************************************************************
 * (c) Impressive Artworx, 2k8                                                     * 
 * @author Manuel Kaess                                                            *
 * @version 1.0                                                                    *
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

public class HtmlParser {
	/** the log4j logger */
	static Logger log = Logger.getLogger(HtmlParser.class.getName())

	private static HtmlParser parserInstance
	private HtmlParser(){}

	def static synchronized HtmlParser getInstance(){
		if(null == parserInstance){
			parserInstance = new HtmlParser()
		}

		return parserInstance
	}

	/**
	 * Tries to retrieve a cover image path for the given album name by searching the Google image web base.
	 * 
	 * @param artist the name of the artist
	 * @param album the title of the album
	 * @return the image path if an image is found, <code>null</code> otherwise
	 */
	def URL[] getCoverImagePathsFromGoogle(String artist, String album) {
		// TODO URLS array should be an object
		if(null == artist || album == null) return null
		
		URL[] urls = new URL[2]

		String searchString = artist.trim() + "+" + album.trim()
		searchString.replaceAll(" ", "+")

		Parser parser = null
		try {
			Locale loc = Locale.getDefault()

			String link = "http://images.google.com/images?hl=de&q=" + searchString + "&btnG=Bilder-Suche&gbv=2"
			parser = new Parser(link)

			def nodeFilter = [
				accept: {
					return true
				}
			] as NodeFilter
			NodeList list = parser.parse(nodeFilter)
			// get the complete source of the html page
			String complete = list.toHtml()
			// parse from the first found image on..
			String beginning = "/imgres?imgurl="
			// ...until the '&' sign
			String theEnd = "&"
			int beginIndex = complete.indexOf(beginning)
			// if there has been an image
			if (beginIndex != -1)
			{
				complete = complete.substring(beginIndex + beginning.length(), complete.length())

				// get the first image
				int endIndex = complete.indexOf(theEnd)
				String firstUrl = complete.substring(0, endIndex)

				if(validImagePath(firstUrl)) {
					try {
						urls[0] = new URL(firstUrl)
					} catch (MalformedURLException e) {
						log.debug(e.getMessage())
					}
				}
				// now get the second image (if available)
				complete = complete.substring(endIndex + 1, complete.length())

				beginIndex = complete.indexOf(beginning)
				if (beginIndex != -1) {
					complete = complete.substring(beginIndex + beginning.length(), complete.length())
					endIndex = complete.indexOf(theEnd)
					String secondUrl = complete.substring(0, endIndex)
					if(validImagePath(secondUrl)) {
						try {
							urls[1] = new URL(secondUrl)
						} catch (MalformedURLException e) {
							log.debug(e.getMessage())
						}
					}
				}

				// if no image could be found
			} else {
				log.debug("No image path found for album '" + album + "'.")
			}
		} catch (ParserException e) {
			log.debug("Unable parsing the html page: "+ e.getMessage())
		}
		return urls
	}


	def String getArtistInfoFromWikipedia(String artist) {
		String ret = null
		if(null == artist || StringUtils.isBlank(artist)) return ret
		
		String noArtistString = "No info found for artist"
		ResourceBundle lang = null;

		try {
			lang = ResourceBundle.getBundle("resources.maintexts")
			noArtistString = lang.getString("searchartist.noartistinfo")
		} catch(MissingResourceException e) {
			log.error(e.getMessage())
		}

		String searchString = artist.trim()
		searchString.replaceAll(" ", "_")

		Parser parser
		try {
			Locale loc = Locale.getDefault()
			String link = "http://" + loc.getLanguage() + ".wikipedia.org/wiki/Special:Search?search=" + searchString + "&go=Go"
			parser = new Parser(link)
			NodeList root = parser.parse(null)

			// NodeClassFilter's
			ArrayList<NodeClassFilter> FilterArray = new ArrayList<NodeClassFilter>()
			FilterArray.add(new NodeClassFilter(HeadTag.class)) // remove the HTML head section
			FilterArray.add(new NodeClassFilter(ScriptTag.class))
			FilterArray.add(new NodeClassFilter(StyleTag.class))
			FilterArray.add(new NodeClassFilter(FrameTag.class))
			FilterArray.add(new NodeClassFilter(FrameSetTag.class))
			FilterArray.add(new NodeClassFilter(FormTag.class))
			FilterArray.add(new NodeClassFilter(BaseHrefTag.class))
			FilterArray.add(new NodeClassFilter(ObjectTag.class))
			FilterArray.add(new NodeClassFilter(AppletTag.class)) // remove applets
			FilterArray.add(new NodeClassFilter(MetaTag.class))
			FilterArray.add(new NodeClassFilter(ImageTag.class)) // remove images
			FilterArray.add(new NodeClassFilter(ProcessingInstructionTag.class))
			for (int j=0; j < FilterArray.size(); j++) {
				NodeList nl = root.extractAllNodesThatMatch((NodeClassFilter) FilterArray.get(j), true)
				for(int i=0; i < nl.size(); i++) {
					TagNode node = (TagNode) nl.elementAt(i)
					if(node != null) {
						if(node.getParent() != null) {
							node.getParent().getChildren().remove(node)
						}
					}
				}
			}

			// remove certain class divs and spans
			NodeList nl2 = root.extractAllNodesThatMatch(new HasAttributeFilter("class"), true)
			for(int i=0; i < nl2.size(); i++)
			{
				TagNode node = (TagNode) nl2.elementAt(i)
				if(node != null)
				{
					String classString = node.getAttribute("class")
					classString = classString.toLowerCase()
					if(classString.equals("internal") || classString.equals("editsection") || classString.equals("printfooter")
					|| classString.equals("noprint") || classString.equals("thumb tright") || classString.equals("thumb tleft")
					|| classString.equals("printfooter") || classString.equals("prettytable float-right")
					|| classString.equals("sidebox"))
					{
						if(node.getParent() != null)
						{
							node.getParent().getChildren().remove(node)
						}
					}
				}
			}

			// remove certain spans, tables and divs with id attributes
			NodeList nl3 = root.extractAllNodesThatMatch(new HasAttributeFilter("id"), true)
			for(int i=0; i < nl3.size(); i++) {
				TagNode node = (TagNode) nl3.elementAt(i)
				if(node != null) {
					String idString = node.getAttribute("id")
					idString = idString.toLowerCase()
					if(idString.equals("toc") || idString.equals("catlinks")) {
						if(node.getParent() != null) {
							node.getParent().getChildren().remove(node)
						}
					}
				}
			}

			// throw comments out
			def nodeFilter = [
				accept: { n ->
					if(n instanceof RemarkNode){
						return true
					}
					return false
				}
			] as NodeFilter
			
			NodeList nl4 = root.extractAllNodesThatMatch(nodeFilter, true)
					
			for(int i=0; i < nl4.size(); i++)
			{
				if(nl4.elementAt(i) instanceof RemarkNode)
				{
					RemarkNode node = (RemarkNode) nl4.elementAt(i)
					if(node != null)
					{
						String text = node.getText().trim()
						if(text.equals("start content") || text.equals("end content"))
						{
							continue
						}
						else if(node.getParent() != null)
						{
							node.getParent().getChildren().remove(node)
						}
					}
				}
			}

			// get the complete HTML
			String complete = root.toHtml()
			ret = complete

			// parse from the beginning on..
			String beginning = "<!-- start content -->"
			// ...until the end tag
			String theEnd = "<!-- end content -->"
			int beginIndex = complete.indexOf(beginning)
			if (beginIndex != -1) {
				complete = complete.substring(beginIndex + beginning.length(), complete.length())
				int endIndex = complete.indexOf(theEnd)
				complete = complete.substring(0, endIndex)

				ret = "<HTML><HEAD></HEAD><BODY>" + complete + "</BODY></HTML>"
				// if no information could be found
			} else {
				ret = "<HTML><HEAD></HEAD><BODY><i>" + noArtistString + " '" + artist + "'.</i></BODY></HTML>"
			}
		} catch(ParserException e) {
			log.debug("Unable parsing the html page: " + e.getMessage())
			ret = "<HTML><HEAD></HEAD><BODY><i>" + noArtistString + " '" + artist + "'.</i></BODY></HTML>"
		}
		return ret
	}

	def URL getArtistImageFromGoogle(String artist) {
		URL url = null
		if(null == artist || StringUtils.isBlank(artist)) return url

		String searchString = artist.trim()
		searchString.replaceAll(" ", "+")

		Parser parser = null
		try {
			Locale loc = Locale.getDefault()

			String link = "http://images.google.com/images?hl=de&q=" + searchString + "&btnG=Bilder-Suche&gbv=2"
			parser = new Parser(link)

			def nodeFilter = [
				accept: { n ->
					return true
				}
			] as NodeFilter
		
			NodeList list = parser.parse(nodeFilter)
			// get the complete source of the html page
			String complete = list.toHtml()
			// parse from the first found image on..
			String beginning = "/imgres?imgurl="
			// ...until the '&' sign
			String theEnd = "&"
			int beginIndex = complete.indexOf(beginning)
			// if there has been an image
			if (beginIndex != -1){
				complete = complete.substring(beginIndex + beginning.length(), complete.length())

				// get the first image
				int endIndex = complete.indexOf(theEnd)
				String firstUrl = complete.substring(0, endIndex)

				if(validImagePath(firstUrl)) {
					try {
						url = new URL(firstUrl)
					} catch (MalformedURLException e) {
						log.debug(e.getMessage())
					}
				}

				// if no image could be found
			} else {
				log.debug("No image path found for artist '" + artist + "'.")
			}
		} catch (ParserException e) {
			log.debug("Unable parsing the html page: "+ e.getMessage())
		}
		return url
	}

	/**
	 * Returns a list of the first three URLs that Google(R) returns when searching for the artist.
	 * 
	 * @param artist the name of the artist
	 * @return a list of URLs for artist images
	 */
	def URL[] getArtistImagePathsFromGoogle(String artist) {
		if(null == artist) return null
		
		URL[] urls = new URL[3]
		if(StringUtils.isBlank(artist)) return urls 

		String searchString = artist.trim()
		searchString.replaceAll(" ", "+")

		Parser parser = null
		try {
			String link = "http://images.google.com" + "/images?hl=de&q=" + searchString + "&btnG=Bilder-Suche&gbv=2"
			parser = new Parser(link)

			def nodeFilter = [
				accept: { n ->
					return true
				}
			]
			
			NodeList list = parser.parse(nodeFilter)
		
			String complete = list.toHtml()
			String beginning = "/imgres?imgurl="
			String theEnd = "&"
			
			int beginIndex = complete.indexOf(beginning)
			if (beginIndex != -1) {
				complete = complete.substring(beginIndex + beginning.length(), complete.length())

				// get the first image
				int endIndex = complete.indexOf(theEnd)
				String firstUrl = complete.substring(0, endIndex)

				if(validImagePath(firstUrl)) {
					try {
						urls[0] = new URL(firstUrl)
					} catch (MalformedURLException e) {
						e.printStackTrace()
						log.debug(e.getMessage())
					}
				}
				// now get the second image (if available)
				complete = complete.substring(endIndex + 1, complete.length())

				beginIndex = complete.indexOf(beginning)
				if (beginIndex != -1) {
					complete = complete.substring(beginIndex + beginning.length(), complete.length())
					endIndex = complete.indexOf(theEnd)
					String secondUrl = complete.substring(0, endIndex)
					if(validImagePath(secondUrl)) {
						try {
							urls[1] = new URL(secondUrl)
						} catch (MalformedURLException e) {
							log.debug(e.getMessage())
						}
					}
				}
				// now get the third image (if available)
				complete = complete.substring(endIndex + 1, complete.length())

				beginIndex = complete.indexOf(beginning)
				if (beginIndex != -1) {
					complete = complete.substring(beginIndex + beginning.length(), complete.length())
					endIndex = complete.indexOf(theEnd)
					String thirdUrl = complete.substring(0, endIndex)
					if(validImagePath(thirdUrl)) {
						try {
							urls[2] = new URL(thirdUrl)
						} catch (MalformedURLException e) {
							log.debug(e.getMessage())
						}
					}
				}

			} else {
				log.debug("No image path found for artist '" + artist + "'.")
			}
		} catch (ParserException e) {
			e.printStackTrace()
			log.debug("Unable parsing the html page: "+ e.getMessage())
		}
		return urls
	}
	
	/**
	 * Checks whether the image path is valid or not.
	 *
	 * @param imagePath the image path
	 * @return <code>true</code> if the path is valid, <code>false</code> otherwise
	 */
	private boolean validImagePath(String imagePath) {
		// wrong image
		if(imagePath.startsWith("http://upload.wikimedia.org/wikipedia/commons/")) {
			return false
		}
		// all possible image types
		// TODO refactor to a map or enum
		else if(imagePath.endsWith("jpg") || imagePath.endsWith("JPG") || imagePath.endsWith("Jpg")
		|| imagePath.endsWith("png") || imagePath.endsWith("PNG") || imagePath.endsWith("Png")
		|| imagePath.endsWith("gif") || imagePath.endsWith("GIF") || imagePath.endsWith("Gif")){
			return true
		}
		// unknown file extension
		return false
	}
	
	
	
}



