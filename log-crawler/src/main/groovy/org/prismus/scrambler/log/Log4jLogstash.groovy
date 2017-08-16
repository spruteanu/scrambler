package org.prismus.scrambler.log

import groovy.transform.CompileStatic

import java.nio.file.Path
import java.nio.file.Paths

/**
 * @author Serge Pruteanu
 */
@CompileStatic
class Log4jLogstash {
    File file
    Comparator<Path> fileSorter = LogCrawler.CREATED_DT_COMPARATOR

    String elasticHost = 'localhost'
    String elasticPort = '9200'
    boolean debug = true

    void toLogstashConfig(String log4jConfig) {
        final log4jConsumerProperties = Log4jConsumer.extractLog4jConsumerProperties(Utils.readResourceText(log4jConfig).readLines())
        if (log4jConsumerProperties.isEmpty()) {
            throw new IllegalArgumentException("Either empty or there are no file loggers defined in '$log4jConfig'")
        }
        final folder = file ?: Paths.get('').toFile()
        for (Map.Entry<String, Map<String, String>> entry : log4jConsumerProperties.entrySet()) {
            final loggerName = entry.key
            final log4jProps = entry.value
            final file = log4jProps.get(Log4jConsumer.APPENDER_FILE_PROPERTY)
            final conversionPattern = log4jProps.get(Log4jConsumer.APPENDER_CONVERSION_PATTERN_PROPERTY)
            if (file && conversionPattern) {
                new File(folder, loggerName + '.conf').withWriter { Writer writer ->
                    writeInput(writer, folder.absolutePath + '//**' + file + '*')
                    writeFilter(writer, conversionPatternToGrok(conversionPattern))
                    writeOutput(writer)
                }
            }
        }
    }

    protected writeInput(Writer writer, String filePath, boolean beginning = true) {
        final lines = []
        lines.add('input {')
        lines.add("    file {")
        lines.add("        path => \"$filePath\"")
        if (beginning) {
            lines.add("        start_position => \"beginning\"")
        }
        lines.add("    }")
        lines.add('}')
        writer.write(lines.join(LineReader.LINE_BREAK))
    }

    protected writeFilter(Writer writer, String grokMatch) {
        final lines = []
        lines.add('filter {')
        lines.add("    grok {")
        lines.add("        match => \"logLine\" => \"$grokMatch\"")
        lines.add("    }")
        lines.add('}')
        writer.write(lines.join(LineReader.LINE_BREAK))
    }

    protected writeOutput(Writer writer) {
        final lines = []
        lines.add('output {')
        lines.add("    elasticsearch { hosts => [\"$elasticHost:$elasticPort\"] }")
        if (debug) {
            lines.add('    stdout { codec => rubydebug }')
        }
        lines.add('}')
        writer.write(lines.join(LineReader.LINE_BREAK))
    }

    protected
    static int specifierToGrok(StringBuilder sb, char ch, int i, String conversionPattern) {
        final matcher = Log4jConsumer.SPEC_PATTERN.matcher(conversionPattern.substring(i + 1))
        if (!matcher.find()) {
            throw new UnsupportedOperationException("Unsupported/unknown logging conversion pattern: '${conversionPattern.substring(i + 1)}'; of '$conversionPattern'")
        }
        String regEx = ''
        String spec = matcher.group(0)
        String padding = matcher.group(1)
        String maxLength = matcher.group(2)
        ch = spec.charAt(spec.length() - 1)
        switch (ch) {
            case 'c': // logging event category (Logger logger = Logger.getLogger("foo.bar")),
                regEx = "%{JAVACLASS:${Log4jConsumer.EVENT_CATEGORY}}%"
                break
            case 'C': // fully qualified class name of the caller
                regEx = "%{JAVACLASS:${Log4jConsumer.CALLER_CLASS}}%"
                break
            case 'd': // date of the logging event. The date conversion specifier may be followed by a date format specifier enclosed between braces. For example, %d{HH:mm:ss,SSS} or %d{dd MMM yyyy HH:mm:ss,SSS}. If no date format specifier is given then ISO8601 format is assumed.
                i = dateFormatToRegex(sb, i, conversionPattern)
                break
            case 'F': // file name where the logging request was issued.
                regEx = "(?<${Log4jConsumer.CALLER_FILE_NAME}>[^ ]+)"
                break
            case 'l': // file name where the logging request was issued. The location information depends on the JVM implementation but usually consists of the fully qualified name of the calling method followed by the callers source the file name and line number between parentheses.
                regEx = "(?<${Log4jConsumer.CALLER_LOCATION}>[^ ]+)"
                break
            case 'L': // line number from where the logging request was issued.
                regEx = "(?<${Log4jConsumer.CALLER_LOCATION}>[\\d^ ]+)"
                break
            case 'm': // message
                regEx = "(?<${Log4jConsumer.MESSAGE}>.+)"
                break
            case 'M': // method name where the logging request was issued.
                regEx = "%{JAVAMETHOD:${Log4jConsumer.CALLER_METHOD}}%"
                break
            case 'n': // line break, skip it
                return i + 2
            case 'p': // priority of the logging event.
                regEx = "(?<${Log4jConsumer.PRIORITY}>[\\w ]+)"
                break
            case 'r': // number of milliseconds
                regEx = "(?<${Log4jConsumer.LOGGING_DURATION}>[\\d^ ]+)"
                break
            case 't': // name of the thread that generated the logging event.
                regEx = "(?<${Log4jConsumer.THREAD_NAME}>.+)"
                break
            case 'x': // NDC (nested diagnostic context) associated with the thread that generated the logging event.
                regEx = "(?<${Log4jConsumer.THREAD_NDC}>[^ ]*)"
                break
            case 'X': // MDC (mapped diagnostic context) associated with the thread that generated the logging event. The X conversion character must be followed by the key for the map placed between braces, as in %X{clientNumber} where clientNumber is the key. The value in the MDC corresponding to the key will be output.
                regEx = "(?<${Log4jConsumer.THREAD_MDC}>[^ ]*)"
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

    protected static String conversionPatternToGrok(final String conversionPattern) {
        final sb = new StringBuilder()
        final cs = '%' as char
        final length = conversionPattern.length()
        final chars = conversionPattern.toCharArray()
        for (int i = 0; i < length; i++) {
            char ch = chars[i]
            if (ch == cs) {
                if (i < length && chars[i + 1] == cs) {
                    Log4jConsumer.appendNonSpecifierChar(sb, '\\' as char)
                    Log4jConsumer.appendNonSpecifierChar(sb, ch)
                    i++
                    continue
                }
                i = specifierToGrok(sb, ch, i, conversionPattern)
            } else {
                Log4jConsumer.appendNonSpecifierChar(sb, ch)
            }
        }
        return sb.toString()
    }

    protected
    static int dateFormatToRegex(StringBuilder sb, int index, String specString) {
        final pattern = ~/d(\{.+\})*/
        final matcher = pattern.matcher(specString.substring(index + 1))
        if (!matcher.find()) {
            throw new UnsupportedOperationException("Unsupported/unknown logging conversion pattern: ${specString.substring(index + 1)}; of $specString")
        }
        String format = matcher.group(1)
        String dateFormat = matcher.group(1)
        if (format) {
            switch (format.trim()) {
                case Log4jConsumer.ABSOLUTE_LDF:
                    dateFormat = "(?<${Log4jConsumer.DATE}>${Log4jConsumer.dateFormatToRegEx(Log4jConsumer.ABSOLUTE_DATE_FORMAT)})"
                    index += Log4jConsumer.ABSOLUTE_LDF.length()
                    break
                case Log4jConsumer.DATE_LDF:
                    dateFormat = "%{TIMESTAMP_ISO8601:${Log4jConsumer.DATE}}%"
                    index += Log4jConsumer.DATE_LDF.length()
                    break
                case Log4jConsumer.ISO8601_LDF:
                    dateFormat = "%{TIMESTAMP_ISO8601:${Log4jConsumer.DATE}}%"
                    index += Log4jConsumer.ISO8601_LDF.length()
                    break
                default:
                    dateFormat = "(?<${Log4jConsumer.DATE}>${Log4jConsumer.dateFormatToRegEx(format.substring(1, format.length() - 1))})"
                    index += dateFormat.length() + 2
                    break
            }
        }
        if (!dateFormat) {
            dateFormat = "%{TIMESTAMP_ISO8601:${Log4jConsumer.DATE}}%"
        }
        sb.append(dateFormat)
        return index
    }

}
