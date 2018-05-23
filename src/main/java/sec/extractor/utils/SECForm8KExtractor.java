/**
 * 
 */
package sec.extractor.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sec.extractor.constant.SECConstants;
import sec.extractor.pojo.SECItemSection;
import sec.extractor.pojo.TitleDetails;

/**
 * @author satyaveer.yadav
 *
 */
public class SECForm8KExtractor extends BaseSECFormExtractor {

	private static final Logger LOGGER = LoggerFactory.getLogger(SECForm8KExtractor.class);
	
	/**
	 * This method prepares the jsoup object from the xhtml and call the main extraction method by passing the 
	 * title property file name and body content.
	 * @param xhtml xhtml
	 * @param sectionTitlesFileName name of the title property file
	 * @return list of section object
	 */
	public static List<SECItemSection> extractFormContent(String xhtml, String sectionTitlesFileName, List<String> expectedItems) {
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
				List<TitleDetails> titles = new ArrayList<>();
				titles.add(extractTitlesWithStdTitle(bodyElements, sectionTitlesFileName));
				listOfItemSections = extractFromContent(bodyElements, titles, expectedItems);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return listOfItemSections;
	}
	
	/**
	 * This method prepares the jsoup object from the xhtml and call the main extraction method by passing the 
	 * title property file name and body content.
	 * @param xhtml xhtml
	 * @param sectionTitlesFileName name of the title property file
	 * @return list of section object
	 */
	public static List<SECItemSection> extractForm8KUsingDiv(String xhtml, String sectionTitlesFileName, List<String> expectedItems) {
		List<SECItemSection> listOfItemSections = new ArrayList<SECItemSection>();
		// 1. add a check to verify xhtml content is not null and empty
		if (null == xhtml || xhtml.isEmpty()) {
			return listOfItemSections;
		}
		try {
			// 2. create the document using xhtml.
			Document doc = Jsoup.parse(xhtml);
			Elements mainBodyElements = doc.select("div");
			List<TitleDetails> titles = new ArrayList<>();
			Elements bodyElements = new Elements();
			
			for(Element elem : mainBodyElements){
				titles.add(extractTitlesWithStdTitle(elem.children(), sectionTitlesFileName));
				bodyElements.addAll(elem.children());
			}
			// 3. iterate each title
			listOfItemSections = extractFromContent(bodyElements, titles, expectedItems);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return listOfItemSections;
	}

	/**
	 * This method prepares the jsoup object from the xhtml and call the main extraction method by passing the 
	 * title property file name and body content.
	 * @param xhtml xhtml
	 * @param sectionTitlesFileName name of the title property file
	 * @return list of section object
	 */
	private static List<SECItemSection> extractFromContent(Elements bodyElements,
			List<TitleDetails> titlesObj, List<String> expectedItemList) {
		List<SECItemSection> listOfItemSections = new ArrayList<SECItemSection>();
		int itemPosition = 0;
		int docElementIndex = 0;
		SECItemSection itemSection = new SECItemSection();
		StringBuilder sectionContent = new StringBuilder();
		List<String> titles = new ArrayList<>();
		Map<String, String> standardTitles = new HashMap<>();
		
/*		String expectedItem[] = expectedItemList.get(0).split("\\|");
		
		for (TitleDetails titleDetail : titlesObj) {
			List<String> titleList = titleDetail.getTitlesFoundInFile();
			for (int i = 1; i < expectedItem.length; i++) {
				for (String title : titleList) {
					if(title.contains(expectedItem[i])) {
						titles.add(title);
					}
				}
			}
			standardTitles.putAll(titleDetail.getStandardTitle());
		}
*/
		//2. prepare titles
		for (TitleDetails titleDetail : titlesObj) {
			titles.addAll(titleDetail.getTitlesFoundInFile());
			standardTitles.putAll(titleDetail.getStandardTitle());
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
						//System.out.println(searchTitle);
						itemSection.setSectionTitle(standardTitles.get(searchTitle));
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
					if ((currentNodeText.contains(SECConstants.STRING_END_THE_ITEM_XHTML) || currentNodeText.contains(SECConstants.STRING_END_THE_ITEM_XHTML_1))
							&& nextTitle == null) {
						canBreak = true;
						isTitleNode = true;
						break;
					}
				}
				if(!isTitleNode) {
					sectionContent.append(currentElement);
				}
				if ((currentNodeText.contains(SECConstants.STRING_END_THE_ITEM_XHTML) || currentNodeText.contains(SECConstants.STRING_END_THE_ITEM_XHTML_1))
						&& nextTitle == null && canBreak) {
					break;
				}
			}
		}
		if(itemSection.getSectionTitle() !=null && !itemSection.getSectionTitle().isEmpty()) {
			itemSection.setSectionContent(sectionContent.toString());
			listOfItemSections.add(itemSection);
		}
		return listOfItemSections;
	}

}
