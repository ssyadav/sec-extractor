package sec.extractor.test;

public class TestString {

	public static void main(String[] args) {
		String str = "ITEM 2: SALES OF EQUITY SECURITIES AND USE OF PROCEEDS";
		System.out.println(str.toLowerCase());
		
		String splitPipe = "1|8.01|9.01";
		String expectedItem[] = splitPipe.split("\\|");
		
		for (int i = 1; i < expectedItem.length; i++) {
			System.out.println(expectedItem[i]);
		}
	}
}
