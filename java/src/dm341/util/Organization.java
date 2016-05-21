package dm341.util;

import java.io.IOException;
import java.util.List;

public class Organization {
	private String orgName;
	private List<String> nameStringList;
	private boolean containsName;
	private List<Candidate> candidates;
	
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

	public void setCandidate(List<Candidate> candidates) {
		this.candidates = candidates;
	}
	
	public List<Candidate> getCandidate() {
		return this.candidates;
	}
	
	public List<String> getNameList() {
		return this.nameStringList;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((orgName == null) ? 0 : orgName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Organization other = (Organization) obj;
		if (orgName == null) {
			if (other.orgName != null)
				return false;
		} else if (!orgName.equals(other.orgName))
			return false;
		return true;
	}
}
