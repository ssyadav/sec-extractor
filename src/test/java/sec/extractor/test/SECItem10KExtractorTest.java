/**
 * 
 */
package sec.extractor.test;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sec.extractor.pojo.SECItemSection;
import sec.extractor.utils.ReadFileTextAsString;
import sec.extractor.utils.SECFormExtractor_OLD;
/**
 * @author satyaveer.yadav
 *
 */
public class SECItem10KExtractorTest extends BaseLayer {

	private static final Logger LOGGER = LoggerFactory.getLogger(SECItem10KExtractorTest.class);
	private static String EMPTY_STRING = "";

	@Test
	public void testSample_1() {
		long startTime = System.currentTimeMillis();
		LOGGER.info(" ***** FORM 10K EXTRATION STARTED ***** ");
		
		//1. Read the xhtml from the file by passing the file path
		String inputFile = localInput + "form10K_xhtml_1.txt";
		String xhtml = reacdInputFile(inputFile);
		
		//2. get the list of sec items
		List<SECItemSection> listOfItemSections = fetchSecItemSection(xhtml);
		
		Assert.assertNotNull(listOfItemSections);
		Assert.assertTrue(listOfItemSections.size() > 0);
		
		long endTime = System.currentTimeMillis();
		LOGGER.info("***** Total time taken : " + (endTime - startTime) + " milli sec ***** ");
		LOGGER.info(" ***** FORM 10K EXTRATION ENDED ***** ");
	}


	/**
	 * 
	 * @param inputFile
	 * @return
	 */
	private String reacdInputFile(String inputFile) {
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
	private List<SECItemSection> fetchSecItemSection(String xhtml) {
		//1. get the list of section items
		List<SECItemSection> listOfItemSections = SECFormExtractor_OLD.extractForm10K(xhtml);
		
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
				File newHtmlFile = new File(localOutput + item.getSectionTitle() + ".html");
				String htmlString;
				try {
					if(!newHtmlFile.exists()) {
						newHtmlFile.createNewFile();
					}
					htmlString = FileUtils.readFileToString(htmlTemplateFile);
					String title = "" + item.getSectionTitle();
					String body = item.getSectionContent().toString();
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

	@Test
	public void testSample_2() {
		
	}
}
