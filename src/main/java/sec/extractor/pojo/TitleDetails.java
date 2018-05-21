/**
 * 
 */
package sec.extractor.pojo;

import java.util.List;
import java.util.Map;

/**
 * @author satyaveer.yadav
 *
 */
public class TitleDetails {

	private List<String> titlesFoundInFile;
	private Map<String, String> standardTitle;
	
	public List<String> getTitlesFoundInFile() {
		return titlesFoundInFile;
	}
	public void setTitlesFoundInFile(List<String> titlesFoundInFile) {
		this.titlesFoundInFile = titlesFoundInFile;
	}
	public Map<String, String> getStandardTitle() {
		return standardTitle;
	}
	public void setStandardTitle(Map<String, String> standardTitle) {
		this.standardTitle = standardTitle;
	}
	
	
}
