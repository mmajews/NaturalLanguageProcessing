package nlp.lab1;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import de.vandermeer.asciitable.v2.RenderedTable;
import de.vandermeer.asciitable.v2.V2_AsciiTable;
import de.vandermeer.asciitable.v2.render.V2_AsciiTableRenderer;
import de.vandermeer.asciitable.v2.render.WidthLongestLine;
import de.vandermeer.asciitable.v2.themes.V2_E_TableThemes;
import nlp.lab1.ngram.statistics.EuklidesMetrics;
import nlp.lab1.ngram.statistics.FileReader;
import nlp.lab1.ngram.statistics.Language;
import nlp.lab1.ngram.statistics.LanguageConstants;
import nlp.lab1.ngram.statistics.Metrics;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static nlp.lab1.AllLanguageStatistics.getFileFromResourceFolder;

public class StatisticsCreator {
	public static void main(String[] args) {
		AllLanguageStatistics allLanguageStatistics = new AllLanguageStatistics();
		allLanguageStatistics.generateStatistics();
		System.out.println("Generating of statistics for languages finished!");

		long start = System.currentTimeMillis();
		String toBeDetected = "";
		Metrics metrics = new EuklidesMetrics();

		Multimap<Language, String> languageToWord = ArrayListMultimap.create();
		Multimap<Language, String> languageToSentence = ArrayListMultimap.create();
		List<File> englishSourceFiles = Lists.newArrayList(
				new File(getFileFromResourceFolder("english1.txt")),
				new File(getFileFromResourceFolder("english2.txt")),
				new File(getFileFromResourceFolder("english3.txt")),
				new File(getFileFromResourceFolder("english4.txt")));
		List<File> finishSourceFiles = Lists.newArrayList(
				new File(getFileFromResourceFolder("finnish1.txt")),
				new File(getFileFromResourceFolder("finnish2.txt")));
		List<File> germanSourceFiles = Lists.newArrayList(
				new File(getFileFromResourceFolder("german1.txt")),
				new File(getFileFromResourceFolder("german2.txt")),
				new File(getFileFromResourceFolder("german3.txt")),
				new File(getFileFromResourceFolder("german4.txt")));
		List<File> italianSourceFiles = Lists.newArrayList(
				new File(getFileFromResourceFolder("italian1.txt")),
				new File(getFileFromResourceFolder("italian2.txt")));
		List<File> polishSourceFiles = Lists.newArrayList(
				new File(getFileFromResourceFolder("polish1.txt")),
				new File(getFileFromResourceFolder("polish2.txt")),
				new File(getFileFromResourceFolder("polish3.txt")));
		List<File> spanishSourceFiles = Lists.newArrayList(
				new File(getFileFromResourceFolder("spanish1.txt")),
				new File(getFileFromResourceFolder("spanish2.txt")));

		processFilesAndAddWordsToMap(languageToWord, Language.ENGLISH, englishSourceFiles, LanguageConstants.REGEX_WORD_SPLIT);
		processFilesAndAddWordsToMap(languageToWord, Language.FINNISH, finishSourceFiles, LanguageConstants.REGEX_WORD_SPLIT);
		processFilesAndAddWordsToMap(languageToWord, Language.GERMAN, germanSourceFiles, LanguageConstants.REGEX_WORD_SPLIT);
		processFilesAndAddWordsToMap(languageToWord, Language.POLISH, polishSourceFiles, LanguageConstants.REGEX_WORD_SPLIT);
		processFilesAndAddWordsToMap(languageToWord, Language.SPANISH, spanishSourceFiles, LanguageConstants.REGEX_WORD_SPLIT);

		processFilesAndAddWordsToMap(languageToSentence, Language.ENGLISH, englishSourceFiles, LanguageConstants.REGEX_SENTENCE_SPLIT);
		processFilesAndAddWordsToMap(languageToSentence, Language.FINNISH, finishSourceFiles, LanguageConstants.REGEX_SENTENCE_SPLIT);
		processFilesAndAddWordsToMap(languageToSentence, Language.GERMAN, germanSourceFiles, LanguageConstants.REGEX_SENTENCE_SPLIT);
		processFilesAndAddWordsToMap(languageToSentence, Language.POLISH, polishSourceFiles, LanguageConstants.REGEX_SENTENCE_SPLIT);
		processFilesAndAddWordsToMap(languageToSentence, Language.SPANISH, spanishSourceFiles, LanguageConstants.REGEX_SENTENCE_SPLIT);


		DetectionStatistics detectionStatisticsWordEnglish2 = statisticForLanguageWords(allLanguageStatistics, languageToWord, 2);
		DetectionStatistics detectionStatisticsWordEnglish3 = statisticForLanguageWords(allLanguageStatistics, languageToWord, 3);
		DetectionStatistics detectionStatisticsWordEnglish4 = statisticForLanguageWords(allLanguageStatistics, languageToWord, 4);

		DetectionStatistics detectionStatisticsSentenceEnglish2 = statisticForLanguageWords(allLanguageStatistics, languageToSentence, 2);
		DetectionStatistics detectionStatisticsSentenceEnglish3 = statisticForLanguageWords(allLanguageStatistics, languageToSentence, 3);
		DetectionStatistics detectionStatisticsSentenceEnglish4 = statisticForLanguageWords(allLanguageStatistics, languageToSentence, 4);

		renderResults(detectionStatisticsSentenceEnglish2, detectionStatisticsSentenceEnglish3, detectionStatisticsSentenceEnglish4);
		renderResults(detectionStatisticsWordEnglish2, detectionStatisticsWordEnglish3, detectionStatisticsWordEnglish4);

		long time = System.currentTimeMillis() - start;
		System.out.println("Creating statistics took: " + time + " milliseconds");
	}

	private static void renderResults(DetectionStatistics detectionStatistics2, DetectionStatistics detectionStatistics3,
			DetectionStatistics detectionStatistics4) {
		V2_AsciiTable at = new V2_AsciiTable();
		at.addRule();
		at.addRow("NGRAM", "PRECISION", "RECALL", "F1", "ACCURACY");
		at.addRule();
		at.addRow(2, detectionStatistics2.getPrecision(), detectionStatistics2.getRecall(),
				detectionStatistics2.getF1(), detectionStatistics2.getAccuracy());
		at.addRule();
		at.addRow(3, detectionStatistics3.getPrecision(), detectionStatistics3.getRecall(),
				detectionStatistics3.getF1(), detectionStatistics3.getAccuracy());
		at.addRule();
		at.addRow(4, detectionStatistics4.getPrecision(), detectionStatistics4.getRecall(),
				detectionStatistics4.getF1(), detectionStatistics4.getAccuracy());
		at.addRule();
		V2_AsciiTableRenderer rend = new V2_AsciiTableRenderer();
		rend.setTheme(V2_E_TableThemes.UTF_LIGHT.get());
		rend.setWidth(new WidthLongestLine());
		RenderedTable rt = rend.render(at);
		System.out.println(rt);
	}

	private static DetectionStatistics statisticForLanguageWords(AllLanguageStatistics allLanguageStatistics, Multimap<Language, String> languageToWord,
			int ngrams) {
		Collection<String> others = languageToWord.get(Language.FINNISH);
		others.addAll(languageToWord.get(Language.GERMAN));
		others.addAll(languageToWord.get(Language.POLISH));
		others.addAll(languageToWord.get(Language.SPANISH));
		return new DetectionStatistics(languageToWord.get(Language.ENGLISH), others, allLanguageStatistics, Language.ENGLISH, ngrams, new EuklidesMetrics());
	}

	private static Multimap<Language, String> processFilesAndAddWordsToMap(Multimap<Language, String> multimap, Language language, List<File> files,
			String regex) {
		for (File file : files) {
			String content = FileReader.getContentOfFile(file);
			String[] split = content.split(regex);
			multimap.putAll(language, Arrays.asList(split));
		}
		return multimap;
	}

}
