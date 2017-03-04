package nlp.lab1.ngram.statistics;

import nlp.lab1.AllLanguageStatistics;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class LanguageDetector {
	private Double PENALTY = 100d;
	private AllLanguageStatistics allLanguageStatistics;
	private String toBeDetected;
	private Metrics metrics;

	public LanguageDetector(AllLanguageStatistics allLanguageStatistics, String toBeDetected, Metrics metrics) {
		this.allLanguageStatistics = allLanguageStatistics;
		this.toBeDetected = toBeDetected;
		this.metrics = metrics;
	}

	public void detectLanguageUsingDiGrams() {
		TreeMap<Double, String> valueToLanguage = new TreeMap<>();
		NgramStatistics ngramStatisticsForSentence = new NgramStatistics();
		ngramStatisticsForSentence.updateNGramWordStatistics(toBeDetected);
		ngramStatisticsForSentence.processAndSortElements();

		List<Double> firstValues = new ArrayList<>();
		for (Map.Entry<String, Integer> wordToOccurrenceWrapper : ngramStatisticsForSentence.getDiGramsSorted().entrySet()) {
			firstValues.add((double) wordToOccurrenceWrapper.getValue());
		}

		for (String language : LanguageConstants.allLanguages) {
			List<Double> secondValues = new ArrayList<>();
			NgramStatistics languageStatistics = allLanguageStatistics.getLanguageStatistics().get(language);
			Map<String, Integer> diGramsSorted = languageStatistics.getDiGramsSorted();
			for (Map.Entry<String, Integer> wordToValue : ngramStatisticsForSentence.getDiGramsSorted().entrySet()) {
				String wordToFind = wordToValue.getKey();
				int i = 0;
				if (!diGramsSorted.containsKey(wordToFind)) {
					secondValues.add(PENALTY);
				} else {
					secondValues.add(Double.valueOf(diGramsSorted.get(wordToFind)));
				}
			}

			double finalValue = metrics.getValue(firstValues, secondValues);
			valueToLanguage.put(finalValue, language);
			System.out.println("Detecting for : " + language + " finished. Value for : " + language + ":" + finalValue);
		}
		System.out.println("This sentence is written in : " + valueToLanguage.firstEntry().getValue());
	}


	public void detectLanguageUsingTriGrams() {
		TreeMap<Double, String> valueToLanguage = new TreeMap<>();
		NgramStatistics ngramStatisticsForSentence = new NgramStatistics();
		ngramStatisticsForSentence.updateNGramWordStatistics(toBeDetected);
		ngramStatisticsForSentence.processAndSortElements();

		List<Double> firstValues = new ArrayList<>();
		for (Map.Entry<String, Integer> wordToOccurrenceWrapper : ngramStatisticsForSentence.getTriGramsSorted().entrySet()) {
			firstValues.add((double) wordToOccurrenceWrapper.getValue());
		}

		for (String language : LanguageConstants.allLanguages) {
			List<Double> secondValues = new ArrayList<>();
			NgramStatistics languageStatistics = allLanguageStatistics.getLanguageStatistics().get(language);
			Map<String, Integer> diGramsSorted = languageStatistics.getTriGramsSorted();
			for (Map.Entry<String, Integer> wordToValue : ngramStatisticsForSentence.getTriGramsSorted().entrySet()) {
				String wordToFind = wordToValue.getKey();
				int i = 0;
				if (!diGramsSorted.containsKey(wordToFind)) {
					secondValues.add(PENALTY);
				} else {
					secondValues.add(Double.valueOf(diGramsSorted.get(wordToFind)));
				}
			}

			double finalValue = metrics.getValue(firstValues, secondValues);
			valueToLanguage.put(finalValue, language);
			System.out.println("Detecting for : " + language + " finished. Value for : " + language + ":" + finalValue);
		}
		System.out.println("This sentence is written in : " + valueToLanguage.firstEntry().getValue());
	}
}
