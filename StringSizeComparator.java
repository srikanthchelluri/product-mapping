public class StringSizeComparator implements java.util.Comparator<String> {

	public StringSizeComparator() {
		super();
	}

	public int compare(String s1, String s2) {
		return s2.length() - s1.length();
	}

}
