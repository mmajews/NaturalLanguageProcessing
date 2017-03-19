package pl.agh.edu.nlp.model;

import java.util.Objects;

public class WordToOccurrence implements Comparable {
	private String word;
	private Integer occurrence;

	public WordToOccurrence(String word, Integer occurrence) {
		this.word = word;
		this.occurrence = occurrence;
	}

	@Override public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		WordToOccurrence that = (WordToOccurrence) o;
		return Objects.equals(word, that.word) &&
				Objects.equals(occurrence, that.occurrence);
	}

	@Override public int hashCode() {
		return Objects.hash(word, occurrence);
	}

	@Override public int compareTo(Object o) {
		if (occurrence < ((WordToOccurrence) o).getOccurrence()) {
			return -1;
		} else if (occurrence > ((WordToOccurrence) o).getOccurrence()) {
			return 1;
		}
		return 0;
	}

	public Integer getOccurrence() {
		return occurrence;
	}

	public String getWord() {
		return word;
	}
}
