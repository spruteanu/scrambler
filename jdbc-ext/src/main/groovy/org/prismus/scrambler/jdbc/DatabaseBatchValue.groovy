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
class DatabaseBatchValue extends Constant<Map<String, Object>> {
    final Map<String, Value> tableValueBatch

    DatabaseBatchValue(Map<String, Value> tableValueBatch) {
        this.tableValueBatch = tableValueBatch
    }

    @Override
    protected Map<String, Object> doNext() {
        final resultMap = new LinkedHashMap<String, Object>(tableValueBatch.size())
        for (Map.Entry<String, Value> entry : tableValueBatch.entrySet()) {
            resultMap.put(entry.key, entry.value.next())
        }
        return resultMap
    }

}
