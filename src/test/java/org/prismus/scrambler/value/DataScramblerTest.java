package org.prismus.scrambler.value;

import org.junit.Test;
import org.prismus.scrambler.DataScrambler;
import org.prismus.scrambler.beans.Address;
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
        final InstanceValue<Person> personValue = DataScrambler.instanceOf(Person.class, "/person-definition.groovy");
        Person person = personValue.next();
        Assert.notNull(person.getFirstName());
        Assert.notNull(person.getLastName());
        Assert.notNull(person.getSex());
        Assert.notNull(person.getDob());

        final InstanceValue<Address> addressValue = DataScrambler.instanceOf(Address.class, "/address-definition.groovy");
        Address address = addressValue.next();
        Assert.notNull(address.getNumber());
        Assert.notNull(address.getStreet());
        Assert.notNull(address.getCity());
        Assert.notNull(address.getState());
        Assert.notNull(address.getPostalCode());

        personValue.usingDefinitions(addressValue.getDefinition());
        person = personValue.next();
        Assert.notNull(person.getFirstName());
        Assert.notNull(person.getLastName());
        Assert.notNull(person.getSex());
        Assert.notNull(person.getDob());
        Assert.that(person.getAddress() != null);
    }

}
