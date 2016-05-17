package dm341.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CandidatesList {
	public static String getCandidateState(String candidateName) {
		return getCandidateState(Arrays.asList(candidateName.split(" ")));
	}
	public static String getCandidateState(List<String> candidateNameStringList) {
		return null;
	}
	public static boolean isNationalCandidate(String candidateName) {
		return isNationalCandidate(Arrays.asList(candidateName.split(" ")));
	}
	public static boolean isNationalCandidate(List<String> candidateNameStringList) {
		return true;
	}
}
