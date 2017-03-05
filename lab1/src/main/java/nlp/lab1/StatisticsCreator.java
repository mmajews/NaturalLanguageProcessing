package nlp.lab1;

import nlp.lab1.ngram.statistics.EuklidesMetrics;
import nlp.lab1.ngram.statistics.LanguageDetector;
import nlp.lab1.ngram.statistics.Metrics;

public class StatisticsCreator {
	public static void main(String[] args) {
		AllLanguageStatistics allLanguageStatistics = new AllLanguageStatistics();
		allLanguageStatistics.generateStatistics();

		long start = System.currentTimeMillis();

		String toBeDetected = "";
		Metrics metrics = new EuklidesMetrics();
		LanguageDetector languageDetector = new LanguageDetector(allLanguageStatistics, toBeDetected, metrics);

		languageDetector.detectLanguageUsingNGrams(2);

		long time = System.currentTimeMillis() - start;
		System.out.println("Detection took: " + time + " miliseconds");
	}

}
