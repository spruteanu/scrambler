package org.prismus.scrambler.log

import groovy.transform.CompileStatic

import java.nio.file.Path
import java.nio.file.Paths

/**
 * @author Serge Pruteanu
 */
@CompileStatic
class Log4jLogstash {
    protected String lfSeparator = LineReader.LINE_BREAK

    File confFolder

    Comparator<Path> fileSorter = LogCrawler.CREATED_DT_COMPARATOR
    String elasticHost = 'localhost'
    String elasticPort = '9200'
    boolean debug = true
    boolean oneLogstash = true

    protected writeInput(Writer writer, String loggerName, String filePath, boolean beginning = true) {
        final lines = []
        writer.write(lfSeparator)
        if (!oneLogstash) {
            lines.add('input {')
        }

        lines.add("    file {")
        lines.add("        path => \"$filePath\"")
        lines.add("        type => \"$loggerName\"")
        if (beginning) {
            lines.add("        start_position => \"beginning\"")
        }
        lines.add("    }")

        if (!oneLogstash) {
            lines.add('}')
        }
        writer.write(lines.join(lfSeparator))
    }

    protected writeFilter(Writer writer, String loggerName, String grokMatch, Map<String, String> fields) {
        final lines = []
        writer.write(lfSeparator)
        if (oneLogstash) {
            lines.add("if [type] == \"$loggerName\" {")
        } else {
            lines.add('filter {')
            lines.add("#if [type] == \"$loggerName\" {")
            lines.add("    #some matching here")
            lines.add("#}")
        }

        lines.add("    grok {")
        lines.add("        match => { \"logLine\" => '$grokMatch' }")
        if (fields) {
            lines.add("        # ${fields.entrySet().collect { "$it.key => $it.value" }.join('; ')}")
        }
        lines.add("    }")
        lines.add('}')
        writer.write(lines.join(lfSeparator))
    }

    protected writeOutput(Writer writer, String loggerName) {
        final lines = []
        writer.write(lfSeparator)
        lines.add('output {')

        lines.add("#if [type] == \"$loggerName\" {")
        lines.add("    #some output here")
        lines.add("#}")

        lines.add("    elasticsearch { hosts => [\"$elasticHost:$elasticPort\"] }")
        if (debug) {
            lines.add('    # Next lines are only for debugging.')
            lines.add('    stdout { codec => rubydebug }')
            lines.add("    # file {path => \"${loggerName}.result\" codec => rubydebug}")
        }
        lines.add('}')
        writer.write(lines.join(lfSeparator))
    }

    protected void writeLogstashConfig(Writer writer, String loggerName,
                                       String destinationPath, String logFile, String conversionPattern) {
        final fields = [:]
        writeInput(writer, loggerName, destinationPath + '/**' + logFile)
        writeFilter(writer, loggerName, conversionPatternToGrok(conversionPattern, fields), fields)
        writeOutput(writer, loggerName)
    }

    protected void appenderLogstash(File folder, Map<String, Map<String, String>> log4jProperties) {
        for (Map.Entry<String, Map<String, String>> entry : log4jProperties.entrySet()) {
            final loggerName = entry.key
            final log4jProps = entry.value
            final file = log4jProps.get(Log4jConsumer.APPENDER_FILE_PROPERTY)
            final conversionPattern = log4jProps.get(Log4jConsumer.APPENDER_CONVERSION_PATTERN_PROPERTY)
            if (file && conversionPattern) {
                new File(folder, loggerName + '.rb').withWriter { Writer writer ->
                    writeLogstashConfig(writer, loggerName, folder.absolutePath, file, conversionPattern)
                }
            }
        }
    }

    protected void oneLogstash(String log4jConfig, File folder, Map<String, Map<String, String>> log4jProperties) {
        final inputWriter = new StringWriter()
        final filterWriter = new StringWriter()
        final outputWriter = new StringWriter()
        for (Map.Entry<String, Map<String, String>> entry : log4jProperties.entrySet()) {
            final loggerName = entry.key
            final log4jProps = entry.value
            final file = log4jProps.get(Log4jConsumer.APPENDER_FILE_PROPERTY)
            final conversionPattern = log4jProps.get(Log4jConsumer.APPENDER_CONVERSION_PATTERN_PROPERTY)
            if (file && conversionPattern) {
                final fields = [:]
                writeInput(inputWriter, loggerName, folder.absolutePath.replaceAll('\\\\', '/') + '/**' + file)
                writeFilter(filterWriter, loggerName, conversionPatternToGrok(conversionPattern, fields), fields)
            }
        }
        new File(folder, new File(log4jConfig).name + '.rb').withWriter {
            it.write('input {')
            it.write(inputWriter.toString())
            it.write(lfSeparator)
            it.write("}")
            it.write(lfSeparator)

            it.write(lfSeparator)
            it.write('filter {')
            it.write(filterWriter.toString())
            it.write(lfSeparator)
            it.write("}")
            it.write(lfSeparator)

            writeOutput(it, 'some-file-name')
            it.write(lfSeparator)
        }
    }

    void toLogstashConfig(String log4jConfig) {
        final log4jProperties = Log4jConsumer.extractLog4jConsumerProperties(Utils.readResourceText(log4jConfig).readLines())
        if (log4jProperties.isEmpty()) {
            throw new IllegalArgumentException("Either empty or there are no confFolder loggers defined in '$log4jConfig'")
        }
        final folder = confFolder ?: Paths.get('').toAbsolutePath().toFile()
        if (oneLogstash) {
            oneLogstash(log4jConfig, folder, log4jProperties)
        } else {
            appenderLogstash(folder, log4jProperties)
        }
    }

    protected int specifierToGrok(StringBuilder sb, char ch, int i, String conversionPattern, Map<String, String> fields = [:]) {
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
                regEx = "%{JAVACLASS:${Log4jConsumer.EVENT_CATEGORY}}"
                break
            case 'C': // fully qualified class name of the caller
                regEx = "%{JAVACLASS:${Log4jConsumer.CALLER_CLASS}}"
                break
            case 'd': // date of the logging event. The date conversion specifier may be followed by a date format specifier enclosed between braces. For example, %d{HH:mm:ss,SSS} or %d{dd MMM yyyy HH:mm:ss,SSS}. If no date format specifier is given then ISO8601 format is assumed.
                i = dateFormatToRegex(sb, i, conversionPattern, fields)
                break
            case 'F': // confFolder name where the logging request was issued.
                regEx = "?<${Log4jConsumer.CALLER_FILE_NAME}>[^ ]+"
                break
            case 'l': // confFolder name where the logging request was issued. The location information depends on the JVM implementation but usually consists of the fully qualified name of the calling method followed by the callers source the confFolder name and line number between parentheses.
                regEx = "?<${Log4jConsumer.CALLER_LOCATION}>[^ ]+"
                break
            case 'L': // line number from where the logging request was issued.
                regEx = "?<${Log4jConsumer.CALLER_LOCATION}>[\\d^ ]+"
                break
            case 'm': // message
                regEx = "?<${Log4jConsumer.MESSAGE}>.+"
                break
            case 'M': // method name where the logging request was issued.
                regEx = "%{JAVAMETHOD:${Log4jConsumer.CALLER_METHOD}}"
                break
            case 'n': // line break, skip it
                return i + 2
            case 'p': // priority of the logging event.
                regEx = "?<${Log4jConsumer.PRIORITY}>[\\w ]+"
                break
            case 'r': // number of milliseconds
                regEx = "?<${Log4jConsumer.LOGGING_DURATION}>[\\d^ ]+"
                break
            case 't': // name of the thread that generated the logging event.
                regEx = "?<${Log4jConsumer.THREAD_NAME}>.+"
                break
            case 'x': // NDC (nested diagnostic context) associated with the thread that generated the logging event.
                regEx = "?<${Log4jConsumer.THREAD_NDC}>[^ ]*"
                break
            case 'X': // MDC (mapped diagnostic context) associated with the thread that generated the logging event. The X conversion character must be followed by the key for the map placed between braces, as in %X{clientNumber} where clientNumber is the key. The value in the MDC corresponding to the key will be output.
                regEx = "?<${Log4jConsumer.THREAD_MDC}>[^ ]*"
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
            if (regEx.startsWith('%')) {
                sb.append(regEx)
            } else {
                sb.append('(').append(regEx).append(')')
            }
        }
        return i + spec.length()
    }

    protected String conversionPatternToGrok(final String conversionPattern, Map<String, String> fields = [:]) {
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
                i = specifierToGrok(sb, ch, i, conversionPattern, fields)
            } else {
                Log4jConsumer.appendNonSpecifierChar(sb, ch)
            }
        }
        return sb.toString()
    }

    protected int dateFormatToRegex(StringBuilder sb, int index, String specString, Map<String, String> fields) {
        final pattern = ~/d(\{.+\})*/
        final matcher = pattern.matcher(specString.substring(index + 1))
        if (!matcher.find()) {
            throw new UnsupportedOperationException("Unsupported/unknown logging conversion pattern: ${specString.substring(index + 1)}; of $specString")
        }
        String format = matcher.group(1)
        String regex = matcher.group(1)
        String dateFormat = null
        if (format) {
            switch (format.trim()) {
                case Log4jConsumer.ABSOLUTE_LDF:
                    dateFormat = Log4jConsumer.ABSOLUTE_DATE_FORMAT
                    regex = "?<${Log4jConsumer.DATE}>${Log4jConsumer.dateFormatToRegEx(dateFormat)}"
                    index += Log4jConsumer.ABSOLUTE_LDF.length()
                    break
                case Log4jConsumer.DATE_LDF:
                    dateFormat = Log4jConsumer.DATE_FORMAT
                    regex = "?<${Log4jConsumer.DATE}>${Log4jConsumer.dateFormatToRegEx(dateFormat)}"
                    index += Log4jConsumer.DATE_LDF.length()
                    break
                case Log4jConsumer.ISO8601_LDF:
                    dateFormat = Log4jConsumer.ISO8601_DATE_FORMAT
                    regex = "%{TIMESTAMP_ISO8601:${Log4jConsumer.DATE}}"
                    index += Log4jConsumer.ISO8601_LDF.length()
                    break
                default:
                    dateFormat = format.substring(1, format.length() - 1)
                    regex = "?<${Log4jConsumer.DATE}>${Log4jConsumer.dateFormatToRegEx(dateFormat)}"
                    index += regex.length() + 2
                    break
            }
        }
        if (!regex) {
            regex = "%{TIMESTAMP_ISO8601:${Log4jConsumer.DATE}}"
            dateFormat = Log4jConsumer.ISO8601_DATE_FORMAT
        }
        if (regex.startsWith('%')) {
            sb.append(regex)
        } else {
            sb.append('(').append(regex).append(')')
        }
        fields.put('timestamp-format', dateFormat)
        return index
    }

    private static void usage() {
        println """
Converts log4j to log-stash config

Usage:
log4j-to-logstash log4j.properties [conf output folder]
"""
    }

    static void main(String[] args) {
        try {
            if (!args) {
                usage()
                return
            }
            final logstash = new Log4jLogstash()
            if (args.length > 1) {
                logstash.confFolder = new File(args[1])
            }
            logstash.toLogstashConfig(args[0])
        } catch (IllegalArgumentException | UnsupportedOperationException ignore) {
            System.err.println(ignore)
            usage()
        }
    }

}
