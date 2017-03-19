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
	private List<WordToOccurrence> rankedList = new ArrayList<>();
	private Dictionary defaultFormDictionary;
	private int totalNumberOfWords = 0;

	public StatisticsCollector(File inputFile, Dictionary defaultFormDictionary) {
		this.defaultFormDictionary = defaultFormDictionary;
		initializeWordOccurrence(inputFile);
		createSortedListOfOccurrence();
		logger.info("Initializing of StatisticsCollector finished");
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

		try {
			FileUtils.writeStringToFile(file, stringBuilder.toString());
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

	public void getCumlativePercentageDataForZipfLaw(File outputFile) {
		double sum = 0d;
		if (outputFile.exists()) {
			outputFile.delete();
		}
		try {
			outputFile.createNewFile();
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

	public void printOutMandelbrotGraph() {

	}

	public void countHapaxLegomena() {

	}

	public void printOutOccurenceOfWords() {

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
