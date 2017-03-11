package metrics;


import com.google.common.base.Objects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TwoElements {
    private List<Integer> elements = new ArrayList<>();

    public TwoElements(int firstElementIndex, int secondElementIndex) {
        Integer[] table = new Integer[2];
        table[0] = firstElementIndex;
        table[1] = secondElementIndex;
        Arrays.sort(table);
        elements = Arrays.asList(table);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TwoElements that = (TwoElements) o;
        return Objects.equal(elements, that.elements);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(elements);
    }

    public Integer getFirst() {
        return elements.get(0);
    }

    public Integer getSecond() {
        return elements.get(1);
    }
}
