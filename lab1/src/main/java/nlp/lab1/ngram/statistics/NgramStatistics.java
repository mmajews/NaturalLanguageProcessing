package nlp.lab1.ngram.statistics;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class NgramStatistics {
	private static final String REGEX_WORD_SPLIT = "[,.!? \":\n\r-]+";
	private final int numberOfLetterInAlphabet;
	private final String nameOfLanguage;
	private List<NGramWord> nGramWordList = new ArrayList<>();
	private Map<String, Double> wordToOccurrenceDiGram = new HashMap<>();
	private Map<String, Double> wordToOccurrenceTriGram = new HashMap<>();

	public NgramStatistics(String nameOfLanguage, int numberOfLetterInAlphabet) {
		this.numberOfLetterInAlphabet = numberOfLetterInAlphabet;
		this.nameOfLanguage = nameOfLanguage;
	}

	private void updateNGramWordStatistics(File sourceFile) {
		String contentOfFile = FileReader.getContentOfFile(sourceFile);
		List<String> allWordsInText = splitContentToListOfWords(contentOfFile);
		for (String word : allWordsInText) {
			nGramWordList.add(new NGramWord(word));
		}
	}

	private List<String> splitContentToListOfWords(String content) {
		String[] slicedContentByWords = content.split(REGEX_WORD_SPLIT);
		return Arrays.asList(slicedContentByWords);
	}

	public void updateNGramWordStatistics(List<File> englishSourceFiles) {
		for (File englishSourceFile : englishSourceFiles) {
			updateNGramWordStatistics(englishSourceFile);
		}

		for (NGramWord nGramWord : nGramWordList) {
			updateDiGrams(nGramWord);
			updateTriGrams(nGramWord);
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

}
