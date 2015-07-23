package org.prismus.scrambler.value

import groovy.transform.CompileStatic
import org.prismus.scrambler.Value

/**
 * todo: add description
 *
 * @author Serge Pruteanu
 */
@CompileStatic
class MapCategory {

    public static <K> MapValue<K> of(Map<K, Object> self, Map<K, Value> keyValueMap) {
        return new MapValue<K>(self, keyValueMap)
    }

}
