/**
 * 
 */
package sec.extractor.utils;

import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author satyaveer.yadav
 *
 */
public class ReadFileTextAsString {

	public static String readFileAsString(String fileName) throws Exception {
		String data = "";
		data = new String(Files.readAllBytes(Paths.get(fileName)));
		return data;
	}
}
