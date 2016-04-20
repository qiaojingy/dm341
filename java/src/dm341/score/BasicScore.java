package dm341.score;

import dm341.index.Query;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

import dm341.util.DistanceMeasure;
import dm341.util.Pair;

public class BasicScore {
	static Integer top_n = 3;
	public static PriorityQueue<Pair<String, Double>> getCandidateAndScores(String name) throws IOException {
		List<String> candidates = Query.query_union(Arrays.asList(name.split(" ")));
		if (candidates == null) return null;
		PriorityQueue<Pair<String, Double>> top_candidates = 
				new PriorityQueue<Pair<String, Double>>(top_n, new Comparator<Pair<String, Double>>(){
				    public int compare(Pair<String, Double> p1, Pair<String, Double> p2) {
				        return p1.getSecond().compareTo(p2.getSecond()); // assumes you want biggest to smallest
				    }
				});
		for (String candidate : candidates) {
			Pair<String, Double> cand_score = new Pair<String, Double>(
					candidate, DistanceMeasure.editDistanceScore(name.toLowerCase(),  candidate.split("\t")[0].toLowerCase()));
			if (top_candidates.size() < top_n) {
				top_candidates.add(cand_score);
			}
			else if (top_candidates.comparator().compare(top_candidates.peek(), cand_score) < 0) {
				top_candidates.poll();
				top_candidates.add(cand_score);
			}
		}
		return top_candidates;
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
		
		/* Input file */
		String input_path = data_path + "/FCC/name_url.txt";
		File input = new File(input_path);
		if (!input.exists()) {
			System.err.println("Invalid input path: " + input);
			return;
		}
		
		/* Read input */
		Reader in = new FileReader(input_path);
		BufferedReader in_buff = new BufferedReader(in);
		String line;
		while ((line = in_buff.readLine()) != null) {
			String[] fields = line.split("\t");
			Pair<String, String> name_url = new Pair<String, String>(fields[0], fields[1]);
			PriorityQueue<Pair<String, Double>> top_candidates = getCandidateAndScores(name_url.getFirst());
			if (top_candidates == null) {
				System.out.println("Query term : \t" + name_url.getFirst());
				System.out.println("Url : \t" + name_url.getSecond());
				System.out.println("No result found !");
				continue;
			}
			List<Pair<String, Double>> tops = new ArrayList<Pair<String, Double>>();
			Pair<String, Double> cand_score;
			while ((cand_score = top_candidates.poll()) != null) {
				tops.add(cand_score);
			}
			Collections.reverse(tops);
			if (tops.get(0).getSecond() <= 0.2 || tops.get(0).getSecond() > 0.4) continue;
			System.out.println("Query term : \t" + name_url.getFirst());
			System.out.println("Url : \t" + name_url.getSecond());
			System.out.println("Candidates \t Id \t Scores");
			for (Pair<String, Double> cand_score_top : tops) {
				System.out.println(cand_score_top.getFirst() + " \t " + cand_score_top.getSecond());
			}
			System.out.println();
		}
		in_buff.close();
	}
}
