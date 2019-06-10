package ru.otus;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static java.util.Collections.addAll;
import static java.util.Collections.reverseOrder;
import static java.util.stream.IntStream.range;

class DIYArrayListTest implements WithAssertions {

    private List<Integer> list;

    @BeforeEach
    void setUp() {
        list = new DIYArrayList<>();
        range(0, 20).forEach(number -> list.add(number));
    }

    @Test
    void shouldPassForCollectionsAddAll() {
        final List<Integer> values = Arrays.asList(20, 21, 22);
        System.out.println("Initial list: " + list);

        addAll(list, values.toArray(new Integer[0]));

        assertThat(list).containsAll(values);
        System.out.println("Result after Collections.addAll: " + list);
    }

    @Test
    void shouldPassForCollectionsCopy() {
        final List<Integer> target = new DIYArrayList<>(20);
        range(20, 40).forEach(target::add);
        System.out.println("Initial list: " + target);

        Collections.copy(target, list);

        assertThat(target).containsExactly(range(0, 20).boxed().toArray(Integer[]::new));
        System.out.println("Result after Collections.copy: " + target);
    }

    @Test
    @SuppressWarnings("Java8ListSort")
    void shouldPassForCollectionsSort() {
        final List<Integer> forSorting = new DIYArrayList<>();
        range(0, 20).boxed().sorted(reverseOrder()).forEach(forSorting::add);
        System.out.println("Unsorted list: " + forSorting);

        Collections.sort(forSorting, testComparator());

        assertThat(forSorting).containsExactly(range(0, 20).boxed().sorted(testComparator()).toArray(Integer[]::new));
        System.out.println("Result after Collections.sort: " + forSorting);
    }

    private Comparator<Integer> testComparator() {
        return (o1, o2) -> {
            final boolean firstEven = o1 % 2 == 0;
            final boolean secondEven  = o2 % 2 == 0;
            if (firstEven && !secondEven) {
                return 1;
            } else if (!firstEven && secondEven) {
                return -1;
            } else {
                return Comparator.<Integer>naturalOrder().compare(o1, o2);
            }
        };
    }
}
