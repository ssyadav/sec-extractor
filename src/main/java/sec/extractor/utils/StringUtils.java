/**
 * 
 */
package sec.extractor.utils;

import sec.extractor.constant.SECConstants;

/**
 * @author satyaveer.yadav
 *
 */
public class StringUtils {

	/**
     * replace all space and then remove html spaces(&nbsp;)
     * @param strToReplace : String from which we want to remove all spaces.
     * @return
     *  {@link String} without whitespaces
     */
    public static String removeAllWhitespaces(String strToReplace){
        String withoutSpace = strToReplace.replaceAll(SECConstants.SPACE_REGX, SECConstants.EMPTY_STRING).replaceAll(
            SECConstants.SPACE_HTML_NBSP, SECConstants.EMPTY_STRING);
        return withoutSpace;
    }
}
