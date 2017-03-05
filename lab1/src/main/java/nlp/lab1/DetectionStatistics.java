package nlp.lab1;

import nlp.lab1.ngram.statistics.Language;
import nlp.lab1.ngram.statistics.LanguageDetector;
import nlp.lab1.ngram.statistics.Metrics;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class DetectionStatistics {
	private final int nGrams;
	private final Language desiredLanguage;
	private final AllLanguageStatistics allLanguageStatistics;
	private final Metrics metrics;
	private double precision;
	private double recall;
	private double F1;
	private double accuracy;
	private Collection<String> shouldBeDetected;
	private Collection<String> shouldNotBeDetected;

	public DetectionStatistics(Collection<String> shouldBeDetected, Collection<String> shouldNotBeDetected, AllLanguageStatistics allLanguageStatistics,
			Language desiredLanguage,
			int nGrams, Metrics metrics) {
		this.shouldBeDetected = shouldBeDetected;
		this.shouldNotBeDetected = shouldNotBeDetected;
		this.allLanguageStatistics = allLanguageStatistics;
		this.nGrams = nGrams;
		this.desiredLanguage = desiredLanguage;
		this.metrics = metrics;
		countStatistics();
	}

	@SuppressWarnings("Duplicates")
	private void countStatistics() {
		Set<String> truePositivesSet = new HashSet<>();
		Set<String> falsePositivesSet = new HashSet<>();
		Set<String> falseNegativesSet = new HashSet<>();
		Set<String> trueNegativesSet = new HashSet<>();

		for (String toBeDetected : shouldBeDetected) {
			LanguageDetector languageDetector = new LanguageDetector(allLanguageStatistics, toBeDetected, metrics);
			if (languageDetector.detectLanguageUsingNGrams(nGrams, true).equals(desiredLanguage)) {
				truePositivesSet.add(toBeDetected);
			} else {
				falseNegativesSet.add(toBeDetected);
			}
		}

		for (String toBeDetected : shouldNotBeDetected) {
			LanguageDetector languageDetector = new LanguageDetector(allLanguageStatistics, toBeDetected, metrics);
			if (languageDetector.detectLanguageUsingNGrams(nGrams, true).equals(desiredLanguage)) {
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

		precision = (double) truePositivesSet.size() / (double) unionTruePositivesAndFalsePositives.size();
		recall = (double) truePositivesSet.size() / (double) unionTruePositivesAndFalseNegatives.size();
		F1 = 2 * (precision * recall) / (precision + recall);

		Set<String> allUnion = new HashSet<>();
		allUnion.addAll(truePositivesSet);
		allUnion.addAll(falsePositivesSet);
		allUnion.addAll(trueNegativesSet);
		allUnion.addAll(falseNegativesSet);
		accuracy = (double) truePositivesAndTrueNegativesUnion.size() / (double) allUnion.size();
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

	public void printOutStatistcs() {
		System.out.println("Ngram: " + nGrams);
		System.out.println("Precision: " + precision);
		System.out.println("Recall: " + recall);
		System.out.println("F1: " + F1);
		System.out.println("Accuracy: " + accuracy);
	}
}
