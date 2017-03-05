package nlp.lab1.ngram.statistics;

import java.util.HashMap;
import java.util.Map;

class NGramWord {
	private final String word;
	private Map<Integer, Map<String, Integer>> nGramsMap = new HashMap<>();

	NGramWord(String word) {
		this.word = word;
		createNGrams(2);
		createNGrams(3);
		createNGrams(4);
	}

	private void createNGrams(int n) {
		if (word.length() < n) {
			nGramsMap.put(n, new HashMap<>());
		}
		Map<String, Integer> mapOfNGrams = nGramsMap.get(n);
		if (mapOfNGrams == null) {
			nGramsMap.put(n, new HashMap<>());
		}
		mapOfNGrams = nGramsMap.get(n);

		int length = word.length();
		//fixme maybe add padding? do not know really
		for (int i = 0; i + n < length + 1; i++) {
			String ngram = word.substring(i, i + n);
			if (mapOfNGrams.containsKey(ngram)) {
				mapOfNGrams.put(ngram, mapOfNGrams.get(ngram) + 1);
			} else {
				mapOfNGrams.put(ngram, 1);
			}
		}

		nGramsMap.put(n, mapOfNGrams);
	}

	public Map<String, Integer> getNGramsMap(int n) {
		return nGramsMap.get(n);
	}
}
