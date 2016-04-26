package dm341.score;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Classify {
	private HashMap<String, ArrayList<String>> aliases;
	
	public Classify() {
		aliases = new HashMap<String, ArrayList<String>>();
	}
	
	public HashMap<String, ArrayList<String>> mergeOrganizations(ArrayList<FCCRecord> records) {
		HashMap<String, HashSet<String>> ads = new HashMap<String, HashSet<String>>();
		for (FCCRecord record: records) {
			String organization = record.getOrgName();
			String stationID = record.getStationID();
			if (!ads.containsKey(organization)) {
				HashSet<String> stations = new HashSet<String>();
				stations.add(stationID);
				ads.put(organization, stations);
			} else {
				ads.get(organization).add(stationID);
			}
		}
	}
	
	
}
