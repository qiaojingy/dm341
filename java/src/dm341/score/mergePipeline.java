package dm341.score;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import dm341.util.Candidate;
import dm341.util.DistanceMeasure;
import dm341.util.FCCRecord;
import dm341.util.IO;
import dm341.util.LSH;
import dm341.util.NameRecognizer;
import dm341.util.Organization;
import dm341.util.Pair;
import dm341.util.Station;

public class mergePipeline {
	
	private static String rootIs(String node, Map<String, String> roots) {
		String curNode = node;
		String parent = null;
		while (!(parent = roots.get(curNode)).equals(curNode)) {
			curNode = parent;
		}
		roots.put(node, parent); //optimize
		return curNode;
	} 
	
	private static Map<String, Set<String>> findSimilarDups(Map<String, Integer> orgCounts) throws Exception {
		int KGRAM = 3;
		int NHASHES = 120; 
		int NBANDS = 40;
		int NBUCKETS = 3613;
		String similarityMeasure = "Jaccard"; // "Jaro", "Jaccard"
		
		double THRESHOLD = 0;
		
		if (similarityMeasure.equals("Jaccard")) {
			THRESHOLD = 0.6;
		} else if (similarityMeasure.equals("Jaro")) {
			THRESHOLD = 0.85;
		} else throw new Exception("Invalid measure");
		
		ArrayList<String> allOrgs = new ArrayList<String>(orgCounts.size()); //orgCounts.keySet());
		Map<String, String> roots = new HashMap<String, String> ();
		for (String org: orgCounts.keySet()) {
			allOrgs.add(org);
			roots.put(org, org);
		}
		
		LSH lsher = new LSH(KGRAM, NHASHES, NBANDS, NBUCKETS);
		lsher.lsh(allOrgs);
		
		
		Map<String, Set<String>> aliases = new HashMap<String, Set<String>> ();
		Set<String> inDict = new HashSet<String>();
		
		
		for (String orgName: allOrgs) {
			//System.out.println("\n\n\n\n\ncurrent org:" + orgName);
			Set<String> candNames = lsher.getCandidates(orgName);
			//System.out.println("candidates are:" + candNames);
			for (String candName: candNames) {
				//System.out.println("\ncurrent cand:" + candName);
				String myRoot = rootIs(orgName, roots);
				String candRoot = rootIs(candName, roots);
				//System.out.println("my root is:" + myRoot + ", cand root is:" + candRoot);
				if (myRoot.equals(candRoot)) continue; // already merged
				// double score = DistanceMeasure.jaroWinklerDistanceScore(myRoot, candRoot);
				//double score = DistanceMeasure.jaroWinklerDistanceScore(orgName, candName);
				double score;
				if (similarityMeasure.equals("Jaccard")) {
					score = DistanceMeasure.JaccardDistanceScore(3, orgName, candName);
				} else {
					score = DistanceMeasure.jaroDistanceScore(orgName, candName);
				}
				
				
				//System.out.println("Score is:" + score);
				if (score >= THRESHOLD) { // need to merge
					//System.out.println("NEW PAIR FOUND!!");
					String newRoot = myRoot;
					String newAlias = candRoot;
					if (orgCounts.get(myRoot) < orgCounts.get(candRoot) || (orgCounts.get(myRoot) == orgCounts.get(candRoot) && myRoot.length() < candRoot.length())) { // reverse
						newRoot = candRoot;
						newAlias = myRoot;
					} 
					if (!aliases.containsKey(newRoot)) {
						aliases.put(newRoot, new HashSet<String>());
					} 
					if (aliases.containsKey(newAlias)) {
						// add all aliases of newNode to newRoot's set
						aliases.get(newRoot).addAll(aliases.get(newAlias));
						aliases.remove(newAlias);
						
					}
					// add newNode to newRoot's set
					aliases.get(newRoot).add(newAlias);
					roots.put(newAlias, newRoot);
					inDict.add(newRoot);
					inDict.add(newAlias);
				}
			}
			if (!inDict.contains(orgName)) { // no merge happened
				aliases.put(orgName, new HashSet<String> ());
			}
		}
		return aliases;
	}
	
	public static Map<Organization, List<FCCRecord>> groupByOrg(List<FCCRecord> fccRecords) throws Exception {

		Map<String, List<FCCRecord>> unitedRecords = new HashMap<String, List<FCCRecord>>();
		Map<String, Integer> orgCounts = new HashMap<String, Integer>();
		// unify exactly duplicated names
		for (FCCRecord record: fccRecords) {
			String orgName = record.getOrgName();
			if (!unitedRecords.containsKey(orgName)) {
				unitedRecords.put(orgName, new ArrayList<FCCRecord>());
				orgCounts.put(orgName, 0);
			}
			unitedRecords.get(orgName).add(record);
			orgCounts.put(orgName, orgCounts.get(orgName) + 1);
		}	
		
		// unify similar duplicated names
		Map<String, Set<String>> aliases = findSimilarDups(orgCounts);
		Map<Organization, List<FCCRecord>> ret = new HashMap<Organization, List<FCCRecord>> ();
		
		int finalCount = 0;
		
		for (Map.Entry<String, Set<String>> entry: aliases.entrySet()) {
			List<FCCRecord> records = new ArrayList<FCCRecord> (unitedRecords.get(entry.getKey()));
			
			for (String alias: entry.getValue()) {
				records.addAll(unitedRecords.get(alias));
			}
			finalCount += records.size();
			ret.put(new Organization(entry.getKey()), records);
		}
		
		//finalCount should equal to fccRecords.count
		if (finalCount != fccRecords.size()) {
			throw new Exception("counts doesn't match!");
		}
		
		return ret;
	}
	
	public static List<FCCRecord> toLowerCase(List<FCCRecord> fccRecords) {
		for (FCCRecord fccRecord : fccRecords) {
			fccRecord.orgName = fccRecord.orgName.toLowerCase();
		}
		return fccRecords;
	}
	
	
	// tag whether orgname is person name
	public static Set<Organization> tagNames(Set<Organization> orgs) throws Exception {
		Set<Organization> orgsWithName = new HashSet<Organization>();
		for (Organization org : orgs) {
			org.nameStringList = NameRecognizer.getNameStringList(org.orgName);
			if (org.nameStringList != null && org.nameStringList.size() > 0) {
				org.containsName = true;
				orgsWithName.add(org);
			}
		}
		return orgsWithName;
	}
	
	
	private static Map<String, Set<Candidate>> buildIndex(List<Candidate> candidates) {
		Map<String, Set<Candidate>> index = new HashMap<String, Set<Candidate>>();
		for (Candidate cand: candidates) {
			String[] candName = cand.getName().split("[,\\s]+");
			for (String token: candName) {
				if (token.length() > 2) {
					if (!index.containsKey(token)) {
						index.put(token, new HashSet<Candidate>());
					}
					index.get(token).add(cand);
				}
			}
		}
		return index;
	}
	
	public static void tagStations(List<FCCRecord> fccRecords) throws IOException {
		Map<String, Station> stationsDict = IO.readStations();
		for (FCCRecord fccRecord : fccRecords) {
			fccRecord.station = stationsDict.get(fccRecord.getStationID());
			if (fccRecord.station == null) {
				fccRecord.stationID = fccRecord.getStationID().substring(1);
				fccRecord.station = stationsDict.get(fccRecord.getStationID());
			}
		}
	}
	
	// tag the candidates
	// COX, JOHN R.
	// GOSAR, PAUL ANTHONY
	// SEWELL, TERRYCINA ANDREA
	// BERRYHILL, MICHAEL CLARE SR
	public static void tagCandidates(Set<Organization> orgs, List<Candidate> candidates) {
		Map<String, Set<Candidate>> index = buildIndex(candidates);
		for (Organization org: orgs) {
			if (org.containsName()) {
				List<String> nameList = org.getNameStringList();
				String temp = nameList.get(0);
				//Set<Candidate> intersect = index.get(temp) != null? new HashSet<Candidate>(index.get(temp)) : new HashSet<Candidate> ();
				
				Map<Candidate, Integer> counts = new HashMap<Candidate, Integer>();
				for (int i = 0; i < nameList.size(); i++) {
					if (index.get(nameList.get(i)) != null) {
						for (Candidate cand: index.get(nameList.get(i))) {
							if (counts.containsKey(cand)) {
								counts.put(cand, counts.get(cand) + 1);
							} else {
								counts.put(cand, 1);
							}
						}
					}
				}
				if (counts.size() == 0) continue;
				List<Map.Entry<Candidate, Integer>> sortList = new ArrayList<Map.Entry<Candidate, Integer>> (counts.entrySet());
				Collections.sort(sortList, new Comparator<Map.Entry<Candidate, Integer>> () {
					@Override
					public int compare(Entry<Candidate, Integer> o1, Entry<Candidate, Integer> o2) {
						// TODO Auto-generated method stub
						return -1 * o1.getValue().compareTo(o2.getValue());
					}});
				
				List<Candidate> myCandidates = new ArrayList<Candidate>();
				int maxCount = sortList.get(0).getValue();
				for (Map.Entry<Candidate, Integer> entry: sortList) {
					if (entry.getValue() != maxCount) break;
					myCandidates.add(entry.getKey());
				}
				org.setCandidate(myCandidates);
			}
		}
	}
	
	public static void tagFECs(Set<Organization> orgs) {
		return;
	}
	
	public static void mergeRecords() throws Exception {
		List<FCCRecord> fccRecords = IO.readFCCRecordsLarge();
		tagStations(fccRecords);
		toLowerCase(fccRecords);
		Map<Organization, List<FCCRecord>> orgToFCCs = groupByOrg(fccRecords);
		tagFECs(orgToFCCs.keySet());
		Set<Organization> orgsWithName = tagNames(orgToFCCs.keySet());
		tagCandidates(orgsWithName, IO.readCandidates());
		/***
		for (Organization org : orgsWithName) {
			System.out.println("name string list: " + org);
			System.out.println("matches: ");
			if (org.getCandidate() != null) {
				for (Candidate candidate : org.getCandidate()) {
					System.out.print(candidate.name + " ** ");
				}
			}
			System.out.println();
			System.out.println("----------------------------");
		}
		***/
		for (Organization org : orgToFCCs.keySet()) {
			DecisionTree.tagGoodness(org, orgToFCCs.get(org));
		}
		for (Organization org : orgToFCCs.keySet()) {
			if (org.goodness != null && org.goodness.compareTo("rid") == 0) {
				System.out.println(org);
				System.out.println(org.candidates);
				System.out.println(org.candidates.get(0).getState());
				for (FCCRecord fccRecord : orgToFCCs.get(org)) {
					System.out.println(fccRecord.getStationState());
				}
				for (FCCRecord fccRecord : orgToFCCs.get(org)) {
					System.out.println(fccRecord.stationID + "\t" + fccRecord.url());
				}
			}
		}
	}
	
	
	public static void main(String[] args) throws Exception {
		/***
		String line = null;
		BufferedReader br = new BufferedReader(new FileReader("/Users/weiwang/Documents/CS/CS341/dm341/java/names.txt"));
		Map<String, Integer> orgCounts = new HashMap<String, Integer>();
		
		while ((line = br.readLine()) != null) {
			String org = line.toLowerCase();
			if (!orgCounts.containsKey(org)) {
				orgCounts.put(org, 0);
			}
			orgCounts.put(org, orgCounts.get(org) + 1);	
			//System.out.println(line);
		}
		System.out.println(orgCounts.size());
		
		System.out.println("Before");
		for (String org: orgCounts.keySet()) {
			System.out.println(org + ":" + orgCounts.get(org));
		}
		
		Map<String, Set<String>> ret = findSimilarDups(orgCounts);
		
		System.out.println("\n\n\n\n\n\n\n\n\n\nAfter");
		List<String> list= new ArrayList<String>(ret.keySet());
		Collections.sort(list);
		for (String org : list) {
			System.out.println(org + ":" + ret.get(org));
		}
		System.out.println(list.size());
		***/
		mergeRecords();
	}


}
