package org.prismus.scrambler.value;

import org.junit.Assert;
import org.junit.Test;
import org.prismus.scrambler.*;
import org.prismus.scrambler.beans.Address;
import org.prismus.scrambler.beans.Person;
import org.prismus.scrambler.beans.School;

import java.io.IOException;
import java.math.BigInteger;
import java.util.*;
import java.util.regex.Pattern;

/**
 * @author Serge Pruteanu
 */
public class DataScramblerTest {

    @Test
    public void test_object_methods() {
        // declare a value instance that represents constant value
        Assert.assertEquals(1, ObjectScrambler.constant(1).next().longValue());

        // declare a value instance that will return randomly generated array
        final Value<Long[]> longValues = ArrayScrambler.randomArray(1L, 10);
        Assert.assertEquals(10, longValues.next().length);

        // declare a value instance that will return randomly generated primitives array
        final Value<long[]> longPrimitives = ArrayScrambler.randomArray(new long[10]);
        Assert.assertEquals(10, longPrimitives.next().length);

        // declare a value instance that will return an element from provided array randomly
        Assert.assertTrue(new HashSet<Integer>(Arrays.asList(1, 2, 3, 4)).contains(
                ArrayScrambler.randomOf(new Integer[]{1, 2, 3, 4}).next()));

        // declare a value instance that will generate an array of Long objects randomly in a specified range
        final Value<Long[]> randomsInRange = ArrayScrambler.arrayOf(new Long[10], NumberScrambler.random(900L, 1000L));
        Assert.assertEquals(10, randomsInRange.next().length);

        // declare a value instance that will generate an array of short primitives randomly in a specified range
        final Value<short[]> primitivesInRange = ArrayScrambler.arrayOf(new short[10], NumberScrambler.random((short) 900, (short) 1000));
        Assert.assertEquals(10, primitivesInRange.next().length);
    }

    @Test
    public void test_boolean_methods() {
        // value instance that will return boolean randomly
        Assert.assertNotNull(ObjectScrambler.random(true).next());

        // value instance that will return randomly generated Boolean array
        final Value<Boolean[]> booleanValues = ArrayScrambler.randomArray(true, 10);
        Assert.assertEquals(10, booleanValues.next().length);

        // value instance that will return randomly generated primitives boolean array
        final Value<boolean[]> booleanPrimitives = ArrayScrambler.randomArray(new boolean[10]);
        Assert.assertEquals(10, booleanPrimitives.next().length);
    }

    @Test
    public void test_number_methods() {
        // generate incremental integer with default step (1)
        System.out.println(NumberScrambler.increment(1).next());

        // generate incremental long with step (100)
        System.out.println(NumberScrambler.increment(1L, 100L).next());

        // generate incremental double with step (12.5)
        System.out.println(NumberScrambler.increment(1.0d, 12.5d).next());

        // generate incremental BigInteger with step (-1)
        System.out.println(NumberScrambler.increment(BigInteger.valueOf(-1), BigInteger.valueOf(-1)).next());

        // generate incremental array with step (100) starting from 0
        Value<Integer[]> integerArray = ArrayScrambler.incrementArray(new Integer[10], 100, 10);
        System.out.println(Arrays.asList(integerArray.next()));

        // generate incremental array with step (100) starting from 1
        integerArray = ArrayScrambler.incrementArray(1, 100, 10);
        System.out.println(Arrays.asList(integerArray.next()));

        // generate incremental array with step (10.5) starting from 0
        Value<float[]> primitiveFloatArray = ArrayScrambler.incrementArray(new float[10], 10.5f, 10);
        System.out.println(Arrays.asList(primitiveFloatArray.next()));

        // generate random integer
        System.out.println(NumberScrambler.random(100).next());

        // generate random long
        System.out.println(NumberScrambler.random(1000L).next());

        // generate random short
        System.out.println(NumberScrambler.random((short) 1000).next());

        // generate random double
        System.out.println(NumberScrambler.random(300.0d).next());

        // generate random big integer
        System.out.println(NumberScrambler.random(BigInteger.valueOf(-1)).next());

        // generate random integer in a range
        System.out.println(NumberScrambler.random(70, 100).next());

        // generate random long in a range
        System.out.println(NumberScrambler.random(900L, 1000L).next());

        // generate random short in a range
        System.out.println(NumberScrambler.random((short) 980, (short) 1000).next());

        // generate random double in a range
        System.out.println(NumberScrambler.random(300.0d, 500.0d).next());

        // generate random big integer in a range
        System.out.println(NumberScrambler.random(BigInteger.valueOf(-1), BigInteger.valueOf(100)).next());
    }

    @Test
    public void test_date_methods() {
        // generate incremented date with default step and default calendar field (DAY)
        System.out.println(DateScrambler.increment(new Date()).next());
        // generate incremented date with provided step and default calendar field (DAY)
        System.out.println(DateScrambler.increment(new Date(), -1).next());
        // generate incremented date with provided step and calendar field (Calendar.MONTH)
        System.out.println(DateScrambler.increment(new Date(), -1, Calendar.MONTH).next());
        // generate incremented date by several calendar fields
        System.out.println(DateScrambler.increment(new Date()).days(-1).hours(2).minutes(15).next());

        // generate an array of dates
        System.out.println(Arrays.asList(DateScrambler.incrementArray(new Date(), 10).next()));
        // generate an array of dates by several fields
        System.out.println(Arrays.asList(DateScrambler.incrementArray(new Date(), new LinkedHashMap<Integer, Integer>() {{
            put(Calendar.DATE, 2);
            put(Calendar.MINUTE, -20);
        }}, 5).next()));

        // generate an array of dates
        System.out.println(Arrays.asList(DateScrambler.arrayOf(DateScrambler.increment(new Date()).days(-1).hours(2).minutes(15), 5).next()));

        // generate random date in a day period
        System.out.println(DateScrambler.random(new Date()).next());
        System.out.println(DateScrambler.random(new Date(),
                DateScrambler.increment(new Date(), -1, Calendar.MONTH).next(),
                DateScrambler.increment(new Date(), 1, Calendar.MONTH).next()
        ).next());

        System.out.println(Arrays.asList(DateScrambler.randomArray(new Date(),
                DateScrambler.increment(new Date(), -1, Calendar.MONTH).next(),
                DateScrambler.increment(new Date(), 1, Calendar.MONTH).next(),
                5).next()));
    }

    @Test
    public void test_string_methods() {
        // Generate an incremental string based on provided String, pattern and index
        System.out.println(StringScrambler.increment("test").next());
        System.out.println(StringScrambler.increment("test", "%s Nr%d").next());
        System.out.println(StringScrambler.increment("test", 100).next());
        System.out.println(StringScrambler.increment("test", "%s Nr%d", 100).next());

        // Generate an incremental string array based on provided String, pattern and index
        System.out.println(Arrays.asList(StringScrambler.incrementArray("test", "%s Nr%d", 100, 10).next()));

        // Generate an random string based on template String, count length
        System.out.println(StringScrambler.random("My Random String 123").next());
        System.out.println(StringScrambler.random("My Random String 123", 35).next());

        // Generate random identifier
        System.out.println(StringScrambler.randomUuid().next());

        // Generate an random string array based on template String, count length
        System.out.println(Arrays.asList(StringScrambler.randomArray("My Random String 123", 10).next()));
        System.out.println(Arrays.asList(StringScrambler.randomArray("My Random String 123", 35, 10).next()));
    }

    @Test
    public void test_collection_methods() {
        // A list of incremented integer with step 1
        System.out.println(CollectionScrambler.of(new ArrayList<Integer>(), NumberScrambler.increment(1)).next());
        // A list of random double in a range 1.0-400.0
        System.out.println(CollectionScrambler.of(new ArrayList<Double>(), NumberScrambler.random(1.0d, 400.0d)).next());

        // A random element from provided collection
        System.out.printf("%s random element: %s%n", new HashSet<String>(Arrays.asList("aa", "bb", "cc")), CollectionScrambler.randomOf(new HashSet<String>(Arrays.asList("aa", "bb", "cc"))).next());
        // A random element from provided collection
        System.out.printf("%s random element: %s%n", new HashSet<Integer>(Arrays.asList(1, 2, 3, 4, 5)), CollectionScrambler.randomOf(Arrays.asList(1, 2, 3, 4, 5)).next());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void test_map_methods() {
        System.out.println(MapScrambler.of(LinkedHashMap.class, new LinkedHashMap() {{
            put("ValueSID", NumberScrambler.increment(1));
            put("SomeID", new Constant(1));
            put("Amount", NumberScrambler.increment(100.0d));
            put("products", CollectionScrambler.collectionOf(ArrayList.class, MapScrambler.of(LinkedHashMap.class, new LinkedHashMap() {{
                put("ProductSID", NumberScrambler.increment(1));
                put("Name", new ListRandomElement<String>(Arrays.asList("Table Tennis Set", "Ping Pong Balls", "Table Tennis Racket")));
                put("Price", NumberScrambler.random(16.0d, 200.0d));
            }})));
        }}).next());
    }

    @Test
    public void test_instance_all_randomly_generated() {
        final InstanceValue<Person> personValue = InstanceScrambler.instanceOf(Person.class);
        Person person = personValue.next();
        Assert.assertNotNull(person.getFirstName());
        Assert.assertNotNull(person.getLastName());
        Assert.assertNotNull(person.getGender());
        Assert.assertNotNull(person.getDob());
        Assert.assertNotNull(person.getAddress());
        Assert.assertNotNull(person.getAddress().getNumber());
        Assert.assertNotNull(person.getAddress().getStreet());
        Assert.assertNotNull(person.getAddress().getCity());
        Assert.assertNotNull(person.getAddress().getState());
    }

    @Test
    public void test_parse_definition() throws IOException {
        final InstanceValue<Person> personValue = InstanceScrambler.instanceOf(Person.class, "/person-definition.groovy");
        Person person = personValue.next();
        Assert.assertNotNull(person.getFirstName());
        Assert.assertNotNull(person.getLastName());
        Assert.assertNotNull(person.getGender());
        Assert.assertNotNull(person.getDob());
        final Pattern pattern = Pattern.compile("[A-Z]{2}-\\d+");
        Assert.assertTrue(false == pattern.matcher(person.getAddress().getPostalCode()).matches());

        final InstanceValue<Address> addressValue = InstanceScrambler.instanceOf(Address.class, "/address-definition.groovy");
        Address address = addressValue.next();
        Assert.assertNotNull(address.getNumber());
        Assert.assertNotNull(address.getStreet());
        Assert.assertNotNull(address.getCity());
        Assert.assertNotNull(address.getState());

        Assert.assertNotNull(address.getPostalCode());

        personValue.usingDefinitions(addressValue.getDefinition()); // and now change definitions instead of random ones
        person = personValue.next();
        Assert.assertNotNull(person.getFirstName());
        Assert.assertNotNull(person.getLastName());
        Assert.assertNotNull(person.getGender());
        Assert.assertNotNull(person.getDob());
        Assert.assertTrue(person.getAddress() != null);
        Assert.assertTrue(person.getAddress().getPostalCode(), true == pattern.matcher(person.getAddress().getPostalCode()).matches());
    }

    @Test
    public void test_parse_definition_with_context_map_injection() throws IOException {
        final InstanceValue<Address> addressValue = InstanceScrambler.instanceOf(Address.class, new HashMap<String, Object>() {{
            put("state", "Washington");
        }}, "/address-definition.groovy");
        Address address = addressValue.next();
        Assert.assertTrue("Washington".equals(address.getState()));
        Assert.assertNotNull(address.getNumber());
        Assert.assertNotNull(address.getStreet());
        Assert.assertNotNull(address.getCity());
        Assert.assertNotNull(address.getPostalCode());
        Assert.assertTrue(address.getPostalCode().startsWith("WA-9"));
    }

    @Test
    public void test_complex_reused_definitions() throws IOException {
        final InstanceValue<School> schoolValue = InstanceScrambler.instanceOf(School.class, new HashMap<String, Object>() {{
            put("state", "Washington");
        }}, "/school-definition.groovy");
        School school = schoolValue.next();
        Assert.assertNotNull(school);
    }

}
