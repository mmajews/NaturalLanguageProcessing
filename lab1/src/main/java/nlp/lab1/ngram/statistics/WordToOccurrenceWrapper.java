package nlp.lab1.ngram.statistics;

import com.google.common.base.Objects;

class WordToOccurrenceWrapper {
	private String word;
	private Double occurrenceNumber;
	private int index;

	public WordToOccurrenceWrapper(String word, Double occurrenceNumber) {
		this.word = word;
		this.occurrenceNumber = occurrenceNumber;
	}

	public WordToOccurrenceWrapper(String diGram, Double occurrenceNumber, int index) {
		this.word = diGram;
		this.occurrenceNumber = occurrenceNumber;
		this.index = index;
	}

	public String getWord() {
		return word;
	}

	public Double getOccurrenceNumber() {
		return occurrenceNumber;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	@Override public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		WordToOccurrenceWrapper that = (WordToOccurrenceWrapper) o;
		return Objects.equal(word, that.word) &&
				Objects.equal(occurrenceNumber, that.occurrenceNumber);
	}

	@Override public int hashCode() {
		return Objects.hashCode(word, occurrenceNumber);
	}
}
