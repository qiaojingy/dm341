package dm341.index;

import dm341.util.Pair;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.Map;
import java.util.TreeMap;
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
	// Doc name -> doc id dictionary
	private static Map<String, Integer> docDict
		= new TreeMap<String, Integer>();
	// Term -> term id dictionary
	private static Map<String, Integer> termDict
		= new TreeMap<String, Integer>();
	// Block queue
	private static LinkedList<File> blockQueue
		= new LinkedList<File>();

	// Total file counter
	private static int totalFileCount = 0;
	// Document counter
	private static int docIdCounter = 0;
	// Term counter
	private static int wordIdCounter = 0;
	// Index
	private static BaseIndex index = null;

	private static boolean writePos = false;
	
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
		String className = "cs276.assignments." + compression + "Index";
		try {
			Class<?> indexClass = Class.forName(className);
			index = (BaseIndex) indexClass.newInstance();
		} catch (Exception e) {
			System.err
					.println("Index method must be \"Basic\", \"VB\", or \"Gamma\"");
			throw new RuntimeException(e);
		}

		/* Read configuration file */
		String config_path = "configure.txt";
		File config = new File(config_path);
		if (!config.exists()) {
			System.err.println("Cannot find configuration file");
			return;
		}
		
		BufferedReader reader = new BufferedReader(new FileReader(config));
		String data_path = reader.readLine();
		reader.close();
		
		/* Input file */
		String input = data_path + "/FEC/data.csv";
		File rootdir = new File(root);
		if (!rootdir.exists() || !rootdir.isDirectory()) {
			System.err.println("Invalid data directory: " + root);
			return;
		}

		/* Output File */
		String output = args[2];
		File outdir = new File(output);
		if (outdir.exists() && !outdir.isDirectory()) {
			System.err.println("Invalid output directory: " + output);
			return;
		}

		if (!outdir.exists()) {
			if (!outdir.mkdirs()) {
				System.err.println("Create output directory failure");
				return;
			}
		}


		Map<Integer, PostingList> indexList = new HashMap<Integer, PostingList>();
		/* Read FEC data */
		BufferedReader reader = new BufferedReader(new FileReader(input));	
		String line;
			while ((line = reader.readLine()) != null) {
					String[] tokens = line.trim().split("\\s+");
					for (String token : tokens) {
						/*
						 * TODO: Your code here
						 *       For each term, build up a list of
						 *       documents in which the term occurs
						 */
						if (!termDict.containsKey(token))
							termDict.put(token, wordIdCounter++);	
						int wordId = termDict.get(token);
						if (!indexList.containsKey(wordId)) {
							PostingList postingList = new PostingList(wordId);
							postingList.addToList(docIdCounter - 1);
							indexList.put(wordId,  postingList);
						}
						else {
							indexList.get(wordId).addToList(docIdCounter - 1);
						}
					}
				}
				reader.close();
			}

			/* Sort and output */
			if (!blockFile.createNewFile()) {
				System.err.println("Create new block failure.");
				return;
			}
			
			RandomAccessFile bfc = new RandomAccessFile(blockFile, "rw");
			
			/*
			 * TODO: Your code here
			 *       Write all posting lists for all terms to file (bfc) 
			 */
			FileChannel fc = bfc.getChannel();
			List<PostingList> list = new ArrayList<PostingList>(indexList.values());
			Collections.sort(list, new Comparator<PostingList>() {
		        public int compare(PostingList pl1, PostingList pl2)
		        {

		            return  pl1.getTermId() - pl2.getTermId();
		        }
		    });
			for (PostingList entry : list) {
				writePosting(fc, entry);
			}
			bfc.close();
		}

		/* Required: output total number of files. */
		System.out.println(totalFileCount);
		System.out.flush();
		/* Merge blocks */
		while (true) {
			if (blockQueue.size() <= 1)
				break;
			if (blockQueue.size() == 2) writePos = true;
			File b1 = blockQueue.removeFirst();
			File b2 = blockQueue.removeFirst();
			
			File combfile = new File(output, b1.getName() + "+" + b2.getName());
			if (!combfile.createNewFile()) {
				System.err.println("Create new block failure.");
				return;
			}

			System.out.println("Merging " + b1.getName() + " and " + b2.getName() + " into " + combfile.getName());
			RandomAccessFile bf1 = new RandomAccessFile(b1, "r");
			RandomAccessFile bf2 = new RandomAccessFile(b2, "r");
			RandomAccessFile mf = new RandomAccessFile(combfile, "rw");
			 
			/*
			 * TODO: Your code here
			 *       Combine blocks bf1 and bf2 into our combined file, mf
			 *       You will want to consider in what order to merge
			 *       the two blocks (based on term ID, perhaps?).
			 *       
			 */
			FileChannel fc = mf.getChannel();
			FileChannel bfc1 = bf1.getChannel();
			FileChannel bfc2 = bf2.getChannel();
			fc.position(0);
			bfc1.position(0);
			bfc2.position(0);
			PostingList pl1 = index.readPosting(bfc1);
			PostingList pl2 = index.readPosting(bfc2);
			while (pl1 != null && pl2 != null) {
				if (pl1.getTermId() < 100 || pl2.getTermId() < 100) {
					System.out.println("id1, id2: " + pl1.getTermId() + "\t" + pl2.getTermId());
				}
				if (pl1.getTermId() == pl2.getTermId()) {
					if (pl1.getTermId() == 20) System.out.println("Merging termId 20!!!!!!!!!!!!!!!");
					writePosting(fc, pl1.merge(pl2));
					pl1 = index.readPosting(bfc1);
					pl2 = index.readPosting(bfc2);
				} else if (pl1.getTermId() < pl2.getTermId()) {
					writePosting(fc, pl1);
					pl1 = index.readPosting(bfc1);
				} else {
					writePosting(fc, pl2);
					pl2 = index.readPosting(bfc2);
				}
			}
			while (pl1 != null) {
				writePosting(fc, pl1);
				pl1 = index.readPosting(bfc1);
			}
			while (pl2 != null) {
				writePosting(fc, pl2);
				pl2 = index.readPosting(bfc2);				
			}
			
			bf1.close();
			bf2.close();
			mf.close();
			//b1.delete();
			//b2.delete();
			blockQueue.add(combfile);
		}

		/* Dump constructed index back into file system */
		File indexFile = blockQueue.removeFirst();
		indexFile.renameTo(new File(output, "corpus.index"));

		BufferedWriter termWriter = new BufferedWriter(new FileWriter(new File(
				output, "term.dict")));
		for (String term : termDict.keySet()) {
			termWriter.write(term + "\t" + termDict.get(term) + "\n");
		}
		termWriter.close();

		BufferedWriter docWriter = new BufferedWriter(new FileWriter(new File(
				output, "doc.dict")));
		for (String doc : docDict.keySet()) {
			docWriter.write(doc + "\t" + docDict.get(doc) + "\n");
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
