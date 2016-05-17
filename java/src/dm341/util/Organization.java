package dm341.util;

import java.io.IOException;
import java.util.List;

public class Organization {
	public String orgName;
	public List<String> nameStringList;
	public boolean containsName;
	public Candidate candidate;
	
	public Organization(String orgName) throws Exception {
		this.orgName = orgName;
	}
	
	public String getOrgName() {
		return this.orgName;
	}
	
	public boolean containsName() {
		return this.containsName;
	}
	
	public List<String> getNameStringList() {
		return this.nameStringList;
	}
	
	public boolean isNationalCandidate() {
		if (!this.containsName) return false;
		return this.candidate.isNationalCandidate();
	}
	
	public String getCandidateState() {
		return this.candidate.getState();
	}
}
