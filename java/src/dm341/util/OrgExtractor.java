package dm341.util;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class OrgExtractor {
	static Set<String> stringSet1 = new HashSet<String>(
			Arrays.asList("president", "local", "state", "federal", "us house", "us senate", "2010", 
					"2011", "2012", "2013", "2014", "2015", "2016"));
	static Set<String> stringSet2 = new HashSet<String>(
			Arrays.asList("ads", "file", "files"));
	static Set<String> dateStrings = new HashSet<String>(
			Arrays.asList("Jan", "Feb", "March", "Apr", "May", "June", "July", "Aug", "Sep",
					"Oct", "Nov", "Dec"));
	public static String extractFromUrl(String url) {
		String[] phrases = url.split("/");
		int phrasesCount = phrases.length;
		String s = phrases[phrasesCount - 3];
	    if (isOrg(s)) return s;
	    s = phrases[phrasesCount - 2];
	    if (isOrg(s)) return s;
	    s = phrases[phrasesCount - 1];
	    String[] tokens = s.split("[\\s._-]+");
	    StringBuilder sb = new StringBuilder();
	    boolean start = true;
	    //sb.append(tokens[0]);
	    for (int i = 0; i < tokens.length; i++) {
	    	String token = tokens[i];
	    	if (isNumOrMarks(token) || isDate(token)) {
	    		if (start) continue;
	    		return sb.toString();
	    	}
	    	else {
	    		if (start) {
	    			start = false;
	    		}
	    		else {
	    			sb.append(" ");
	    		}
	    		sb.append(token);
	    	}
	    }
	    return null;
	}
	
	private static boolean isNumOrMarks(String s) {
		String regex = "\\(?[0-9]{1,}.*";
		if (Pattern.matches(regex, s)) return true;
		return false;
	}
	
	private static boolean isOrg(String s) {
		if (stringSet1.contains(s.toLowerCase())) {
			return false;
		}
		String[] ss = s.split("\\s+");
		if (stringSet2.contains(ss[ss.length - 1].toLowerCase())) {
			return false;
		};
		if (isDate(ss[ss.length - 1])) {
			return false;
		}
		String l = s.toLowerCase();
		if (l.compareTo("terms and disclosures") == 0)
			return false;
		if (l.startsWith("nab"))
			return false;
		if (l.startsWith("contract"))
			return false;
		if (l.endsWith("file"))
			return false;
		for (String token : ss) {
			if (dateStrings.contains(token)) return false;
		}
		if (Pattern.matches(".{0,5}[0-9]{5,}.*", l)) {
			return false;
		}
		return true;
	}
	
	private static boolean isDate(String string) {
		if (Pattern.matches(".*[0-9]{1,2}[\\.-][0-9]{1,2}[\\.-][0-9]{2,4}.*", string)) {
			return true;
		}
		if (Pattern.matches(".*[0-9]{1,2}[\\.-][0-9]{1,2}.*", string)) {
			return true;
		}
		return false;
	}

	public static void main(String[] args) throws IOException {
		List<String> urls = IO.readUrls();
		String saved = "";
		for (String url : urls) {
			String org = extractFromUrl(url);
			if (org != null) {
				if (org.compareTo(saved) != 0) {
					//System.out.println(org);
					saved = org;
				}
			}
			else System.out.println(url);
		}
		/***
		System.out.println(isNumOrMarks("(123"));
		System.out.println(isNumOrMarks("123"));
		System.out.println(isNumOrMarks("??"));
		System.out.println(isNumOrMarks("1273a"));
		System.out.println(isNumOrMarks("aaa"));
		***/
		
	}
}
