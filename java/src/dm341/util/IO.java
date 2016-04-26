package dm341.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class IO {
	static String data_path;
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
	static List<FCCRecord> readFCCRecords() throws IOException {
		/* Get index directory */
		String input_path = data_path + "/FCC/docs.txt";
		BufferedReader adRecordReader = new BufferedReader(new FileReader(new File(
				input_path)));
		String line;
		line = adRecordReader.readLine();
		System.out.println(line);
		/***
		while ((line = adRecordReader.readLine()) != null) {
			String[] tokens = line.split("\t");
			termDict.put(tokens[0], Integer.parseInt(tokens[1]));
		}
		termReader.close();
		***/
		return null;
	}
}
