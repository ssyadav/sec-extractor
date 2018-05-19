/**
 * 
 */
package sec.extractor.utils;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sec.extractor.constant.SECConstants;
import sec.extractor.pojo.SECItemSection;

/**
 * @author satyaveer.yadav
 *
 */
public class SECFormExtractor10Q {



	
	private static final Logger LOGGER = LoggerFactory.getLogger(SECFormExtractor_11.class);
	
	/**
	 * This method extract the form 10K items.
	 * @param xhtml content as a string
	 * @return returns list of extracted items
	 */
	public static List<SECItemSection> extractForm10Q(String xhtml) {
		List<SECItemSection> listOfItemSections = new ArrayList<SECItemSection>();
		// 1. add a check to verify xhtml content is not null and empty
		if (null == xhtml || xhtml.isEmpty()) {
			return listOfItemSections;
		}
		try {
			// 2. create the document using xhtml.
			Document doc = Jsoup.parse(xhtml);

			if (null != doc) {
				Element body = doc.getElementsByTag(SECConstants.STRING_BODY).get(0);
				Elements bodyElements = body.children();

				int size = bodyElements.size();

				if (size == 1) {
					Element hasDivDivElement = doc.select(SECConstants.STRING_DIV).first();
					bodyElements = hasDivDivElement.children();
				}

				int itemPosition = 0;
				int docElementIndex = 0;
				SECItemSection itemSection = new SECItemSection();
				StringBuilder sectionContent = new StringBuilder();
				List<String> titles = extractTitles(bodyElements, SECConstants.FORM_10Q_FILE_NAME);
				// 3. iterate each title
				for (int titleIndex = 0; titleIndex < titles.size(); titleIndex++) {

					String searchTitle = titles.get(titleIndex);
					itemSection = new SECItemSection();
					String nextTitle = null;
					itemPosition = titleIndex;

					if (++itemPosition != titles.size()) {
						nextTitle = titles.get(itemPosition);
					}

					// 4.iterate each body element
					for (int elementIndex = docElementIndex; elementIndex < bodyElements.size(); elementIndex++) {
						Element currentElement = bodyElements.get(elementIndex);
						String currentNodeText = currentElement.text();
						boolean canBreak = false;
						boolean isTitleNode = false;
						if (null != currentNodeText && !currentNodeText.equals(SECConstants.EMPTY_STRING) 
								&& !currentNodeText.equals(SECConstants.STRING_WITH_SINGLE_SPACE)) {
							String nodeText = prepareTitle(currentNodeText);
							String searchTitleFound = findMatchString(nodeText, searchTitle);
							if (!searchTitleFound.isEmpty()) {
								sectionContent = new StringBuilder();
								//System.out.println(searchTitle);
								itemSection.setSectionTitle(searchTitle);
								String theDigits = searchTitle.replaceAll("[^0-9]", "");
								//System.out.println(theDigits);
								itemSection.setItemNumber(theDigits);
								isTitleNode = true;
								docElementIndex = elementIndex;
								LOGGER.info(" ***** SEARCH TITLE FOUND ***** " + currentNodeText);
							} else {
								String nextTitleFound = findMatchString(nodeText, nextTitle);
								if (!nextTitleFound.isEmpty()) {
									String content = sectionContent.toString().replaceAll("Table of Contents", "");
									itemSection.setSectionContent(content);
									if (null != itemSection.getSectionTitle()) {
										listOfItemSections.add(itemSection);
									}
									LOGGER.info(" ***** NEXT TITLE FOUND ***** " + currentNodeText);
									LOGGER.info(" ************************************************************************************* ");
									canBreak = true;
									isTitleNode = true;
									docElementIndex = elementIndex;
									break;
								}
							}
							if (currentNodeText.contains(SECConstants.STRING_END_THE_ITEM_XHTML) 
									&& nextTitle == null && canBreak) {
								break;
							}
						}
						if(!isTitleNode) {
							sectionContent.append(currentElement);
						}
					}
				}
				if(itemSection.getSectionTitle() !=null && !itemSection.getSectionTitle().isEmpty()) {
					itemSection.setSectionContent(sectionContent.toString());
					listOfItemSections.add(itemSection);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		if(listOfItemSections.size() == 0) {
			listOfItemSections = extractForm10QUsingDiv(xhtml);
		}
		return listOfItemSections;
	}
	
	public static List<SECItemSection> extractForm10QUsingDiv(String xhtml) {
		List<SECItemSection> listOfItemSections = new ArrayList<SECItemSection>();
		// 1. add a check to verify xhtml content is not null and empty
		if (null == xhtml || xhtml.isEmpty()) {
			return listOfItemSections;
		}
		try {
			// 2. create the document using xhtml.
			Document doc = Jsoup.parse(xhtml);
			Elements mainBodyElements = doc.select("div");
			int itemPosition = 0;
			int docElementIndex = 0;
			SECItemSection itemSection = new SECItemSection();
			StringBuilder sectionContent = new StringBuilder();
			List<String> titles = new ArrayList<>();
			Elements bodyElements = new Elements();
			for(Element elem : mainBodyElements){
				titles.addAll(extractTitles(elem.children(), SECConstants.FORM_10Q_FILE_NAME));
				bodyElements.addAll(elem.children());
			}
			// 3. iterate each title
			for (int titleIndex = 0; titleIndex < titles.size(); titleIndex++) {

				String searchTitle = titles.get(titleIndex);
				itemSection = new SECItemSection();
				String nextTitle = null;
				itemPosition = titleIndex;

				if (++itemPosition != titles.size()) {
					nextTitle = titles.get(itemPosition);
				}

				// 4.iterate each body element
				for (int elementIndex = docElementIndex; elementIndex < bodyElements.size(); elementIndex++) {
					Element currentElement = bodyElements.get(elementIndex);
					String currentNodeText = currentElement.text();
					boolean canBreak = false;
					boolean isTitleNode = false;
					if (null != currentNodeText && !currentNodeText.equals(SECConstants.EMPTY_STRING) 
							&& !currentNodeText.equals(SECConstants.STRING_WITH_SINGLE_SPACE)) {
						String nodeText = prepareTitle(currentNodeText);
						String searchTitleFound = findMatchString(nodeText, searchTitle);
						if (!searchTitleFound.isEmpty()) {
							sectionContent = new StringBuilder();
							itemSection.setSectionTitle(searchTitle);
							String theDigits = searchTitle.replaceAll("[^0-9]", "");
							itemSection.setItemNumber(theDigits);
							isTitleNode = true;
							docElementIndex = elementIndex;
							LOGGER.info(" ***** SEARCH TITLE FOUND ***** " + currentNodeText);
						} else {
							String nextTitleFound = findMatchString(nodeText, nextTitle);
							if (!nextTitleFound.isEmpty()) {
								String content = sectionContent.toString().replaceAll("Table of Contents", "");
								itemSection.setSectionContent(content);
								if (null != itemSection.getSectionTitle()) {
									listOfItemSections.add(itemSection);
								}
								LOGGER.info(" ***** NEXT TITLE FOUND ***** " + currentNodeText);
								LOGGER.info(" ************************************************************************************* ");
								canBreak = true;
								isTitleNode = true;
								docElementIndex = elementIndex;
								break;
							}
						}
						if (currentNodeText.contains(SECConstants.STRING_END_THE_ITEM_XHTML) 
								&& nextTitle == null && canBreak) {
							break;
						}
					}
					if(!isTitleNode) {
						sectionContent.append(currentElement);
					}
				}
			}
			if(itemSection.getSectionTitle() !=null && !itemSection.getSectionTitle().isEmpty()) {
				itemSection.setSectionContent(sectionContent.toString());
				listOfItemSections.add(itemSection);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return listOfItemSections;
	}
	
	/**
	 * This method extracts the titles from the xhtml.
	 * @param bodyElements body elements
	 * @return list of titles found in the xhtml
	 */
	private static List<String> extractTitles(Elements bodyElements, String propertyFileName) {
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
	
	/**
	 * This method does
	 * 1. convert the string in lower case.
	 * 2. If string contains dot(.) at the end of the string then it removes that dot(.).
	 * 3. replace all the spaces including html(&nbsp;) spaces.
	 * @param input both node text as well as titles from property file
	 * @return lower case without space and dot at the end of the string 
	 */
	private static String prepareTitle(String input) {
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
	private static String findMatchString(String nodeText, String searchTitle) {
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
	private static String findMatchedTitleFromXhtml(String currentNodeText, List<String> searchTitles) {
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
	

}
