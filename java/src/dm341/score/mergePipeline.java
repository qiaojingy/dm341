package dm341.score;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import dm341.util.FCCRecord;
import dm341.util.NameRecognizer;
import dm341.util.Organization;

public class mergePipeline {
	public static List<FCCRecord> toLowerCase(List<FCCRecord> fccRecords) {
		for (FCCRecord fccRecord : fccRecords) {
			fccRecord.orgName = fccRecord.orgName.toLowerCase();
		}
		return fccRecords;
	}
	
	public static Map<Organization, List<FCCRecord>> groupByOrg(List<FCCRecord> fccRecords) {
		return null;
	}
	
	// tag whether orgname is person name
	public static Set<Organization> tagNames(Set<Organization> orgs) throws Exception {
		Set<Organization> orgsWithName = new HashSet<Organization>();
		for (Organization org : orgs) {
			org.nameStringList = NameRecognizer.getNameStringList(org.orgName);
			if (org.nameStringList != null) {
				org.containsName = true;
				orgsWithName.add(org);
			}
		}
		return orgsWithName;
	}
	
	// tag the candidates
	public static void tagCandidates(Set<Organization> orgs) {
		return;
	}
}
