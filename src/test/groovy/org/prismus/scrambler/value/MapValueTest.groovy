package org.prismus.scrambler.value

import org.prismus.scrambler.Value
import spock.lang.Specification

/**
 * @author Serge Pruteanu
 */
class MapValueTest extends Specification {

    @SuppressWarnings("GroovyAssignabilityCheck")
    void 'verify map creation'() {
        given:
        Map<String, Value> keyValueMap = ['ValueSID': NumberValue.increment(1), 'SomeID': new Constant(1), 'Amount': NumberValue.increment(100.0d)]
        MapValue mapValue = MapValue.of(new HashMap(), keyValueMap)
        final generatedMap = mapValue.next()

        expect:
        generatedMap.keySet().containsAll(keyValueMap.keySet())
        generatedMap == ClassValue.of(HashMap,
                ['ValueSID': NumberValue.increment(1), 'SomeID': new Constant(1), 'Amount': NumberValue.increment(100.0d)]
        ).next()

        and: 'verify case where a map of map is generated'
        ClassValue.of(Hashtable,
                ['ValueSID': NumberValue.increment(1), 'SomeID': new Constant(1), 'Amount': NumberValue.increment(100.0d),
                 'products': CollectionValue.of(
                         ArrayList,
                         ClassValue.of(LinkedHashMap, [
                                 'ProductSID': NumberValue.increment(1),
                                 'Name': new ListRandomElement<String>(Arrays.asList('Table Tennis Set', 'Ping Pong Balls', 'Table Tennis Racket')),
                                 'Price': NumberValue.random(16.0d, 200.0d),
                         ])
                 )
                ]
        ).next().products.size() > 0
    }

}
