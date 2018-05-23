/**
 * 
 */
package sec.extractor.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import org.apache.commons.io.IOUtils;

/**
 * @author satyaveer.yadav
 *
 */
public class ReadTitles {

	/**
	 * This method is used to reads property file
	 * @param propertyFileName name of the property file
	 * @return list of titles from property file
	 */
	public static List<String> readTitlesFromPropertiy(String propertyFileName) {
		Scanner scanner = null;
		List<String> titles = new ArrayList<String>();
		try {
			scanner = new Scanner(new File(propertyFileName));
			while (scanner.hasNextLine()) {
				String title = scanner.nextLine();
				//System.out.println(title);
				titles.add(title);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			if(null != scanner) {
				scanner.close();
			}
		}
		return titles;
	}
	
	public static Properties fetchTitlesFromPropertyFile(String fineName) {
        Properties fDBSConfig = null;
        BufferedReader input = null;
        try {
            File inputFile = new File(fineName);
            if (!inputFile.exists()) {
                throw new RuntimeException("Could not find " + fineName + "!!");
            }
            input = new BufferedReader(new InputStreamReader(
                    new FileInputStream(inputFile), Charset.forName("UTF-8")));
            fDBSConfig = new Properties();
            fDBSConfig.load(input);
        } catch (IOException ex) {
            
        } finally {
            IOUtils.closeQuietly(input);
        }
        return fDBSConfig;
    }
	
	public static List<String> readExpectedItemsFile(String eXPECTED_ITEMS_FILE_NAME2) {
		return readTitlesFromPropertiy(eXPECTED_ITEMS_FILE_NAME2);
	}
}

