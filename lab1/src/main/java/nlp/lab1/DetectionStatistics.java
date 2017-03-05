package nlp.lab1;

import nlp.lab1.ngram.statistics.Language;
import nlp.lab1.ngram.statistics.LanguageDetector;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DetectionStatistics {
	private final int nGrams;
	private final Language desiredLanguage;
	private double precision;
	private double recall;
	private double F1;
	private double accuracy;
	private List<String> shouldBeDetected;
	private List<String> shouldNotBeDetected;
	private LanguageDetector languageDetector;

	public DetectionStatistics(List<String> shouldBeDetected, List<String> shouldNotBeDetected, LanguageDetector languageDetector, Language desiredLanguage,
			int nGrams) {
		this.shouldBeDetected = shouldBeDetected;
		this.shouldNotBeDetected = shouldNotBeDetected;
		this.languageDetector = languageDetector;
		this.nGrams = nGrams;
		this.desiredLanguage = desiredLanguage;
		countStatistics();
	}

	private void countStatistics() {
		Set<String> truePositivesSet = new HashSet<>();
		Set<String> falsePositivesSet = new HashSet<>();
		Set<String> falseNegativesSet = new HashSet<>();
		Set<String> trueNegativesSet = new HashSet<>();

		for (String toBeDetected : shouldBeDetected) {
			if (languageDetector.detectLanguageUsingNGrams(nGrams) == desiredLanguage) {
				truePositivesSet.add(toBeDetected);
			} else {
				falseNegativesSet.add(toBeDetected);
			}
		}

		for (String toBeDetected : shouldNotBeDetected) {
			if (languageDetector.detectLanguageUsingNGrams(nGrams) == desiredLanguage) {
				falsePositivesSet.add(toBeDetected);
			} else {
				trueNegativesSet.add(toBeDetected);
			}
		}

		Set<String> unionTruePositivesAndFalsePositives = new HashSet<>();
		unionTruePositivesAndFalsePositives.addAll(truePositivesSet);
		unionTruePositivesAndFalsePositives.addAll(falsePositivesSet);

		Set<String> unionTruePositivesAndFalseNegatives = new HashSet<>();
		unionTruePositivesAndFalseNegatives.addAll(truePositivesSet);
		unionTruePositivesAndFalseNegatives.addAll(falseNegativesSet);

		Set<String> truePositivesAndTrueNegativesUnion = new HashSet<>();
		truePositivesAndTrueNegativesUnion.addAll(truePositivesSet);
		truePositivesAndTrueNegativesUnion.addAll(trueNegativesSet);

		precision = truePositivesSet.size() / unionTruePositivesAndFalsePositives.size();
		recall = truePositivesSet.size() / unionTruePositivesAndFalseNegatives.size();
		F1 = 2 * (precision * recall) / (precision + recall);

		Set<String> allUnion = new HashSet<>();
		allUnion.addAll(truePositivesSet);
		allUnion.addAll(falsePositivesSet);
		allUnion.addAll(trueNegativesSet);
		allUnion.addAll(falseNegativesSet);
		accuracy = truePositivesAndTrueNegativesUnion.size() / allUnion.size();
	}

	public double getPrecision() {
		return precision;
	}

	public double getRecall() {
		return recall;
	}

	public double getF1() {
		return F1;
	}

	public double getAccuracy() {
		return accuracy;
	}
}
