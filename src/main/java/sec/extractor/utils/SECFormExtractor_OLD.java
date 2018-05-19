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
public class SECFormExtractor_OLD {

	private static final Logger LOGGER = LoggerFactory.getLogger(SECFormExtractor_OLD.class);
	
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
				SECItemSection itemSection = new SECItemSection();
				StringBuilder sectionContent = new StringBuilder();

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
					for (int elementIndex = 0; elementIndex < bodyElements.size(); elementIndex++) {
						Element currentElement = bodyElements.get(elementIndex);
						String currentNodeText = currentElement.text();
						boolean canBreak = false;
						boolean isTitleNode = false;
						if (null != currentNodeText && !currentNodeText.equals(SECConstants.EMPTY_STRING) 
								&& !currentNodeText.equals(SECConstants.STRING_WITH_SINGLE_SPACE)) {
							String nodeText = prepareTitle(currentNodeText);
							boolean searchTitleFound = isSearchTitleMatched(nodeText, searchTitles);
							if (searchTitleFound) {
								sectionContent = new StringBuilder();
								itemSection.setSectionTitle(currentNodeText);
								isTitleNode = true;
								LOGGER.info(" ***** SEARCH TITLE FOUND ***** " + currentNodeText);
							} else {
								boolean nextTitleFound = isSearchTitleMatched(nodeText, nextTitles);
								if (nextTitleFound) {
									itemSection.setSectionContent(sectionContent.toString());
									if (null != itemSection.getSectionTitle()) {
										listOfItemSections.add(itemSection);
									}
									LOGGER.info(" ***** NEXT TITLE FOUND ***** " + currentNodeText);
									LOGGER.info(" ************************************************************************************* ");
									canBreak = true;
									isTitleNode = true;
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
		if(listOfItemSections.size() == 0) {
			listOfItemSections = SECFromExtractorHelper.extractForm10KUsingContent(xhtml);
		}
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
		return withoutSpace;
	}
	
	/**
	 * This method compares the node text with the each search/next title.
	 * @param nodeText data from node
	 * @param searchTitles titles from property file
	 * @return if node text matches any of the title then tru else false.
	 */
	private static boolean isSearchTitleMatched(String nodeText, String[] searchTitles) {
		boolean isFound = false;
		if (null != searchTitles && searchTitles.length > 0) {
			for (int i = 0; i < searchTitles.length; i++) {
				String title = prepareTitle(searchTitles[i]);
				if(nodeText.equalsIgnoreCase(title)) {
					isFound = true;
					break;
				}
			}
		}
		return isFound;
	}
	
	
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
		List<String> titles = ReadTitles.readTitlesFromPropertiy(SECConstants.FORM_10Q_FILE_NAME);

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
				SECItemSection itemSection = new SECItemSection();
				StringBuilder sectionContent = new StringBuilder();

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
					for (int elementIndex = 0; elementIndex < bodyElements.size(); elementIndex++) {
						Element currentElement = bodyElements.get(elementIndex);
						String currentNodeText = currentElement.text();
						boolean canBreak = false;
						boolean isTitleNode = false;
						if (null != currentNodeText && !currentNodeText.equals(SECConstants.EMPTY_STRING) 
								&& !currentNodeText.equals(SECConstants.STRING_WITH_SINGLE_SPACE)) {
							String nodeText = prepareTitle(currentNodeText);
							boolean searchTitleFound = isSearchTitleMatched(nodeText, searchTitles);
							if (searchTitleFound) {
								sectionContent = new StringBuilder();
								itemSection.setSectionTitle(currentNodeText);
								isTitleNode = true;
								LOGGER.info(" ***** SEARCH TITLE FOUND ***** " + currentNodeText);
							} else {
								boolean nextTitleFound = isSearchTitleMatched(nodeText, nextTitles);
								if (nextTitleFound) {
									itemSection.setSectionContent(sectionContent.toString());
									if (null != itemSection.getSectionTitle()) {
										listOfItemSections.add(itemSection);
									}
									LOGGER.info(" ***** NEXT TITLE FOUND ***** " + currentNodeText);
									LOGGER.info(" ************************************************************************************* ");
									canBreak = true;
									isTitleNode = true;
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
		if(listOfItemSections.size() == 0) {
			listOfItemSections = SECFromExtractorHelper.extractForm10KUsingContent(xhtml);
		}
		return listOfItemSections;
	}
}
