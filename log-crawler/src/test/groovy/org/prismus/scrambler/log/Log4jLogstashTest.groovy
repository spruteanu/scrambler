package org.prismus.scrambler.log

import spock.lang.Specification

/**
 * @author Serge Pruteanu
 */
class Log4jLogstashTest extends Specification {

    void 'verify log4j to grok config conversion'() {
        given:
        final logstash = new Log4jLogstash(lfSeparator: '\n')
        final writer = new StringWriter()
        logstash.writeLogstashConfig(writer, 'c:/temp', 'sample-3.log', '%d %5p %c [%t] - %m%n')

        expect:
'''
input {
    file {
        path => "c:/temp/**sample-3.log"
        start_position => "beginning"
    }
}
filter {
    grok {
        match => "logLine" => "%{TIMESTAMP_ISO8601:Date}% (?<Priority>[\\w ]{5,}) %{JAVACLASS:EventCategory}% \\[(?<Thread>.+)\\] - (?<Message>.+)"
        # timestamp-format => yyyy-MM-dd HH:mm:ss,SSS
    }
}
output {
    elasticsearch { hosts => ["localhost:9200"] }
    stdout { codec => rubydebug }
}''' == writer.toString()
    }

    void 'verify log4j to grok config file conversion'() {
        given:
        final folder = new File(LogCrawlerTest.protectionDomain.codeSource.location.path)
        final logstash = new Log4jLogstash(lfSeparator: '\n', confFolder: folder)
        logstash.toLogstashConfig(new File(folder, 'log4j.properties').path)

        expect:
        3 == LogCrawler.listFiles(folder, '*.conf').size()
    }

}
