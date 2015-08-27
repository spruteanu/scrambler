package org.prismus.scrambler.value

import org.prismus.scrambler.CollectionScrambler
import org.prismus.scrambler.MapScrambler
import org.prismus.scrambler.NumberScrambler
import org.prismus.scrambler.Value
import spock.lang.Specification

/**
 * @author Serge Pruteanu
 */
class MapValueTest extends Specification {

    @SuppressWarnings("GroovyAssignabilityCheck")
    void 'verify map creation'() {
        given:
        Map<String, Value> keyValueMap = ['ValueSID': NumberScrambler.increment(1), 'SomeID': new Constant(1), 'Amount': NumberScrambler.increment(100.0d)]
        MapValue mapValue = MapScrambler.of(new HashMap(), keyValueMap)
        final generatedMap = mapValue.next()

        expect:
        generatedMap.keySet().containsAll(keyValueMap.keySet())
        generatedMap == MapScrambler.of(HashMap,
                ['ValueSID': NumberScrambler.increment(1), 'SomeID': new Constant(1), 'Amount': NumberScrambler.increment(100.0d)]
        ).next()

        and: 'verify case where a map of map is generated'
        MapScrambler.of(Hashtable,
                ['ValueSID': NumberScrambler.increment(1), 'SomeID': new Constant(1), 'Amount': NumberScrambler.increment(100.0d),
                 'products': CollectionScrambler.collectionOf(
                         ArrayList,
                         MapScrambler.of(LinkedHashMap, [
                                 'ProductSID': NumberScrambler.increment(1),
                                 'Name': new ListRandomElement<String>(Arrays.asList('Table Tennis Set', 'Ping Pong Balls', 'Table Tennis Racket')),
                                 'Price': NumberScrambler.random(16.0d, 200.0d),
                         ])
                 )
                ]
        ).next().products.size() > 0
    }

}
