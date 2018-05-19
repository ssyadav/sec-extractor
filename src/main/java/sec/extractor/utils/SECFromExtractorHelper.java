/**
 * 
 */
package sec.extractor.utils;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

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
public class SECFromExtractorHelper {

	private static final Logger LOGGER = LoggerFactory.getLogger(SECFormExtractor_OLD.class);

	public static List<SECItemSection> extractForm10KUsingContent(String xhtml) {
		List<SECItemSection> listOfItemSections = new ArrayList<>();
		// 1. add a check to verify xhtml content is not null and empty
		if (null == xhtml || xhtml.trim().isEmpty()) {
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
				List<String> links = null;
				int contentDivIndex = -1;
				for (int elementIndex = 0; elementIndex < bodyElements.size(); elementIndex++) {
					Element divElement = bodyElements.get(elementIndex);
					String divText = StringUtils.removeAllWhitespaces(divElement.text().toLowerCase());
					System.out.println(divText);
					if (divText.contains(SECConstants.STRING_TABLE_OF_CONTENT)
							&& divText.contains(SECConstants.STRING_PAGE)) {
						links = extractAnchorsFromContent(divElement);
						contentDivIndex = elementIndex;
						break;
					} else if((divText.contains("item1.business") && divText.contains("item1a.riskfactors"))
							|| (divText.contains("item10.directors,executiveofficersandcorporategovernance") && (divText.contains("item11.executivecompensation")))) {
						links = extractAnchorsFromContent(divElement);
						contentDivIndex = elementIndex;
						break;
					}
				}
				bodyElements.subList(0, contentDivIndex + 1).clear();

				String bodyText = bodyElements.toString();
				int nextTitleIdx = 0;
				boolean isLastTitle = false;
				String currntTitle = SECConstants.EMPTY_STRING;
				String itemNumber = SECConstants.EMPTY_STRING;
				Properties property = ReadTitles.fetchTitlesFromPropertyFile(SECConstants.FORM_10K_LINK_FILE_NAME);
				for (int titleIndex = 0; titleIndex < links.size(); titleIndex++) {
					String startLink = links.get(titleIndex);
					nextTitleIdx = titleIndex + 1;
					String nextLink = null;
					if (links.size() > nextTitleIdx) {
						nextLink = links.get(nextTitleIdx);
					} else {
						isLastTitle = true;
					}
					String titlePlusItemNumber[] = property.getProperty(startLink).split(SECConstants.STRING_COLON_HYFHAN);
					currntTitle = titlePlusItemNumber[0];
					itemNumber = titlePlusItemNumber[1];
					
					List<Object> extractedSectionData = extractSectionString(bodyText, startLink, nextLink, isLastTitle, currntTitle, itemNumber);
					SECItemSection item = (SECItemSection) extractedSectionData.get(0);
					if (!extractedSectionData.isEmpty() && null != item.getSectionTitle()) {
						listOfItemSections.add((SECItemSection) extractedSectionData.get(0));
						if (!isLastTitle) {
							bodyText = (String) extractedSectionData.get(1);
						}
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
		if ( listOfItemSections.size() == 0) {
			SECFormExtractorFailureHelper.extractForm10K(xhtml);
		}
		return listOfItemSections;
	}

	private static List<String> extractAnchorsFromContent(Element contentElement) {
		Elements anchors = contentElement.select(SECConstants.STRING_ANCHOR);
		Set<String> linkHref = new LinkedHashSet<>();
		for (int i = 0; i < anchors.size(); i++) {
			Element e = anchors.get(i);
			if (!StringUtils.removeAllWhitespaces(e.text().toLowerCase()).contains(SECConstants.STRING_PART)) {
				String href = e.attr(SECConstants.STRING_HREF).trim().replaceAll(SECConstants.STRING_HASH,
						SECConstants.EMPTY_STRING);
				if (!href.isEmpty() && !href.equalsIgnoreCase("SIGNATURES_926123")) {
					linkHref.add(href);
				}
			}
		}
		return new ArrayList<>(linkHref);
	}

	private static List<Object> extractSectionString(String bodyText, String firstTitle, String endTitle,
			boolean isLastTitle, String currntTitle, String itemNumber) {
		List<Object> sectionDataAndNewBody = new ArrayList<>();
		SECItemSection secItemSection = null;
		int firstTitleIdx = -1;
		int nextTitleIdx = -1;
		firstTitleIdx = bodyText.indexOf(firstTitle);
		if (firstTitleIdx != -1) {
			secItemSection = new SECItemSection();
			System.out.println(firstTitle);
			secItemSection.setItemNumber(itemNumber);
			secItemSection.setSectionTitle(currntTitle);
			bodyText = bodyText.substring(firstTitleIdx, bodyText.length());
			if(null == endTitle) {
				nextTitleIdx = bodyText.indexOf("SIGNATURES");
			} else {
				nextTitleIdx = bodyText.indexOf(endTitle);
			}
			if (nextTitleIdx != -1) {
				Document doc = Jsoup.parse(bodyText.substring(0, nextTitleIdx));
				Element body = doc.getElementsByTag(SECConstants.STRING_BODY).get(0);
				Elements bodyElements = body.children();
				String content = "";
				for (int elementIndex = 0; elementIndex < bodyElements.size()-1; elementIndex++) {
					Element currentElement = bodyElements.get(elementIndex);
					content = content + currentElement.toString();
				}
				if(!content.isEmpty()) {
					content = content.replace("Table of Contents", "");
					
				}
				secItemSection.setSectionContent(content);
				sectionDataAndNewBody.add(secItemSection);
				if (!isLastTitle) {
					sectionDataAndNewBody.add(bodyText.substring(nextTitleIdx, bodyText.length()));
				}
			}
		}

		return sectionDataAndNewBody;
	}
}
