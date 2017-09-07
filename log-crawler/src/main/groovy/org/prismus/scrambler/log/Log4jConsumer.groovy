/*
 * Log crawler, tool that allows to extract/crawl log files for further analysis
 *
 * Copyright (c) 2015, Sergiu Prutean. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library.
 */

package org.prismus.scrambler.log

import groovy.transform.CompileStatic

import java.text.SimpleDateFormat
import java.util.regex.Pattern

/**
 * @author Serge Pruteanu
 */
@CompileStatic
class Log4jConsumer extends RegexConsumer {
//    protected static final String CLASS_REGEX = '(?:[a-zA-Z$_][a-zA-Z$_0-9]*\\.)*[a-zA-Z$_][a-zA-Z$_0-9]*'
    protected static final String ABSOLUTE_LDF = '{ABSOLUTE}'
    protected static final String DATE_LDF = '{DATE}'
    protected static final String ISO8601_LDF = '{ISO8601}'
    static final String ABSOLUTE_DATE_FORMAT = 'HH:mm:ss,SSS'
    static final String ISO8601_DATE_FORMAT = 'yyyy-MM-dd HH:mm:ss,SSS' // ISO8601DateFormat
    static final String DATE_FORMAT = 'dd MMM yyyy HH:mm:ss,SSS'

    private static final Set<Character> REG_EX_CHARS_SET = toSet('[]{}\\^$|?*+()')
    protected static Pattern SPEC_PATTERN = ~/([-\d]*)([\.\d]*)[cCdFlLmMnprtxX]/

    static final String EVENT_CATEGORY = 'EventCategory'
    static final String CALLER_CLASS = 'CallerClass'
    static final String DATE = 'Date'
    static final String CALLER_FILE_NAME = 'CallerFileName'
    static final String CALLER_LOCATION = 'CallerLocation'
    static final String CALLER_LINE = 'CallerLine'
    static final String MESSAGE = 'Message'
    static final String CALLER_METHOD = 'Method'
    static final String PRIORITY = 'Priority'
    static final String LOGGING_DURATION = 'LoggingDuration'
    static final String THREAD_NAME = 'Thread'
    static final String THREAD_MDC = 'ThreadMdc'
    static final String THREAD_NDC = 'ThreadNdc'

    static final String APPENDER_FILE_PROPERTY = 'File'
    static final String APPENDER_CONVERSION_PATTERN_PROPERTY = 'ConversionPattern'

    String dateFormat

    Log4jConsumer group(String group, Integer index = null, LogConsumer consumer = null) {
        if (index == null) {
            index = groupIndexMap.size() + 1
        }
        super.group(group, index, consumer)
        return this
    }

    protected Log4jConsumer dateFormat(String dateFormat) {
        group(DATE)
        this.dateFormat = dateFormat
        return this
    }

    Log4jConsumer date(String dateFormat = null) {
        if (!dateFormat) {
            dateFormat = this.dateFormat
        }
        add(DATE, DateConsumer.of(dateFormat, DATE))
        return this
    }

    Log4jConsumer exception() {
        add(MESSAGE, new ExceptionConsumer(MESSAGE))
        return this
    }

    Log4jConsumer message(LogConsumer consumer) {
        add(MESSAGE, consumer)
        return this
    }

    Log4jConsumer message(@DelegatesTo(LogEntry) Closure closure) {
        return message(new ClosureConsumer(closure))
    }

    static Log4jConsumer of(String conversionPattern) {
        final consumer = new Log4jConsumer()
        conversionPatternToRegex(consumer, conversionPattern)
        return consumer
    }

    protected static void appendNonSpecifierChar(StringBuilder sb, char ch) {
        if (REG_EX_CHARS_SET.contains(ch)) {
            sb.append('\\' as char)
            sb.append(ch)
        } else {
            sb.append(ch)
        }
    }

    protected
    static int dateFormatToRegex(Log4jConsumer consumer, StringBuilder sb, int index, String specString) {
        final pattern = ~/d(\{.+\})*/
        final matcher = pattern.matcher(specString.substring(index + 1))
        if (!matcher.find()) {
            throw new UnsupportedOperationException("Unsupported/unknown logging conversion pattern: ${specString.substring(index + 1)}; of $specString")
        }
        String format = matcher.group(1)
        String dateFormat = matcher.group(1)
        if (format) {
            switch (format.trim()) {
                case ABSOLUTE_LDF:
                    dateFormat = ABSOLUTE_DATE_FORMAT
                    index += ABSOLUTE_LDF.length()
                    break
                case DATE_LDF:
                    dateFormat = DATE_FORMAT
                    index += DATE_LDF.length()
                    break
                case ISO8601_LDF:
                    dateFormat = ISO8601_DATE_FORMAT
                    index += ISO8601_LDF.length()
                    break
                default:
                    dateFormat = format.substring(1, format.length() - 1)
                    index += dateFormat.length() + 2
                    break
            }
        }
        if (!dateFormat) {
            dateFormat = ISO8601_DATE_FORMAT
        }
        sb.append('(').append(dateFormatToRegEx(dateFormat)).append(')')
        consumer.dateFormat(dateFormat)
        return index
    }

    protected
    static int specifierToRegex(Log4jConsumer consumer, StringBuilder sb, char ch, int i, String conversionPattern) {
        final matcher = SPEC_PATTERN.matcher(conversionPattern.substring(i + 1))
        if (!matcher.find()) {
            throw new UnsupportedOperationException("Unsupported/unknown logging conversion pattern: '${conversionPattern.substring(i + 1)}'; of '$conversionPattern'")
        }
        String regEx = ''
        String spec = matcher.group(0)
        String padding = matcher.group(1)
        String maxLength = matcher.group(2)
        ch = spec.charAt(spec.length() - 1)
        switch (ch) {
            case 'c': // logging event category
                regEx = '[^ ]+'
                consumer.group(EVENT_CATEGORY)
                break
            case 'C': // fully qualified class name of the caller
                regEx = '[^ ]+'
                consumer.group(CALLER_CLASS)
                break
            case 'd': // date of the logging event. The date conversion specifier may be followed by a date format specifier enclosed between braces. For example, %d{HH:mm:ss,SSS} or %d{dd MMM yyyy HH:mm:ss,SSS}. If no date format specifier is given then ISO8601 format is assumed.
                i = dateFormatToRegex(consumer, sb, i, conversionPattern)
                break
            case 'F': // path name where the logging request was issued.
                regEx = '[^ ]+'
                consumer.group(CALLER_FILE_NAME)
                break
            case 'l': // path name where the logging request was issued. The location information depends on the JVM implementation but usually consists of the fully qualified name of the calling method followed by the callers source the path name and line number between parentheses.
                regEx = '[^ ]+'
                consumer.group(CALLER_LOCATION)
                break
            case 'L': // line number from where the logging request was issued.
                regEx = '[\\d^ ]+'
                consumer.group(CALLER_LINE)
                break
            case 'm': // message
                regEx = '.+'
                consumer.group(MESSAGE)
                break
            case 'M': // method name where the logging request was issued.
                regEx = '[^ ]+'
                consumer.group(CALLER_METHOD)
                break
            case 'n': // line break, skip it
                return i + 2
            case 'p': // priority of the logging event.
                regEx = '[\\w ]+'
                consumer.group(PRIORITY)
                break
            case 'r': // number of milliseconds
                regEx = '[\\d^ ]+'
                consumer.group(LOGGING_DURATION)
                break
            case 't': // name of the thread that generated the logging event.
                regEx = '.+'
                consumer.group(THREAD_NAME)
                break
            case 'x': // NDC (nested diagnostic context) associated with the thread that generated the logging event.
                regEx = '[^ ]*'
                consumer.group(THREAD_NDC)
                break
            case 'X': // MDC (mapped diagnostic context) associated with the thread that generated the logging event. The X conversion character must be followed by the key for the map placed between braces, as in %X{clientNumber} where clientNumber is the key. The value in the MDC corresponding to the key will be output.
                regEx = '[^ ]*'
                consumer.group(THREAD_MDC)
                break
            default:
                throw new UnsupportedOperationException("Unsupported/unknown logging conversion pattern: '${conversionPattern.substring(i + 1)}'; of '$conversionPattern'")
        }
        String paddingRegEx = ''
        if (padding) {
            padding = padding.trim()
            if (padding.charAt(0) == '-' as char) {
                padding = padding.substring(1)
                regEx = '\\s*' + regEx
            }
            paddingRegEx = padding
        }
        if (maxLength) {
            if (!paddingRegEx) {
                paddingRegEx = '1'
            }
            maxLength = maxLength.substring(1).trim()
            regEx = "${regEx.substring(0, regEx.length() - 1)}{$paddingRegEx,${maxLength.trim()}}"
        } else {
            if (paddingRegEx) {
                regEx = "${regEx.substring(0, regEx.length() - 1)}{$paddingRegEx,}"
            }
        }
        if (regEx) {
            sb.append('(').append(regEx).append(')')
        }
        return i + spec.length()
    }

    protected static String conversionPatternToRegex(final Log4jConsumer consumer, final String conversionPattern) {
        final sb = new StringBuilder()
        final cs = '%' as char
        final length = conversionPattern.length()
        final chars = conversionPattern.toCharArray()
        for (int i = 0; i < length; i++) {
            char ch = chars[i]
            if (ch == cs) {
                if (i < length && chars[i + 1] == cs) {
                    appendNonSpecifierChar(sb, '\\' as char)
                    appendNonSpecifierChar(sb, ch)
                    i++
                    continue
                }
                i = specifierToRegex(consumer, sb, ch, i, conversionPattern)
            } else {
                appendNonSpecifierChar(sb, ch)
            }
        }
        final regex = sb.toString()
        consumer.pattern = ~/^(?ms)$regex/
        return regex
    }

    protected static Map<String, Map<String, String>> extractLog4jConsumerProperties(List<String> lines) {
        final Map<String, Map<String, String>> appenderProps = [:]
        final Set<String> log4jSet = [APPENDER_FILE_PROPERTY, APPENDER_CONVERSION_PATTERN_PROPERTY, 'builder', 'consumer'] as Set
        final filePattern = ~/(?<fileFilter>[^\/\\]+\..+)/

        for (String line : lines) {
            if (!line.startsWith('log4j.appender.')) {
                continue
            }
            line = line.substring('log4j.appender.'.length())
            final keys = line.split('=')
            final apps = keys[0].split('\\.')
            String value = keys.length == 2 ? keys[1] : null
            if (apps.length == 1) {
                appenderProps.put(apps[0], [:])
            } else {
                final String cat
                if (apps.length == 2 && log4jSet.contains(apps[1])) {
                    cat = apps[1]
                    value = toMap(filePattern, value).get('fileFilter').toString() + '*'
                } else if (apps.length == 3) {
                    if (log4jSet.contains(apps[1])) {
                        cat = apps[1]
                        value = apps[2]
                    } else if (log4jSet.contains(apps[2])) {
                        cat = apps[2]
                    }
                }
                if (cat) {
                    appenderProps.get(apps[0])?.put(cat, value)
                }
            }
        }
        return appenderProps
    }

    protected static Map<String, String> toLog4jFileConversionPattern(Map<String, Map<String, String>> log4jConsumerProps) {
        final filterMap = [:]
        for (Map<String, String> log4jProps : log4jConsumerProps.values()) {
            final file = log4jProps.get(APPENDER_FILE_PROPERTY)
            final conversionPattern = log4jProps.get(APPENDER_CONVERSION_PATTERN_PROPERTY)
            if (file && conversionPattern) {
                filterMap.put(file, conversionPattern)
            }
        }
        return filterMap
    }

    private static Set<Character> toSet(String chString) {
        final set = new HashSet<Character>(chString.length())
        for (char ch : chString.toCharArray()) {
            set.add(ch)
        }
        return set
    }

    /**
     * @author Serge Pruteanu
     */
    @CompileStatic
    static class Builder extends RegexConsumer.Builder {

        Builder() {
        }

        Builder(LogCrawler.Builder contextBuilder, def consumer) {
            super(contextBuilder, consumer)
        }

        Builder date(String dateFormat = null) {
            group(DATE, dateFormat ? DateConsumer.of(dateFormat, DATE) : new DateConsumer(null, DATE))
            return this
        }

        Builder date(SimpleDateFormat dateFormat) {
            group(DATE, DateConsumer.of(dateFormat, DATE))
            return this
        }

        Builder exception() {
            group(MESSAGE, new ExceptionConsumer(MESSAGE))
            return this
        }

        Builder message(@DelegatesTo(LogEntry) Closure closure) {
            group(MESSAGE, closure)
            return this
        }

        Builder message(LogConsumer consumer) {
            super.group(MESSAGE, consumer)
            if (consumer instanceof RegexConsumer) {
                ((RegexConsumer) consumer).group = MESSAGE
            }
            return this
        }

        Builder message(ConsumerBuilder builder) {
            return message(builder.build())
        }

        Builder message(String pattern, @DelegatesTo(RegexConsumer.Builder) Closure closure = null) {
            return message(Pattern.compile(pattern), closure)
        }

        Builder message(Pattern pattern, @DelegatesTo(RegexConsumer.Builder) Closure closure = null) {
            final builder = new RegexConsumer.Builder(contextBuilder, of(pattern))
            LogCrawler.checkDelegateClosure(closure, builder)
            return message(builder)
        }

        RegexConsumer match(Pattern pattern, @DelegatesTo(RegexConsumer.Builder) Closure closure = null) {
            final builder = new RegexConsumer.Builder(contextBuilder, of(pattern))
            LogCrawler.checkDelegateClosure(closure, builder)
            return builder.build()
        }

        protected void buildConsumers(RegexConsumer instance) {
            Log4jConsumer result = instance as Log4jConsumer
            for (Map.Entry<String, List> entry : consumerMap.entrySet()) {
                final consumers = entry.value
                for (Object obj : consumers) {
                    final LogConsumer cs = newConsumer(obj)
                    if (cs instanceof DateConsumer) {
                        ((DateConsumer) cs).setDateFormat(new SimpleDateFormat(result.dateFormat))
                    }
                    result.group(entry.key, cs)
                }
            }
        }
    }
}
