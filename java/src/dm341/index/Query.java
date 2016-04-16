package cs276.assignments;

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
	    	System.out.println("In intersect, l1[i], l2[j]: " + l1.get(i) + "\t" + l2.get(j));
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
	    System.out.println(answer);
	    return answer;	  
	}

	public static void main(String[] args) throws IOException {
		/* Parse command line */
		if (args.length != 2) {
			System.err.println("Usage: java Query [Basic|VB|Gamma] index_dir");
			return;
		}

		/* Get index */
		String className = "cs276.assignments." + args[0] + "Index";
		try {
			Class<?> indexClass = Class.forName(className);
			index = (BaseIndex) indexClass.newInstance();
		} catch (Exception e) {
			System.err
					.println("Index method must be \"Basic\", \"VB\", or \"Gamma\"");
			throw new RuntimeException(e);
		}

		/* Get index directory */
		String input = args[1];
		File inputdir = new File(input);
		if (!inputdir.exists() || !inputdir.isDirectory()) {
			System.err.println("Invalid index directory: " + input);
			return;
		}

		/* Index file */
		RandomAccessFile indexFile = new RandomAccessFile(new File(input,
				"corpus.index"), "r");

		String line = null;
		/* Term dictionary */
		BufferedReader termReader = new BufferedReader(new FileReader(new File(
				input, "term.dict")));
		while ((line = termReader.readLine()) != null) {
			String[] tokens = line.split("\t");
			termDict.put(tokens[0], Integer.parseInt(tokens[1]));
		}
		termReader.close();

		/* Doc dictionary */
		BufferedReader docReader = new BufferedReader(new FileReader(new File(
				input, "doc.dict")));
		while ((line = docReader.readLine()) != null) {
			String[] tokens = line.split("\t");
			docDict.put(Integer.parseInt(tokens[1]), tokens[0]);
		}
		docReader.close();

		/* Posting dictionary */
		BufferedReader postReader = new BufferedReader(new FileReader(new File(
				input, "posting.dict")));
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
			for (String queryWord:queryWords) {
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
			for (int i = 0; i < 20; i++) System.out.println(docDict.get(pls.get(0).getList().get(i)));
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
					System.out.println("current: " + current);
					System.out.println("toBeAdd: " + toBeAdd);
					current = intersect(current, toBeAdd);
					System.out.println("result: " + current);
				}
				if (current.size() == 0) {
					System.out.println("no results found");
				} else {
					for (int docId : current) {
						System.out.println(docDict.get(docId));
					}
				}
			}
		}
		br.close();
		indexFile.close();
	}
}
