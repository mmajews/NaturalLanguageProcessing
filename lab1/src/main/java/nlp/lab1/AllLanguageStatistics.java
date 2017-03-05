package nlp.lab1;

import com.google.common.collect.Lists;
import nlp.lab1.ngram.statistics.Language;
import nlp.lab1.ngram.statistics.NgramStatistics;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static nlp.lab1.ngram.statistics.Language.ENGLISH;
import static nlp.lab1.ngram.statistics.Language.FINNISH;
import static nlp.lab1.ngram.statistics.Language.GERMAN;
import static nlp.lab1.ngram.statistics.Language.ITALIAN;
import static nlp.lab1.ngram.statistics.Language.POLISH;
import static nlp.lab1.ngram.statistics.Language.SPANISH;

public class AllLanguageStatistics {

	private Map<Language, NgramStatistics> languageStatistics = new HashMap<>();

	public Map<Language, NgramStatistics> getLanguageStatistics() {
		return languageStatistics;
	}

	void generateStatistics() {
		long start = System.currentTimeMillis();

		NgramStatistics englishStatistics = new NgramStatistics(ENGLISH, 26);
		List<File> englishSourceFiles = Lists.newArrayList(
				new File(getFileFromResourceFolder("english1.txt")),
				new File(getFileFromResourceFolder("english2.txt")),
				new File(getFileFromResourceFolder("english3.txt")),
				new File(getFileFromResourceFolder("english4.txt")));
		englishStatistics.updateNGramWordStatistics(englishSourceFiles);
		languageStatistics.put(ENGLISH, englishStatistics);
		System.out.println("English done!");

		NgramStatistics finishStatistics = new NgramStatistics(FINNISH, 29);
		List<File> finishSourceFiles = Lists.newArrayList(
				new File(getFileFromResourceFolder("finnish1.txt")),
				new File(getFileFromResourceFolder("finnish2.txt")));
		finishStatistics.updateNGramWordStatistics(finishSourceFiles);
		languageStatistics.put(FINNISH, finishStatistics);
		System.out.println("Finnish done!");

		NgramStatistics germanStatistics = new NgramStatistics(GERMAN, 26);
		List<File> germanSourceFiles = Lists.newArrayList(
				new File(getFileFromResourceFolder("german1.txt")),
				new File(getFileFromResourceFolder("german2.txt")),
				new File(getFileFromResourceFolder("german3.txt")),
				new File(getFileFromResourceFolder("german4.txt")));
		languageStatistics.put(GERMAN, germanStatistics);
		germanStatistics.updateNGramWordStatistics(germanSourceFiles);
		System.out.println("German done!");

		NgramStatistics italianStatistics = new NgramStatistics(ITALIAN, 21);
		List<File> italianSourceFiles = Lists.newArrayList(
				new File(getFileFromResourceFolder("italian1.txt")),
				new File(getFileFromResourceFolder("italian2.txt")));
		italianStatistics.updateNGramWordStatistics(italianSourceFiles);
		languageStatistics.put(ITALIAN, italianStatistics);
		System.out.println("Italian done!");

		NgramStatistics polishStatistics = new NgramStatistics(POLISH, 32);
		List<File> polishSourceFiles = Lists.newArrayList(
				new File(getFileFromResourceFolder("polish1.txt")),
				new File(getFileFromResourceFolder("polish2.txt")),
				new File(getFileFromResourceFolder("polish3.txt")));
		polishStatistics.updateNGramWordStatistics(polishSourceFiles);
		languageStatistics.put(POLISH, polishStatistics);
		System.out.println("Polish done!");

		NgramStatistics spanishStatistics = new NgramStatistics(SPANISH, 27);
		List<File> spanishSourceFiles = Lists.newArrayList(
				new File(getFileFromResourceFolder("spanish1.txt")),
				new File(getFileFromResourceFolder("spanish2.txt")));
		spanishStatistics.updateNGramWordStatistics(spanishSourceFiles);
		languageStatistics.put(SPANISH, spanishStatistics);
		System.out.println("Spanish done!");

		long time = System.currentTimeMillis() - start;
		System.out.println("Generating took: " + time / 1000 + " seconds");
	}

	public static String getFileFromResourceFolder(String fileName) {
		return Thread.currentThread().getContextClassLoader().getResource(fileName).getFile();
	}

}
