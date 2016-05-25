package dm341.util;

public class Station {
	public String ID;
	public String name;
	public String state;
	public String city;
		
	public Station(String ID, String name, String state, String city) {
		this.ID = ID;
		this.name = name;
		this.state = state;
		this.city = city;
	}
	
	public String toString() {
		return ID + "\t" + name + "\t" + state + "\t" + city;
	}
}
