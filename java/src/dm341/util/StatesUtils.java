package dm341.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class StatesUtils {
	public static Set<String> hubCities;
	public static Map<String, String> stateDict;
	public static Set<String> stateNames;
	public static Set<String> states;
	public static Map<String, Set<String>> adjacentStatesDict;
	
	private static Set<String> getHubCities() {
		if (hubCities == null) {
			hubCities.add("New York");
			hubCities.add("NY");
			hubCities.add("Washington DC");
			hubCities.add("Washington");
			hubCities.add("DC");
			hubCities.add("Chicago");
			hubCities.add("Boston");
			hubCities.add("Philadelphia");
		}
		return hubCities;
	}
	
	public static boolean isHubCity(String cityName) {
		return false;
	}
	
	public static boolean isContiguous(String state1, String state2) throws IOException {
		if (adjacentStatesDict == null) {
			adjacentStatesDict = IO.readAjacentStatesDict();
		}
		return adjacentStatesDict.get(state1).contains(state2);
	}
	
	public static boolean isContiguous(String state, List<String> states) throws IOException {
		for (String s : states) {
			if (isContiguous(state, s)) return true;
		}
		return false;
	}
	public static boolean isContiguous(List<String> states2) throws IOException {
		if (states2.size() <= 1) return true;
		List<String> currentGroup = new ArrayList<String>();
		currentGroup.add(states2.get(0));
		Set<Integer> unGroupedStates = new HashSet<Integer>();
		for (int i = 1; i < states2.size(); i++) {
			unGroupedStates.add(i);
		}
		while (!unGroupedStates.isEmpty()) {
			boolean found = false;
			for (Integer index : unGroupedStates) {
				if (isContiguous(states2.get(index), currentGroup)) {
					found = true;
					currentGroup.add(states2.get(index));
				}
			}
			if (!found) return false;
		}
		return true;
	}
	
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
	
	public static void main(String[] args) throws IOException {
		System.out.println(isContiguous("TX", "WI"));
		System.out.println(isContiguous("WA", "OR"));
		System.out.println(isContiguous("VA", "KY"));
		System.out.println(isContiguous("KS", "OK"));
		System.out.println(isContiguous("AZ", "LA"));
	}
}
