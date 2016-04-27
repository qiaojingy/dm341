package dm341.util;

public class FCCRecord {
	String stationID;
	String orgName;
	String url;
	
	public FCCRecord(String stationID, String url) {
		this.stationID = stationID;
		String[] fields = url.split("/");
		this.orgName = fields[fields.length - 2];
		this.url = url;
	}
	
	public String getStationID() {
		return stationID;
	}
	
	public String getOrgName() {
		return orgName;
	}
	
	public String url() {
		return url;
	}
}
