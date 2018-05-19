/**
 * 
 */
package sec.extractor.utils;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sec.extractor.constant.SECConstants;
import sec.extractor.pojo.SECItemSection;

/**
 * @author satyaveer.yadav
 *
 */
public class SECFormExtractor {

	
	private static final Logger LOGGER = LoggerFactory.getLogger(SECFormExtractor.class);
	
	/**
	 * This method extract the form 10K items.
	 * @param xhtml content as a string
	 * @return returns list of extracted items
	 */
	public static List<SECItemSection> extractForm10K(String xhtml) {
		LOGGER.info("SECFormExtractor extractForm10K : STARTED");
		String sectionTitlesFileName = SECConstants.FORM_10K_FILE_NAME;
		List<SECItemSection> listOfItemSections = SECForm10KExtractor.extractFormContent(xhtml, sectionTitlesFileName);
		
		if(listOfItemSections.size() == 0) {
			LOGGER.info("SECFormExtractor extractForm10K : LIST SIZE found ZERO : trying extracting content using DIV");
			listOfItemSections = SECForm10KExtractor.extractForm10UsingDiv(xhtml);
		}
		return listOfItemSections;
	}
	
	/**
	 * This method extract the form 10Q items.
	 * @param xhtml content as a string
	 * @return returns list of extracted items
	 */
	public static List<SECItemSection> extractForm10Q(String xhtml) {
		LOGGER.info("SECFormExtractor extractForm10Q : STARTED");
		String sectionTitlesFileName = SECConstants.FORM_10Q_FILE_NAME;
		List<SECItemSection> listOfItemSections = SECForm10QExtractor.extractFormContent(xhtml, sectionTitlesFileName);
		
		if(listOfItemSections.size() == 0) {
			LOGGER.info("SECFormExtractor extractForm10Q : LIST SIZE found ZERO : trying extracting content using DIV");
			listOfItemSections = SECForm10QExtractor.extractForm10UsingDiv(xhtml);
		}
		return listOfItemSections;
	}

	
	
	
	
	
	
}
