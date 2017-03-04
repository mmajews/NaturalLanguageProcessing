package nlp.lab1;

import nlp.lab1.ngram.statistics.EuklidesMetrics;
import nlp.lab1.ngram.statistics.LanguageDetector;
import nlp.lab1.ngram.statistics.Metrics;

public class Main {
	public static void main(String[] args) {
		AllLanguageStatistics allLanguageStatistics = new AllLanguageStatistics();
		allLanguageStatistics.generateStatistics();

		String toBeDetected = "Sie sahen ihn schon von weitem auf sich zukommen, denn er fiel auf. Er hatte ein ganz altes Gesicht, aber wie er ging, daran sah man, dass er erst zwanzig war. Er setzte sich mit seinem alten Gesicht zu ihnen auf die Bank. Und dann zeigte er ihnen, was er in der Hand trug.\n"
				+ "Das war unsere Küchenuhr, sagte er und sah sie alle der Reihe nach an, die auf der Bank in der Sonne saßen. Ja, ich habe sie noch gefunden. Sie ist übrig geblieben. Er hielt eine runde tellerweiße Küchenuhr vor sich hin";
		System.out.println("Finding language that is good fit for sentence : \"" + toBeDetected + "\"");

		long start = System.currentTimeMillis();

		Metrics metrics = new EuklidesMetrics();
		LanguageDetector languageDetector = new LanguageDetector(allLanguageStatistics, toBeDetected, metrics);
		languageDetector.detectLanguageUsingDiGrams();
		System.out.println();
		languageDetector.detectLanguageUsingTriGrams();

		long time = System.currentTimeMillis() - start;
		System.out.println("Detection took: " + time + " miliseconds");
	}

}
