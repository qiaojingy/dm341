package dm341.util;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

public class IO {
	static String data_path;
	static boolean initialized = false;
	static void initialize() throws IOException {
		/* Read configuration file */
		String config_path = "./configure.txt";
		File config = new File(config_path);
		if (!config.exists()) {
			System.err.println("Cannot find configuration file");
			return;
		}
		BufferedReader reader = new BufferedReader(new FileReader(config));
		data_path = reader.readLine();
		reader.close();
	}
	public static List<FCCRecord> readFCCRecords() throws IOException {
		if (!initialized) {
		    initialize();
		    initialized = true;
		}
		/* Get index directory */
		String input_path = data_path + "/FCC/docs.txt";
		Reader reader = new InputStreamReader(new FileInputStream(input_path), "UTF-8");
		BufferedReader adRecordReader = new BufferedReader(reader);
		String line;
		line = adRecordReader.readLine();
		adRecordReader.readLine();
		String fieldsLine = line.replace("\0", "");
		fieldsLine = fieldsLine.substring(2);
		String[] fields = fieldsLine.split("\\|");
		Map<String, Integer> fieldsDict = new HashMap<String, Integer>();
		for (int i = 0; i < fields.length; i++) {
			fieldsDict.put(fields[i], i);
		}
		List<FCCRecord> FCCRecords = new ArrayList<FCCRecord>();
		StringBuilder sb = new StringBuilder();
		while ((line = adRecordReader.readLine()) != null) {
			if (line.length() == 1 && line.charAt(0) == 0 && sb.length() == 0) continue;
			if (line.length() == 1 && line.charAt(0) == 0) {
				String s = sb.toString();
				s = s.replace("\0", "");
				s = s.substring(2);
				String[] datum = s.split("\\|");
				FCCRecords.add(new FCCRecord(datum[fieldsDict.get("station_id")], datum[fieldsDict.get("url")]));
				sb.setLength(0);
			} else {
				sb.append(line);
			}
		}
		adRecordReader.close();
		return FCCRecords;
	}
	
	public static List<FCCRecord> readFCCRecordsLarge() throws IOException {
		if (!initialized) {
		    initialize();
		    initialized = true;
		}
		/* Get index directory */
		String input_path = data_path + "/FCC/id_url.csv";
		Reader reader = new InputStreamReader(new FileInputStream(input_path));
		BufferedReader adRecordReader = new BufferedReader(reader);
		String line;
		List<FCCRecord> FCCRecords = new ArrayList<FCCRecord>();
		while ((line = adRecordReader.readLine()) != null) {
			String[] datum = line.split(",");
			FCCRecords.add(new FCCRecord(datum[0], datum[1]));
		}
		adRecordReader.close();
		return FCCRecords;
	}
	
	public static List<String> readUrls() throws IOException {
		if (!initialized) {
			initialize();
			initialized = true;
		}
		String input_path = data_path + "/FCC/urls copy.csv";
		Reader reader = new InputStreamReader(new FileInputStream(input_path), "UTF-8");
		BufferedReader urlReader = new BufferedReader(reader);
		String line;
		List<String> urls = new ArrayList<String>();
		while ((line = urlReader.readLine()) != null) {
			urls.add(line);
		}
		urlReader.close();
		return urls;
	}
	
	public static Map<String, Station> readStations() throws IOException {
		if (!initialized) {
			initialize();
			initialized = true;
		}
		String input_path = data_path + "/FCC/stations.txt";
		Reader reader = new InputStreamReader(new FileInputStream(input_path), "UTF-8");
		BufferedReader stationsReader = new BufferedReader(reader);
		String line;
		line = stationsReader.readLine();
		stationsReader.readLine();
		String fieldsLine = line.replace("\0", "");
		fieldsLine = fieldsLine.substring(2);
		String[] fields = fieldsLine.split("\\|");
		Map<String, Integer> fieldsDict = new HashMap<String, Integer>();
		for (int i = 0; i < fields.length; i++) {
			fieldsDict.put(fields[i], i);
		}
		Map<String, Station> stationsDict = new HashMap<String, Station>();
		while ((line = stationsReader.readLine()) != null) {
			line = line.replace("\0", "");
			line = line.substring(2);
			String[] datum = line.split("\\|");
			stationsDict.put(datum[fieldsDict.get("id")], new Station(datum[fieldsDict.get("id")], datum[fieldsDict.get("partyName")], datum[fieldsDict.get("communityState")], datum[fieldsDict.get("communityCity")]));
		}
		stationsReader.close();
		return stationsDict;
	}
	
	public static List<Candidate> readCandidates() throws IOException {
		if (!initialized) {
			initialize();
			initialized = true;
		}
		String input_path = data_path + "/FEC/candidates.csv";
		Reader reader = new InputStreamReader(new FileInputStream(input_path), "UTF-8");
		Iterable<CSVRecord> records = CSVFormat.EXCEL.withHeader().parse(reader);
		List<Candidate> candidates = new ArrayList<Candidate>();
		for (CSVRecord record : records) {
		    String name = record.get("name").toLowerCase();
		    String office = record.get("office");
		    String state = record.get("state");
			candidates.add(new Candidate(name, office, state));			
		}
		return candidates;
	}

	public static List<Committee> readCommittees() throws IOException {
		if (!initialized) {
			initialize();
			initialized = true;
		}
		String input_path = data_path + "/FEC/data.csv";
		Reader reader = new InputStreamReader(new FileInputStream(input_path), "UTF-8");
		Iterable<CSVRecord> records = CSVFormat.EXCEL.withHeader().parse(reader);
		List<Committee> commitees = new ArrayList<Committee>();
		for (CSVRecord record : records) {
		    String name = record.get("name").toLowerCase();
			commitees.add(new Committee(name));	
		}
		return commitees;
	}
	
	public static Map<String, Set<String>> readAjacentStatesDict() throws IOException {
		if (!initialized) {
			initialize();
			initialized = true;
		}
		String input_path = data_path + "/neighbors-states.csv";
		Reader reader = new InputStreamReader(new FileInputStream(input_path), "UTF-8");
		Iterable<CSVRecord> records = CSVFormat.EXCEL.withHeader().parse(reader);
		Map<String, Set<String>> adjacentStatesDict = new HashMap<String, Set<String>>();
		for (CSVRecord record : records) {
		    String state1 = record.get("StateCode");
		    String state2 = record.get("NeighborStateCode");
			if (!adjacentStatesDict.containsKey(state1)) {
				adjacentStatesDict.put(state1, new HashSet<String>());
			}
			adjacentStatesDict.get(state1).add(state2);
			if (!adjacentStatesDict.containsKey(state2)) {
				adjacentStatesDict.put(state2, new HashSet<String>());
			}
			adjacentStatesDict.get(state2).add(state1);
		}
		return adjacentStatesDict;
	}
	
	public static void main(String[] args) throws IOException {
		List<Committee> committees = readCommittees();
		for (Committee committee : committees) {
			System.out.println(committee.name);
		}
	}

}
