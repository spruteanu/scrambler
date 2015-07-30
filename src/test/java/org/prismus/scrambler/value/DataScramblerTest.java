package org.prismus.scrambler.value;

import org.junit.Test;
import org.prismus.scrambler.DataScrambler;
import org.prismus.scrambler.beans.Address;
import org.prismus.scrambler.beans.Person;
import org.prismus.scrambler.beans.School;
import org.spockframework.util.Assert;

import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Pattern;

/**
 * @author Serge Pruteanu
 */
public class DataScramblerTest {

    @Test
    public void test_parse_definition() throws IOException {
        final InstanceValue<Person> personValue = DataScrambler.instanceOf(Person.class, "/person-definition.groovy");
        Person person = personValue.next();
        Assert.notNull(person.getFirstName());
        Assert.notNull(person.getLastName());
        Assert.notNull(person.getSex());
        Assert.notNull(person.getDob());
        final Pattern pattern = Pattern.compile("[A-W]{2}-\\d+");
        Assert.that(false == pattern.matcher(person.getAddress().getPostalCode()).matches());

        final InstanceValue<Address> addressValue = DataScrambler.instanceOf(Address.class, "/address-definition.groovy");
        Address address = addressValue.next();
        Assert.notNull(address.getNumber());
        Assert.notNull(address.getStreet());
        Assert.notNull(address.getCity());
        Assert.notNull(address.getState());

        Assert.notNull(address.getPostalCode());

        personValue.usingDefinitions(addressValue.getDefinition()); // and now change definitions instead of random ones
        person = personValue.next();
        Assert.notNull(person.getFirstName());
        Assert.notNull(person.getLastName());
        Assert.notNull(person.getSex());
        Assert.notNull(person.getDob());
        Assert.that(person.getAddress() != null);
        Assert.that(true == pattern.matcher(person.getAddress().getPostalCode()).matches());
    }

    @Test
    public void test_parse_definition_with_context_map_injection() throws IOException {
        final InstanceValue<Address> addressValue = DataScrambler.instanceOf(Address.class, new HashMap<String, Object>() {{
            put("state", "Washington");
        }}, "/address-definition.groovy");
        Address address = addressValue.next();
        Assert.that("Washington".equals(address.getState()));
        Assert.notNull(address.getNumber());
        Assert.notNull(address.getStreet());
        Assert.notNull(address.getCity());
        Assert.notNull(address.getPostalCode());
        Assert.that(address.getPostalCode().startsWith("WA-9"));
    }

    @Test
    public void test_complex_reused_definitions() throws IOException {
        final InstanceValue<School> schoolValue = DataScrambler.instanceOf(School.class, new HashMap<String, Object>() {{
            put("state", "Washington");
        }}, "/school-definition.groovy");
        School school = schoolValue.next();
        Assert.notNull(school);
    }

}
