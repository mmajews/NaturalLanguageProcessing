import metrics.Metrics;
import metrics.TwoElements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Element {
    private static final Logger logger = LoggerFactory.getLogger(Element.class);
    private Map<Integer, Double> elToDist = new HashMap<>();
    private String element;
    private int index;

    public Element(String element) {
        this.element = element;
    }

    public String getString() {
        return element;
    }

    public void countDistanceToAll(Map<Integer, String> mapOfElements, Metrics lcsMetrics, AtomicInteger atomicInteger, ConcurrentHashMap<TwoElements, Double> distances) {
        for (Map.Entry<Integer, String> entry : mapOfElements.entrySet()) {
            TwoElements twoElements = new TwoElements(index, entry.getKey());
            if (index == entry.getKey()) {
                continue;
            }
            elToDist.put(entry.getKey(), lcsMetrics.getValue(element, entry.getValue()));
//            if (index != entry.getKey()) {
//                if (!distances.containsKey(twoElements)) {
//                    distances.putIfAbsent(twoElements, lcsMetrics.getValue(element, entry.getValue()));
//                }
//            }

        }
        atomicInteger.incrementAndGet();
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Map<Integer, Double> getElToDist() {
        return elToDist;
    }

    public int getIndex() {
        return index;
    }
}
