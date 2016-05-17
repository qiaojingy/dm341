package dm341.util;

import java.io.IOException;
import java.util.List;

public class Organization {
	private String orgName;
	public List<String> nameStringList;
	public boolean containsName;
	public boolean isNationalCandidate;
	public String candidateRunningState;
	
	public Organization(String orgName) throws Exception {
		this.orgName = orgName;
		this.nameStringList = NameRecognizer.getNameStringList(orgName);
		if (this.nameStringList != null) this.containsName = true;
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
		return isNationalCandidate;
	}
}
