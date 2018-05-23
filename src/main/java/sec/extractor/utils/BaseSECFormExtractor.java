/**
 * 
 */
package sec.extractor.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import sec.extractor.constant.SECConstants;
import sec.extractor.pojo.TitleDetails;

/**
 * @author satyaveer.yadav
 *
 */
public class BaseSECFormExtractor {

	/**
	 * This method extracts the titles from the xhtml.
	 * @param bodyElements body elements
	 * @return list of titles found in the xhtml
	 */
/*	public static List<String> extractTitles(Elements bodyElements, String propertyFileName) {
		List<String> listOfTitles = new ArrayList<>();
		int docElementIndex = 0;
		List<String> titles = ReadTitles.readTitlesFromPropertiy(propertyFileName);
		for (int elementIndex = docElementIndex; elementIndex < bodyElements.size(); elementIndex++) {
			Element currentElement = bodyElements.get(elementIndex);
			String currentNodeText = currentElement.text();
			if (null != currentNodeText && !currentNodeText.equals(SECConstants.EMPTY_STRING) 
					&& !currentNodeText.equals(SECConstants.STRING_WITH_SINGLE_SPACE)) {
				String foundTtile = findMatchedTitleFromXhtml(currentNodeText, titles);
				if(!foundTtile.isEmpty()) {
					listOfTitles.add(foundTtile);
				}
				
			}
		}
		return listOfTitles;
	}
*/
	/**
	 * This method extracts the titles from the xhtml.
	 * @param bodyElements body elements
	 * @return list of titles found in the xhtml
	 */
	public static TitleDetails extractTitlesWithStdTitle(Elements bodyElements, String propertyFileName) {
		TitleDetails titleDetails = new TitleDetails();
		Map<String, String> standardTitles = new HashMap<>();
		List<String> listOfTitles = new ArrayList<>();
		int docElementIndex = 0;
		List<String> titles = ReadTitles.readTitlesFromPropertiy(propertyFileName);
		for (int elementIndex = docElementIndex; elementIndex < bodyElements.size(); elementIndex++) {
			Element currentElement = bodyElements.get(elementIndex);
			String currentNodeText = currentElement.text();
			if (null != currentNodeText && !currentNodeText.equals(SECConstants.EMPTY_STRING) 
					&& !currentNodeText.equals(SECConstants.STRING_WITH_SINGLE_SPACE)) {
				String foundTitle = findMatchedTitleFromXhtml(currentNodeText, titles);
				if(!foundTitle.isEmpty()) {
					String temptitles[] = foundTitle.split(SECConstants.STRING_COLON_HYFHAN);
					standardTitles.put(temptitles[0], temptitles[1]);
					listOfTitles.add(temptitles[0]);
				}
			}
		}
		titleDetails.setStandardTitle(standardTitles);
		titleDetails.setTitlesFoundInFile(listOfTitles);
		return titleDetails;
	}
	
	/**
	 * This method does
	 * 1. convert the string in lower case.
	 * 2. If string contains dot(.) at the end of the string then it removes that dot(.).
	 * 3. replace all the spaces including html(&nbsp;) spaces.
	 * @param input both node text as well as titles from property file
	 * @return lower case without space and dot at the end of the string 
	 */
	public static String prepareTitle(String input) {
		String lowercase = input.trim().toLowerCase();
		if(lowercase.endsWith(SECConstants.STRING_DOT)) {
			lowercase = lowercase.substring(0, lowercase.length() - 1);
		}
		// replace all space and then remove html spaces(&nbsp;)
		String withoutSpace = lowercase.replaceAll(SECConstants.SPACE_REGX, SECConstants.EMPTY_STRING).replaceAll(
				SECConstants.SPACE_HTML_NBSP, SECConstants.EMPTY_STRING);
		
		return withoutSpace.replaceAll("[^a-zA-Z0-9]", SECConstants.EMPTY_STRING);
	}
	
	
	/**
	 * This method compares the node text with the each search/next title.
	 * @param nodeText data from node
	 * @param searchTitles titles from property file
	 * @return if node text matches any of the title then tru else false.
	 */
	public static String findMatchString(String nodeText, String searchTitle) {
		String matchedString = SECConstants.EMPTY_STRING;
		if(null != searchTitle) {
			String title = prepareTitle(searchTitle.toLowerCase());
			if (nodeText.length() < 150 && (nodeText.equalsIgnoreCase(title) || nodeText.contains(title))) {
				matchedString = title;
			}
		}
		return matchedString;
	}
	
	
	/**
	 * This method compares the node text with the each search/next title.
	 * @param nodeText data from node
	 * @param searchTitles titles from property file
	 * @return if node text matches any of the title then true else false.
	 */
/*	public static String findMatchedTitleFromXhtml(String currentNodeText, List<String> searchTitles) {
		String matchedString = SECConstants.EMPTY_STRING;
		boolean isFound = false;
		if (null != searchTitles && searchTitles.size() > 0) {
			String nodeText = prepareTitle(currentNodeText);
			for (int i = 0; i < searchTitles.size(); i++) {
				String subTitles[] = searchTitles.get(i).split(SECConstants.STRING_COLON_HYFHAN);
				for (int j = 0; j < subTitles.length; j++) {
					String title = prepareTitle(subTitles[j].toLowerCase());
					if (nodeText.length() < 150 && (nodeText.equalsIgnoreCase(title) || nodeText.contains(title)) 
							&& !nodeText.contains("seeitem") && !nodeText.contains("fromitem") 
							&& !nodeText.contains("refertoitem") && !nodeText.contains("partiitem")
							&& !nodeText.contains("partivitem")) {
						matchedString = currentNodeText;
						isFound = true;
						break;
					}
				}
				if (isFound) {
					break;
				}
			}
		}
		return matchedString;
	}
*/	
	
	/**
	 * This method compares the node text with the each search/next title.
	 * @param nodeText data from node
	 * @param searchTitles titles from property file
	 * @return if node text matches any of the title then true else false.
	 */
	public static String findMatchedTitleFromXhtml(String currentNodeText, List<String> searchTitles) {
		String matchedString = SECConstants.EMPTY_STRING;
		boolean isFound = false;
		if (null != searchTitles && searchTitles.size() > 0) {
			String nodeText = prepareTitle(currentNodeText);
			for (int i = 0; i < searchTitles.size(); i++) {
				String subTitles[] = searchTitles.get(i).split(SECConstants.STRING_COLON_HYFHAN);
				for (int j = 0; j < subTitles.length; j++) {
					String title = prepareTitle(subTitles[j].toLowerCase());
					if (nodeText.length() < 150 && (nodeText.equalsIgnoreCase(title) || nodeText.contains(title)) 
							&& !nodeText.contains("seeitem") && !nodeText.contains("fromitem") 
							&& !nodeText.contains("refertoitem") && !nodeText.contains("partiitem")
							&& !nodeText.contains("partivitem") && !nodeText.contains("seeaccompany")
							&& !nodeText.contains("theaccompanying") ) {
						matchedString =   currentNodeText + ":-" + subTitles[0];
						isFound = true;
						break;
					}
				}
				if (isFound) {
					break;
				}
			}
		}
		return matchedString;
	}
	
	
}
