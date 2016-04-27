package dm341.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
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
		String input_path = data_path + "/FCC/test.txt";
		BufferedReader adRecordReader = new BufferedReader(new InputStreamReader(new FileInputStream(input_path),
		        "UTF8"));
		String line;
		line = adRecordReader.readLine();
		adRecordReader.readLine();
		String fieldsLine = line.substring(2);
		fieldsLine = line.replace("\0", "");
		System.out.println((int) fieldsLine.charAt(0));
		System.out.println((int) fieldsLine.charAt(1));
		System.out.println((int) fieldsLine.charAt(2));
		System.out.println((int) fieldsLine.charAt(3));
		System.out.println((int) fieldsLine.charAt(4));
		System.out.println((int) fieldsLine.charAt(5));
		System.out.println((int) fieldsLine.charAt(6));
		System.out.println((int) fieldsLine.charAt(7));
		System.out.println((int) fieldsLine.charAt(8));
		System.out.println((int) fieldsLine.charAt(9));

		String[] fields = fieldsLine.split("|");
		Map<String, Integer> fieldsDict = new HashMap<String, Integer>();
		for (int i = 0; i < fields.length; i++) {
			fieldsDict.put(fields[i], i);
		}
		List<FCCRecord> FCCRecords = new ArrayList<FCCRecord>();
		StringBuilder sb = new StringBuilder();
		while ((line = adRecordReader.readLine()) != null) {
			if (line.length() == 1 && line.charAt(0) == 0) {
				System.out.println("**********");
				String s = sb.toString();
				String[] datum = s.split("|");
				/***
				for (int i = 0; i < datum.length; i++) {
					System.out.println(datum[i]);
				}
				***/
				FCCRecords.add(new FCCRecord(datum[fieldsDict.get("station_id")], datum[fieldsDict.get("url")]));
				sb.setLength(0);
			} else {
				sb.append(line);
			}
		}
		adRecordReader.close();
		for (FCCRecord fr : FCCRecords) {
			System.out.println(fr.getOrgName());
		}
		return null;
	}
	public static void main(String[] args) throws IOException {
		readFCCRecords();
	}
}
