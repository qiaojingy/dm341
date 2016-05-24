package dm341.util;

import java.io.IOException;
import java.util.List;

public class Organization {
	public String orgName;
	public List<String> nameStringList;
	public boolean containsName = false;
	public List<Candidate> candidates;
	public String goodness;
	
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
		for (Candidate candidate : candidates) {
			if (candidate.isNationalCandidate())
				return true;
		}
		return false;
	}
	
	public String getCandidateState() {
		return this.candidates.get(0).getState();
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
