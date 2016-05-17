package dm341.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class StateCounter {
	public static Map<String, String> stateDict;
	public static Set<String> stateNames;
	public static Set<String> states;
	private static Map<String, String> getStateDict() {
		if (stateDict == null) {
			stateDict = new HashMap<String, String>();
			stateDict.put("Alabama", "AL");
			stateDict.put("Alaska", "AK");
			stateDict.put("American Samoa", "AS");
			stateDict.put("Arizona", "AZ");
			stateDict.put("Arkansas",  "AR");
			stateDict.put("California", "CA");
			stateDict.put("Colorado", "CO");
			stateDict.put("Connecticut",  "CT");
			stateDict.put("Delaware",  "DE");
			stateDict.put("Dist. of Columbia", "DC");
			stateDict.put("Florida", "FL");
			stateDict.put("Georgia", "GA");
			stateDict.put("Guam", "GU");
			stateDict.put("Hawaii", "HI");
			stateDict.put("Idaho", "ID");
			stateDict.put("Illinois", "IL");
			stateDict.put("Indiana", "IN");
			stateDict.put("Iowa", "IA");
			stateDict.put("Kansas", "KS");
			stateDict.put("Kentucky", "KY");
			stateDict.put("Louisiana", "LA");
			stateDict.put("Maine", "ME");
			stateDict.put("Maryland", "MD");
			stateDict.put("Marshall Islands", "MH");
			stateDict.put("Massachusetts", "MA");
			stateDict.put("Michigan", "MI");
			stateDict.put("Micronesia", "FM");
			stateDict.put("Minnesota", "MN");
			stateDict.put("Mississippi", "MS");
			stateDict.put("Missouri", "MO");
			stateDict.put("Montana", "MT");
			stateDict.put("Nebraska", "NE");
			stateDict.put("Nevada", "NV");
			stateDict.put("New Hampshire", "NH");
			stateDict.put("New Jersey", "NJ");
			stateDict.put("New Mexico", "NM");
			stateDict.put("New York", "NY");
			stateDict.put("North Carolina", "NC");
			stateDict.put("North Dakota", "ND");
			stateDict.put("Northern Marianas", "MP");
			stateDict.put("Ohio", "OH");
			stateDict.put("Oklahoma", "OK");
			stateDict.put("Oregon", "OR");
			stateDict.put("Palau",  "PW");
			stateDict.put("Pennsylvania", "PA");
			stateDict.put("Puerto Rico", "PR");
			stateDict.put("Rhode Island", "RI");
			stateDict.put("South Carolina", "SC");
			stateDict.put("South Dakota", "SD");
			stateDict.put("Tennessee", "TN");
			stateDict.put("Texas", "TX");
			stateDict.put("Utah", "UT");
			stateDict.put("Vermont",  "VT");
			stateDict.put("Virginia", "VA");
			stateDict.put("Virgin Islands", "VI");
			stateDict.put("Washington",  "WA");
			stateDict.put("West Virginia", "WV");
			stateDict.put("Wisconsin",  "WI");
			stateDict.put("Wyoming", "WY");	
		}
		stateNames = stateDict.keySet();
		states = new HashSet<String>(stateDict.values());
		return stateDict;
	}	
	
	public static String getState(String s) {
		if (stateNames == null) {
			getStateDict();
		}
		String ss = s.toUpperCase();
		if (states.contains(ss)) return ss;
		else {
			StringBuilder sb = new StringBuilder(s.toLowerCase());
			sb.replace(0, 1, s.substring(0, 1).toUpperCase());
			String sss = sb.toString();
			if (stateNames.contains(sss)) return stateDict.get(sss);
		}
		return null;
	}
}
