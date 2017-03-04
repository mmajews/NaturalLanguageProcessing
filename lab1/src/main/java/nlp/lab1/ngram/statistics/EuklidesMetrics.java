package nlp.lab1.ngram.statistics;

import java.util.List;

public class EuklidesMetrics implements Metrics {

	@Override
	public double getValue(List<Double> firstValues, List<Double> secondValues) {
		double value = 0;
		int index = 0;
		while (index < firstValues.size()) {
			value += Math.pow(firstValues.get(index) - secondValues.get(index), 2d);
			index++;
		}
		return value;
	}
}
