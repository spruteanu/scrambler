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
        Map<String, Value> keyValueMap = ['ValueSID': NumberCategory.increment(1), 'SomeID': new Constant(1), 'Amount': NumberCategory.increment(100.0d)]
        MapValue mapValue = MapCategory.of(new HashMap(), keyValueMap)
        final generatedMap = mapValue.next()

        expect:
        generatedMap.keySet().containsAll(keyValueMap.keySet())
        generatedMap == ClassCategory.mapOf(HashMap,
                ['ValueSID': NumberCategory.increment(1), 'SomeID': new Constant(1), 'Amount': NumberCategory.increment(100.0d)]
        ).next()

        and: 'verify case where a map of map is generated'
        ClassCategory.mapOf(Hashtable,
                ['ValueSID': NumberCategory.increment(1), 'SomeID': new Constant(1), 'Amount': NumberCategory.increment(100.0d),
                 'products': ClassCategory.collectionOf(
                         ArrayList,
                         ClassCategory.mapOf(LinkedHashMap, [
                                 'ProductSID': NumberCategory.increment(1),
                                 'Name': new ListRandomElement<String>(Arrays.asList('Table Tennis Set', 'Ping Pong Balls', 'Table Tennis Racket')),
                                 'Price': NumberCategory.random(16.0d, 200.0d),
                         ])
                 )
                ]
        ).next().products.size() > 0
    }

}
