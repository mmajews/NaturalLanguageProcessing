package nlp.lab1.ngram.statistics;

import java.util.HashMap;
import java.util.Map;

public class NGramWord {
	private final String word;
	private Map<String, Integer> digrams = new HashMap<>();
	private Map<String, Integer> trigrams = new HashMap<>();

	NGramWord(String word) {
		this.word = word;
		if (word.length() >= 2) {
			createNGrams(2, digrams);
		}
		if (word.length() >= 3) {
			createNGrams(3, trigrams);
		}
	}

	private void createNGrams(int n, Map<String, Integer> mapOfNGrams) {
		int length = word.length();
		//fixme maybe add padding?
		for (int i = 0; i + n < length + 1; i++) {
			String ngram = word.substring(i, i + n);
			if (mapOfNGrams.containsKey(ngram)) {
				mapOfNGrams.put(ngram, mapOfNGrams.get(ngram) + 1);
			} else{
				mapOfNGrams.put(ngram, 1);
			}
		}
	}

	public Map<String, Integer> getDigrams() {
		return digrams;
	}

	public Map<String, Integer> getTrigrams() {
		return trigrams;
	}
}
