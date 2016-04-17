package dm341.index;

import dm341.util.Pair;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.nio.channels.FileChannel;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class Index {

	// Term id -> (position in index file, doc frequency) dictionary
	private static Map<Integer, Pair<Long, Integer>> postingDict 
		= new TreeMap<Integer, Pair<Long, Integer>>();
	// Term -> term id dictionary
	private static Map<String, Integer> termDict
		= new TreeMap<String, Integer>();
	// Committee name -> Committee id dictionary
	private static Map<String, Integer> comDict
		= new TreeMap<String, Integer>();
	// Committee counter
	private static int comIdCounter = 0;
	// Term counter
	private static int wordIdCounter = 0;
	// Index
	private static BaseIndex index = null;

	private static boolean writePos = true;
	
	/* 
	 * Write a posting list to the given file 
	 * You should record the file position of this posting list
	 * so that you can read it back during retrieval
	 * 
	 * */
	private static void writePosting(FileChannel fc, PostingList posting)
			throws IOException {
		/*
		 * TODO: Your code here
		 *	 
		 */
		if (writePos) {
			long pos = fc.position();
			postingDict.put(posting.getTermId(), new Pair<Long, Integer> (pos, posting.getList().size()));
		}
		index.writePosting(fc, posting);
	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		/* Compression Method */
		String compression = "Gamma";
		/* Get index */
		String className = "dm341.index." + compression + "Index";
		try {
			Class<?> indexClass = Class.forName(className);
			index = (BaseIndex) indexClass.newInstance();
		} catch (Exception e) {
			System.err
					.println("Index method must be \"Basic\", \"VB\", or \"Gamma\"");
			throw new RuntimeException(e);
		}

		/* Read configuration file */
		String config_path = "./configure.txt";
		File config = new File(config_path);
		if (!config.exists()) {
			System.err.println("Cannot find configuration file");
			return;
		}
		
		BufferedReader reader = new BufferedReader(new FileReader(config));
		String data_path = reader.readLine();
		reader.close();
		
		/* Input file */
		String input_path = data_path + "/FEC/data.csv";
		File input = new File(input_path);
		if (!input.exists()) {
			System.err.println("Invalid input path: " + input);
			return;
		}

		/* Output directory */
		String output_path = data_path + "/FEC/output";
		File output = new File(output_path);
		if (output.exists() && !output.isDirectory()) {
			System.err.println("Invalid output directory: " + output_path);
			return;
		}

		if (!output.exists()) {
			if (!output.mkdirs()) {
				System.err.println("Create output directory failure");
				return;
			}
		}

		Map<Integer, PostingList> indexList = new HashMap<Integer, PostingList>();
		/* Read FEC data */
		Reader in = new FileReader(input_path);
		BufferedReader in_buff = new BufferedReader(in);
		String[] fields= in_buff.readLine().split(",");
		Iterable<CSVRecord> records = CSVFormat.EXCEL.withHeader(fields).parse(in);
		for (CSVRecord record : records) {
		    String name = record.get("name");
		    name = name.toLowerCase();
		    comDict.put(name, comIdCounter);
		    String[] tokens = name.trim().split("\\s+");
		    for (String token : tokens) {
		    	if (!termDict.containsKey(token))
					termDict.put(token, wordIdCounter++);	
				int wordId = termDict.get(token);
				if (!indexList.containsKey(wordId)) {
					PostingList postingList = new PostingList(wordId);
					postingList.addToList(comIdCounter);
					indexList.put(wordId,  postingList);
				}
				else {
					indexList.get(wordId).addToList(comIdCounter);
				}
		    }
		    comIdCounter++;
		}
		
		/* Output index file */
		String index_path = output_path + "/corpus.index";
		File index = new File(index_path);


		/* Sort and output */
		if (!index.createNewFile()) {
			System.err.println("Create new block failure.");
			return;
		}
		
		RandomAccessFile bfc = new RandomAccessFile(index, "rw");
		FileChannel fc = bfc.getChannel();
		List<PostingList> list = new ArrayList<PostingList>(indexList.values());
		for (PostingList entry : list) {
			writePosting(fc, entry);
		}
		bfc.close();

		BufferedWriter termWriter = new BufferedWriter(new FileWriter(new File(
				output, "term.dict")));
		for (String term : termDict.keySet()) {
			termWriter.write(term + "\t" + termDict.get(term) + "\n");
		}
		termWriter.close();


		BufferedWriter docWriter = new BufferedWriter(new FileWriter(new File(
				output, "com.dict")));
		for (String doc : comDict.keySet()) {
			docWriter.write(doc + "\t" + comDict.get(doc) + "\n");
		}
		docWriter.close();
		
		BufferedWriter postWriter = new BufferedWriter(new FileWriter(new File(
				output, "posting.dict")));
		for (Integer termId : postingDict.keySet()) {
			postWriter.write(termId + "\t" + postingDict.get(termId).getFirst()
					+ "\t" + postingDict.get(termId).getSecond() + "\n");
		}
		postWriter.close();

		
	}
}
