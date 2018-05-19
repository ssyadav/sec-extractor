package sec.extractor.pojo;

/**
 * @author satyaveer.yadav
 *
 */
public class SECItemSection {

	/**
	 * Item # and description
	 *  ex: "Item 1. Business"
	 *  
	 *  (we should normalize these to the standard titles given in the list above)
	 */
	 private String sectionTitle; 
	 
	 /**
	  * for now, raw XHTML content as a String
	 */
	 private Object sectionContent;
	 
	 /**
	  * Holds the item number i.e. "1A", "9B", "10", etc
	  */
	 private String itemNumber;
	 

	public String getSectionTitle() {
		return sectionTitle;
	}

	public void setSectionTitle(String sectionTitle) {
		this.sectionTitle = sectionTitle;
	}

	public Object getSectionContent() {
		return sectionContent;
	}

	public void setSectionContent(Object sectionContent) {
		this.sectionContent = sectionContent;
	}

	public String getItemNumber() {
		return itemNumber;
	}

	public void setItemNumber(String itemNumber) {
		this.itemNumber = itemNumber;
	}  
	 
	 
	 
}
