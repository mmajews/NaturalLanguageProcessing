package nlp.lab1;

import nlp.lab1.ngram.statistics.EuklidesMetrics;
import nlp.lab1.ngram.statistics.LanguageDetector;
import nlp.lab1.ngram.statistics.Metrics;

public class DetectionOfSentenceLanguage {
	public static void main(String[] args) {
		AllLanguageStatistics allLanguageStatistics = new AllLanguageStatistics();
		allLanguageStatistics.generateStatistics();

		String toBeDetected = "Terytorium Polski zostalo podzielone pomiedzy III Rzesze a Zwiazek Radziecki na mocy paktu pomiedzy tymi krajami. Tereny zajete przez Armie Czerwona wcielone zostaly do ZSRR, czesc terenow okupowanych przez Niemcy wlaczona zostala do III Rzeszy, na czesci utworzono tzw. Generalne Gubernatorstwo. Rownoczesnie z wprowadzeniem wladzy okupacyjnej rozpoczely sie represje wymierzone w spolecze�stwo polskie. Na terenach okupowanych przez Niemcy i ZSRR prowadzono masowe aresztowania i egzekucje osob pelniacych wczesniej funkcje pa�stwowe lub zaangazowanych w ruch oporu. Szczegolny rodzaj przesladowa� wymierzony zostal w zydowska ludnosc Polski � osoby pochodzenia zydowskiego zmuszone zostaly do zamieszkania w zamknietych dzielnicach � gettach, stopniowo odebrane zostaly im wszelkie prawa. Od 1941 roku w niemieckich obozach zaglady zginelo okolo 90% zydow bedacych obywatelami Polski. Hitlerowcy prowadzili rowniez masowe wysiedlenia ludnosci polskiej, w wyniku ktorych wiele osob pozbawionych zostalo miejsca zamieszkania lub zmuszonych do przymusowej pracy na terenie Niemiec.\n";
		System.out.println("Finding language that is good fit for sentence : \"" + toBeDetected + "\"");

		long start = System.currentTimeMillis();

		Metrics metrics = new EuklidesMetrics();
		LanguageDetector languageDetector = new LanguageDetector(allLanguageStatistics, toBeDetected, metrics);
		languageDetector.detectLanguageUsingNGrams(4, false);

		long time = System.currentTimeMillis() - start;
		System.out.println("Detection took: " + time + " miliseconds");
	}

}
