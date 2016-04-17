package dm341.util;

public class DistanceMeasure {

	public static double editDistanceScore(String s1, String s2) {
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
		return 1 - (double)editDistance/maxLen;
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
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
