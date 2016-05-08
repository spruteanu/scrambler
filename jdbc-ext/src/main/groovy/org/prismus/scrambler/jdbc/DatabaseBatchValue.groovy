package org.prismus.scrambler.jdbc

import groovy.transform.CompileStatic
import groovy.transform.PackageScope
import org.prismus.scrambler.Value
import org.prismus.scrambler.value.Constant

/**
 * todo: add description
 *
 * @author Serge Pruteanu
 */
@CompileStatic
@PackageScope
class DatabaseBatchValue extends Constant<Map<String, Map<String, Object>>> {
    final Map<String, Value<Map<String, Object>>> tableValueBatch

    DatabaseBatchValue(Map<String, Value<Map<String, Object>>> tableValueBatch) {
        this.tableValueBatch = tableValueBatch
    }

    @Override
    protected Map<String, Map<String, Object>> doNext() {
        final resultMap = new TreeMap<String, Map<String, Object>>(String.CASE_INSENSITIVE_ORDER)
        for (Map.Entry<String, Value<Map<String, Object>>> entry : tableValueBatch.entrySet()) {
            resultMap.put(entry.key, entry.value.next())
        }
        return resultMap
    }

}
