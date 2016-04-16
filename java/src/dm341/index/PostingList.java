package dm341.index;

import java.util.ArrayList;
import java.util.List;

public class PostingList {

	private int termId;
	/* A list of docIDs (i.e. postings) */
	private List<Integer> postings;

	public PostingList(int termId, List<Integer> list) {
		this.termId = termId;
		this.postings = list;
	}

	public PostingList(int termId) {
		this.termId = termId;
		this.postings = new ArrayList<Integer>();
	}
	
	public int getTermId() {
		return this.termId;
	}

	public List<Integer> getList() {
		return this.postings;
	}
	
	public void addToList(int docId) {
		if (!this.postings.contains(docId))
			this.postings.add(docId);
	}
	
	public String toString() {
		String s = Integer.toString(termId).concat("\t");
		boolean first = true;
		for (Integer docId : postings) {
			if (!first) s = s.concat(" ");
			s = s.concat(Integer.toString(docId));
			first = false;
		}
		s = s.concat("\n");
		return s;
	}
	
	public PostingList merge(PostingList other) {
		List<Integer> newPostings = new ArrayList<Integer>();
		List<Integer> otherPostings = other.getList();
		int i = 0, j = 0;
		if (termId == 20) {
			System.out.println("***********");
			System.out.println("list 1: " + postings);
			System.out.println("list 2: " + otherPostings);
		}
		while (i < postings.size() && j < otherPostings.size()) {
			int nextDoc = 0;
			if (postings.get(i) == otherPostings.get(j)) {
				nextDoc = postings.get(i);
				i++;
				j++;
			} else if (postings.get(i) < otherPostings.get(j)) {
				nextDoc = postings.get(i);
				i++;
			} else {
				nextDoc = otherPostings.get(j);
				j++;
			}
			newPostings.add(nextDoc);
		}
		while (i < postings.size()) {
			newPostings.add(postings.get(i++));
		}
		while (j < otherPostings.size()) {
			newPostings.add(otherPostings.get(j++));
		}
		if (termId == 20) {
			System.out.println("***********");
			System.out.println("merged list: " + newPostings);
		}
		postings = newPostings;
		return this;
	}
	
	public int size() {
		return this.postings.size();
	}
}
