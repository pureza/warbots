package io.github.pureza.warbots.fuzzy;

import nrc.fuzzy.FuzzyException;
import nrc.fuzzy.FuzzyRule;
import nrc.fuzzy.TrapezoidFuzzySet;
import nrc.fuzzy.TriangleFuzzySet;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class RuleBuilderTest {

    @Test
    public void buildsSimpleRule() throws FuzzyException {
        Variable temperature = new Variable("temperature", 0, 8);
        Term cold = temperature.addTerm(new Term("cold", new TrapezoidFuzzySet(0, 0, 1, 4)));
        Term hot = temperature.addTerm(new Term("hot", new TriangleFuzzySet(1, 4, 8)));

        Variable airConditioner = new Variable("airConditioner", 0, 8);
        Term off = airConditioner.addTerm(new Term("off", new TrapezoidFuzzySet(0, 0, 1, 4)));
        Term on = airConditioner.addTerm(new Term("on", new TriangleFuzzySet(1, 4, 8)));

        FuzzyRule rule = RuleBuilder.when(temperature.is(cold))
                        .then(airConditioner.is(off))
                        .toFuzzyRule();

        assertThat(rule.antecedentsSize(), is(1));
        assertThat(rule.antecedentAt(0).getFuzzyVariable().getName(), is("temperature"));

        assertThat(rule.conclusionsSize(), is(1));
        assertThat(rule.conclusionAt(0).getFuzzyVariable().getName(), is("airConditioner"));
    }


    @Test
    public void buildsSimpleComplexRule() throws FuzzyException {
        Variable temperature = new Variable("temperature", 0, 8);
        Term cold = temperature.addTerm(new Term("cold", new TrapezoidFuzzySet(0, 0, 1, 4)));
        Term hot = temperature.addTerm(new Term("hot", new TriangleFuzzySet(1, 4, 8)));

        Variable humidity = new Variable("humidity", 0, 8);
        Term low = humidity.addTerm(new Term("low", new TrapezoidFuzzySet(0, 0, 1, 4)));
        Term high = humidity.addTerm(new Term("high", new TriangleFuzzySet(1, 4, 8)));


        Variable airConditioner = new Variable("airConditioner", 0, 8);
        Term off = airConditioner.addTerm(new Term("off", new TrapezoidFuzzySet(0, 0, 1, 4)));
        Term on = airConditioner.addTerm(new Term("on", new TriangleFuzzySet(1, 4, 8)));

        Variable dehumidifier = new Variable("dehumidifier", 0, 8);
        Term enabled = dehumidifier.addTerm(new Term("enabled", new TrapezoidFuzzySet(0, 0, 1, 4)));
        Term disabled = dehumidifier.addTerm(new Term("disabled", new TriangleFuzzySet(1, 4, 8)));

        FuzzyRule rule = RuleBuilder.when(temperature.is(cold))
                .and(humidity.is(high))
                .then(airConditioner.is(off))
                .and(dehumidifier.is(enabled))
                .toFuzzyRule();

        assertThat(rule.antecedentsSize(), is(2));
        assertThat(rule.antecedentAt(0).getFuzzyVariable().getName(), is("temperature"));
        assertThat(rule.antecedentAt(1).getFuzzyVariable().getName(), is("humidity"));

        assertThat(rule.conclusionsSize(), is(2));
        assertThat(rule.conclusionAt(0).getFuzzyVariable().getName(), is("airConditioner"));
        assertThat(rule.conclusionAt(1).getFuzzyVariable().getName(), is("dehumidifier"));
    }
}
