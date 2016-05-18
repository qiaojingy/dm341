package dm341.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;

public class NameRecognizer {
	// public final static String serializedClassifier = "classifiers/english.all.3class.caseless.distsim.crf.ser.gz";
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
	
	public static List<String> getNameStringList(String s) throws ClassCastException, ClassNotFoundException, IOException {
		if (classifier == null) classifier = CRFClassifier.getClassifier(serializedClassifier);
		List<List<CoreLabel>> results = classifier.classify(s);
		List<String> nameString = new ArrayList<String>();
		for (List<CoreLabel> sentence : results) {
	        for (CoreLabel word : sentence) {
	            if(word.get(CoreAnnotations.AnswerAnnotation.class).equals("PERSON")) {
	        	    nameString.add(word.toString());
	            }
	        }
	    }
		return nameString;
	}
	
	public static void main(String[] args) throws ClassCastException, ClassNotFoundException, IOException {
		System.out.println(getNameStringList("Hilary for America"));
		System.out.println(getNameStringList("American Crossroads"));
		System.out.println(getNameStringList("CONSERVATIVE LEADERSHIP FOR AZ"));
		System.out.println(getNameStringList("Freedom Partners"));
		System.out.println(getNameStringList("MICHELE REAGAN"));
		System.out.println(getNameStringList("MoveOnOrg"));
		System.out.println(getNameStringList("TOM HORNE AG"));
		System.out.println(getNameStringList("ARIZONAS LEGACY"));
		System.out.println(getNameStringList("Berry"));
		System.out.println(getNameStringList("Coleman"));
		System.out.println(getNameStringList("F ROTELLINI AG"));
		System.out.println(getNameStringList("Kay Hagen"));
		System.out.println(getNameStringList("MO HRCCCHUCK BAYSE"));
		System.out.println(getNameStringList("REPUB ATTY GEN ASSOC"));
	}
}
