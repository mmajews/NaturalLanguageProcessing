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
	private Language nameOfLanguage;
	private List<NGramWord> nGramWordList = new ArrayList<>();

	private Map<Integer, Map<String, Integer>> nGramsSorted = new HashMap<>();
	private Map<String, Integer> diGramsSorted = new HashMap<>();
	private Map<String, Integer> triGramsSorted = new HashMap<>();
	private Map<String, Integer> fourGramsSorted = new HashMap<>();

	private Map<String, Double> wordToOccurrenceDiGram = new HashMap<>();
	private Map<String, Double> wordToOccurrenceTriGram = new HashMap<>();
	private Map<String, Double> wordToOccurrenceFourGram = new HashMap<>();

	public NgramStatistics(Language language, int numberOfLetterInAlphabet) {
		this.numberOfLetterInAlphabet = numberOfLetterInAlphabet;
		this.nameOfLanguage = language;
	}

	NgramStatistics() {
	}

	void updateNGramWordStatistics(String content) {
		List<String> allWordsInText = splitContentToListOfWords(content);
		for (String word : allWordsInText) {
			nGramWordList.add(new NGramWord(word));
		}
	}

	private List<String> splitContentToListOfWords(String content) {
		String[] slicedContentByWords = content.split(REGEX_WORD_SPLIT);
		return Arrays.asList(slicedContentByWords);
	}

	public void updateNGramWordStatistics(List<File> sourceFiles) {
		for (File sourceFile : sourceFiles) {
			String contentOfFile = FileReader.getContentOfFile(sourceFile);
			updateNGramWordStatistics(contentOfFile);
		}
		processAndSortElements();
	}

	private void createSortedSetAndAddToMapWithIndex(Map<String, Double> wordsToOccurrence, Map<String, Integer> sorted) {
		TreeSet<WordToOccurrenceWrapper> nGramsSorted = new TreeSet<>(new WordToOccurenceWrapperReverseComparator());
		for (Map.Entry<String, Double> wordToOccurrence : wordsToOccurrence.entrySet()) {
			nGramsSorted.add(new WordToOccurrenceWrapper(wordToOccurrence.getKey(), wordToOccurrence.getValue()));
		}
		int index = 0;
		for (WordToOccurrenceWrapper diGram : nGramsSorted) {
			sorted.put(diGram.getWord(), index);
			index++;
		}
	}

	void processAndSortElements() {
		for (NGramWord nGramWord : nGramWordList) {
			updateNGrams(nGramWord, wordToOccurrenceDiGram, 2);
			updateNGrams(nGramWord, wordToOccurrenceTriGram, 3);
			updateNGrams(nGramWord, wordToOccurrenceFourGram, 4);
		}

		createSortedSetAndAddToMapWithIndex(wordToOccurrenceDiGram, diGramsSorted);
		createSortedSetAndAddToMapWithIndex(wordToOccurrenceTriGram, triGramsSorted);
		createSortedSetAndAddToMapWithIndex(wordToOccurrenceFourGram, fourGramsSorted);

		nGramsSorted.put(2, diGramsSorted);
		nGramsSorted.put(3, triGramsSorted);
		nGramsSorted.put(4, fourGramsSorted);
	}

	private void updateNGrams(NGramWord nGramWord, Map<String, Double> wordToOccurrence, int nGram) {
		for (Map.Entry<String, Integer> entry : nGramWord.getNGramsMap(nGram).entrySet()) {
			String currNGramWord = entry.getKey();
			if (wordToOccurrence.containsKey(currNGramWord)) {
				wordToOccurrence.put(currNGramWord, wordToOccurrence.get(currNGramWord) + entry.getValue());
			} else {
				wordToOccurrence.put(currNGramWord, Double.valueOf(entry.getValue()));
			}
		}
	}

	Map<String, Integer> getNGramsSorted(int n) {
		return nGramsSorted.get(n);
	}

}
