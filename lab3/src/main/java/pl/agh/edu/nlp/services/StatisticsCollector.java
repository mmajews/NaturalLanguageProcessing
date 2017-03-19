package pl.agh.edu.nlp.services;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.agh.edu.nlp.model.Dictionary;
import pl.agh.edu.nlp.model.WordToOccurrence;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Collections.sort;

public class StatisticsCollector {
	private static final Logger logger = LoggerFactory.getLogger(StatisticsCollector.class);
	private Map<String, Integer> wordToOccurrence = new HashMap<>();
	private Map<String, Integer> diGramsOccurrence = new HashMap<>();
	private Map<String, Integer> triGramsOccurrence = new HashMap<>();
	private List<WordToOccurrence> rankedList = new ArrayList<>();
	private Dictionary defaultFormDictionary;
	private int totalNumberOfWords = 0;

	public StatisticsCollector(File inputFile, Dictionary defaultFormDictionary) {
		this.defaultFormDictionary = defaultFormDictionary;
		initializeWordOccurrence(inputFile);
		createSortedListOfOccurrence();
		createDiGramsAndTriGramsStatistics(inputFile);
		logger.info("Initializing of StatisticsCollector finished");
	}

	private void createDiGramsAndTriGramsStatistics(File inputFile) {
		String contentOfFile = FileReader.getContentOfFile(inputFile);
		List<String> allWords = getWordsFromString(contentOfFile);
		for (String word : allWords) {
			List<String> diGrams = getNGrams(2, word);
			List<String> triGrams = getNGrams(3, word);

			//fixme can refactor
			for (String diGram : diGrams) {
				if (diGramsOccurrence.containsKey(diGram)) {
					diGramsOccurrence.put(diGram, diGramsOccurrence.get(diGram) + 1);
				} else {
					diGramsOccurrence.put(diGram, 1);
				}
			}

			for (String triGram : triGrams) {
				if (triGramsOccurrence.containsKey(triGram)) {
					triGramsOccurrence.put(triGram, triGramsOccurrence.get(triGram) + 1);
				} else {
					triGramsOccurrence.put(triGram, 1);
				}
			}
		}

		logger.info("Finishing diGrams and triGrams statistics");
	}

	public void getOutputRankingListNGrams(File outputFileDiGrams, File outputFileTrigrams) {
		List<WordToOccurrence> diGramsRankingList;
		List<WordToOccurrence> triGramsRankingList;

		diGramsRankingList = createRankingList(diGramsOccurrence);
		triGramsRankingList = createRankingList(triGramsOccurrence);

		StringBuilder stringBuilder = new StringBuilder();
		for (WordToOccurrence wordToOccurrence : diGramsRankingList) {
			stringBuilder.append(wordToOccurrence.getWord()).append("\t").append(wordToOccurrence.getOccurrence());
			stringBuilder.append(System.lineSeparator());
		}
		String diGramsRanking = stringBuilder.toString();

		stringBuilder = new StringBuilder();
		for (WordToOccurrence wordToOccurrence : triGramsRankingList) {
			stringBuilder.append(wordToOccurrence.getWord()).append("\t").append(wordToOccurrence.getOccurrence());
			stringBuilder.append(System.lineSeparator());
		}
		String triGramsRanking = stringBuilder.toString();

		try {
			FileUtils.writeStringToFile(outputFileDiGrams, diGramsRanking);
			FileUtils.writeStringToFile(outputFileTrigrams, triGramsRanking);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private List<WordToOccurrence> createRankingList(Map<String, Integer> occurrenceMap) {
		return occurrenceMap.entrySet()
				.stream()
				.map(entry -> new WordToOccurrence(entry.getKey(), entry.getValue()))
				.sorted()
				.collect(Collectors.toList());
	}

	private List<String> getNGrams(int n, String word) {
		int length = word.length();
		List<String> nGrams = new ArrayList<>();
		for (int i = 0; i + n < length + 1; i++) {
			String ngram = word.substring(i, i + n);
			nGrams.add(ngram);
		}
		return nGrams;
	}

	public void createFileWithRanking(File file) {
		if (file.exists()) {
			file.delete();
		}

		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				logger.error("Creating new file: {}", file.getAbsolutePath());
			}
		}

		StringBuilder stringBuilder = new StringBuilder();
		for (WordToOccurrence wordToOccurence : rankedList) {
			stringBuilder.append(wordToOccurence.getWord()).append("\t").append(wordToOccurence.getOccurrence());
			stringBuilder.append(System.lineSeparator());
		}

		String content = stringBuilder.toString();

		try {
			FileUtils.writeStringToFile(file, content);
		} catch (IOException e) {
			logger.error("Failed while trying to write string to : {}", file.getAbsolutePath());
		}
	}

	private void createSortedListOfOccurrence() {
		for (Map.Entry<String, Integer> entry : wordToOccurrence.entrySet()) {
			WordToOccurrence wordToOccurrence = new WordToOccurrence(entry.getKey(), entry.getValue());
			rankedList.add(wordToOccurrence);
		}
		sort(rankedList);
	}

	private void initializeWordOccurrence(File file) {
		String contentOfFile = FileReader.getContentOfFile(file);
		List<String> allWords = getWordsFromString(contentOfFile);
		for (String word : allWords) {
			if (!StringUtils.isAlpha(word)) {
				continue;
			}
			totalNumberOfWords += 1;
			word = defaultFormDictionary.getDefaultForm(word);
			if (wordToOccurrence.containsKey(word)) {
				wordToOccurrence.put(word, wordToOccurrence.get(word) + 1);
			} else {
				wordToOccurrence.put(word, 1);
			}
		}
	}

	private List<String> getWordsFromString(String contentOfFile) {
		return Arrays.stream(contentOfFile.split("\\s+"))
				.map(StringHelper::trimCleanUpperCase)
				.collect(Collectors.toList());
	}

	public void getCumulativePercentageDataForZipfLaw(File outputFile) {
		double sum = 0d;
		if (outputFile.exists()) {
			boolean result = outputFile.delete();
			if (!result) {
				logger.error("Cannot delete file: {}", outputFile.getAbsolutePath());
			}
		}
		try {
			boolean result = outputFile.createNewFile();
			if (!result) {
				logger.error("Cannot create file: {}", outputFile.getAbsolutePath());
			}
		} catch (IOException e) {
			logger.error("Cannot create new file : {}", outputFile.getAbsolutePath());
		}

		StringBuilder stringBuilder = new StringBuilder();
		for (WordToOccurrence wordToOccurrence : rankedList) {
			stringBuilder.append(wordToOccurrence.getWord());
			stringBuilder.append("\t");
			final double percentage = (double) wordToOccurrence.getOccurrence() / (double) totalNumberOfWords;
			sum += percentage;
			stringBuilder.append(sum * 100);
			stringBuilder.append(System.lineSeparator());
		}

		try {
			FileUtils.writeStringToFile(outputFile, stringBuilder.toString());
		} catch (IOException e) {
			logger.error("Cannot write string to file: {}", outputFile.getAbsolutePath());
		}
		logger.info("Sum of percentage: {}", sum * 100);
	}

	public Integer getHapaxLegonema() {
		return rankedList.stream()
				.filter(wordToOccur -> wordToOccur.getOccurrence() != 1)
				.collect(Collectors.toList())
				.size();
	}

	public Integer get50PercentageFromTop() {
		ListIterator<WordToOccurrence> li = rankedList.listIterator(rankedList.size());
		double sum = 0d;
		int numberOfWords = 0;
		while (li.hasPrevious()) {
			sum += li.previous().getOccurrence();
			numberOfWords++;
			double percentage = sum / (double) totalNumberOfWords;
			if (percentage >= 0.5) {
				break;
			}
		}
		return numberOfWords;
	}

	public Integer get50PercentageFromBottom() {
		ListIterator<WordToOccurrence> li = rankedList.listIterator(0);
		double sum = 0d;
		int numberOfWords = 0;
		while (li.hasNext()) {
			sum += li.next().getOccurrence();
			numberOfWords++;
			double percentage = sum / (double) totalNumberOfWords;
			if (percentage >= 0.5) {
				break;
			}
		}
		return numberOfWords;
	}
}
