package org.prismus.scrambler.log

import groovy.transform.CompileStatic

import java.util.regex.Pattern

/**
 * @author Serge Pruteanu
 */
@CompileStatic
class ExceptionConsumer implements LogConsumer {
    protected static final String EXCEPTION_REGEX = '^.+Exception[^\\r\\n]++(?:\\s+at .++)+'

    private static final Pattern EXCEPTION_PATTERN = ~/(?ms)(${EXCEPTION_REGEX})/
    private static final Pattern MESSAGE_WITH_EXCEPTION_PATTERN = ~/(?ms)([^\r\n]+)[\r\n]+(${EXCEPTION_REGEX})/
    private static final Pattern EXCEPTION_CLASS_MESSAGE_PATTERN = ~/^(.+Exception):([^\r\n]+)*/
    private static final Pattern TRACE_CLASS_METHOD_SOURCE_PATTERN = ~/^\s* at\s(.+)\((.+):(\d+)\)/

    static final String LOG_ERROR_MESSAGE = 'LogErrorMessage'
    static final String EXCEPTION = 'Exception'
    static final String EXCEPTION_MESSAGE = 'ExceptionMessage'
    static final String EXCEPTION_CLASS = 'ExceptionClass'
    static final String EXCEPTION_TRACES = 'ExceptionTraces'
    static final String CALLER_CLASS_METHOD = 'CallerClassMethod'
    static final String SOURCE_NAME = 'SourceName'
    static final String SOURCE_LINE = 'SourceLine'

    String group
    boolean includeTraces

    ExceptionConsumer(String group = null) {
        this.group = group
    }

    ExceptionConsumer includeTraces() {
        includeTraces = true
        return this
    }

    @Override
    void consume(LogEntry entry) {
        final value = entry.get(group)
        if (value && EXCEPTION_PATTERN.matcher(value.toString()).matches()) {
            entry.logValueMap.putAll(RegexConsumer.toMap(MESSAGE_WITH_EXCEPTION_PATTERN, value.toString(), [(LOG_ERROR_MESSAGE): 1, (EXCEPTION): 2]))
            if (entry.logValueMap.containsKey(EXCEPTION)) {
                final exception = entry.get(EXCEPTION).toString()
                final lines = exception.split('\r\n|\r|\n').toList()
                entry.logValueMap.putAll(RegexConsumer.toMap(EXCEPTION_CLASS_MESSAGE_PATTERN, lines[0], [(EXCEPTION_CLASS): 1, (EXCEPTION_MESSAGE): 2]))
                if (includeTraces) {
                    final traces = []
                    for (String traceLine : lines.subList(1, lines.size())) {
                        traces.add(RegexConsumer.toMap(TRACE_CLASS_METHOD_SOURCE_PATTERN, traceLine, [(CALLER_CLASS_METHOD): 1, (SOURCE_NAME): 2, (SOURCE_LINE): 3]))
                    }
                    if (traces) {
                        entry.put(EXCEPTION_TRACES, traces)
                    }
                }
            }
        }
    }
}
