package org.prismus.scrambler.value

import org.prismus.scrambler.DataScrambler
import org.prismus.scrambler.Value
import spock.lang.Specification

/**
 * @author Serge Pruteanu
 */
class MapValueTest extends Specification {

    @SuppressWarnings("GroovyAssignabilityCheck")
    void 'verify map creation'() {
        given:
        Map<String, Value> keyValueMap = ['ValueSID': DataScrambler.increment(1), 'SomeID': new Constant(1), 'Amount': DataScrambler.increment(100.0d)]
        MapValue mapValue = DataScrambler.of(new HashMap(), keyValueMap)
        final generatedMap = mapValue.next()

        expect:
        generatedMap.keySet().containsAll(keyValueMap.keySet())
        generatedMap == DataScrambler.mapOf(HashMap,
                ['ValueSID': DataScrambler.increment(1), 'SomeID': new Constant(1), 'Amount': DataScrambler.increment(100.0d)]
        ).next()

        and: 'verify case where a map of map is generated'
        DataScrambler.mapOf(Hashtable,
                ['ValueSID': DataScrambler.increment(1), 'SomeID': new Constant(1), 'Amount': DataScrambler.increment(100.0d),
                 'products': DataScrambler.collectionOf(
                         ArrayList,
                         DataScrambler.mapOf(LinkedHashMap, [
                                 'ProductSID': DataScrambler.increment(1),
                                 'Name': new ListRandomElement<String>(Arrays.asList('Table Tennis Set', 'Ping Pong Balls', 'Table Tennis Racket')),
                                 'Price': DataScrambler.random(16.0d, 200.0d),
                         ])
                 )
                ]
        ).next().products.size() > 0
    }

}
