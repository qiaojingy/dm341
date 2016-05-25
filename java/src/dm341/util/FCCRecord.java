package dm341.util;
import dm341.util.Pair;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;

import javax.json.Json;
import javax.json.JsonReader;
import javax.json.JsonObject;

public class FCCRecord implements Serializable{
	public String stationID;
	public String orgName;
	String url;
	public Station station;
	// String staName;
	// String staState;
	// String staCity;
	String type; // invoice, contract, nab, request
	
	static String FCCPrefix = "http://data.fcc.gov/mediabureau/v01/tv/facility/id/";
	
	public FCCRecord(String stationID, String url) {
		this.stationID = stationID.trim();
		//String[] fields = url.split("/");
		//this.orgName = fields[fields.length - 2];
		this.url = url;
		this.orgName = OrgExtractor.extractFromUrl(url);
		// Pair<String, String> staNameState = this.getStaNameState(stationID);
		// this.staName = staNameState.getFirst();
		// this.staState = staNameState.getSecond();
	}
	
	public String toString() {
		return stationID + "\t" + url;
	}
	public String getStationID() {
		return stationID;
	}
	
	public String getStationName(){
		return station.name;
	}
	
	public String getStationState(){
		return station.state;
	}
	
	public String getStationCity(){
		return station.city;
	}
	
	public String getOrgName() {
		return orgName;
	}
	
	public String url() {
		return url;
	}
	
	private Pair<String, String> getStaNameState(String stationID){
		return new Pair<String, String>("", "");
		/***
		String recordURL = FCCPrefix + stationID + ".json";
		try {
			URL url = new URL(recordURL);
			InputStream is = url.openStream();
			JsonReader rdr = Json.createReader(is);
			JsonObject obj = rdr.readObject();
			JsonObject res = obj.getJsonObject("results").getJsonObject("facility");
			String staName = res.getString("callSign");
			String staState = res.getString("communityState");
			return new Pair<String, String>(staName,staState);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		***/
	}
	
	private String getStaCity(String stationID){
		String recordURL = FCCPrefix + stationID + ".json";
		try {
			URL url = new URL(recordURL);
			InputStream is = url.openStream();
			JsonReader rdr = Json.createReader(is);
			JsonObject obj = rdr.readObject();
			JsonObject res = obj.getJsonObject("results").getJsonObject("facility");
			String staCity = res.getString("communityCity");
			return staCity;
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	/*public static void main(String[] args) throws IOException {
		FCCRecord test = new FCCRecord("49157","https://stations.fcc.gov//collect/files/49157/Political File/2014/Federal/US Senate/Kay Hagen/WCCB 10.27-11.4 2nd TV Add 1 (14140892445774)_.pdf");
		System.out.println("name of station is: "+test.getStationName());
		System.out.println("State of station is: "+test.getStationState());
	}*/
}

