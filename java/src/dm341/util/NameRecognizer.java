package dm341.util;

import java.io.IOException;
import java.util.List;

import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;

public class NameRecognizer {
	public final static String serializedClassifier = "classifiers/english.all.3class.distsim.crf.ser.gz";
	static AbstractSequenceClassifier<CoreLabel> classifier;
	public static boolean containsName(String s) throws ClassCastException, ClassNotFoundException, IOException {
		if (classifier == null) classifier = CRFClassifier.getClassifier(serializedClassifier);
		List<List<CoreLabel>> results = classifier.classify(s);
		for (List<CoreLabel> sentence : results) {
	        for (CoreLabel word : sentence) {
	            if(word.get(CoreAnnotations.AnswerAnnotation.class).equals("PERSON")) {
	        	    return true;
	            }
	        }
	    }
		return false;
	}
	public static void main(String[] args) throws ClassCastException, ClassNotFoundException, IOException {
		System.out.println(containsName("Hilary for America"));
		System.out.println(containsName("American Crossroads"));
		System.out.println(containsName("CONSERVATIVE LEADERSHIP FOR AZ"));
		System.out.println(containsName("Freedom Partners"));
		System.out.println(containsName("MICHELE REAGAN"));
		System.out.println(containsName("MoveOnOrg"));
		System.out.println(containsName("TOM HORNE AG"));
		System.out.println(containsName("ARIZONAS LEGACY"));
		System.out.println(containsName("Berry"));
		System.out.println(containsName("Coleman"));
		System.out.println(containsName("F ROTELLINI AG"));
		System.out.println(containsName("Kay Hagen"));
		System.out.println(containsName("MO HRCCCHUCK BAYSE"));
		System.out.println(containsName("REPUB ATTY GEN ASSOC"));
	}
}
