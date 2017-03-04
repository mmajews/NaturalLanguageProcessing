package nlp.lab1.ngram.statistics;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

public class NgramStatistics {
	private static final String REGEX_WORD_SPLIT = "[,.!? \":\n\r-]+";
	private int numberOfLetterInAlphabet;
	private String nameOfLanguage;
	private List<NGramWord> nGramWordList = new ArrayList<>();
	private Map<String, Integer> diGramsSorted = new HashMap<>();
	private Map<String, Integer> triGramsSorted = new HashMap<>();
	private Map<String, Double> wordToOccurrenceDiGram = new HashMap<>();
	private Map<String, Double> wordToOccurrenceTriGram = new HashMap<>();

	public NgramStatistics(String nameOfLanguage, int numberOfLetterInAlphabet) {
		this.numberOfLetterInAlphabet = numberOfLetterInAlphabet;
		this.nameOfLanguage = nameOfLanguage;
	}

	public NgramStatistics() {
	}

	public void updateNGramWordStatistics(String content) {
		List<String> allWordsInText = splitContentToListOfWords(content);
		for (String word : allWordsInText) {
			nGramWordList.add(new NGramWord(word));
		}
	}

	private List<String> splitContentToListOfWords(String content) {
		String[] slicedContentByWords = content.split(REGEX_WORD_SPLIT);
		return Arrays.asList(slicedContentByWords);
	}

	public void updateNGramWordStatistics(List<File> englishSourceFiles) {
		for (File sourceFile : englishSourceFiles) {
			String contentOfFile = FileReader.getContentOfFile(sourceFile);
			updateNGramWordStatistics(contentOfFile);
		}
		processAndSortElements();
	}

	public void processAndSortElements() {
		for (NGramWord nGramWord : nGramWordList) {
			updateDiGrams(nGramWord);
			updateTriGrams(nGramWord);
		}

		TreeSet<WordToOccurrenceWrapper> diGramsSortedTemp = new TreeSet<>(new WordToOccurenceWrapperReverseComparator());
		TreeSet<WordToOccurrenceWrapper> triGramsSortedTemp = new TreeSet<>(new WordToOccurenceWrapperReverseComparator());
		for (Map.Entry<String, Double> wordToOccurence : wordToOccurrenceDiGram.entrySet()) {
			diGramsSortedTemp.add(new WordToOccurrenceWrapper(wordToOccurence.getKey(), wordToOccurence.getValue()));
		}

		for (Map.Entry<String, Double> wordToOccurence : wordToOccurrenceTriGram.entrySet()) {
			triGramsSortedTemp.add(new WordToOccurrenceWrapper(wordToOccurence.getKey(), wordToOccurence.getValue()));
		}

		int index = 0;
		for (WordToOccurrenceWrapper diGram : diGramsSortedTemp) {
			diGramsSorted.put(diGram.getWord(), index);
			index++;
		}

		index = 0;
		for (WordToOccurrenceWrapper triGram : triGramsSortedTemp) {
			triGramsSorted.put(triGram.getWord(), index);
			index++;
		}
	}

	private void updateDiGrams(NGramWord nGramWord) {
		for (Map.Entry<String, Integer> entry : nGramWord.getDigrams().entrySet()) {
			String digram = entry.getKey();
			if (wordToOccurrenceDiGram.containsKey(digram)) {
				wordToOccurrenceDiGram.put(digram, wordToOccurrenceDiGram.get(digram) + entry.getValue());
			} else {
				wordToOccurrenceDiGram.put(digram, Double.valueOf(entry.getValue()));
			}
		}
	}

	private void updateTriGrams(NGramWord nGramWord) {
		for (Map.Entry<String, Integer> entry : nGramWord.getTrigrams().entrySet()) {
			String trigram = entry.getKey();
			if (wordToOccurrenceTriGram.containsKey(trigram)) {
				wordToOccurrenceTriGram.put(trigram, wordToOccurrenceTriGram.get(trigram) + entry.getValue());
			} else {
				wordToOccurrenceTriGram.put(trigram, Double.valueOf(entry.getValue()));
			}
		}
	}


	public Map<String, Integer> getDiGramsSorted() {
		return diGramsSorted;
	}

	public Map<String, Integer> getTriGramsSorted() {
		return triGramsSorted;
	}
}
