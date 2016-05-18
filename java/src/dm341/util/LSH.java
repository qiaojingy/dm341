package dm341.util;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LSH {
	
	private int KGRAM;
	private int NHASHES;
	private int NBANDS;
	private int NBUCKETS;
	
	private List<Set<Integer>> candidates;
	
	private Map<String, Integer> docToId;
	private List<String> idToDoc;
	
	public LSH(int kGram, int numOfHashes, int numOfBands, int numOfBuckets) {
		this.KGRAM = kGram;
		this.NHASHES = numOfHashes;
		this.NBANDS = numOfBands;
		this.NBUCKETS = numOfBuckets;
	}
	
	private Set<String> shingling(String doc) {
		Set<String> shingles = new HashSet<String> ();
		for (int i = 0; i < doc.length() - KGRAM + 1; i++) {
			shingles.add(doc.substring(i, i + KGRAM));
		}
		return shingles;
	}
	
	// given a bitset and a shuffled indices, find the 1st set bit
	private static int firstOnePos(BitSet v, List<Integer> indices) {
		for (int i = 0; i < indices.size(); i++) {
			if (v.get(indices.get(i))) {
				return i;
			}
		}
		return 0;
	}
	
	// given a collection of documents, return the signature matrix
	private List<List<Integer>> minHashing(List<Set<String>> docs) {
		Map<String, Integer> shinglesToInteger = new HashMap<String, Integer> ();
		int docSize = docs.size();
		// convert shingles to numbers
		for (Set<String> doc: docs) {
			for (String shingle: doc) {
				if (!shinglesToInteger.containsKey(shingle)) {
					shinglesToInteger.put(shingle, shinglesToInteger.size());
				}
			}
		}
		// System.out.println("shinglesToInteger: " + shinglesToInteger);
		
		int shingleSize = shinglesToInteger.size();
		// generate input matrix
		List<BitSet> matrix = new ArrayList<BitSet>(docSize); // each element is a bitset represent a document
		for (Set<String> doc: docs) {
			BitSet myVector = new BitSet(shingleSize);
			for (String shingle: doc) {
				myVector.set(shinglesToInteger.get(shingle));
			}
			matrix.add(myVector);
		}
		
		// System.out.println("matrix: " + matrix);
		
		List<Integer> indices = new ArrayList<Integer> (shingleSize);
		for (int i = 0; i < shingleSize; i++) {
			indices.add(i);
		}
		
		List<List<Integer>> signatureMatrix = new ArrayList<List<Integer>>(docSize);
		for (int i = 0; i < docSize; i++) {
			signatureMatrix.add(new ArrayList<Integer>(NHASHES));
		}
		
		for (int i = 0; i < NHASHES; i++) {
			Collections.shuffle(indices);
			// System.out.println("shuffled indices: " + indices);
			for (int j = 0; j < docSize; j++) {
				signatureMatrix.get(j).add(firstOnePos(matrix.get(j), indices));
			}
		}
		// System.out.println("signatureMatrix: " + signatureMatrix);
		return signatureMatrix;
	}
	
	public void lsh(List<String> docs) {
		idToDoc = docs;
		docToId = new HashMap<String, Integer>();
		candidates = new ArrayList<Set<Integer>> (docs.size());
		for (int i = 0; i < docs.size(); i++) {
			candidates.add(new HashSet<Integer>());
		}
		
		List<Set<String>> shingledDocs = new ArrayList<Set<String>> (docs.size());
		for (int i = 0; i < docs.size(); i++) {
			String doc = docs.get(i);
			docToId.put(doc, i);
			shingledDocs.add(shingling(docs.get(i)));
		}
		// System.out.println("idToDoc: " + idToDoc);
		// System.out.println("docToId: " + docToId);
		// System.out.println("shingledDocs: " + shingledDocs);
		
		List<List<Integer>> signatures = minHashing(shingledDocs);
		
		hashSignatures(signatures);
	}
	
	private void hashSignatures(List<List<Integer>> signatures) {
		int NROWS = NHASHES/NBANDS;

		for (int i = 0; i < NBANDS; i++) {
			// bucket id-> set of sigs in bucket
			List<Set<Integer>> buckets = new ArrayList<Set<Integer>> (NBUCKETS);
			for (int temp = 0; temp < NBUCKETS; temp++) {
				buckets.add(new HashSet<Integer>());
			}
			// System.out.println("band: " + i);
			for (int j = 0; j < signatures.size(); j++) {
				List<Integer> num = new ArrayList<Integer>(NROWS);
				//Integer num = 0;
				for (int k = i * NROWS; k < (i+1) * NROWS && k < signatures.get(j).size(); k++) {
					num.add(signatures.get(j).get(k));
					//num = num*10 + signatures.get(j).get(k);
				}
				//System.out.println("num: " + num + "-> hashCode: " + num.hashCode());
				int bucketId = (num.hashCode()) % NBUCKETS;
				buckets.get(bucketId).add(j);
			}
			
			// System.out.println("buckets: " + buckets);
			
			// put same buckets into candidates
			for (Set<Integer> bucket: buckets) {
				for (int myId: bucket) {
					for (int otherId: bucket) {
						if (myId != otherId) {
							candidates.get(myId).add(otherId);
						}
					}
				}
			}
		}
	}
	
	public Set<String> getCandidates(String doc) throws Exception {
		Set<String> cands = new HashSet<String>();
		if (!docToId.containsKey(doc)) {
			throw new Exception("Unseen docoment!");
		}
		int myId = docToId.get(doc);
		for (int otherId: candidates.get(myId)) {
			cands.add(idToDoc.get(otherId));
		}
		return cands;
	}
	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		
		String a = "abcdefg";
		String b = "xyzwv";
		String c = "bcdef";
		String d = "wrtye";
		String e = "poiukl";
		String f = "piukl";
		List<String> docs = new ArrayList<String> ();
		docs.add(a);
		docs.add(b);
		docs.add(c);
		docs.add(d);
		docs.add(e);
		docs.add(f);
		LSH lsher = new LSH(3, 12, 6, 347);
		lsher.lsh(docs);
		System.out.println(a + ":" + lsher.getCandidates(a));
		System.out.println(b + ":" + lsher.getCandidates(b));
		System.out.println(c + ":" + lsher.getCandidates(c));
		System.out.println(d + ":" + lsher.getCandidates(d));
		System.out.println(e + ":" + lsher.getCandidates(e));
		System.out.println(f + ":" + lsher.getCandidates(f));
	}
}
