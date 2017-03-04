package nlp.lab1.ngram.statistics;

import java.util.Comparator;


public class WordToOccurenceWrapperReverseComparator implements Comparator<WordToOccurrenceWrapper>{
	@Override public int compare(WordToOccurrenceWrapper o1, WordToOccurrenceWrapper o2) {
		double delta = o1.getOccurrenceNumber() - o2.getOccurrenceNumber();
		if (delta < 0)
			return 1;
		if (delta > 0)
			return -1;
		return 0;
	}
}
