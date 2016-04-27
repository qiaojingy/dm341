package dm341.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
		System.out.println("here" + data_path);
		reader.close();
	}
	static List<FCCRecord> readFCCRecords() throws IOException {
		if (!initialized)
		    initialize();
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
	public static void main(String[] args) throws IOException {
		readFCCRecords();
	}
}
