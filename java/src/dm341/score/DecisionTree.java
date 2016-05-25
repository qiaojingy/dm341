package dm341.score;

import dm341.util.Organization;
import dm341.util.StatesUtils;
import dm341.util.Candidate;
import dm341.util.CandidatesList;
import dm341.util.FCCRecord;
import dm341.util.NameRecognizer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DecisionTree {
	static boolean tagGoodness(Organization org, List<FCCRecord> fccRecords) throws Exception {
		// Count the number of states
		List<String> candidateNameStringList = org.nameStringList;
		Set<String> states = new HashSet<String>();
		for (FCCRecord fccRecord : fccRecords) {
			states.add(fccRecord.getStationState());
		}
		if (states.size() == 1) {
			if (candidateNameStringList == null) {
				org.goodness = null;
				return false;
			} else {
				if (org.candidates != null && org.candidates.size() == 1 && org.candidates.get(0).getState().compareTo((String) states.toArray()[0]) != 0 && org.candidates.get(0).getState().compareTo("US") != 0) {
					org.goodness = "rid";
					return false;
				} else {
					return true;
				}
			}
		} else {
			for (FCCRecord fccRecord : fccRecords) {
				if (StatesUtils.isHubCity(fccRecord.getStationCity())) {
					if (StatesUtils.isContiguous(new ArrayList<String>(states))) return true;
					else break;
				}
			}
			if (org.candidates == null) return true;
			for (Candidate candidate : org.candidates) { 
				if (candidate.isNationalCandidate()) {
					org.goodness = null;
					return true;
				}
			}
				return false;
		}	
	}
}
