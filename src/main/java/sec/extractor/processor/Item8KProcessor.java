/**
 * 
 */
package sec.extractor.processor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sec.extractor.pojo.SECItemSection;
import sec.extractor.utils.ReadFileTextAsString;
import sec.extractor.utils.ReadTitles;
import sec.extractor.utils.SECFormExtractor;

/**
 * @author satyaveer.yadav
 *
 */
public class Item8KProcessor {

	private static final Logger LOGGER = LoggerFactory.getLogger(Item8KProcessor.class);
	private static String EMPTY_STRING = "";
	private static String EXPECTED_ITEMS_FILE_NAME = "8-k-expected-items.properties";
	
	static String localInput = "test/input/8k/";
	static String localOutput = "test/output/8k/";
	static String localTemplate = "test/template/";
	
	
	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();
		LOGGER.info(" ***** FORM 8K EXTRATION STARTED ***** ");
		int itemnumber = 28;
		
		//1. Read the xhtml from the file by passing the file path
		String inputFile = localInput + "form8k_xtxt_"+ itemnumber +".txt";
		List<String> expectedItems = new ArrayList<String>();
		String xhtml = readInputFile(inputFile);
		LOGGER.info(" ***** EXTRATING ***** " + inputFile);
		
		//2. read the expectedItems list from the property file.
		List<String> allAxpectedItems = ReadTitles.readExpectedItemsFile(EXPECTED_ITEMS_FILE_NAME);
		expectedItems.add(allAxpectedItems.get(itemnumber - 1));
		
		
		//3. get the list of sec items
		List<SECItemSection> listOfItemSections = fetchSecItemSection(xhtml, expectedItems);
		LOGGER.info("***** Total items extrated ***** " + listOfItemSections.size());
		
		long endTime = System.currentTimeMillis();
		LOGGER.info("***** Total time taken : " + (endTime - startTime) + " milli sec ***** ");
		LOGGER.info(" ***** FORM 8K EXTRATION ENDED ***** ");
	}
	
	/**
	 * 
	 * @param inputFile
	 * @return
	 */
	private static String readInputFile(String inputFile) {
		String xhtml = EMPTY_STRING;
		try {
			xhtml = ReadFileTextAsString.readFileAsString(inputFile);
		} catch (Exception e) {
			LOGGER.error("Error occurred when reading file", e);
		}
		return xhtml;
	}
	
	
	/**
	 * 
	 * @param xhtml
	 */
	private static List<SECItemSection> fetchSecItemSection(String xhtml, List<String> expectedItems) {
		//1. get the list of section items
		List<SECItemSection> listOfItemSections = SECFormExtractor.extractForm8K(xhtml, expectedItems);
		
		if(!listOfItemSections.isEmpty()) {
			//2. delete all the previous files from output folder.
			File parentDir = new File(localOutput);
			if (parentDir.exists()) {
				File[] listOfReadsetFiles = parentDir.listFiles();
				for (File file : listOfReadsetFiles) {
					try {
						file.delete();
					} catch (Exception ex) {
						LOGGER.error(" ***** ERROR while deleting file ***** " + file.getAbsolutePath() 
							+ ex.getMessage());
					}
				}
			}
			
			//3. read the default template
			File htmlTemplateFile = new File(localTemplate + "template.html");
			
			//4 Iterate the list of item and create the item specific html
			for (SECItemSection item : listOfItemSections) {
				File newHtmlFile = new File(localOutput + item.getSectionTitle().replaceAll(";", " ") + ".html");
				String htmlString;
				try {
					if(!newHtmlFile.exists()) {
						newHtmlFile.createNewFile();
					}
					htmlString = FileUtils.readFileToString(htmlTemplateFile);
					String title = "" + item.getSectionTitle();
					String body = title + "<br> " + item.getSectionContent().toString();
					htmlString = htmlString.replace("$title", title);
					htmlString = htmlString.replace("$body", body);
					FileUtils.writeStringToFile(newHtmlFile, htmlString);
				} catch (IOException e) {
					LOGGER.error("Error occurred when creating item specific html files", e);
				}
				
			}
		}
		return listOfItemSections;
	}
}
