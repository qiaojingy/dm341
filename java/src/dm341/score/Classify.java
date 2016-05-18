package dm341.score;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import dm341.util.DistanceMeasure;
import dm341.util.FCCRecord;
import dm341.util.IO;
import dm341.util.LSH;
import dm341.util.Pair;

public class Classify {
	
	private HashMap<String, Pair<Integer, HashSet<String>>> aliases; // org name -> current vote, set of aliases
	private HashMap<String, Pair<Integer, HashSet<String>>> ads; // 
	public static double THRESHOLD = 0.9;
	public static int KGram = 2;	
	
	public Classify(List<FCCRecord> list) {
		aliases = new HashMap<String, Pair<Integer, HashSet<String>>>();
		ads = new HashMap<String, Pair<Integer, HashSet<String>>> ();
		
		// merge same 
		for (FCCRecord record: list) {
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
	
	private String findMyRoot(Map<String, String> roots, String node) {
		String currNode = node;
		while (roots.containsKey(currNode) || !roots.get(currNode).equals(currNode)) {
			currNode = roots.get(currNode);
		}
		if (!currNode.equals(node)) roots.put(node, currNode);
		return currNode;
	}
	
	public HashMap<String, HashSet<String>> mergeOrganizationsLSH(ArrayList<FCCRecord> records) throws Exception {
		HashMap<String, HashSet<String>> uniqueOrgAds = new HashMap<String, HashSet<String>>();
		LSH lsher = new LSH(3, 100, 20, 347);
		List<String> allOrgs = new ArrayList<String>(ads.keySet());
		lsher.lsh(allOrgs);
		Map<String, String> roots = new HashMap<String, String> ();
		Set<String> mergedOrgs = new HashSet<String>();
		for (String org: allOrgs) {
			if (!mergedOrgs.contains(org)) {
				aliases.put(org, Pair.make(ads.get(org).getFirst(), new HashSet<String>()));
			}
			Set<String> candidates = lsher.getCandidates(org);
			for (String candidate : candidates) {
				String myRoot = findMyRoot(roots, org);
				String candRoot = findMyRoot(roots, candidate);
				if (myRoot.equals(candRoot)) continue; // already merged org with candidate
				double score = DistanceMeasure.jaroWinklerDistanceScore(myRoot, candRoot);
				if (score >= THRESHOLD) {
					int myRootCount = ads.get(myRoot).getFirst();
					int candRootCount = ads.get(candRoot).getFirst();
					String newName = null, aliase = null;
					// select my root to be the official org name
					// add all cand root's aliases into my root's aliases
					// change cand root's root to my root
					// delete cand root from aliases
					if (myRootCount >= candRootCount) {
						newName = myRoot;
						aliase = candRoot;		
					} else {
						newName = candRoot;
						aliase = myRoot;
					}
					HashSet<String> dups = null;// new HashSet<String>();
					if (aliases.containsKey(newName)) {
						dups = aliases.get(newName).getSecond();
					} else {
						dups = new HashSet<String>();
					}
					if (aliases.containsKey(aliase)) dups.addAll(aliases.get(aliase).getSecond());
					aliases.remove(aliase);
					dups.add(aliase);
					aliases.put(newName, Pair.make(ads.get(newName).getFirst(), dups));
					roots.put(aliase, newName);
					mergedOrgs.add(aliase);
					mergedOrgs.add(newName);
				}
			}
		}
		return uniqueOrgAds;
	}
	
	
	public HashMap<String, HashSet<String>> mergeOrganizations() {
		// merge similar
		HashMap<String, HashSet<String>> uniqueOrgAds = new HashMap<String, HashSet<String>>();
		HashSet<String> mergedOrgs = new HashSet<String> ();
		
		for (String currOrgName : ads.keySet()) {
			if (!mergedOrgs.contains(currOrgName)) {
				aliases.put(currOrgName, new Pair<Integer, HashSet<String>> (ads.get(currOrgName).getFirst(), new HashSet<String>()));
			}
			if (mergedOrgs.contains(currOrgName)) continue;
			
			HashSet<String> stationIds = new HashSet<String> ();
			stationIds.addAll(ads.get(currOrgName).getSecond());
			String currKey = currOrgName;
			
			for (String otherOrgName : ads.keySet()) {
				
				if (currOrgName.equals(otherOrgName) || currKey.equals(otherOrgName)) continue;
				double score = DistanceMeasure.jaroWinklerDistanceScore(currKey, otherOrgName);
				
				//System.out.println("currKey "+currKey+" otherOrgName "+otherOrgName+ " score "+score);
				if (score >= THRESHOLD) {
					
					int otherCount = ads.get(otherOrgName).getFirst();
					if (ads.get(currKey).getFirst() >= otherCount) {
						//System.out.println("current key is "+currKey);
						//System.out.println(aliases.get(currKey).getSecond().size());
						aliases.get(currKey).getSecond().add(otherOrgName);
					} else {
						//System.out.println("current key is "+currKey);
						//System.out.println("Incoming current key is "+otherOrgName);
						aliases.put(otherOrgName, aliases.get(currKey));
						aliases.get(otherOrgName).setFirst(otherCount);
						aliases.get(otherOrgName).getSecond().add(currKey);
						aliases.remove(currKey);
						currKey = otherOrgName;
					}
					mergedOrgs.add(otherOrgName);
					stationIds.addAll(ads.get(otherOrgName).getSecond());
				} 
			}
			uniqueOrgAds.put(currKey, stationIds);
		}
		return uniqueOrgAds;
	}	
	
	public static void main(String args[]) throws IOException {
		Classify cf = new Classify(IO.readFCCRecordsLarge());
		HashMap<String, HashSet<String>> results = cf.mergeOrganizations();
		for (String result:results.keySet()) {
			System.out.println("Organziation name:" + result + ", aliases:" + cf.getAliases(result));
		}
	}
}