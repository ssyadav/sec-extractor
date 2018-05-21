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
import sec.extractor.pojo.TitleDetails;

/**
 * @author satyaveer.yadav
 *
 */
public class TitleExtractorFromXhtml {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(TitleExtractorFromXhtml.class);
	private static String EMPTY_STRING = "";
	static String localInput = "test/input/10k-25/";
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String inputFile = localInput + "form10K_xhtml_25.txt";
		String xhtml = reacdInputFile(inputFile);
		List<TitleDetails> listOfTitles = extractTitlesFromXhtml(xhtml);
		for (TitleDetails titleDetails : listOfTitles) {
			//System.out.println(titleDetails.getTitleName() + "  ::  " + titleDetails.getStartIndex());
		}

	}

	public static List<TitleDetails> extractTitlesFromXhtml(String xhtml) {
		List<TitleDetails> listOfTitles = new ArrayList<>();
		
		Document doc = Jsoup.parse(xhtml);

		if (null != doc) {
			Element body = doc.getElementsByTag(SECConstants.STRING_BODY).get(0);
			System.out.println(body.toString());
			String bodyAsString = body.toString();
			Elements bodyElements = body.children();

			int size = bodyElements.size();

			if (size == 1) {
				Element hasDivDivElement = doc.select(SECConstants.STRING_DIV).first();
				bodyElements = hasDivDivElement.children();
			}
			extractTitles(listOfTitles, bodyAsString, bodyElements);
			
		}
		return listOfTitles;
	}

	private static void extractTitles(List<TitleDetails> listOfTitles, String bodyAsString, Elements bodyElements) {
		int docElementIndex = 0;
		int titleIndex = 0;
		TitleDetails titleDetails = null;
		List<String> titles = ReadTitles.readTitlesFromPropertiy(SECConstants.FORM_10K_FILE_NAME);
		for (int elementIndex = docElementIndex; elementIndex < bodyElements.size(); elementIndex++) {
			Element currentElement = bodyElements.get(elementIndex);
			String currentNodeText = currentElement.text();
			if (null != currentNodeText && !currentNodeText.equals(SECConstants.EMPTY_STRING) 
					&& !currentNodeText.equals(SECConstants.STRING_WITH_SINGLE_SPACE)) {
				String foundTtile = findMatchedTitleFromXhtml(currentNodeText, titles);
				if(!foundTtile.isEmpty()) {
					titleDetails = new TitleDetails();
					int index = bodyAsString.indexOf(foundTtile, titleIndex);
					titleIndex = titleIndex + index;
//					titleDetails.setStartIndex(index);
//					titleDetails.setTitleName(foundTtile);;
					listOfTitles.add(titleDetails);
				}
				
			}
		}
	}
	
	
	/**
	 * 
	 * @param inputFile
	 * @return
	 */
	private static String reacdInputFile(String inputFile) {
		String xhtml = EMPTY_STRING;
		try {
			xhtml = ReadFileTextAsString.readFileAsString(inputFile);
		} catch (Exception e) {
			LOGGER.error("Error occurred when reading file", e);
		}
		return xhtml;
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
					if (nodeText.length() < 150 && (nodeText.equalsIgnoreCase(title) || nodeText.contains(title))) {
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
