package dm341.score;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import dm341.util.DistanceMeasure;
import dm341.util.Pair;

public class Classify {
	private HashMap<String, Pair<Integer, HashSet<String>>> aliases;
	private HashMap<String, Pair<Integer, HashSet<String>>> ads;  
	public static double THRESHOLD = 0.6;
	public static int KGram = 3;	
	
	public Classify(ArrayList<FCCRecord> records) {
		aliases = new HashMap<String, Pair<Integer, HashSet<String>>>();
		ads = new HashMap<String, Pair<Integer, HashSet<String>>> ();
		// merge same 
		for (FCCRecord record: records) {
			String organization = record.getOrgName();
			String stationID = record.getStationID();
			if (!ads.containsKey(organization)) {
				HashSet<String> stations = new HashSet<String>();
				stations.add(stationID);
				ads.put(organization, new Pair<Integer, HashSet<String>> (1, stations));
			} else {
				Pair<Integer, HashSet<String>> myPair = ads.get(organization);
				myPair.setFirst(myPair.getFirst() + 1);
				myPair.getSecond().add(stationID);
			}
		}
	}
	
	public HashSet<String> getAliases(String organization) {
		if (!aliases.containsKey(organization)) {
			System.err.println("Bad Call!");
		}
		return aliases.get(organization).getSecond();
	}
	
	public HashMap<String, HashSet<String>> mergeOrganizationsLSH(ArrayList<FCCRecord> records) {
		
	}
	
	
	public HashMap<String, HashSet<String>> mergeOrganizations() {
		// merge similar
		HashMap<String, HashSet<String>> uniqueOrgAds = new HashMap<String, HashSet<String>>();
		HashSet<String> mergedOrgs = new HashSet<String> ();
		
		for (String currOrgName : ads.keySet()) {
			if (mergedOrgs.contains(currOrgName)) continue;
			
			HashSet<String> stationIds = new HashSet<String> ();
			stationIds.addAll(ads.get(currOrgName).getSecond());
			String currKey = currOrgName;
			
			for (String otherOrgName : ads.keySet()) {
				if (currOrgName.equals(otherOrgName) || currKey.equals(otherOrgName)) continue;
				if (DistanceMeasure.JaccardSimilarity(KGram, currKey, otherOrgName) >= THRESHOLD) {
					int otherCount = ads.get(otherOrgName).getFirst();
					if (ads.get(currKey).getFirst() >= otherCount) {
						aliases.get(currKey).getSecond().add(otherOrgName);
					} else {
						aliases.put(otherOrgName, aliases.get(currKey));
						aliases.get(otherOrgName).setFirst(otherCount);
						aliases.get(otherOrgName).getSecond().add(currKey);
						aliases.remove(currKey);
					}
					mergedOrgs.add(otherOrgName);
					stationIds.addAll(ads.get(otherOrgName).getSecond());
				} 
			}
			
			uniqueOrgAds.put(currKey, stationIds);
		}
		return uniqueOrgAds;
	}	
}
