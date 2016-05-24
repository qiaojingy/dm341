package dm341.util;

import java.util.*;
import java.lang.*;
/*for all algorithms, we regulate that for scores, the lower the better(two strings are more similar) */
public class DistanceMeasure {
	public static double editDistanceScore(String s1, String s2) {
		return 1 - editSimilarity(s1, s2);
	}
	
	public static double editSimilarity(String s1, String s2) {
		if (s1.isEmpty() && s2.isEmpty()) return 1;
		if (s1.isEmpty() || s2.isEmpty()) return 0;
		
		int[][] minDis = new int[s1.length()][s2.length()];
		for (int i = 0; i < s1.length(); i++) {
			for (int j = 0; j < s2.length(); j++) {
				minDis[i][j] = -1;
			}
		}
		int editDistance = editDistance(s1, s1.length()-1, s2, s2.length()-1, minDis);
		int maxLen = Math.max(s1.length(), s2.length());
		return (double)editDistance/maxLen;
	}
	
	private static int editDistance(String s1, int m, String s2, int n, int[][] minDis) {
		if (m == -1 || n == -1) return m == -1? n+1: m+1;
		if (minDis[m][n] != -1) return minDis[m][n];
		if (s1.charAt(m) == s2.charAt(n)) {
			minDis[m][n] = editDistance(s1, m-1, s2, n-1, minDis);
		} else {
			minDis[m][n] = 1 + Math.min(editDistance(s1, m-1, s2, n, minDis), Math.min(editDistance(s1, m, s2, n-1, minDis), editDistance(s1, m-1, s2, n-1, minDis)));
		}
		return minDis[m][n];
	}
	
	/* JaccardSimilarity */
	public static double JaccardSimilarity(Integer kGram, String s1, String s2){
		if (s1.isEmpty() || s2.isEmpty()) return 0;
		if(s1.length()<kGram || s2.length()<kGram) return 0;
		Set<String> set1 = new HashSet<String>();
		Set<String> set2 = new HashSet<String>();
		for(int i =0;i<=s1.length()-kGram;i++){
			set1.add(s1.substring(i,i+kGram));
		}
		for(int i =0;i<=s2.length()-kGram;i++){
			set2.add(s2.substring(i,i+kGram));
		}
		Set<String> union= new HashSet<String>(set1);
		union.addAll(set2);
		Set<String> intersection = new HashSet<String>(set1);
		intersection.retainAll(set2);
		double jaccardSim = intersection.size()*1.0/union.size();
		return jaccardSim;
	}
	
	

	/* Overlap coefficient
		https://en.wikipedia.org/wiki/Overlap_coefficient*/
	public static double overlapCoeffSimilarity(Integer kGram, String s1, String s2){
		return 1- overlapCoeff(kGram, s1,s2);
	}


	private static double overlapCoeff(Integer kGram, String s1, String s2){
		if (s1.isEmpty() || s2.isEmpty()) return 0;
		if(s1.length()<kGram || s2.length()<kGram) return 0;
		Set<String> set1 = new HashSet<String>();
		Set<String> set2 = new HashSet<String>();
		for(int i =0;i<=s1.length()-kGram;i++){
			set1.add(s1.substring(i,i+kGram));
		}
		for(int i =0;i<=s2.length()-kGram;i++){
			set2.add(s2.substring(i,i+kGram));
		}
		Set<String> intersection = new HashSet<String>(set1);
		intersection.retainAll(set2);
		double overlapCoefficient = intersection.size()*1.0/Math.min(s1.length(),s2.length());
		return overlapCoefficient;
	}


	/* Jaro–Winkler distance
	   see https://en.wikipedia.org/wiki/Jaro-Winkler_distance */

	public static double jaroWinklerSimilarityScore(String s1, String s2){
		return 1- jaroWinklerDistanceScore(s1,s2);
	}

	public static double jaroWinklerSimilarityScore(double threshold, String s1, String s2){
		return 1- jaroWinklerDistanceScore(threshold, s1,s2);
	}

	// comparing to Jaro–Winkler, focus less on matching of prefix
	public static double jaroSimilarityScore(String s1, String s2){
		return 1- jaroDistanceScore(s1, s2);
	}

	

	public static double jaroWinklerDistanceScore(String s1, String s2){
		return jaroWinklerDistanceScore(0.7, s1,s2);
	}

	private static double jaroWinklerDistanceScore(double threshold, String s1, String s2){
		int prefix = 0;
		double scalingFactor = 0.1;
		for(int i =0;i<Math.min(s1.length(),s2.length());i++){
			if(s1.charAt(i) == s2.charAt(i)){
				prefix +=1;
			}else {
				break;
			}
		}
		//length of common prefix at the start of the string up to a maximum of 4 characters
		prefix = Math.min(4,prefix);
		double jaroDistance = jaroDistanceScore(s1,s2);
		if (jaroDistance < threshold){
			return jaroDistance;
		}else {
			return jaroDistance + prefix*scalingFactor*(1-jaroDistance);
		}
	}


	private static double jaroDistanceScore(String s1, String s2){
		int matchThreshold = Math.min(s1.length(),s2.length())/2-1;
		int match = 0;
		//boolean[] s1Matched = new boolean[s1.length()];
		char[] s1Compare = new char[s1.length()];
		boolean[] s2Matched = new boolean[s2.length()];
		int[] s2MatchedIndex = new int[s2.length()];
		for (int i =0;i<s2.length();i++){
			s2Matched[i] = false;
		}
		for(int i =0;i<s1.length();i++){
			char cur = s1.charAt(i);
			int start = Math.max(0,i-matchThreshold);
			if(start >= s2.length()) break;
			int end = Math.min(s2.length()-1,i+matchThreshold);
			for(int j = start;j<=end;j++){
				if((s2.charAt(j) == cur) && (s2Matched[j] == false)){
					//s1Matched[i] = true;
					s1Compare[match] = cur;
					s2Matched[j] = true;
					//s2MatchedIndex[match] = j;
					match +=1;
					//System.out.println("char is " + cur);
					//System.out.println("current match is " + Integer.toString(match));
					break;
				}
			}
		}
		//compute transposition
		char[] s2Compare = new char[match];
		for(int i =0,curIndex = 0;i<s2.length();i++){
			if(s2Matched[i] == true){
				s2Compare[curIndex] = s2.charAt(i);
				curIndex++;
			}
		}
		int transposition = 0;
		for(int i =0;i<match;i++){
			//System.out.println("s1Compare" + Integer.toString(i)+" "+s1Compare[i]);
			//System.out.println("s2Compare" + Integer.toString(i)+" "+s1Compare[2]);
			if(s1Compare[i] != s2Compare[i]){
				transposition +=1;
			}
		}
		int t = transposition/2;
		//System.out.println("t is " + Integer.toString(t));
		double jaroDistance = (match*1.0/s1.length()+match*1.0/s2.length()+(match-t)*1.0/match)/3.0;
		return jaroDistance;
	}


	public static void main(String[] args) {
		// TODO Auto-generated method stub
		double aaa = jaroDistanceScore("americans for prosperity","americans for fair skies");
		double bbb = jaroDistanceScore("americans for prosperity","afp americans for prosperity");
		System.out.println(aaa);
		System.out.println(bbb);
	}

}
