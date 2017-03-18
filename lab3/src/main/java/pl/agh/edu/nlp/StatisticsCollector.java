package pl.agh.edu.nlp;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Collections.sort;

public class StatisticsCollector {
	private static final Logger logger = LoggerFactory.getLogger(StatisticsCollector.class);
	private Map<String, Integer> wordToOccurrence = new HashMap<>();
	private List<WordToOccurrence> rankedList = new ArrayList<>();
	private Dictionary defaultFormDictionary;

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

	public void printOutZipfLawGraph() {

	}

	public void printOutMandelbrotGraph() {

	}

	public void countHapaxLegomena() {

	}

	public void printOutOccurenceOfWords() {

	}
}
