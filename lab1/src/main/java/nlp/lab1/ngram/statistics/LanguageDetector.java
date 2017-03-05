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

	public Language detectLanguageUsingNGrams(int n) {
		TreeMap<Double, Language> valueToLanguage = new TreeMap<>();
		NgramStatistics ngramStatisticsForSentence = new NgramStatistics();
		ngramStatisticsForSentence.updateNGramWordStatistics(toBeDetected);
		ngramStatisticsForSentence.processAndSortElements();

		List<Double> firstValues = new ArrayList<>();
		for (Map.Entry<String, Integer> wordToOccurrenceWrapper : ngramStatisticsForSentence.getNGramsSorted(n).entrySet()) {
			firstValues.add((double) wordToOccurrenceWrapper.getValue());
		}

		for (Language language : Language.values()) {
			List<Double> secondValues = new ArrayList<>();
			NgramStatistics languageStatistics = allLanguageStatistics.getLanguageStatistics().get(language);
			Map<String, Integer> nGramsSorted = languageStatistics.getNGramsSorted(2);
			for (Map.Entry<String, Integer> wordToValue : ngramStatisticsForSentence.getNGramsSorted(2).entrySet()) {
				String wordToFind = wordToValue.getKey();
				int i = 0;
				if (!nGramsSorted.containsKey(wordToFind)) {
					secondValues.add(PENALTY);
				} else {
					secondValues.add(Double.valueOf(nGramsSorted.get(wordToFind)));
				}
			}

			double finalValue = metrics.getValue(firstValues, secondValues);
			valueToLanguage.put(finalValue, language);
			System.out.println("Detecting for : " + language + " finished. Value for : " + language + ":" + finalValue);
		}
		final Language detectedLanguage = valueToLanguage.firstEntry().getValue();
		System.out.println("This sentence is written in : " + detectedLanguage);
		return detectedLanguage;
	}

}
