package dm341.score;

import dm341.util.Organization;
import dm341.util.StatesUtils;
import dm341.util.CandidatesList;
import dm341.util.FCCRecord;
import dm341.util.NameRecognizer;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DecisionTree {
	boolean isGood(Organization org, List<FCCRecord> fccRecords) throws Exception {
		// Count the number of states
		List<String> candidateNameStringList = NameRecognizer.getNameStringList(org.getOrgName());
		Set<String> states = new HashSet<String>();
		for (FCCRecord fccRecord : fccRecords) {
			states.add(fccRecord.getStationState());
		}
		if (states.size() == 1) {
			if (candidateNameStringList == null) {
				return false;
			} else {
				if (CandidatesList.getCandidateState(candidateNameStringList).equals(
					states.toArray()[0])) {
					return true;
				} else {
					return false;
				}
			}
		} else {
			for (FCCRecord fccRecord : fccRecords) {
				if (StatesUtils.isHubCity(fccRecord.getStationCity())) {
					if (StatesUtils.isContiguous(states)) return true;
					else break;
				}
			}
			if (CandidatesList.isNationalCandidate(candidateNameStringList)) 
				return true;
			else 
				return false;
		}	
	}
}
