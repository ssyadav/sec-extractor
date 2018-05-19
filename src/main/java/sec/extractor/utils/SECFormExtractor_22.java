/**
 * 
 */
package sec.extractor.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

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
public class SECFormExtractor_22 {

//	public static void main(String[] args) {
//		System.out.println("ITEM 9. CHANGES IN AND DISAGREEMENTS WITH ACCOUNTANTS ON FINANCIAL DISCLOSURE".toLowerCase());
//	}
	private static final Logger LOGGER = LoggerFactory.getLogger(SECFormExtractor_11.class);
	
	/**
	 * This method extract the form 10K items.
	 * @param xhtml content as a string
	 * @return returns list of extracted items
	 */
	public static List<SECItemSection> extractForm10K(String xhtml) {
		List<SECItemSection> listOfItemSections = new ArrayList<SECItemSection>();
		// 1. add a check to verify xhtml content is not null and empty
		if (null == xhtml || xhtml.isEmpty()) {
			return listOfItemSections;
		}
		List<String> titles = ReadTitles.readTitlesFromPropertiy(SECConstants.FORM_10K_FILE_NAME);

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
				Properties property = ReadTitles.fetchTitlesFromPropertyFile(SECConstants.FORM_10K_LINK_FILE_NAME);
				// 3. iterate each title
				for (int titleIndex = 0; titleIndex < titles.size(); titleIndex++) {

					String searchTitles[] = titles.get(titleIndex).toLowerCase().split(SECConstants.STRING_COLON_HYFHAN);
					itemSection = new SECItemSection();
					String nextTitles[] = {};
					itemPosition = titleIndex;

					if (++itemPosition != titles.size()) {
						nextTitles = titles.get(itemPosition).toLowerCase().split(SECConstants.STRING_COLON_HYFHAN);
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
							String searchTitleFound = findMatchString(nodeText, searchTitles);
							if (!searchTitleFound.isEmpty()) {
								sectionContent = new StringBuilder();
								System.out.println(searchTitleFound);
								String titlePlusItemNumber[] = property.getProperty(searchTitleFound).split(SECConstants.STRING_COLON_HYFHAN);
								itemSection.setSectionTitle(titlePlusItemNumber[0]);
								itemSection.setItemNumber(titlePlusItemNumber[1]);
								isTitleNode = true;
								docElementIndex = elementIndex;
								LOGGER.info(" ***** SEARCH TITLE FOUND ***** " + currentNodeText);
							} else {
								String nextTitleFound = findMatchString(nodeText, nextTitles);
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
									&& nextTitles.length == 0 && canBreak) {
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
//		if(listOfItemSections.size() == 0) {
//			listOfItemSections = SECFromExtractorHelper.extractForm10KUsingContent(xhtml);
//		}
		return listOfItemSections;
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
/*	private static boolean isSearchTitleMatched(String nodeText, String[] searchTitles) {
		boolean isFound = false;
		if (null != searchTitles && searchTitles.length > 0) {
			for (int i = 0; i < searchTitles.length; i++) {
				String title = prepareTitle(searchTitles[i]);
				if (nodeText.length() < 150 && (nodeText.equalsIgnoreCase(title) || nodeText.contains(title))) {
					isFound = true;
					break;
				}
			}
		}
		return isFound;
	}
*/	
	/**
	 * This method compares the node text with the each search/next title.
	 * @param nodeText data from node
	 * @param searchTitles titles from property file
	 * @return if node text matches any of the title then tru else false.
	 */
	private static String findMatchString(String nodeText, String[] searchTitles) {
		String matchedString = SECConstants.EMPTY_STRING;
		if (null != searchTitles && searchTitles.length > 0) {
			for (int i = 0; i < searchTitles.length; i++) {
				String title = prepareTitle(searchTitles[i]);
				if (nodeText.length() < 150 && (nodeText.equalsIgnoreCase(title) || nodeText.contains(title))) {
					matchedString = title;
					break;
				}
			}
		}
		return matchedString;
	}
	
	/**
	 * This method compares the node text with the each search/next title.
	 * @param nodeText data from node
	 * @param searchTitles titles from property file
	 * @return if node text matches any of the title then tru else false.
	 */
	private static String findMatchStringFromTitleArray(String nodeText, List<String> searchTitles) {
		String matchedString = SECConstants.EMPTY_STRING;
		boolean isFound = false;
		if (null != searchTitles && searchTitles.size() > 0) {
			for (int i = 0; i < searchTitles.size(); i++) {
				String subTitles[] = searchTitles.get(i).toLowerCase().split(SECConstants.STRING_COLON_HYFHAN);
				for (int j = 0; j < subTitles.length; j++) {
					String title = prepareTitle(subTitles[j]);
					if (nodeText.length() < 150 && (nodeText.equalsIgnoreCase(title) || nodeText.contains(title))) {
						matchedString = title;
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
