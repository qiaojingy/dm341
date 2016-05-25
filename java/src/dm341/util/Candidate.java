package dm341.util;

public class Candidate {
	public String name;
	private enum Office {
	    House, President, Senate
	}
	Office office;
	String state;
	
	public Candidate(String name, String office, String state) {
		this.name = name;
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
	
	public String getName() {
		return name;
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
	
	@Override
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((office == null) ? 0 : office.hashCode());
		result = prime * result + ((state == null) ? 0 : state.hashCode());
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
		Candidate other = (Candidate) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (office != other.office)
			return false;
		if (state == null) {
			if (other.state != null)
				return false;
		} else if (!state.equals(other.state))
			return false;
		return true;
	}

}
