package org.prismus.scrambler.value;

import org.junit.Test;
import org.prismus.scrambler.DataScrambler;
import org.prismus.scrambler.beans.Person;
import org.spockframework.util.Assert;

import java.io.IOException;
import java.util.Date;

/**
 * @author Serge Pruteanu
 */
public class DataScramblerTest {

    @Test
    public void test_parse_definition() throws IOException {
        DataScrambler.random(new Date());
        final InstanceValue<Person> value = DataScrambler.instanceOf(Person.class, "/person-definition.groovy");
        Assert.notNull(value.next());
    }

}
