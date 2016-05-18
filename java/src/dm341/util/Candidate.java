package dm341.util;

public class Candidate {
	public String name;
	public enum Office {
	    House, President, Senate
	}
	Office office;
	String state;
	
	public Candidate(String name, String office, String state) {
		this.name = name;
		System.out.println(office);
		switch (office) {
		case "H":
			this.office = Office.House;
		case "P":
			this.office = Office.President;
		case "S":
			this.office = Office.Senate;
		}
		this.state = state;
	}
	
	public Office getOffice() {
		return office;
	}
	
	public boolean isNationalCandidate() {
		if (this.office == Office.President) return true;
		else return false;
	}
	
	public String getState() {
		return this.state;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.name);
		sb.append("\t");
		switch (this.office) {
		case House:
			sb.append("House");
		case President:
			sb.append("President");
		case Senate:
			sb.append("Senate");
		}
		sb.append("\t");
		sb.append(this.state);
		return sb.toString();
	}
}
