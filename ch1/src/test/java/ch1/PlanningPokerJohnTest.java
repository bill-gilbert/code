package ch1;

import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.Combinators;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


class PlanningPokerJohnTest {

    @Test
    void rejectNullInput() {
        assertThatThrownBy(() -> new PlanningPokerByJohn().identifyExtremes(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rejectEmptyList() {
        assertThatThrownBy(() -> {
            List<Estimate> emptyList = Collections.emptyList();
            new PlanningPokerByJohn().identifyExtremes(emptyList);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rejectSingleEstimate() {
        assertThatThrownBy(() -> {
            List<Estimate> list = Collections.singletonList(new Estimate("Eleanor", 1));
            new PlanningPokerByJohn().identifyExtremes(list);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void twoEstimates() {
        List<Estimate> list = Arrays.asList(
                new Estimate("Mauricio", 10),
                new Estimate("Frank", 5)
        );

        List<String> devs = new PlanningPokerByJohn().identifyExtremes(list);

        assertThat(devs)
                .containsExactlyInAnyOrder("Mauricio", "Frank");
    }

    // this test was later deleted by Eleanor, as the property based testing
    // replaces this one.
    @Test
    void manyEstimates() {
        List<Estimate> list = Arrays.asList(
                new Estimate("Mauricio", 10),
                new Estimate("Arie", 5),
                new Estimate("Frank", 7)
        );

        List<String> devs = new PlanningPoker().identifyExtremes(list);

        assertThat(devs)
                .containsExactlyInAnyOrder("Mauricio", "Arie");
    }

    @Property
    void estimatesInAnyOrder(@ForAll("estimates") List<Estimate> estimates) {

        estimates.add(new Estimate("MrLowEstimate", 1));
        estimates.add(new Estimate("MsHighEstimate", 100));
        Collections.shuffle(estimates);

        List<String> dev = new PlanningPokerByJohn().identifyExtremes(estimates);

        assertThat(dev)
                .containsExactlyInAnyOrder("MrLowEstimate", "MsHighEstimate");
    }

    @Provide
    Arbitrary<List<Estimate>> estimates() {
        Arbitrary<String> names = Arbitraries.strings().withCharRange('a', 'z').ofLength(5);
        Arbitrary<Integer> values = Arbitraries.integers().between(2, 99);

        Arbitrary<Estimate> estimates = Combinators.combine(names, values)
                .as((name, value) -> new Estimate(name, value));

        return estimates.list().ofMinSize(1);
    }

    @Test
    void developersWithSameEstimates() {
        List<Estimate> list = Arrays.asList(
                new Estimate("Mauricio", 10),
                new Estimate("Arie", 5),
                new Estimate("Andy", 10),
                new Estimate("Frank", 7),
                new Estimate("Annibale", 5)
        );

        List<String> devs = new PlanningPokerByJohn().identifyExtremes(list);

        assertThat(devs)
                .containsExactlyInAnyOrder("Mauricio", "Arie");
    }

    @Test
    void developerAscOrder() {
        List<Estimate> list = Arrays.asList(
                new Estimate("Mauricio", 1),
                new Estimate("Arie", 2),
                new Estimate("Andy", 3),
                new Estimate("Frank", 4),
                new Estimate("Annibale", 5)
        );

        List<String> devs = new PlanningPokerByJohn().identifyExtremes(list);

        assertThat(devs)
                .containsExactlyInAnyOrder("Mauricio", "Annibale");
    }

    @Test
    void allDevelopersWithTheSameEstimate() {
        List<Estimate> list = Arrays.asList(
                new Estimate("Mauricio", 10),
        new Estimate("Arie", 10),
                new Estimate("Andy", 10),
                new Estimate("Frank", 10),
                new Estimate("Annibale", 10)
  );
        List<String> devs = new PlanningPokerByJohn().identifyExtremes(list);

        assertThat(devs).isEmpty();

    }

}
