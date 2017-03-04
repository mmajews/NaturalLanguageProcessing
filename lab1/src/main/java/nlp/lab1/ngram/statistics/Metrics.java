package nlp.lab1.ngram.statistics;

import java.util.List;

public interface Metrics {
	double getValue(List<Double> firstValues, List<Double> secondValues);
}
