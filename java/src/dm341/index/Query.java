package dm341.index;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Query {

	// Term id -> position in index file
	private static Map<Integer, Long> posDict = new TreeMap<Integer, Long>();
	// Term id -> document frequency
	private static Map<Integer, Integer> freqDict = new TreeMap<Integer, Integer>();
	// Doc id -> doc name dictionary
	private static Map<Integer, String> docDict = new TreeMap<Integer, String>();
	// Term -> term id dictionary
	private static Map<String, Integer> termDict = new TreeMap<String, Integer>();
	// Index
	private static BaseIndex index = null;

	
	/* 
	 * Write a posting list with a given termID from the file 
	 * You should seek to the file position of this specific
	 * posting list and read it back.
	 * */
	private static PostingList readPosting(FileChannel fc, int termId)
			throws IOException {
		/*
		 * TODO: Your code here
		 */
		if (!posDict.containsKey(termId)) return null;
		fc.position(posDict.get(termId));
		return index.readPosting(fc);
	}
	
	private static List<Integer> intersect(List<Integer> l1, List<Integer> l2) {
	    List<Integer> answer = new ArrayList<Integer>();
	    int i = 0;
	    int j = 0;

	    // WRITE ALGORITHM HERE
	    while (i < l1.size() && j < l2.size()) {
	    	if (l1.get(i).compareTo(l2.get(j)) == 0) {
	    		answer.add(l1.get(i));
	    		i++;
	    		j++;
	    	}
	    	else if (l1.get(i).compareTo(l2.get(j)) < 0) {
	    		i++;
	    	}
	    	else {
	    		j++;
	    	}
	    }
	    return answer;	  
	}

	public static void main(String[] args) throws IOException {		
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

		/* Compression method */
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

		/* Get index directory */
		String input_path = data_path + "/FEC/output";
		File inputdir = new File(input_path);
		if (!inputdir.exists() || !inputdir.isDirectory()) {
			System.err.println("Invalid index directory: " + input_path);
			return;
		}

		/* Index file */
		RandomAccessFile indexFile = new RandomAccessFile(new File(input_path,
				"corpus.index"), "r");

		String line = null;
		/* Term dictionary */
		BufferedReader termReader = new BufferedReader(new FileReader(new File(
				input_path, "term.dict")));
		while ((line = termReader.readLine()) != null) {
			String[] tokens = line.split("\t");
			termDict.put(tokens[0], Integer.parseInt(tokens[1]));
		}
		termReader.close();

		/* Com dictionary */
		BufferedReader docReader = new BufferedReader(new FileReader(new File(
				input_path, "com.dict")));
		while ((line = docReader.readLine()) != null) {
			String[] tokens = line.split("\t");
			docDict.put(Integer.parseInt(tokens[2]), tokens[0] + "\t" + tokens[1]);
		}
		docReader.close();

		/* Posting dictionary */
		BufferedReader postReader = new BufferedReader(new FileReader(new File(
				input_path, "posting.dict")));
		while ((line = postReader.readLine()) != null) {
			String[] tokens = line.split("\t");
			posDict.put(Integer.parseInt(tokens[0]), Long.parseLong(tokens[1]));
			freqDict.put(Integer.parseInt(tokens[0]),
					Integer.parseInt(tokens[2]));
		}
		postReader.close();


		/* Processing queries */
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		FileChannel fc = indexFile.getChannel();
		/* For each query */
		System.out.print("Input query :   ");
		while ((line = br.readLine()) != null) {
			/*
			 * TODO: Your code here
			 *       Perform query processing with the inverted index.
			 *       Make sure to print to stdout the list of documents
			 *       containing the query terms, one document file on each
			 *       line, sorted in lexicographical order.
			 */
			String[] queryWords = line.split(" ");
			List<PostingList> pls = new ArrayList<PostingList> ();
			for (String queryWord : queryWords) {
				queryWord = queryWord.toLowerCase();
				if (!termDict.containsKey(queryWord)) {
					pls.clear();
					break;
				}
				PostingList pl = readPosting(fc, termDict.get(queryWord));
				if (pl == null) {
					pls.clear();
					break;
				}
				pls.add(readPosting(fc, termDict.get(queryWord)));
			}
			Collections.sort(pls, new Comparator<PostingList>(){
			    public int compare(PostingList p1, PostingList p2) {
			        return p1.size() - p2.size(); // assumes you want biggest to smallest
			    }
			});
			if (pls.size() == 0) {
				System.out.println("no results found");
			} else {
				List<Integer> current = pls.get(0).getList();
				for (int i = 1; i < pls.size(); i++) {
					List<Integer> toBeAdd = pls.get(i).getList();
					current = intersect(current, toBeAdd);
				}
				if (current.size() == 0) {
					System.out.println("no results found");
				} else {
					System.out.println("Result : "); 
					for (int docId : current) {
						System.out.println(docDict.get(docId));
					}
				}
			}
			System.out.print("Input query :   ");
		}
		System.out.println();
		br.close();
		indexFile.close();
	}
}