package nlp.lab1.ngram.statistics;


class WordToOccurrenceWrapper {
	private String word;
	private Double occurrenceNumber;

	public WordToOccurrenceWrapper(String word, Double occurrenceNumber) {
		this.word = word;
		this.occurrenceNumber = occurrenceNumber;
	}

	public String getWord() {
		return word;
	}

	public Double getOccurrenceNumber() {
		return occurrenceNumber;
	}
}
